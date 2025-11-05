package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.User;
import com.campus.forum.service.FollowService;
import com.campus.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/user/{targetId}")
    public Result toggleFollow(@PathVariable("targetId") int targetId) {
        User me = hostHolder.getUser();
        if (me == null) return Result.error(403, "你还没有登录哦");
        boolean followed = followService.hasFollowed(me.getId(), targetId);
        if (followed) followService.unfollowUser(me.getId(), targetId);
        else followService.followUser(me.getId(), targetId);
        java.util.Map<String,Object> data = new java.util.HashMap<>();
        data.put("followed", !followed);
        data.put("followerCount", followService.findFollowerCount(targetId));
        return Result.success(data);
    }

    @GetMapping("/status")
    public Result followStatus(@RequestParam("targetId") int targetId) {
        User me = hostHolder.getUser();
        boolean followed = (me != null) && followService.hasFollowed(me.getId(), targetId);
        return Result.success(followed);
    }

    @GetMapping("/counts/{targetId}")
    public Result followCounts(@PathVariable("targetId") int targetId) {
        java.util.Map<String,Object> map = new java.util.HashMap<>();
        map.put("followers", followService.findFollowerCount(targetId));
        User me = hostHolder.getUser();
        map.put("myFollowees", me == null ? 0 : followService.findFolloweeCount(me.getId()));
        return Result.success(map);
    }
}
