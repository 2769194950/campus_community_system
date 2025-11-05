package com.campus.forum.service;

import com.campus.forum.dal.domain.Post;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface PostService {

    List<Post> findPosts(int userId, Integer partitionId);
    
    Post findPostById(int id);

    int addPost(Post post);
    
    int updateFavoriteCount(int postId, long favoriteCount);
    
    int deletePost(int postId);
    
    List<Post> findHotPosts(int limit);
    
    List<Post> searchPosts(String keyword);
}

