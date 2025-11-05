# 调试403错误 - 详细日志版本

## 🔍 当前状态

你的错误显示：
```
更新个人简介: 123
更新失败: Error: Request failed with status code 403
```

说明：
- ✅ 前端成功发送了请求
- ❌ 后端返回了403错误
- ❓ 需要查看后端日志来确定原因

---

## 📝 已添加的调试日志

### 1. 拦截器日志 (LoginTicketInterceptor)
```java
>>> Interceptor: PUT /user/profile
>>> Authorization header: xxx
>>> Cookie ticket: xxx
>>> LoginTicket from Redis: xxx
>>> Found user: username
>>> User set to HostHolder and SecurityContext
```

### 2. Controller日志 (UserController)
```java
>>> updateProfile called
>>> currentUser: User(id=11, username=jennifer)
>>> currentUser ID: 11, username: jennifer
>>> Updating introduction to: 123
>>> Update successful
```

---

## 🔧 操作步骤

### 步骤1：重新编译后端（必须！）

**在IDE中**：
```
1. 停止Spring Boot应用
2. Build → Clean Project
3. Build → Rebuild Project
4. 等待编译完成（查看控制台）
5. 重新运行 CampusCommunitySystemApplication
```

**验证编译成功**：
- 后端控制台显示启动成功
- 看到 "Started CampusCommunitySystemApplication"

### 步骤2：清除浏览器缓存

在浏览器控制台（F12 → Console）运行：
```javascript
localStorage.clear();
location.reload();
```

### 步骤3：重新登录

1. 输入用户名和密码
2. 登录成功后，在控制台验证：
```javascript
console.log("Token:", localStorage.getItem("token"));
console.log("User:", localStorage.getItem("user"));
```

### 步骤4：测试保存并查看后端日志

1. 进入"编辑个人资料"
2. 修改个人简介为："测试123"
3. 点击"保存修改"
4. **立即切换到后端控制台**
5. 查看输出的日志

---

## 📊 预期的后端日志

### 成功的完整日志：
```
>>> Interceptor: PUT /user/profile
>>> Authorization header: xxxxxxxxxxxxxxxxxx
>>> LoginTicket from Redis: LoginTicket(userId=11, ticket=xxx, status=0, expired=...)
>>> Found user: jennifer
>>> User set to HostHolder and SecurityContext
>>> updateProfile called
>>> currentUser: User(id=11, username=jennifer, ...)
>>> currentUser ID: 11, username: jennifer
>>> Updating introduction to: 测试123
>>> Update successful
```

### 失败的日志（问题1 - 没有token）：
```
>>> Interceptor: PUT /user/profile
>>> Authorization header: null
>>> Cookie ticket: null
>>> No ticket found
>>> updateProfile called
>>> currentUser: null
>>> currentUser is NULL! Returning 403
```
**解决**：重新登录

### 失败的日志（问题2 - token无效）：
```
>>> Interceptor: PUT /user/profile
>>> Authorization header: xxxxxxxxxx
>>> LoginTicket from Redis: null
>>> LoginTicket is invalid or expired
>>> updateProfile called
>>> currentUser: null
>>> currentUser is NULL! Returning 403
```
**解决**：token已过期或无效，需要重新登录

### 失败的日志（问题3 - 拦截器未执行）：
```
>>> updateProfile called
>>> currentUser: null
>>> currentUser is NULL! Returning 403
```
**解决**：拦截器配置有问题或未重新编译

---

## 🐛 根据日志诊断问题

### 情况1：看不到任何">>> Interceptor"日志

**原因**：后端没有重新编译，仍在运行旧代码

**解决**：
1. 完全停止后端应用
2. 在IDE中执行 Build → Rebuild Project
3. 确认看到 "Build successful" 消息
4. 重新启动应用

### 情况2：看到">>> Authorization header: null"

**原因**：前端没有发送Authorization header

**解决**：
```javascript
// 在浏览器控制台检查
console.log("Token:", localStorage.getItem("token"));

// 如果是null，需要重新登录
localStorage.clear();
location.reload();
// 然后重新登录
```

### 情况3：看到">>> LoginTicket from Redis: null"

**原因**：Redis中没有这个ticket，可能已过期或未登录

**解决**：
```javascript
// 清除旧token，重新登录
localStorage.clear();
location.reload();
```

### 情况4：看到">>> Found user: null"

**原因**：用户ID在数据库中不存在

**解决**：检查数据库，确认用户是否存在

### 情况5：看到所有日志都正常，但还是403

**原因**：Spring Security拦截了请求

**检查**：
1. SecurityConfig中的权限配置
2. 用户的权限是否正确（AUTHORITY_USER）

---

## 🔍 额外诊断步骤

### 检查1：验证前端发送的请求

在浏览器的Network标签中：
1. 找到`profile`请求
2. 点击查看详情
3. 查看Request Headers：

**应该包含**：
```
Authorization: xxxxxxxxxx  ← 必须有这个！
Content-Type: application/json
Origin: http://localhost:8081
```

### 检查2：查看响应内容

在Network标签的Response部分：
```json
{
  "code": 403,
  "message": "你还没有登录哦"
}
```

### 检查3：测试其他API

在浏览器控制台测试：
```javascript
// 测试获取用户信息（不需要认证）
axios.get("/api/user/profile/11")
  .then(res => console.log("✅ 获取成功:", res.data))
  .catch(err => console.error("❌ 获取失败:", err));

// 测试需要认证的API
axios.get("/api/message/unread-count")
  .then(res => console.log("✅ 认证成功:", res.data))
  .catch(err => console.error("❌ 认证失败:", err));
```

---

## 📋 完整的检查清单

### 后端检查
- [ ] 已停止旧的应用
- [ ] 已执行 Clean Project
- [ ] 已执行 Rebuild Project
- [ ] 看到编译成功消息
- [ ] 重新启动应用
- [ ] 控制台显示"Started CampusCommunitySystemApplication"
- [ ] Redis服务正在运行

### 前端检查
- [ ] 已清除localStorage
- [ ] 已刷新页面
- [ ] 重新登录
- [ ] localStorage中有token
- [ ] localStorage中有user

### 测试检查
- [ ] 进入编辑页面
- [ ] 修改个人简介
- [ ] 点击保存
- [ ] 查看前端控制台
- [ ] **查看后端控制台**（重要！）
- [ ] 查看Network标签

---

## 🎯 关键提示

### 最常见的原因

**90%的情况是**：后端没有真正重新编译

**验证方法**：
1. 保存个人资料
2. 立即查看后端控制台
3. 如果看不到">>> Interceptor"日志 → 没有重新编译
4. 如果看到">>> Interceptor"日志 → 已经重新编译，继续看其他日志

### 如何确保重新编译

**方式1：完全重启IDE**
```
1. 关闭所有IDE窗口
2. 重新打开项目
3. Build → Rebuild Project
4. 启动应用
```

**方式2：使用Maven命令**
```bash
cd campus-community-system
mvn clean
mvn install -DskipTests
# 然后在IDE中启动应用
```

**方式3：删除target目录**
```
1. 在项目中找到所有target目录
2. 手动删除它们
3. 在IDE中 Build → Rebuild Project
4. 启动应用
```

---

## 🚀 完整测试流程

### 1. 准备阶段
```
✓ 后端完全停止
✓ 执行 Clean + Rebuild
✓ 重新启动后端
✓ 查看启动日志确认成功
```

### 2. 前端准备
```javascript
// 在浏览器控制台
localStorage.clear();
location.reload();
// 重新登录
```

### 3. 测试并查看日志
```
1. 登录成功
2. 进入编辑页面
3. 修改简介
4. 点击保存
5. **立即查看后端控制台**
6. 截图后端日志
7. 告诉我看到了什么
```

---

## 📸 需要提供的信息

如果还是失败，请告诉我：

### 后端日志
```
粘贴从">>> Interceptor"开始的所有日志
```

### 前端错误
```
粘贴浏览器控制台的完整错误信息
```

### Network信息
```
Request Headers:
- Authorization: ?
- Content-Type: ?

Response:
- Status: ?
- Body: ?
```

---

**现在请按照上述步骤操作，特别注意查看后端控制台的日志，然后告诉我看到了什么！** 🔍


