# 修复403 Forbidden错误

## 🔍 错误分析

**错误信息**：
```
Failed to load resource: the server responded with a status of 403 (Forbidden)
更新失败: Error: Request failed with status code 403
```

**403错误原因**：
1. ❌ 跨域配置不包含前端地址（8081端口）
2. ❌ Spring Security拦截了请求
3. ❌ 拦截器无法从Authorization header获取token

---

## ✅ 已修复的三个问题

### 1. 跨域配置 - WebMvcConfig.java

**问题**：只允许`http://localhost:8080`，但前端运行在8081

**修改前**：
```java
.allowedOrigins("http://localhost:8080")
```

**修改后**：
```java
.allowedOrigins("http://localhost:8080", "http://localhost:8081")  // 添加8081
```

### 2. 拦截器认证 - LoginTicketInterceptor.java

**问题**：只从Cookie获取token，无法识别Authorization header

**修改前**：
```java
String ticket = CookieUtil.getValue(request, "ticket");
```

**修改后**：
```java
// 优先从Authorization header获取
String ticket = request.getHeader("Authorization");
if (ticket == null || ticket.isEmpty()) {
    ticket = CookieUtil.getValue(request, "ticket");
}
```

### 3. Security配置 - SecurityConfig.java

**问题**：缺少`/user/email`端点配置

**修改后**：
```java
.requestMatchers(
        "/post/add",
        "/comment/add",
        "/message/**",
        "/like",
        "/favorite/**",
        "/user/profile",
        "/user/avatar",
        "/user/email"  // 新增
).hasAnyAuthority(AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_MODERATOR)
```

---

## 🔧 重新编译后端

**重要**：必须重新编译后端！

### 方式1：IDE重新编译
```
1. 停止当前Spring Boot应用
2. Build → Clean Project
3. Build → Rebuild Project  
4. 重新运行 CampusCommunitySystemApplication
```

### 方式2：Maven命令
```bash
cd campus-community-system
mvn clean install -DskipTests
# 然后在IDE中重新启动应用
```

### 验证编译成功
后端启动日志应该显示：
```
Started CampusCommunitySystemApplication in X.XXX seconds
```

---

## 🧪 测试步骤

### 步骤1：清除缓存并重新登录

```javascript
// 在浏览器控制台执行
localStorage.clear();
window.location.reload();
```

然后重新登录

### 步骤2：验证token

```javascript
// 在浏览器控制台执行
console.log("Token:", localStorage.getItem("token"));
console.log("User:", JSON.parse(localStorage.getItem("user")));
```

**预期结果**：
```
Token: "xxxxxxxxx"  // 不是null
User: {id: 11, username: "jennifer", ...}
```

### 步骤3：测试保存功能

1. 打开开发者工具（F12）
2. 切换到**Console**标签
3. 进入"编辑个人资料"
4. 修改个人简介
5. 点击"保存修改"

### 步骤4：查看日志

**成功的日志**：
```
开始更新用户资料...
原始数据: {username: "jennifer", introduction: "旧的简介", ...}
新数据: {username: "jennifer", introduction: "新的简介", ...}
更新个人简介: 新的简介
简介更新结果: {code: 200, data: null, message: null}
所有更新完成
✅ 个人资料更新成功！
```

**失败的日志**（如果还是403）：
```
更新失败: Error: Request failed with status code 403
```

### 步骤5：检查Network

1. 切换到**Network**标签
2. 找到`profile`请求
3. 点击查看详情

**Request Headers应该包含**：
```
Authorization: xxx-xxx-xxx
Content-Type: application/json
Origin: http://localhost:8081
```

**Response应该是**：
```
Status: 200 OK
{code: 200, data: null, message: null}
```

---

## 🐛 如果还是403错误

### 检查清单

#### ✅ 1. 确认后端已重新编译
```bash
# 检查class文件的修改时间
ls -lt campus-community-system/forum-api/target/classes/com/campus/forum/config/
ls -lt campus-community-system/forum-api/target/classes/com/campus/forum/api/interceptor/
```

最新修改的文件应该是：
- WebMvcConfig.class
- LoginTicketInterceptor.class
- SecurityConfig.class（在forum-service中）

#### ✅ 2. 确认已清除缓存并重新登录
```javascript
localStorage.clear();
// 刷新页面
// 重新登录
```

#### ✅ 3. 查看后端日志

在拦截器中添加日志：
```java
// LoginTicketInterceptor.java
@Override
public boolean preHandle(...) {
    String ticket = request.getHeader("Authorization");
    System.out.println(">>> Request URI: " + request.getRequestURI());
    System.out.println(">>> Authorization Header: " + ticket);
    System.out.println(">>> Request Method: " + request.getMethod());
    
    if (ticket == null || ticket.isEmpty()) {
        ticket = CookieUtil.getValue(request, "ticket");
        System.out.println(">>> Cookie Ticket: " + ticket);
    }
    
    // ... 后续代码
}
```

**预期后端日志**：
```
>>> Request URI: /user/profile
>>> Authorization Header: xxx-xxx-xxx
>>> Request Method: PUT
```

#### ✅ 4. 确认用户权限

在`UserServiceImpl`的`getAuthorities`方法中添加日志：
```java
public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
    User user = this.findUserById(userId);
    System.out.println(">>> Getting authorities for user: " + user.getUsername());
    System.out.println(">>> User type: " + user.getType());
    
    List<GrantedAuthority> list = new ArrayList<>();
    list.add(new GrantedAuthority() {
        @Override
        public String getAuthority() {
            switch (user.getType()) {
                case 1:
                    System.out.println(">>> Authority: ADMIN");
                    return AUTHORITY_ADMIN;
                case 2:
                    System.out.println(">>> Authority: MODERATOR");
                    return AUTHORITY_MODERATOR;
                default:
                    System.out.println(">>> Authority: USER");
                    return AUTHORITY_USER;
            }
        }
    });
    return list;
}
```

**预期输出**：
```
>>> Getting authorities for user: jennifer
>>> User type: 0
>>> Authority: USER
```

---

## 📋 修改文件清单

### 后端文件（3个）
1. ✅ `LoginTicketInterceptor.java` - 支持Authorization header
2. ✅ `WebMvcConfig.java` - 添加8081端口跨域
3. ✅ `SecurityConfig.java` - 添加/user/email端点

---

## 🎯 完整测试用例

### 测试1：保存个人简介
```
1. 登录系统
2. 进入编辑页面
3. 修改简介为："这是测试简介"
4. 点击保存
期望：✅ 成功保存，显示成功提示
```

### 测试2：上传头像
```
1. 点击"选择本地图片"
2. 选择一张图片
3. 预览确认
4. 点击保存
期望：✅ 成功保存，头像更新
```

### 测试3：修改邮箱
```
1. 输入新邮箱：test@example.com
2. 点击保存
期望：✅ 成功保存（即使后端未实现也应返回200）
```

### 测试4：退出登录后访问
```
1. 退出登录
2. 直接访问 /profile/edit
期望：✅ 重定向到首页或显示"请先登录"
```

---

## 🔐 认证流程图

```
用户登录
  ↓
生成Token (ticket)
  ↓
存储到localStorage
  ↓
设置axios header: Authorization: token
  ↓
发送PUT请求
  ↓
后端拦截器 (LoginTicketInterceptor)
  ├─ 从Authorization header获取token
  ├─ 从Redis查询LoginTicket
  ├─ 验证token有效性
  ├─ 查询用户信息
  ├─ 设置到HostHolder
  └─ 设置到SecurityContext
  ↓
Spring Security过滤器
  ├─ 检查URL: /user/profile
  ├─ 检查权限: AUTHORITY_USER
  └─ 通过 ✅
  ↓
Controller (UserController)
  ├─ hostHolder.getUser() → 获取当前用户 ✅
  ├─ userService.updateProfile()
  └─ 返回 200 OK
```

---

## 💡 常见问题FAQ

### Q1: 为什么要支持Authorization header和Cookie两种方式？
**A**: 
- Authorization header：现代SPA应用（如Vue/React）
- Cookie：传统服务端渲染应用

### Q2: 为什么需要重新登录？
**A**: 清除缓存后token会丢失，需要重新生成新的token

### Q3: SecurityConfig的权限配置是什么意思？
**A**: 
- `hasAnyAuthority(AUTHORITY_USER, ...)` - 需要普通用户、管理员或版主权限
- `permitAll()` - 任何人都可以访问

### Q4: 403和401有什么区别？
**A**:
- 401 Unauthorized - 未认证（没有登录）
- 403 Forbidden - 已认证但没有权限

---

## 🎉 成功标准

1. ✅ 可以正常保存个人简介
2. ✅ 可以正常保存头像
3. ✅ 显示"个人资料更新成功！"
4. ✅ 自动跳转回个人主页
5. ✅ Network显示200 OK
6. ✅ 控制台无403错误

---

**现在请重新编译后端，清除浏览器缓存，重新登录，然后测试！** 🚀


