package com.campus.forum.service;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.domain.LikeLog;

import java.util.*;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface LikeService {

    int insertLikeLog(LikeLog likeLog);

    int deleteLikeLogById(int id);

    LikeLog selectLikeLog(int userId,int entityType,int entityId);

    /**
     * 点赞
     * @param userId 点赞者id
     * @param entityType
     * @param entityId
     * @param entityUserId 被点赞者id
     */
    void like(int userId, int entityType, int entityId, int entityUserId);

    /**
     * 查询某实体点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 查询某人对某实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    List<com.campus.forum.dal.domain.LikeLog> findMyLikes(int userId);
    List<com.campus.forum.dal.domain.LikeLog> findLikesOnMe(int myUserId);
    // 新增：我点赞过的帖子ID
    List<Integer> findLikedPostIdsByUser(int userId);
    List<Post> findLikedPostsByUser(int userId);

    boolean hasLiked(int userId, int postId);
    void like(int userId, int postId);
    void unlike(int userId, int postId);
}