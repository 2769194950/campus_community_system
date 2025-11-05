package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updateAvatar(@Param("id") int id, @Param("avatarUrl") String avatarUrl);

    int updatePassword(@Param("id") int id, @Param("password") String password);

    int updateIntroduction(@Param("id") int id, @Param("introduction") String introduction);

    int updateEmail(@Param("id") int id, @Param("email") String email);
    
    int updateUsername(@Param("id") int id, @Param("username") String username);
    
    int updateSecurityQuestion(@Param("id") int id, 
                               @Param("question") String question, 
                               @Param("answer") String answer);
    
    /**
     * 统计用户总数
     */
    int countUsers();
    
    /**
     * 获取所有用户列表（管理员使用）
     */
    List<User> selectAllUsers(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 获取活跃用户列表（根据发帖和评论数量排序）
     */
    List<User> selectActiveUsers(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据时间周期获取活跃用户列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @param period 时间周期：daily-日榜, weekly-周榜, monthly-月榜, all-总榜
     */
    List<User> selectActiveUsersByPeriod(@Param("offset") int offset, @Param("limit") int limit, @Param("period") String period);
}

