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
    private PostMapper postMapper;   // 已存在
    @Autowired
    private PostService postService; // 已存在

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId) {
        return commentMapper.selectCommentsByEntity(entityType, entityId);
    }

    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 设置状态,0正常，1拉黑
        comment.setStatus(0);
        int isSuccess = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            postMapper.incrementCommentCount(comment.getEntityId());

        }
        return isSuccess;
    }

    @Override
    public int deleteComment(int commentId) {
        int postId=commentMapper.selectByIdComment(commentId).getEntityId();
        // 删除评论
        int isSuccess= commentMapper.delById(commentId);
        if (isSuccess!=0){
            // 评论数减一
            postMapper.decrementCommentCount(postId);
        }
        return  isSuccess;
    }

    // --- 新增 ---
    @Override
    public List<Comment> findCommentsByUser(int userId) {
        return commentMapper.selectCommentsByUser(userId);
    }

    @Override
    public List<Comment> findCommentsOnMyPosts(int myUserId) {
        return commentMapper.selectCommentsOnMyPosts(myUserId);
    }

    @Override
    public List<Comment> findRepliesToMe(int myUserId) {
        return commentMapper.selectRepliesToMe(myUserId);
    }
    
    // --- 管理员专用方法实现 ---
    @Override
    public List<Comment> findAllComments(int offset, int limit) {
        return commentMapper.selectAllComments(offset, limit);
    }
    
    @Override
    public List<Comment> searchComments(String keyword, int offset, int limit) {
        return commentMapper.searchComments(keyword, offset, limit);
    }
    
    @Override
    public int batchDeleteComments(List<Integer> commentIds) {
        return commentMapper.batchDeleteComments(commentIds);
    }
}

