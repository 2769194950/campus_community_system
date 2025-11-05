# 登录问题调试指南

## 问题描述
登录成功后提示"登录成功！"，但是：
- 调试信息仍显示"未登录状态"
- 页面顶部没有显示用户头像
- 用户状态没有正确更新

## 可能的原因

### 1. 后端未重新编译
我们修改了 `UserServiceImpl.java` 来返回用户信息，但后端可能还没有重新编译。

### 2. 前端状态管理问题
Vue的响应式更新可能存在延迟或问题。

## 调试步骤

### 步骤 1: 检查后端返回数据

1. 打开浏览器开发者工具 (F12)
2. 切换到 "Console" (控制台) 标签
3. 尝试登录
4. 查看控制台输出，应该看到类似这样的日志：

```
Login response: {code: 200, data: {...}}
Response data: {ticket: "xxx", user: {...}}
Login successful, user: {...}, token: "xxx"
Setting user: {...}
User saved to localStorage
Setting token: "xxx"
Token saved to localStorage
```

**如果看到 `user: undefined`**，说明后端没有返回用户信息，需要重新编译后端。

### 步骤 2: 重新编译并启动后端

#### 方式 1: 使用IDE (推荐)
1. 在 IntelliJ IDEA 中停止当前运行的应用
2. 点击 "Build" -> "Rebuild Project"
3. 重新运行 `CampusCommunitySystemApplication.java`

#### 方式 2: 使用Maven命令
```bash
cd campus-community-system
mvn clean install
```

然后重新启动后端应用。

### 步骤 3: 清除浏览器缓存

1. 打开浏览器开发者工具 (F12)
2. 右键点击浏览器刷新按钮
3. 选择 "清空缓存并硬性重新加载"
4. 或者在控制台运行：
```javascript
localStorage.clear()
location.reload()
```

### 步骤 4: 测试登录流程

1. 重新打开前端页面 (http://localhost:8081)
2. 点击"登录"按钮
3. 输入测试账号：
   - 用户名: `user1`
   - 密码: `123456`
4. 点击登录
5. 查看控制台日志

### 步骤 5: 验证结果

登录成功后应该：
1. ✅ 看到"登录成功！"提示
2. ✅ 页面自动刷新
3. ✅ 顶部显示用户头像和用户名
4. ✅ 调试信息显示"当前用户: user1 (ID: x)"
5. ✅ 点击头像可以看到下拉菜单

## 数据格式说明

### 后端应该返回的格式：
```json
{
  "code": 200,
  "data": {
    "ticket": "uuid-token-string",
    "user": {
      "id": 1,
      "username": "user1",
      "avatarUrl": "http://...",
      "type": 0,
      "status": 1,
      ...
    }
  }
}
```

### 前端存储的格式：
- localStorage["user"]: 用户对象的JSON字符串
- localStorage["token"]: ticket字符串

## 常见问题

### Q1: 控制台显示 "user: undefined"
**原因**: 后端没有返回用户信息
**解决**: 重新编译后端

### Q2: 登录后页面没有刷新
**原因**: 页面刷新功能已添加，会在登录成功500ms后自动刷新
**解决**: 等待页面自动刷新

### Q3: 刷新后用户状态丢失
**原因**: localStorage没有正确保存
**解决**: 检查浏览器是否禁用了localStorage

### Q4: 头像不显示
**原因**: 用户对象中的avatarUrl可能无效
**解决**: 检查数据库中的avatar_url字段

## 手动验证

在浏览器控制台运行以下命令来手动检查状态：

```javascript
// 查看localStorage中的数据
console.log("User:", localStorage.getItem("user"))
console.log("Token:", localStorage.getItem("token"))

// 查看Vuex状态
console.log("Vuex user:", window.$store?.state.user)
console.log("Vuex token:", window.$store?.state.token)
```

## 如果问题仍然存在

请提供以下信息：
1. 控制台的完整日志输出
2. Network标签中login请求的响应数据
3. localStorage中的实际数据
4. 后端是否已重新编译


