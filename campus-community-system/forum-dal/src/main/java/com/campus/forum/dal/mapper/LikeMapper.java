package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LikeMapper {

    /** 我点赞过的帖子（倒序） */
    List<Post> selectLikedPostsByUserId(@Param("userId") int userId);

    /** 是否已点赞 */
    Integer existsPostLike(@Param("userId") int userId, @Param("postId") int postId);

    /** 点赞（幂等） */
    int insertPostLike(@Param("userId") int userId, @Param("postId") int postId);

    /** 取消点赞（幂等） */
    int deletePostLike(@Param("userId") int userId, @Param("postId") int postId);
}
