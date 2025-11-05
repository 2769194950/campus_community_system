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
    public List<Post> searchPosts(String keyword) {
        return postMapper.selectPostsByKeyword(keyword);
    }
}

