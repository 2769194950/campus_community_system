package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Mapper
public interface CommentMapper {

	List<Comment> selectCommentsByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

	int insertComment(Comment comment);
    
	int updateStatus(@Param("id") int id, @Param("status") int status);
}

