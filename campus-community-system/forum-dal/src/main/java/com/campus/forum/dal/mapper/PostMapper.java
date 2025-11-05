package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Mapper
public interface PostMapper {

    List<Post> selectPosts(@Param("userId") int userId, @Param("partitionId") Integer partitionId);
    
    Post selectPostById(int id);
    
    int insertPost(Post post);

    int updateCommentCount(int id, int commentCount);
    
    int updateFavoriteCount(@Param("id") int id, @Param("favoriteCount") long favoriteCount);
    
    int updateStatus(@Param("id") int id, @Param("status") int status);
    
    int updateLikeCount(@Param("id") int id, @Param("likeCount") long likeCount);
    
    List<Post> selectHotPosts(@Param("limit") int limit);
    
    List<Post> selectPostsByKeyword(@Param("keyword") String keyword);
    
    List<Post> selectAllPosts(@Param("offset") int offset, @Param("limit") int limit);
}