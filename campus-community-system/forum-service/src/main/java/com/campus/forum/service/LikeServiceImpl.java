package com.campus.forum.service;

import com.campus.forum.dal.domain.LikeLog;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.mapper.LikeLogMapper;
import com.campus.forum.dal.mapper.PostMapper;
import com.campus.forum.util.RedisKeyUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.mapper.LikeMapper;

import java.util.Date;
import java.util.List;

import static com.campus.forum.common.ForumConstant.COMMENT_LIKED_KEY;
import static com.campus.forum.common.ForumConstant.POST_LIKED_KEY;

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
    @Resource
    private PostService postService;

    @Override
    public int insertLikeLog(LikeLog likeLog) {
        return likeLogMapper.insertLikeLog(likeLog);
    }

    @Override
    public int deleteLikeLogById(int id) {
        return likeLogMapper.deleteLikeLogById(id);
    }

    @Override
    public LikeLog selectLikeLog(int userId, int entityType, int entityId) {
        return likeLogMapper.selectLikeLog(userId,entityType,entityId);
    }

    /**
     * 暂时只考虑post
     * @param userId 点赞者id
     * @param entityType
     * @param entityId
     * @param entityUserId 被点赞者id
     */
    @Override
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        String key;
        if(entityType==1){
            key= POST_LIKED_KEY+entityId;
        } else{
            key=COMMENT_LIKED_KEY+entityId;
        }
        // 查看是否点赞
        Double score=redisTemplate.opsForZSet().score(key,String.valueOf(userId));
        LikeLog isExit=likeLogMapper.selectLikeLog(userId,entityType,entityId);
        if (score==null){
            LikeLog likedLog=new LikeLog();
            likedLog.setUserId(userId);
            likedLog.setEntityId(entityId);
            likedLog.setEntityType(entityType);
            likedLog.setEntityUserId(entityUserId);
            likedLog.setCreateTime(new Date(System.currentTimeMillis()));
            likedLog.setUpdateTime(new Date(System.currentTimeMillis()));
            likedLog.setStatus(1);
            int isSuccess;
            if (isExit!=null){
                //修改状态
                isSuccess=likeLogMapper.updateLikeLogStatus(userId,entityType,entityId,1);
            }else {
                // 添加入库
                isSuccess =likeLogMapper.insertLikeLog(likedLog);
            }

            // 更改点赞数目
            postService.incrementLikeCount(entityId);
            if (isSuccess!=0){
                redisTemplate.opsForZSet().add(key,String.valueOf(userId),System.currentTimeMillis());
            }
        }else {
            // 数据库数据修改状态
            int isSuccess=likeLogMapper.updateLikeLogStatus(userId,entityType,entityId,0);
            // 点赞减一
            postService.decrementLikeCount(entityId);
            if (isSuccess!=0){
                redisTemplate.opsForZSet().remove(key, String.valueOf(userId));
            }
        }

    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        return likeLogMapper.countEntityLikes(entityType,entityId);
    }

    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        LikeLog likeLog = likeLogMapper.selectLikeLog(userId, entityType, entityId);
        return likeLog == null ? 0 : likeLog.getStatus();
    }

    @Override
    public java.util.List<LikeLog> findMyLikes(int userId) {
        return likeLogMapper.selectMyLikes(userId);
    }

    @Override
    public java.util.List<LikeLog> findLikesOnMe(int myUserId) {
        return likeLogMapper.selectLikesOnMe(myUserId);
    }

    // 新增实现
    @Override
    public List<Integer> findLikedPostIdsByUser(int userId) {
        return likeLogMapper.selectLikedPostIdsByUser(userId);
    }
    @Autowired private LikeMapper likeMapper;

    @Override
    public List<Post> findLikedPostsByUser(int userId) {
        return likeMapper.selectLikedPostsByUserId(userId);
    }

    @Override
    public boolean hasLiked(int userId, int postId) {
        Integer n = likeMapper.existsPostLike(userId, postId);
        return n != null && n > 0;
    }

    @Override
    public void like(int userId, int postId) { likeMapper.insertPostLike(userId, postId); }

    @Override
    public void unlike(int userId, int postId) { likeMapper.deletePostLike(userId, postId); }
}