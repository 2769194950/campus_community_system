package com.campus.forum.service;

import com.campus.forum.dal.domain.Post;
import java.util.List;

public interface FavoriteService {

    /**
     * Add a post to user's favorites.
     * @param userId the user id
     * @param postId the post id
     */
    void addFavorite(int userId, int postId);

    /**
     * Remove a post from user's favorites.
     * @param userId the user id
     * @param postId the post id
     */
    void removeFavorite(int userId, int postId);

    /**
     * Check if a user has favorited a post.
     * @param userId the user id
     * @param postId the post id
     * @return true if favorited, false otherwise
     */
    boolean isFavorited(int userId, int postId);

    /**
     * Find all favorite posts for a user.
     * @param userId the user id
     * @return a list of posts
     */
    List<Post> findFavoritesByUserId(int userId);

    /**
     * Count the number of users who favorited a post.
     * @param postId the post id
     * @return the count of favorites
     */
    long countFavoritesByPostId(int postId);
}
