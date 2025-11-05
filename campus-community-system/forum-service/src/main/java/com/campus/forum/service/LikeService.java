package com.campus.forum.service;

import java.util.Map;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface LikeService {

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

    /**
     * 查询某个用户获得的赞
     * @param userId
     * @return
     */
    int findUserLikeCount(int userId);
    
    /**
     * 同步实体点赞数到数据库
     * @param entityType 实体类型
     * @param entityId 实体ID
     */
    void syncEntityLikeCount(int entityType, int entityId);
}