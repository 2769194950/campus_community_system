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

import java.util.*;
import com.campus.forum.util.HostHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;

import com.campus.forum.service.CommentService;
import com.campus.forum.service.FavoriteService;
import com.campus.forum.service.LikeService;
import com.campus.forum.service.PostService;
import org.springframework.web.bind.annotation.*;

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

    /** 获取用户资料（用于个人主页和他人主页） */
    @GetMapping("/profile/{id}")
    public Result getProfile(@PathVariable("id") int id) {
        User u = userService.findUserById(id);
        if (u == null) return Result.error(404, "用户不存在");
        return Result.success(u);
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
    // 供帖子详情/他人主页使用
    @Autowired private PostService postService;

    // 个人主页三个 Tab：我的评论 / 我的点赞 / 我的收藏
    @Autowired private CommentService commentService;
    @Autowired private LikeService likeService;
    @Autowired private FavoriteService favoriteService;

    // ===== 基础资料 =====

    /**
     * 活跃用户榜（支持 period+limit）
     */
    @GetMapping("/active")
    public Result getActiveUsers(@RequestParam(defaultValue = "10") int limit,
                                 @RequestParam(required = false, defaultValue = "all") String period) {
        java.util.List<User> users = userService.findActiveUsersByPeriod(limit, period);
        return Result.success(users);
    }
    /** 当前登录用户自己的资料（如果你需要） */
    @GetMapping("/me")
    public Result me() {
        User u = hostHolder.getUser();
        if (u == null) return Result.error(403, "你还没有登录哦");
        return Result.success(userService.findUserById(u.getId()));
    }

    // ===== 个人主页 - 三个 Tab =====

    /** 我的评论（按时间倒序） */
    @GetMapping("/{id}/comments")
    public Result myComments(@PathVariable("id") int userId) {
        return Result.success(commentService.findCommentsByUser(userId));
    }

    // 我点赞的帖子（用于“我的”页 Tab）
    @GetMapping("/{id}/liked-posts")
    public Result myLikedPosts(@PathVariable("id") int userId) {
        List<Integer> ids = likeService.findLikedPostIdsByUser(userId);
        return Result.success(postService.findByIds(ids));
    }

    /** 我的收藏（按时间倒序） */
    @GetMapping("/{id}/favorites")
    public Result myFavorites(@PathVariable("id") int userId) {
        return Result.success(favoriteService.findFavoritesByUserId(userId));
    }

    // ===== 与我相关（可选：如果前端需要“与我相关”的聚合） =====
    @GetMapping("/{id}/related")
    public Result relatedToMe(@PathVariable("id") int myUserId) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("commentsOnMyPosts", commentService.findCommentsOnMyPosts(myUserId));
        map.put("repliesToMe", commentService.findRepliesToMe(myUserId));
        map.put("likesOnMe", likeService.findLikesOnMe(myUserId));
        // 如果你实现了别人收藏了我的帖子：
        // map.put("favoritesOnMyPosts", favoriteService.findFavoritesOnMyPosts(myUserId));
        return Result.success(map);
    }
}