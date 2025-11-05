package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Leaderboard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 榜单配置Mapper接口
 * @author System
 * @date 2025/01/01
 */
@Mapper
public interface LeaderboardMapper {
    
    /**
     * 查询所有榜单配置
     */
    List<Leaderboard> selectAllLeaderboards();
    
    /**
     * 根据ID查询榜单配置
     */
    Leaderboard selectLeaderboardById(@Param("id") Integer id);
    
    /**
     * 插入榜单配置
     */
    int insertLeaderboard(Leaderboard leaderboard);
    
    /**
     * 更新榜单配置
     */
    int updateLeaderboard(Leaderboard leaderboard);
    
    /**
     * 删除榜单配置
     */
    int deleteLeaderboardById(@Param("id") Integer id);
    
    /**
     * 更新榜单启用状态
     */
    int updateLeaderboardEnabled(@Param("id") Integer id, @Param("enabled") Boolean enabled);
    
    /**
     * 查询启用的榜单配置
     */
    List<Leaderboard> selectEnabledLeaderboards();
}