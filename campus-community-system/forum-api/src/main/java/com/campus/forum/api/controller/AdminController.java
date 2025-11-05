package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.Comment;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.domain.User;
import com.campus.forum.dal.domain.Leaderboard;
import com.campus.forum.dal.mapper.CommentMapper;
import com.campus.forum.dal.mapper.PostMapper;
import com.campus.forum.dal.mapper.UserMapper;
import com.campus.forum.dal.mapper.LeaderboardMapper;
import com.campus.forum.service.CommentService;
import com.campus.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * @author System
 * @date 2025/01/01
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LeaderboardMapper leaderboardMapper;

    /**
     * 测试端点
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("AdminController is working!");
    }
    
    // 管理员权限检查方法
    private boolean isAdmin() {
        User user = hostHolder.getUser();
        return user != null && user.getType() == 1; // 1表示管理员
    }

    /**
     * 获取管理员统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取用户总数
            int totalUsers = userMapper.countUsers();
            statistics.put("totalUsers", totalUsers);
            
            // 获取帖子总数
            int totalPosts = postMapper.countPosts();
            statistics.put("totalPosts", totalPosts);
            
            // 获取评论总数
            int totalComments = commentMapper.countComments();
            statistics.put("totalComments", totalComments);
            
            // 待处理举报数量（暂时设为0）
            statistics.put("pendingReports", 0);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息
            return Result.error(500, "获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有帖子列表（管理员用）
     */
    @GetMapping("/posts")
    public Result<List<Post>> getAllPosts(@RequestParam(defaultValue = "0") int offset,
                                          @RequestParam(defaultValue = "20") int limit) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
                                          
        List<Post> posts = postMapper.selectAllPosts(offset, limit);
        return Result.success(posts);
    }

    /**
     * 获取所有用户列表（管理员用）
     */
    @GetMapping("/users")
    public Result<List<User>> getAllUsers(@RequestParam(defaultValue = "0") int offset,
                                          @RequestParam(defaultValue = "20") int limit) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
                                          
        List<User> users = userMapper.selectAllUsers(offset, limit);
        return Result.success(users);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{postId}")
    public Result<String> deletePost(@PathVariable("postId") int postId) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            postMapper.updateStatus(postId, 2); // 2表示已删除
            return Result.success("帖子删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "删除帖子失败: " + e.getMessage());
        }
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/users/{userId}/status")
    public Result<String> updateUserStatus(@PathVariable("userId") int userId,
                                           @RequestParam("status") int status) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
                                           
        try {
            userMapper.updateStatus(userId, status);
            return Result.success("用户状态更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "更新用户状态失败: " + e.getMessage());
        }
    }

    // ===== 评论管理 =====
    
    /**
     * 获取所有评论列表（管理员用）
     */
    @GetMapping("/comments")
    public Result<List<Comment>> getAllComments(@RequestParam(defaultValue = "0") int offset,
                                                @RequestParam(defaultValue = "20") int limit,
                                                @RequestParam(required = false) String keyword) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
                                                        
        try {
            List<Comment> comments;
            if (keyword != null && !keyword.trim().isEmpty()) {
                comments = commentService.searchComments(keyword.trim(), offset, limit);
            } else {
                comments = commentService.findAllComments(offset, limit);
            }
            return Result.success(comments);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除单个评论
     */
    @DeleteMapping("/comments/{commentId}")
    public Result<String> deleteComment(@PathVariable("commentId") int commentId) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            commentService.deleteComment(commentId);
            return Result.success("评论删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping("/comments/batch")
    public Result<String> batchDeleteComments(@RequestBody List<Integer> commentIds) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            if (commentIds == null || commentIds.isEmpty()) {
                return Result.error(400, "评论ID列表不能为空");
            }
            int deletedCount = commentService.batchDeleteComments(commentIds);
            return Result.success("成功删除 " + deletedCount + " 条评论");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "批量删除评论失败: " + e.getMessage());
        }
    }

    // ===== 榜单管理 =====

    /**
     * 获取所有榜单配置
     */
    @GetMapping("/leaderboards")
    public Result<List<Map<String, Object>>> getLeaderboards() {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            List<Leaderboard> leaderboards = leaderboardMapper.selectAllLeaderboards();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Leaderboard leaderboard : leaderboards) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", leaderboard.getId());
                item.put("title", leaderboard.getTitle());
                item.put("description", leaderboard.getDescription());
                item.put("type", leaderboard.getType());
                item.put("period", leaderboard.getPeriod());
                item.put("enabled", leaderboard.getEnabled());
                item.put("lastUpdate", leaderboard.getUpdateTime());
                
                // 获取预览数据
                List<Map<String, Object>> topItems = new ArrayList<>();
                if ("post".equals(leaderboard.getType())) {
                    // 获取热门帖子预览数据
                    List<Post> hotPostsList = postMapper.selectHotPosts(5);
                    for (Post post : hotPostsList) {
                        Map<String, Object> postItem = new HashMap<>();
                        postItem.put("title", post.getTitle());
                        postItem.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        postItem.put("score", post.getLikeCount() + post.getCommentCount() + post.getFavoriteCount());
                        topItems.add(postItem);
                    }
                } else if ("user".equals(leaderboard.getType())) {
                    // 获取活跃用户预览数据
                    List<User> activeUsersList = userMapper.selectActiveUsers(0, 5);
                    for (User user : activeUsersList) {
                        Map<String, Object> userItem = new HashMap<>();
                        userItem.put("username", user.getUsername());
                        userItem.put("score", user.getPostCount() + user.getCommentCount());
                        topItems.add(userItem);
                    }
                } else if ("favorite".equals(leaderboard.getType())) {
                    // 获取收藏帖子预览数据
                    List<Post> favoritePostsList = postMapper.selectPostsByFavorites(5, null);
                    for (Post post : favoritePostsList) {
                        Map<String, Object> postItem = new HashMap<>();
                        postItem.put("title", post.getTitle());
                        postItem.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        postItem.put("score", post.getFavoriteCount());
                        topItems.add(postItem);
                    }
                } else if ("like".equals(leaderboard.getType())) {
                    // 获取点赞帖子预览数据
                    List<Post> likePostsList = postMapper.selectPostsByLikes(5, null);
                    for (Post post : likePostsList) {
                        Map<String, Object> postItem = new HashMap<>();
                        postItem.put("title", post.getTitle());
                        postItem.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        postItem.put("score", post.getLikeCount());
                        topItems.add(postItem);
                    }
                }
                item.put("topItems", topItems);
                result.add(item);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取榜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取榜单详细数据
     */
    @GetMapping("/leaderboards/{id}/detail")
    public Result<List<Map<String, Object>>> getLeaderboardDetail(@PathVariable("id") int id) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            List<Map<String, Object>> detailData = new ArrayList<>();
            
            // 根据榜单类型获取详细数据
            Leaderboard leaderboard = leaderboardMapper.selectLeaderboardById(id);
            if (leaderboard == null) {
                return Result.error(404, "榜单不存在");
            }
            
            if ("post".equals(leaderboard.getType())) { // 热门帖子榜
                List<Post> posts;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    posts = postMapper.selectHotPosts(50);
                } else if ("latest".equals(period)) {
                    posts = postMapper.selectLatestPosts(50);
                } else {
                    posts = postMapper.selectHotPostsByPeriod(50, period);
                }
                
                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("postId", post.getId());
                        item.put("title", post.getTitle());
                        item.put("likeCount", post.getLikeCount());
                        item.put("commentCount", post.getCommentCount());
                        item.put("favoriteCount", post.getFavoriteCount());
                        item.put("score", post.getLikeCount() + post.getCommentCount() + post.getFavoriteCount());
                        item.put("createTime", post.getCreateTime());
                        detailData.add(item);
                    }
                }
            } else if ("user".equals(leaderboard.getType())) { // 活跃用户榜
                List<User> users;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    users = userMapper.selectActiveUsers(0, 50);
                } else {
                    users = userMapper.selectActiveUsersByPeriod(0, 50, period);
                }
                
                if (users != null) {
                    for (User user : users) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", user.getId());
                        item.put("username", user.getUsername());
                        item.put("avatarUrl", user.getAvatarUrl());
                        item.put("score", (user.getPostCount() != null ? user.getPostCount() : 0) + (user.getCommentCount() != null ? user.getCommentCount() : 0));
                        item.put("postCount", user.getPostCount());
                        item.put("commentCount", user.getCommentCount());
                        item.put("createTime", user.getCreateTime());
                        detailData.add(item);
                    }
                }
            } else if ("favorite".equals(leaderboard.getType())) { // 收藏排行榜
                List<Post> posts;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    posts = postMapper.selectPostsByFavorites(50, null);
                } else if ("latest".equals(period)) {
                    posts = postMapper.selectPostsByFavorites(50, "latest");
                } else {
                    posts = postMapper.selectPostsByFavorites(50, period);
                }
                
                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("postId", post.getId());
                        item.put("title", post.getTitle());
                        item.put("favoriteCount", post.getFavoriteCount());
                        item.put("score", post.getFavoriteCount());
                        item.put("createTime", post.getCreateTime());
                        detailData.add(item);
                    }
                }
            } else if ("like".equals(leaderboard.getType())) { // 点赞排行榜
                List<Post> posts;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    posts = postMapper.selectPostsByLikes(50, null);
                } else if ("latest".equals(period)) {
                    posts = postMapper.selectPostsByLikes(50, "latest");
                } else {
                    posts = postMapper.selectPostsByLikes(50, period);
                }
                
                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("postId", post.getId());
                        item.put("title", post.getTitle());
                        item.put("likeCount", post.getLikeCount());
                        item.put("score", post.getLikeCount());
                        item.put("createTime", post.getCreateTime());
                        detailData.add(item);
                    }
                }
            }
            
            return Result.success(detailData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取榜单详细数据失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用榜单
     */
    @PutMapping("/leaderboards/{id}/toggle")
    public Result<String> toggleLeaderboard(@PathVariable("id") int id, @RequestBody Map<String, Boolean> params) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            Boolean enabled = params.get("enabled");
            int result = leaderboardMapper.updateLeaderboardEnabled(id, enabled);
            if (result > 0) {
                return Result.success(enabled ? "榜单已启用" : "榜单已禁用");
            } else {
                return Result.error(404, "榜单不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "操作失败: " + e.getMessage());
        }
    }

    /**
     * 刷新榜单数据
     */
    @PostMapping("/leaderboards/{id}/refresh")
    public Result<Map<String, Object>> refreshLeaderboard(@PathVariable("id") int id) {
        try {
            // 获取榜单配置
            Leaderboard leaderboard = leaderboardMapper.selectLeaderboardById(id);
            if (leaderboard == null) {
                return Result.error(404, "榜单不存在");
            }
            
            // 根据榜单类型刷新数据
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> items = new ArrayList<>();
            
            if ("post".equals(leaderboard.getType())) {
                // 根据榜单配置选择合适的查询方法
                List<Post> posts;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    posts = postMapper.selectHotPosts(leaderboard.getDisplayCount());
                } else if ("latest".equals(period)) {
                    posts = postMapper.selectLatestPosts(leaderboard.getDisplayCount());
                } else {
                    posts = postMapper.selectHotPostsByPeriod(leaderboard.getDisplayCount(), period);
                }
                
                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", post.getId());
                        item.put("title", post.getTitle());
                        item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        item.put("score", post.getLikeCount() + post.getCommentCount() + post.getFavoriteCount());
                        item.put("createTime", post.getCreateTime());
                        items.add(item);
                    }
                }
            } else if ("user".equals(leaderboard.getType())) {
                // 根据榜单配置选择合适的查询方法
                List<User> users;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    users = userMapper.selectActiveUsers(0, leaderboard.getDisplayCount());
                } else {
                    users = userMapper.selectActiveUsersByPeriod(0, leaderboard.getDisplayCount(), period);
                }
                
                if (users != null) {
                    for (User user : users) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", user.getId());
                        item.put("username", user.getUsername());
                        item.put("avatarUrl", user.getAvatarUrl());
                        item.put("score", user.getPostCount() + user.getCommentCount());
                        item.put("createTime", user.getCreateTime());
                        items.add(item);
                    }
                }
            } else if ("favorite".equals(leaderboard.getType())) {
                // 收藏排行榜
                List<Post> posts;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    posts = postMapper.selectPostsByFavorites(leaderboard.getDisplayCount(), null);
                } else if ("latest".equals(period)) {
                    // 对于收藏榜，latest模式按照创建时间排序
                    posts = postMapper.selectPostsByFavorites(leaderboard.getDisplayCount(), "latest");
                } else {
                    posts = postMapper.selectPostsByFavorites(leaderboard.getDisplayCount(), period);
                }
                
                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", post.getId());
                        item.put("title", post.getTitle());
                        item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        item.put("score", post.getFavoriteCount());
                        item.put("favoriteCount", post.getFavoriteCount());
                        item.put("createTime", post.getCreateTime());
                        items.add(item);
                    }
                }
            } else if ("like".equals(leaderboard.getType())) {
                // 点赞排行榜
                List<Post> posts;
                String period = leaderboard.getPeriod();
                
                if ("all".equals(period)) {
                    posts = postMapper.selectPostsByLikes(leaderboard.getDisplayCount(), null);
                } else if ("latest".equals(period)) {
                    // 对于点赞榜，latest模式按照创建时间排序
                    posts = postMapper.selectPostsByLikes(leaderboard.getDisplayCount(), "latest");
                } else {
                    posts = postMapper.selectPostsByLikes(leaderboard.getDisplayCount(), period);
                }
                
                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", post.getId());
                        item.put("title", post.getTitle());
                        item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        item.put("score", post.getLikeCount());
                        item.put("likeCount", post.getLikeCount());
                        item.put("createTime", post.getCreateTime());
                        items.add(item);
                    }
                }
            }
            
            result.put("leaderboard", leaderboard);
            result.put("items", items);
            result.put("refreshTime", new Date());
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "刷新失败: " + e.getMessage());
        }
    }

    /**
     * 刷新所有榜单数据
     */
    @PostMapping("/leaderboards/refresh-all")
    public Result<Map<String, Object>> refreshAllLeaderboards() {
        try {
            // 获取所有启用的榜单
            List<Leaderboard> leaderboards = leaderboardMapper.selectAllLeaderboards();
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (Leaderboard leaderboard : leaderboards) {
                if (leaderboard.getEnabled()) {
                    Map<String, Object> result = new HashMap<>();
                    List<Map<String, Object>> items = new ArrayList<>();
                    
                    if ("post".equals(leaderboard.getType())) {
                        // 根据榜单配置选择合适的查询方法
                        List<Post> posts;
                        String period = leaderboard.getPeriod();
                        
                        if ("all".equals(period)) {
                            posts = postMapper.selectHotPosts(leaderboard.getDisplayCount());
                        } else {
                            posts = postMapper.selectHotPostsByPeriod(leaderboard.getDisplayCount(), period);
                        }
                        
                        for (Post post : posts) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("id", post.getId());
                            item.put("title", post.getTitle());
                            item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                            item.put("score", post.getLikeCount() + post.getCommentCount() + post.getFavoriteCount());
                            item.put("createTime", post.getCreateTime());
                            items.add(item);
                        }
                    } else if ("user".equals(leaderboard.getType())) {
                        // 根据榜单配置选择合适的查询方法
                        List<User> users;
                        String period = leaderboard.getPeriod();
                        
                        if ("all".equals(period)) {
                            users = userMapper.selectActiveUsers(0, leaderboard.getDisplayCount());
                        } else {
                            users = userMapper.selectActiveUsersByPeriod(0, leaderboard.getDisplayCount(), period);
                        }
                        
                        for (User user : users) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("id", user.getId());
                            item.put("username", user.getUsername());
                            item.put("avatarUrl", user.getAvatarUrl());
                            item.put("score", user.getPostCount() + user.getCommentCount());
                            item.put("createTime", user.getCreateTime());
                            items.add(item);
                        }
                    } else if ("favorite".equals(leaderboard.getType())) {
                        // 收藏排行榜
                        List<Post> posts;
                        String period = leaderboard.getPeriod();
                        
                        if ("all".equals(period)) {
                            posts = postMapper.selectPostsByFavorites(leaderboard.getDisplayCount(), null);
                        } else if ("latest".equals(period)) {
                            posts = postMapper.selectPostsByFavorites(leaderboard.getDisplayCount(), "latest");
                        } else {
                            posts = postMapper.selectPostsByFavorites(leaderboard.getDisplayCount(), period);
                        }
                        
                        for (Post post : posts) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("id", post.getId());
                            item.put("title", post.getTitle());
                            item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                            item.put("score", post.getFavoriteCount());
                            item.put("createTime", post.getCreateTime());
                            items.add(item);
                        }
                    } else if ("like".equals(leaderboard.getType())) {
                        // 点赞排行榜
                        List<Post> posts;
                        String period = leaderboard.getPeriod();
                        
                        if ("all".equals(period)) {
                            posts = postMapper.selectPostsByLikes(leaderboard.getDisplayCount(), null);
                        } else if ("latest".equals(period)) {
                            posts = postMapper.selectPostsByLikes(leaderboard.getDisplayCount(), "latest");
                        } else {
                            posts = postMapper.selectPostsByLikes(leaderboard.getDisplayCount(), period);
                        }
                        
                        for (Post post : posts) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("id", post.getId());
                            item.put("title", post.getTitle());
                            item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                            item.put("score", post.getLikeCount());
                            item.put("createTime", post.getCreateTime());
                            items.add(item);
                        }
                    }
                    
                    result.put("leaderboard", leaderboard);
                    result.put("items", items);
                    results.add(result);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("results", results);
            response.put("refreshTime", new Date());
            response.put("count", results.size());
            
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "刷新失败: " + e.getMessage());
        }
    }

    /**
     * 创建榜单
     */
    @PostMapping("/leaderboards")
    public Result<String> createLeaderboard(@RequestBody Map<String, Object> leaderboardData) {
        try {
            Leaderboard leaderboard = new Leaderboard();
            leaderboard.setTitle((String) leaderboardData.get("title"));
            leaderboard.setDescription((String) leaderboardData.get("description"));
            leaderboard.setType((String) leaderboardData.get("type"));
            leaderboard.setPeriod((String) leaderboardData.get("period"));
            leaderboard.setEnabled(leaderboardData.containsKey("enabled") ? (Boolean) leaderboardData.get("enabled") : true);
            leaderboard.setSortOrder(leaderboardData.containsKey("sortOrder") ? (Integer) leaderboardData.get("sortOrder") : 0);
            
            int result = leaderboardMapper.insertLeaderboard(leaderboard);
            if (result > 0) {
                return Result.success("榜单创建成功");
            } else {
                return Result.error(500, "创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新榜单
     */
    @PutMapping("/leaderboards/{id}")
    public Result<String> updateLeaderboard(@PathVariable("id") int id, @RequestBody Map<String, Object> leaderboardData) {
        try {
            Leaderboard leaderboard = leaderboardMapper.selectLeaderboardById(id);
            if (leaderboard == null) {
                return Result.error(404, "榜单不存在");
            }
            
            // 更新榜单信息
            if (leaderboardData.containsKey("title")) {
                leaderboard.setTitle((String) leaderboardData.get("title"));
            }
            if (leaderboardData.containsKey("description")) {
                leaderboard.setDescription((String) leaderboardData.get("description"));
            }
            if (leaderboardData.containsKey("type")) {
                leaderboard.setType((String) leaderboardData.get("type"));
            }
            if (leaderboardData.containsKey("period")) {
                leaderboard.setPeriod((String) leaderboardData.get("period"));
            }
            if (leaderboardData.containsKey("enabled")) {
                leaderboard.setEnabled((Boolean) leaderboardData.get("enabled"));
            }
            if (leaderboardData.containsKey("sortOrder")) {
                leaderboard.setSortOrder((Integer) leaderboardData.get("sortOrder"));
            }
            
            int result = leaderboardMapper.updateLeaderboard(leaderboard);
            if (result > 0) {
                return Result.success("榜单更新成功");
            } else {
                return Result.error(500, "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除榜单
     */
    @DeleteMapping("/leaderboards/{id}")
    public Result<String> deleteLeaderboard(@PathVariable("id") int id) {
        try {
            int result = leaderboardMapper.deleteLeaderboardById(id);
            if (result > 0) {
                return Result.success("榜单删除成功");
            } else {
                return Result.error(404, "榜单不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取启用的榜单列表（前端使用）
     */
    @GetMapping("/public/leaderboards")
    public Result<List<Map<String, Object>>> getPublicLeaderboards() {
        try {
            List<Leaderboard> leaderboards = leaderboardMapper.selectEnabledLeaderboards();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Leaderboard leaderboard : leaderboards) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", leaderboard.getId());
                item.put("title", leaderboard.getTitle());
                item.put("description", leaderboard.getDescription());
                item.put("type", leaderboard.getType());
                item.put("period", leaderboard.getPeriod());
                item.put("displayCount", leaderboard.getDisplayCount());
                result.add(item);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取榜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取榜单数据（前端使用）
     */
    @GetMapping("/public/leaderboards/{id}/data")
    public Result<Map<String, Object>> getPublicLeaderboardData(@PathVariable("id") int id) {
        try {
            // 获取榜单配置
            Leaderboard leaderboard = leaderboardMapper.selectLeaderboardById(id);
            if (leaderboard == null || !leaderboard.getEnabled()) {
                return Result.error(404, "榜单不存在或已禁用");
            }

            Integer displayCountObj = leaderboard.getDisplayCount();
            int limit = (displayCountObj != null && displayCountObj > 0) ? displayCountObj : 10;

            // 根据榜单类型获取数据
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> items = new ArrayList<>();

            if ("post".equals(leaderboard.getType())) {
                // 根据榜单配置选择合适的查询方法
                List<Post> posts;
                String period = leaderboard.getPeriod();

                if ("all".equals(period)) {
                    posts = postMapper.selectHotPosts(limit);
                } else if ("latest".equals(period)) {
                    posts = postMapper.selectLatestPosts(limit);
                } else {
                    posts = postMapper.selectHotPostsByPeriod(limit, period);
                }

                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", post.getId());
                        item.put("title", post.getTitle());
                        item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        int likeCount = post.getLikeCount() == null ? 0 : post.getLikeCount().intValue();
                        int commentCount = post.getCommentCount() == null ? 0 : post.getCommentCount().intValue();
                        int favoriteCount = post.getFavoriteCount() == null ? 0 : post.getFavoriteCount().intValue();
                        int score = likeCount + commentCount + favoriteCount;
                        item.put("score", score);
                        item.put("likeCount", likeCount);
                        item.put("commentCount", commentCount);
                        item.put("favoriteCount", favoriteCount);
                        item.put("createTime", post.getCreateTime());
                        items.add(item);
                    }
                }
            } else if ("user".equals(leaderboard.getType())) {
                // 根据榜单配置选择合适的查询方法
                List<User> users;
                String period = leaderboard.getPeriod();

                if ("all".equals(period)) {
                    users = userMapper.selectActiveUsers(0, limit);
                } else {
                    users = userMapper.selectActiveUsersByPeriod(0, limit, period);
                }

                if (users != null) {
                    for (User user : users) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", user.getId());
                        item.put("username", user.getUsername());
                        item.put("avatarUrl", user.getAvatarUrl());
                        int postCount = user.getPostCount() == null ? 0 : user.getPostCount().intValue();
                        int commentCount = user.getCommentCount() == null ? 0 : user.getCommentCount().intValue();
                        int score = postCount + commentCount;
                        item.put("score", score);
                        item.put("postCount", postCount);
                        item.put("commentCount", commentCount);
                        item.put("createTime", user.getCreateTime());
                        items.add(item);
                    }
                }
            } else if ("favorite".equals(leaderboard.getType())) {
                // 收藏排行榜
                List<Post> posts;
                String period = leaderboard.getPeriod();

                if ("all".equals(period)) {
                    posts = postMapper.selectPostsByFavorites(limit, null);
                } else if ("latest".equals(period)) {
                    posts = postMapper.selectPostsByFavorites(limit, "latest");
                } else {
                    posts = postMapper.selectPostsByFavorites(limit, period);
                }

                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", post.getId());
                        item.put("title", post.getTitle());
                        item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        int favoriteCount = post.getFavoriteCount() == null ? 0 : post.getFavoriteCount().intValue();
                        item.put("score", favoriteCount);
                        item.put("favoriteCount", favoriteCount);
                        item.put("createTime", post.getCreateTime());
                        items.add(item);
                    }
                }
            } else if ("like".equals(leaderboard.getType())) {
                // 点赞排行榜
                List<Post> posts;
                String period = leaderboard.getPeriod();

                if ("all".equals(period)) {
                    posts = postMapper.selectPostsByLikes(limit, null);
                } else if ("latest".equals(period)) {
                    posts = postMapper.selectPostsByLikes(limit, "latest");
                } else {
                    posts = postMapper.selectPostsByLikes(limit, period);
                }

                if (posts != null) {
                    for (Post post : posts) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", post.getId());
                        item.put("title", post.getTitle());
                        item.put("username", post.getUser() != null ? post.getUser().getUsername() : "未知用户");
                        int likeCount = post.getLikeCount() == null ? 0 : post.getLikeCount().intValue();
                        item.put("score", likeCount);
                        item.put("likeCount", likeCount);
                        item.put("createTime", post.getCreateTime());
                        items.add(item);
                    }
                }
            }

            result.put("leaderboard", leaderboard);
            result.put("items", items);

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取榜单数据失败: " + e.getMessage());
        }
    }
}