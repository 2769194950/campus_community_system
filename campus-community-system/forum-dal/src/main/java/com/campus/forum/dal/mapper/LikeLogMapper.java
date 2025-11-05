package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.LikeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface LikeLogMapper {

    LikeLog selectLikeLog(@Param("userId") int userId, @Param("entityType") int entityType, @Param("entityId") int entityId);
    int insertLikeLog(LikeLog likeLog);
    int updateLikeLogStatus(@Param("userId") int userId, @Param("entityType") int entityType, @Param("entityId") int entityId, @Param("status") int status);
    int countEntityLikes(@Param("entityType") int entityType, @Param("entityId") int entityId);
    int deleteLikeLogById(@Param("id") int id);

    // --- 新增：我的点赞记录（status=1）---
    List<LikeLog> selectMyLikes(@Param("userId") int userId);

    // --- 新增：别人给我点的赞（status=1）---
    List<LikeLog> selectLikesOnMe(@Param("myUserId") int myUserId);

    // 我点赞过的帖子ID（entity_type=1：帖子）
    List<Integer> selectLikedPostIdsByUser(@Param("userId") int userId);
}
