package com.campus.forum.service;

import com.campus.forum.common.ForumConstant;
import com.campus.forum.dal.domain.Comment;
import com.campus.forum.dal.mapper.CommentMapper;
import com.campus.forum.dal.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Service
public class CommentServiceImpl implements CommentService, ForumConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId) {
        return commentMapper.selectCommentsByEntity(entityType, entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCommentsByEntity(comment.getEntityType(), comment.getEntityId()).size();
            postMapper.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    @Override
    public int deleteComment(int commentId) {
        return commentMapper.updateStatus(commentId, 2); // 2 represents deleted status
    }
}

