# 个人资料保存功能修复

## 🔍 问题根本原因

**问题**：修改个人资料后点击保存，提示"更新失败，请稍后再试"

**根本原因**：后端拦截器无法正确识别前端的认证token

### 认证流程分析

#### 前端认证方式
```javascript
// store/index.js
axios.defaults.headers.common["Authorization"] = token;
```
前端将token存储在**Authorization header**中

#### 后端拦截器（修复前）
```java
// LoginTicketInterceptor.java - 旧代码
String ticket = CookieUtil.getValue(request, "ticket");
```
拦截器只从**Cookie**中获取ticket，获取不到则`HostHolder.getUser()`返回null

#### 结果
```java
// UserController.java
User currentUser = hostHolder.getUser();
if (currentUser == null) {
    return Result.error(403, "你还没有登录哦");  // ❌ 总是返回这个错误
}
```

---

## ✅ 修复方案

### 修改拦截器支持两种认证方式

**文件**：`LoginTicketInterceptor.java`

**修改前**：
```java
@Override
public boolean preHandle(...) {
    // 只从cookie获取
    String ticket = CookieUtil.getValue(request, "ticket");
    // ...
}
```

**修改后**：
```java
@Override
public boolean preHandle(...) {
    // 优先从Authorization header获取，如果没有则从cookie获取
    String ticket = request.getHeader("Authorization");
    if (ticket == null || ticket.isEmpty()) {
        ticket = CookieUtil.getValue(request, "ticket");
    }
    // ...
}
```

### 支持的认证方式

| 认证方式 | 使用场景 | 优先级 |
|---------|---------|--------|
| Authorization Header | 前端Ajax请求 | 高 |
| Cookie | 传统表单提交 | 低 |

---

## 🧪 测试步骤

### 1. 重新编译后端

**重要**：必须重新编译后端才能生效！

#### 方式1：使用IDE
```
1. 停止当前运行的应用
2. Build → Rebuild Project
3. 运行 CampusCommunitySystemApplication
```

#### 方式2：使用Maven
```bash
cd campus-community-system
mvn clean install
# 然后重新运行应用
```

### 2. 测试登录状态

1. 打开浏览器，按F12打开开发者工具
2. 切换到**Console**标签
3. 登录系统
4. 在控制台输入：
```javascript
console.log("Token:", localStorage.getItem("token"));
console.log("User:", localStorage.getItem("user"));
```

**预期结果**：
```
Token: "xxx-xxx-xxx-xxx"
User: "{\"id\":11,\"username\":\"jennifer\",...}"
```

### 3. 测试个人资料保存

#### 步骤
1. 登录后点击"个人主页"
2. 点击"编辑个人资料"
3. 修改个人简介，例如改为："测试保存功能"
4. 点击"保存修改"

#### 查看控制台日志

**成功的日志**：
```
开始更新用户资料...
原始数据: {username: "jennifer", ...}
新数据: {introduction: "测试保存功能", ...}
更新个人简介: 测试保存功能
简介更新结果: {code: 200, data: null, message: null}
所有更新完成
✅ 个人资料更新成功！
```

**失败的日志**：
```
开始更新用户资料...
原始数据: {...}
新数据: {...}
更新个人简介: ...
更新失败: Error: Request failed with status code 403
❌ 你还没有登录哦
```

### 4. 检查Network请求

1. 切换到**Network**标签
2. 点击保存
3. 找到`profile`请求
4. 查看**Request Headers**

**应该包含**：
```
Authorization: xxx-xxx-xxx-xxx
Content-Type: application/json
```

**查看Response**：
- 成功：`{code: 200, data: null}`
- 失败：`{code: 403, message: "你还没有登录哦"}`

---

## 🐛 如果还是失败

### 检查清单

#### 1. 确认后端已重新编译
```bash
# 检查编译时间
ls -l campus-community-system/forum-api/target/classes/com/campus/forum/api/interceptor/

# 确认 LoginTicketInterceptor.class 的修改时间是最新的
```

#### 2. 确认token存在
在浏览器控制台执行：
```javascript
console.log(localStorage.getItem("token"));
// 应该输出token字符串，不是null
```

#### 3. 确认请求包含Authorization header
在Network标签中查看请求头

#### 4. 查看后端日志
在后端控制台添加日志：
```java
// LoginTicketInterceptor.java
@Override
public boolean preHandle(...) {
    String ticket = request.getHeader("Authorization");
    System.out.println(">>> Authorization header: " + ticket);
    
    if (ticket == null || ticket.isEmpty()) {
        ticket = CookieUtil.getValue(request, "ticket");
        System.out.println(">>> Cookie ticket: " + ticket);
    }
    
    // ... 后续代码
}
```

### 常见问题

#### Q1: 重新编译后还是失败？
**A**: 确保：
1. 完全停止了旧的应用
2. 清理了target目录：`mvn clean`
3. 重新编译：`mvn install`
4. 重新启动应用

#### Q2: token是null？
**A**: 需要重新登录
1. 点击"退出登录"
2. 重新登录
3. 再次尝试保存

#### Q3: 后端获取不到Authorization header？
**A**: 检查跨域配置
```java
// WebMvcConfig.java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:8080", "http://localhost:8081") // 添加8081
            .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true)
            .maxAge(3600)
            .allowedHeaders("*");  // 允许所有header
}
```

---

## 📝 修改文件清单

### 后端
- ✅ `LoginTicketInterceptor.java` - 支持Authorization header认证

### 前端
- ✅ `ProfileEdit.vue` - 已添加详细日志（之前的修改）

---

## 🎯 验证成功标准

1. ✅ 可以正常保存个人简介
2. ✅ 可以正常保存头像
3. ✅ 保存后显示"个人资料更新成功！"
4. ✅ 自动跳转回个人主页
5. ✅ 个人主页显示更新后的信息
6. ✅ 刷新页面，信息保持不变

---

## 💡 技术总结

### 认证机制

#### 传统Web应用
```
浏览器 → Cookie (HttpOnly) → 后端
```

#### 现代SPA应用
```
浏览器 → localStorage → Authorization Header → 后端
```

### 为什么要支持两种方式？

1. **兼容性**：支持传统表单提交和Ajax请求
2. **灵活性**：适应不同的前端架构
3. **安全性**：保持现有的token验证机制

### 最佳实践

**生产环境建议**：
1. 使用HttpOnly Cookie（防止XSS攻击）
2. 使用Secure标志（仅HTTPS传输）
3. 设置合理的过期时间
4. 实现Token刷新机制

---

## 🚀 下一步

修复完成后，建议：
1. 测试所有需要登录的功能
2. 测试退出登录后的访问限制
3. 测试Token过期后的行为
4. 优化错误提示信息

---

**现在请重新编译后端，然后按照测试步骤验证保存功能！** 🎉


