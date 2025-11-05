package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Mapper
public interface PostMapper {

    List<Post> selectPosts(@Param("userId") int userId, @Param("partitionId") Integer partitionId);
    
    Post selectPostById(@Param("id") int id);
    
    int insertPost(Post post);

    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    int selectViewCount(@Param("id") int id);

    int updateViewCount(@Param("id") int id, @Param("viewCount") int viewCount);

    int updateFavoriteCount(@Param("id") int id, @Param("favoriteCount") long favoriteCount);
    
    int updateStatus(@Param("id") int id, @Param("status") int status);
    
    int updateLikeCount(@Param("id") int id, @Param("likeCount") long likeCount);

    int incrementLikeCount(@Param("id") int id);
    int decrementLikeCount(@Param("id") int id);

    int incrementViewCount(@Param("id") int id);

    int incrementCommentCount(@Param("id") int id);
    int decrementCommentCount(@Param("id") int id);

    List<Post> selectHotPosts(@Param("limit") int limit);
    
    /**
     * 根据时间周期获取热门帖子
     * @param limit 限制数量
     * @param period 时间周期：daily-日榜, weekly-周榜, monthly-月榜, all-总榜
     */
    List<Post> selectHotPostsByPeriod(@Param("limit") int limit, @Param("period") String period);

    List<Post> selectLatestPosts(@Param("limit") int limit);
    
    /**
     * 根据收藏数排序获取帖子
     */
    List<Post> selectPostsByFavorites(@Param("limit") int limit, @Param("period") String period);
    
    /**
     * 根据点赞数排序获取帖子
     */
    List<Post> selectPostsByLikes(@Param("limit") int limit, @Param("period") String period);
    
    List<Post> selectPostsByKeyword(@Param("keyword") String keyword);
    
    List<Post> selectAllPosts(@Param("offset") int offset, @Param("limit") int limit);

    List<Post> selectByIds(@Param("ids") List<Integer> ids);
    
    /**
     * 统计帖子总数（排除被拉黑的帖子）
     */
    int countPosts();
}