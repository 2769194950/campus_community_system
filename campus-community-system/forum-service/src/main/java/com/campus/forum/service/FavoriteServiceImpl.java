package com.campus.forum.service;

import com.campus.forum.dal.domain.Favorite;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.mapper.FavoriteMapper;
import com.campus.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private PostService postService;

    @Override
    public void addFavorite(int userId, int postId) {
        // For simplicity, we directly operate on DB and then update cache.
        // A more robust implementation might use cache-aside pattern.
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        favoriteMapper.insertFavorite(favorite);
        
        // Update post's favorite count
        postService.updateFavoriteCount(postId, countFavoritesByPostId(postId));
    }

    @Override
    public void removeFavorite(int userId, int postId) {
        favoriteMapper.deleteFavorite(userId, postId);
        
        // Update post's favorite count
        postService.updateFavoriteCount(postId, countFavoritesByPostId(postId));
    }

    @Override
    public boolean isFavorited(int userId, int postId) {
        return favoriteMapper.selectFavorite(userId, postId) != null;
    }

    @Override
    public List<Post> findFavoritesByUserId(int userId) {
        return favoriteMapper.selectFavoritesByUserId(userId);
    }

    @Override
    public long countFavoritesByPostId(int postId) {
        return favoriteMapper.countFavoritesByPostId(postId);
    }
}
