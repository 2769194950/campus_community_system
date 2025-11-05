package com.campus.forum.service;

import com.campus.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    // 显式指定泛型，避免 K/V 推断不一致导致的编译报错
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void followUser(int userId, int targetUserId) {
        // 关注的人（我 -> 他）
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, 3);     // 3 表示“用户”实体
        // 我的粉丝（他人 -> 我）
        String followerKey = RedisKeyUtil.getFollowerKey(3, targetUserId);

        double now = System.currentTimeMillis();

        // 直接使用 ZSet 操作；无需 SessionCallback
        redisTemplate.opsForZSet().add(followeeKey, targetUserId, now);
        redisTemplate.opsForZSet().add(followerKey, userId, now);
    }

    @Override
    public void unfollowUser(int userId, int targetUserId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, 3);
        String followerKey = RedisKeyUtil.getFollowerKey(3, targetUserId);

        redisTemplate.opsForZSet().remove(followeeKey, targetUserId);
        redisTemplate.opsForZSet().remove(followerKey, userId);
    }

    @Override
    public boolean hasFollowed(int userId, int targetUserId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, 3);
        // 若存在得分（时间戳），说明已关注
        Double score = redisTemplate.opsForZSet().score(followeeKey, targetUserId);
        return score != null;
    }

    @Override
    public long findFollowerCount(int targetUserId) {
        String followerKey = RedisKeyUtil.getFollowerKey(3, targetUserId);
        Long size = redisTemplate.opsForZSet().zCard(followerKey);
        return size == null ? 0L : size;
    }

    @Override
    public long findFolloweeCount(int userId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, 3);
        Long size = redisTemplate.opsForZSet().zCard(followeeKey);
        return size == null ? 0L : size;
    }
}
