package com.campus.forum.api.controller;

import com.campus.forum.api.vo.request.LoginRequest;
import com.campus.forum.api.vo.request.RegisterRequest;
import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.User;
import com.campus.forum.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import com.campus.forum.util.HostHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest registerRequest) {
        User user = new User();
        BeanUtils.copyProperties(registerRequest, user);
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            return Result.success();
        } else {
            return Result.error(400, (String) map.values().iterator().next());
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> map = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (map.containsKey("ticket")) {
            return Result.success(map);
        } else {
            return Result.error(400, (String) map.values().iterator().next());
        }
    }

    @GetMapping("/logout")
    public Result logout(@CookieValue(value = "ticket", required = false) String cookieTicket,
                         @RequestHeader(value = "Authorization", required = false) String headerTicket) {
        // 优先使用Header中的ticket，如果没有则使用Cookie中的
        String ticket = headerTicket != null ? headerTicket : cookieTicket;
        if (ticket != null && !ticket.isEmpty()) {
            userService.logout(ticket);
        }
        SecurityContextHolder.clearContext();
        return Result.success();
    }

    @GetMapping("/profile/{userId}")
    public Result getProfile(@PathVariable("userId") int userId) {
        User user = userService.findUserById(userId);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result updateProfile(@RequestBody User user) {
        System.out.println(">>> updateProfile called");
        User currentUser = hostHolder.getUser();
        System.out.println(">>> currentUser: " + currentUser);
        if (currentUser == null) {
            System.out.println(">>> currentUser is NULL! Returning 403");
            return Result.error(403, "你还没有登录哦");
        }
        System.out.println(">>> currentUser ID: " + currentUser.getId() + ", username: " + currentUser.getUsername());
        if (user.getIntroduction() != null) {
            System.out.println(">>> Updating introduction to: " + user.getIntroduction());
            userService.updateIntroduction(currentUser.getId(), user.getIntroduction());
        }
        System.out.println(">>> Update successful");
        return Result.success();
    }

    @PutMapping("/avatar")
    public Result updateAvatar(@RequestBody Map<String, String> params) {
        User currentUser = hostHolder.getUser();
        if (currentUser == null) {
            return Result.error(403, "你还没有登录哦");
        }
        String avatarUrl = params.get("avatarUrl");
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            userService.updateAvatar(currentUser.getId(), avatarUrl);
        }
        return Result.success();
    }

    @PutMapping("/email")
    public Result updateEmail(@RequestBody Map<String, String> params) {
        User currentUser = hostHolder.getUser();
        if (currentUser == null) {
            return Result.error(403, "你还没有登录哦");
        }
        String email = params.get("email");
        if (email != null && !email.isEmpty()) {
            userService.updateEmail(currentUser.getId(), email);
        }
        return Result.success();
    }

    @PutMapping("/username")
    public Result updateUsername(@RequestBody Map<String, String> params) {
        User currentUser = hostHolder.getUser();
        if (currentUser == null) {
            return Result.error(403, "你还没有登录哦");
        }
        String username = params.get("username");
        if (username != null && !username.isEmpty()) {
            // 检查用户名是否已存在
            User existingUser = userService.findUserByName(username);
            if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                return Result.error(400, "该用户名已被使用！");
            }
            userService.updateUsername(currentUser.getId(), username);
        }
        return Result.success();
    }

    // ==================== 找回密码功能 ====================
    
    @PostMapping("/forgot-password")
    public Result forgotPassword(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        Map<String, Object> result = userService.sendResetPasswordEmail(email);
        
        if (result.containsKey("emailMsg")) {
            return Result.error(400, (String) result.get("emailMsg"));
        }
        
        return Result.success("重置密码邮件已发送，请查收邮箱！");
    }
    
    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody Map<String, String> params) {
        String token = params.get("token");
        String newPassword = params.get("newPassword");
        
        Map<String, Object> result = userService.resetPassword(token, newPassword);
        
        if (result.containsKey("tokenMsg")) {
            return Result.error(400, (String) result.get("tokenMsg"));
        }
        if (result.containsKey("passwordMsg")) {
            return Result.error(400, (String) result.get("passwordMsg"));
        }
        
        return Result.success("密码重置成功！");
    }
    
    // ==================== 密保问题功能 ====================
    
    /**
     * 设置密保问题
     */
    @PostMapping("/security-question")
    public Result setSecurityQuestion(@RequestBody Map<String, String> params) {
        User currentUser = hostHolder.getUser();
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        
        String question = params.get("question");
        String answer = params.get("answer");
        
        if (StringUtils.isBlank(question) || StringUtils.isBlank(answer)) {
            return Result.error(400, "请填写密保问题和答案！");
        }
        
        userService.updateSecurityQuestion(currentUser.getId(), question, answer);
        
        return Result.success("密保问题设置成功！");
    }
    
    /**
     * 获取用户的密保问题（用于找回密码）
     */
    @GetMapping("/security-question")
    public Result getSecurityQuestion(@RequestParam("username") String username) {
        Map<String, Object> result = userService.getSecurityQuestion(username);
        
        if (result.containsKey("usernameMsg")) {
            return Result.error(400, (String) result.get("usernameMsg"));
        }
        if (result.containsKey("questionMsg")) {
            return Result.error(400, (String) result.get("questionMsg"));
        }
        
        return Result.success(result);
    }
    
    /**
     * 通过密保问题重置密码
     */
    @PostMapping("/reset-password-by-security")
    public Result resetPasswordBySecurity(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String answer = params.get("answer");
        String newPassword = params.get("newPassword");
        
        Map<String, Object> result = userService.resetPasswordBySecurityQuestion(
            username, answer, newPassword
        );
        
        if (result.containsKey("error")) {
            return Result.error(400, (String) result.get("error"));
        }
        if (result.containsKey("passwordMsg")) {
            return Result.error(400, (String) result.get("passwordMsg"));
        }
        
        return Result.success("密码重置成功！");
    }
}
