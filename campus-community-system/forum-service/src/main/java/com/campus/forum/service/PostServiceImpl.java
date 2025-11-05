package com.campus.forum.service;

import com.campus.forum.dal.domain.Post;
import com.campus.forum.dal.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public int incrementLikeCount(int id){
        return postMapper.incrementLikeCount(id);
    }

    @Override
    public int decrementLikeCount(int id) {
        return postMapper.decrementLikeCount(id);
    }


    @Override
    public List<Post> findPosts(int userId, Integer partitionId) {
        return postMapper.selectPosts(userId, partitionId);
    }
    
    @Override
    public Post findPostById(int id) {
        return postMapper.selectPostById(id);
    }

    @Override
    public int addPost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 设置默认值
        if (post.getType() == null) {
            post.setType(0); // 0-普通帖子
        }
        if (post.getStatus() == null) {
            post.setStatus(0); // 0-正常状态
        }
        if (post.getViewCount() == null) {
            post.setViewCount(0);
        }
        if (post.getCommentCount() == null) {
            post.setCommentCount(0);
        }
        if (post.getLikeCount() == null) {
            post.setLikeCount(0);
        }
        if (post.getFavoriteCount() == null) {
            post.setFavoriteCount(0);
        }
        if (post.getScore() == null) {
            post.setScore(0.0);
        }

        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        return postMapper.insertPost(post);
    }

    @Override
    public int updateFavoriteCount(int postId, long favoriteCount) {
        return postMapper.updateFavoriteCount(postId, favoriteCount);
    }

    @Override
    public int deletePost(int postId) {
        return postMapper.updateStatus(postId, 2); // 2 represents deleted status
    }

    @Override
    public List<Post> findHotPosts(int limit) {
        return postMapper.selectHotPosts(limit);
    }

    @Override
    public List<Post> findHotPostsByPeriod(int limit, String period) {
        // period: daily/weekly/monthly/latest/all
        if ("all".equals(period) || period == null || period.isEmpty()) {
            return postMapper.selectHotPosts(limit);
        }
        return postMapper.selectHotPostsByPeriod(limit, period);
    }

    @Override
    public List<Post> findTopPostsByFavorites(int limit, String period) {
        if ("all".equals(period)) period = null; // XML 使用 null 表示不限制时间
        return postMapper.selectPostsByFavorites(limit, period);
    }

    @Override
    public List<Post> findTopPostsByLikes(int limit, String period) {
        if ("all".equals(period)) period = null;
        return postMapper.selectPostsByLikes(limit, period);
    }

    @Override
    public List<Post> searchPosts(String keyword) {
        return postMapper.selectPostsByKeyword(keyword);
    }

    @Override
    public Post getPostDetail(int postId) {
        // 修改浏览数
        postMapper.incrementViewCount(postId);
        // 获取数据
        //返回
        return postMapper.selectPostById(postId);
    }

    @Override
    public List<Post> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return java.util.Collections.emptyList();
        return postMapper.selectByIds(ids);
    }
}

