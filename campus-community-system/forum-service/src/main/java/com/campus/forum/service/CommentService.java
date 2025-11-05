package com.campus.forum.service;

import com.campus.forum.dal.domain.Comment;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface CommentService {

    List<Comment> findCommentsByEntity(int entityType, int entityId);

    int addComment(Comment comment);
    
    int deleteComment(int commentId);

}

