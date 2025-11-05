package com.campus.forum.service;

public interface FollowService {
    void followUser(int userId, int targetUserId);
    void unfollowUser(int userId, int targetUserId);
    boolean hasFollowed(int userId, int targetUserId);
    long findFollowerCount(int targetUserId);
    long findFolloweeCount(int userId);
}
