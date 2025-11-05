package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}

