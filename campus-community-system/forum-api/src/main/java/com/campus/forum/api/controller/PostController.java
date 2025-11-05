package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.domain.User;
import com.campus.forum.dal.mapper.CommentMapper;
import com.campus.forum.dal.mapper.PostMapper;
import com.campus.forum.service.PostService;
import com.campus.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;

    @PostMapping("/add")
    public Result addPost(@RequestBody Post post) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.error(403, "你还没有登录哦");
        }
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        postService.addPost(post);
        return Result.success();
    }

    @GetMapping("/detail/{postId}")
    public Result getPost(@PathVariable("postId") int postId) {
//        // 统一数据库的评论数据问题，仅调试可用
//        postMapper.updateCommentCount(postId,commentMapper.countCommentByEntity(1,postId));
        Post post = postService.getPostDetail(postId);
        return Result.success(post);
    }

    @GetMapping("/list")
    public Result getPostList(@RequestParam(value = "userId", required = false) Integer userId, 
                              @RequestParam(name = "partitionId", required = false) Integer partitionId) {
        // 如果没有提供 userId，尝试从当前登录用户中获取
        if (userId == null) {
            User currentUser = hostHolder.getUser();
            if (currentUser != null) {
                userId = currentUser.getId();
            }
        }

        List<Post> posts;
        if (userId != null) {
            posts = postService.findPosts(userId, partitionId);
        } else {
            // 如果 userId 仍然为空，表示获取所有帖子
            posts = postService.findPosts(0, partitionId); // 假设 userId 为 0 表示获取所有帖子
        }
        return Result.success(posts);
    }

    @DeleteMapping("/delete/{postId}")
    public Result deletePost(@PathVariable("postId") int postId) {
        postService.deletePost(postId);
        return Result.success();
    }

    @GetMapping("/hot")
    public Result getHotPosts(@RequestParam(defaultValue = "10") int limit,
                              @RequestParam(required = false, defaultValue = "all") String period) {
        List<Post> hotPosts;
        if (period == null || period.isEmpty() || "all".equalsIgnoreCase(period)) {
            hotPosts = postService.findHotPosts(limit);
        } else {
            hotPosts = postService.findHotPostsByPeriod(limit, period);
        }
        return Result.success(hotPosts);
    }

    /**
     * 收藏排行榜（支持 period+limit）
     */
    @GetMapping("/hot/favorites")
    public Result getTopByFavorites(@RequestParam(defaultValue = "10") int limit,
                                    @RequestParam(required = false, defaultValue = "all") String period) {
        List<Post> posts = postService.findTopPostsByFavorites(limit, period);
        return Result.success(posts);
    }

    /**
     * 点赞排行榜（支持 period+limit）
     */
    @GetMapping("/hot/likes")
    public Result getTopByLikes(@RequestParam(defaultValue = "10") int limit,
                                @RequestParam(required = false, defaultValue = "all") String period) {
        List<Post> posts = postService.findTopPostsByLikes(limit, period);
        return Result.success(posts);
    }

    @GetMapping("/search")
    public Result searchPosts(@RequestParam("keyword") String keyword) {
        List<Post> posts = postService.searchPosts(keyword);
        return Result.success(posts);
    }
}
