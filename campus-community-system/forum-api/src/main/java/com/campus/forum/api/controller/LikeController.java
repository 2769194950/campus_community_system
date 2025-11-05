package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.User;
import com.campus.forum.dal.mapper.PostMapper;
import com.campus.forum.service.LikeService;
import com.campus.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private PostMapper postMapper;

    @PostMapping
    public Result like(@RequestBody Map<String, Object> payload) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.error(403, "你还没有登录哦");
        }
        
        int entityType = (Integer) payload.get("entityType");
        int entityId = (Integer) payload.get("entityId");
        int entityUserId = (Integer) payload.get("entityUserId");

        likeService.like(user.getId(), entityType, entityId, entityUserId);

        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 用于统一数据库问题，书写不规范仅调试可用
        postMapper.updateLikeCount(entityId,likeCount);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return Result.success(map);
    }

    @GetMapping("/status")
    public Result getLikeStatus(@RequestParam("entityType") int entityType, @RequestParam("entityId") int entityId) {
        User user = hostHolder.getUser();
        if (user == null) {
            // Return 0 if user is not logged in, or handle as an error
            return Result.success(0);
        }
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        return Result.success(likeStatus);
    }
}