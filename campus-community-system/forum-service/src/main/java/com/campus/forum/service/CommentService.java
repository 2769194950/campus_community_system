package com.campus.forum.service;

import com.campus.forum.dal.domain.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByEntity(int entityType, int entityId);

    int addComment(Comment comment);

    int deleteComment(int commentId);

    // --- 新增 ---
    List<Comment> findCommentsByUser(int userId);

    List<Comment> findCommentsOnMyPosts(int myUserId);

    List<Comment> findRepliesToMe(int myUserId);
    
    // --- 管理员专用方法 ---
    List<Comment> findAllComments(int offset, int limit);
    
    List<Comment> searchComments(String keyword, int offset, int limit);
    
    int batchDeleteComments(List<Integer> commentIds);
}