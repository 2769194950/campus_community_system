package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);
    Comment selectByIdComment(@Param("id") int id);
    int countCommentByEntity(@Param("entityType") int entityType,@Param("entityId") int entityId);
    int insertComment(Comment comment);
    int updateStatus(@Param("id") int id, @Param("status") int status);
    int delById(@Param("id") int id);

    // --- 新增：我的评论 ---
    List<Comment> selectCommentsByUser(@Param("userId") int userId);

    // --- 新增：评论了我的帖子（不含回复他人评论）---
    List<Comment> selectCommentsOnMyPosts(@Param("myUserId") int myUserId);

    // --- 新增：回复了我（目标是我）---
    List<Comment> selectRepliesToMe(@Param("myUserId") int myUserId);

    /** 我发表过的评论（按时间倒序） */
    List<Comment> selectByUserId(@Param("userId") int userId);
    
    /**
     * 统计评论总数（排除被拉黑的评论）
     */
    int countComments();
    
    // --- 管理员专用方法 ---
    /**
     * 获取所有评论（管理员用，包含用户信息）
     */
    List<Comment> selectAllComments(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据关键词搜索评论（管理员用）
     */
    List<Comment> searchComments(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 批量删除评论
     */
    int batchDeleteComments(@Param("commentIds") List<Integer> commentIds);
}
