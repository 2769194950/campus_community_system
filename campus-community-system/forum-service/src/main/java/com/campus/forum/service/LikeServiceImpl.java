package com.campus.forum.service;

import com.campus.forum.dal.domain.LikeLog;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.mapper.LikeLogMapper;
import com.campus.forum.dal.mapper.PostMapper;
import com.campus.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private LikeLogMapper likeLogMapper;
    
    @Autowired
    private PostMapper postMapper;

    @Override
    @Transactional
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        // 先操作数据库
        LikeLog likeLog = likeLogMapper.selectLikeLog(userId, entityType, entityId);
        
        if (likeLog == null) {
            // 插入新的点赞记录
            likeLog = new LikeLog();
            likeLog.setUserId(userId);
            likeLog.setEntityType(entityType);
            likeLog.setEntityId(entityId);
            likeLog.setEntityUserId(entityUserId);
            likeLog.setStatus(1); // 1表示已点赞
            likeLog.setCreateTime(new Date());
            likeLog.setUpdateTime(new Date());
            likeLogMapper.insertLikeLog(likeLog);
        } else {
            // 更新现有点赞记录状态
            int newStatus = likeLog.getStatus() == 1 ? 0 : 1;
            likeLogMapper.updateLikeLogStatus(userId, entityType, entityId, newStatus);
        }
        
        // 再操作Redis
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserKey(entityUserId);
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();

                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
        
        // 同步实体点赞数到数据库
        syncEntityLikeCount(entityType, entityId);
    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
    
    @Override
    public void syncEntityLikeCount(int entityType, int entityId) {
        // 从数据库统计实体点赞数
        int likeCount = likeLogMapper.countEntityLikes(entityType, entityId);
        
        // 更新到对应的表中
        if (entityType == 1) { // 帖子类型
            postMapper.updateLikeCount(entityId, likeCount);
        }
        // 可以添加其他实体类型的处理逻辑
    }
    
    /**
     * 同步所有帖子的点赞数
     * 在系统维护或数据异常时使用
     */
    public void syncAllPostLikeCounts() {
        // 分批同步所有帖子的点赞数
        int offset = 0;
        int limit = 100;
        List<Post> posts;
        
        do {
            posts = postMapper.selectAllPosts(offset, limit);
            for (Post post : posts) {
                syncEntityLikeCount(1, post.getId()); // 1表示帖子类型
            }
            offset += limit;
        } while (posts.size() == limit);
    }
}