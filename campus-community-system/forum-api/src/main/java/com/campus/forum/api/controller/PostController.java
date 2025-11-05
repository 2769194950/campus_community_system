package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.domain.User;
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
        Post post = postService.findPostById(postId);
        return Result.success(post);
    }

    @GetMapping("/list")
    public Result getPostList(@RequestParam("userId") int userId, 
                              @RequestParam(name = "partitionId", required = false) Integer partitionId) {
        List<Post> posts = postService.findPosts(userId, partitionId);
        return Result.success(posts);
    }

    @DeleteMapping("/delete/{postId}")
    public Result deletePost(@PathVariable("postId") int postId) {
        postService.deletePost(postId);
        return Result.success();
    }

    @GetMapping("/hot")
    public Result getHotPosts(@RequestParam(defaultValue = "10") int limit) {
        List<Post> hotPosts = postService.findHotPosts(limit);
        return Result.success(hotPosts);
    }

    @GetMapping("/search")
    public Result searchPosts(@RequestParam("keyword") String keyword) {
        List<Post> posts = postService.searchPosts(keyword);
        return Result.success(posts);
    }
}
