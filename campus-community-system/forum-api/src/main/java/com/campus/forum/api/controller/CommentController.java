package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.Comment;
import com.campus.forum.dal.domain.User;
import com.campus.forum.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @GetMapping("/list/{entityType}/{entityId}")
    public Result getComments(@PathVariable("entityType") int entityType,
                              @PathVariable("entityId") int entityId) {
        List<Comment> comments = commentService.findCommentsByEntity(entityType, entityId);
        return Result.success(comments);
    }

    @PostMapping("/add")
    public Result addComment(@RequestBody Comment comment) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.error(403, "你还没有登录哦");
        }
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date(System.currentTimeMillis()));
        commentService.addComment(comment);
        return Result.success();
    }

    @DeleteMapping("/delete/{commentId}")
    public Result deleteComment(@PathVariable("commentId") int commentId) {
        commentService.deleteComment(commentId);
        return Result.success();
    }
}
