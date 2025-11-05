package com.campus.forum.service;

import com.campus.forum.dal.domain.Post;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface PostService {

    int incrementLikeCount(int id);

    int decrementLikeCount(int id);

    List<Post> findPosts(int userId, Integer partitionId);
    
    Post findPostById(int id);

    int addPost(Post post);
    
    int updateFavoriteCount(int postId, long favoriteCount);
    
    int deletePost(int postId);
    
    List<Post> findHotPosts(int limit);
    
    /**
     * 根据周期获取热门帖子（like+comment+favorite 综合）
     */
    List<Post> findHotPostsByPeriod(int limit, String period);
    
    /**
     * 根据收藏数排序获取帖子
     */
    List<Post> findTopPostsByFavorites(int limit, String period);
    
    /**
     * 根据点赞数排序获取帖子
     */
    List<Post> findTopPostsByLikes(int limit, String period);
    
    List<Post> searchPosts(String keyword);

    Post getPostDetail(int postId);

    List<Post> findByIds(List<Integer> ids);
}

