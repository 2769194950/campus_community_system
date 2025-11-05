package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.User;
import com.campus.forum.service.FavoriteService;
import com.campus.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.campus.forum.dal.domain.Post;
import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping
    public Result toggleFavorite(@RequestBody Map<String, Integer> payload) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.error(403, "请先登录！");
        }

        int postId = payload.get("postId");
        boolean isFavorited = favoriteService.isFavorited(user.getId(), postId);

        if (isFavorited) {
            favoriteService.removeFavorite(user.getId(), postId);
        } else {
            favoriteService.addFavorite(user.getId(), postId);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("isFavorited", !isFavorited);
        data.put("favoriteCount", favoriteService.countFavoritesByPostId(postId));

        return Result.success(data);
    }

    @GetMapping("/status")
    public Result getFavoriteStatus(@RequestParam("postId") int postId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.success(false); // Not favorited if not logged in
        }
        boolean isFavorited = favoriteService.isFavorited(user.getId(), postId);
        return Result.success(isFavorited);
    }

    @GetMapping("/list/{userId}")
    public Result getFavoriteList(@PathVariable("userId") int userId) {
        List<Post> favoritePosts = favoriteService.findFavoritesByUserId(userId);
        return Result.success(favoritePosts);
    }
}
