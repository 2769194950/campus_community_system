package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Favorite;
import com.campus.forum.dal.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    void insertFavorite(Favorite favorite);

    void deleteFavorite(@Param("userId") int userId, @Param("postId") int postId);

    Favorite selectFavorite(@Param("userId") int userId, @Param("postId") int postId);

    List<Post> selectFavoritesByUserId(int userId);

    long countFavoritesByPostId(int postId);
}
