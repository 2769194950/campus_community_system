package com.campus.forum.service;

import com.campus.forum.common.ForumConstant;
import com.campus.forum.dal.domain.LoginTicket;
import com.campus.forum.dal.domain.User;
import com.campus.forum.dal.mapper.UserMapper;
import com.campus.forum.util.ForumUtil;
import com.campus.forum.util.MailClient;
import com.campus.forum.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Service
public class UserServiceImpl implements UserService, ForumConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Override
    public User findUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
//        if (StringUtils.isBlank(user.getEmail())) {
//            map.put("emailMsg", "邮箱不能为空!");
//            return map;
//        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

//        // 验证邮箱
//        u = userMapper.selectByEmail(user.getEmail());
//        if (u != null) {
//            map.put("emailMsg", "该邮箱已被注册!");
//            return map;
//        }

        // 注册用户
        user.setSalt(ForumUtil.generateUUID().substring(0, 5));
        user.setPassword(ForumUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(1);
        user.setActivationCode(ForumUtil.generateUUID());
        user.setAvatarUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setIntroduction("这个人很懒，什么都没有留下...");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

//        // 激活邮件
//        Context context = new Context();
//        context.setVariable("email", user.getEmail());
//        // http://localhost:8080/community/activation/101/code
//        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
//        context.setVariable("url", url);
//        String content = templateEngine.process("/mail/activation", context);
//        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = ForumUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(ForumUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + DEFAULT_EXPIRED_SECONDS * 1000L));
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        map.put("user", user);
        return map;
    }

    @Override
    public void logout(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        if (loginTicket != null) {
            loginTicket.setStatus(1);
            redisTemplate.opsForValue().set(redisKey, loginTicket);
        }
    }



    @Override
    public int updateAvatar(int userId, String avatarUrl) {
//        return userMapper.updateAvatar(userId, avatarUrl);
        int rows = userMapper.updateAvatar(userId, avatarUrl);
        clearCache(userId);
        return rows;
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public int updateIntroduction(int userId, String introduction) {
        int rows = userMapper.updateIntroduction(userId, introduction);
        clearCache(userId);
        return rows;
    }

    @Override
    public int updateEmail(int userId, String email) {
        int rows = userMapper.updateEmail(userId, email);
        clearCache(userId);
        return rows;
    }

    @Override
    public int updateUsername(int userId, String username) {
        int rows = userMapper.updateUsername(userId, username);
        clearCache(userId);
        return rows;
    }

    // ===== 活跃用户榜 =====
    @Override
    public java.util.List<com.campus.forum.dal.domain.User> findActiveUsers(int limit) {
        return userMapper.selectActiveUsers(0, limit);
    }

    @Override
    public java.util.List<com.campus.forum.dal.domain.User> findActiveUsersByPeriod(int limit, String period) {
        if (period == null || period.isEmpty() || "all".equalsIgnoreCase(period)) {
            return userMapper.selectActiveUsers(0, limit);
        }
        return userMapper.selectActiveUsersByPeriod(0, limit, period);
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        Object obj = redisTemplate.opsForValue().get(redisKey);
        
        // 增加类型检查，防止类型转换异常
        if (obj instanceof User) {
            return (User) obj;
        } else if (obj != null) {
            // 如果Redis中存储的不是User对象，清除该键并返回null
            redisTemplate.delete(redisKey);
        }
        
        return null;
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        try {
            String redisKey = RedisKeyUtil.getUserKey(userId);
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            // Redis 连接失败时忽略，不影响主要功能
            System.err.println(">>> Redis clearCache failed (ignored): " + e.getMessage());
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

    // ==================== 找回密码功能 ====================
    
    @Override
    public Map<String, Object> sendResetPasswordEmail(String email) {
        Map<String, Object> map = new HashMap<>();
        
        // 验证邮箱
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        
        // 查找用户
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱未注册！");
            return map;
        }
        
        // 生成重置密码令牌（6小时有效）
        String resetToken = ForumUtil.generateUUID();
        String redisKey = "reset:password:" + resetToken;
        redisTemplate.opsForValue().set(redisKey, user.getId(), 6, TimeUnit.HOURS);
        
        // 发送邮件
        try {
            Context context = new Context();
            context.setVariable("email", email);
            context.setVariable("resetUrl", domain + "/reset-password?token=" + resetToken);
            String content = templateEngine.process("/mail/reset-password", context);
            mailClient.sendMail(email, "重置密码 - 校园论坛", content);
        } catch (Exception e) {
            System.err.println(">>> 邮件发送失败（已忽略）: " + e.getMessage());
            // 开发环境：打印重置链接到控制台，方便测试
            System.out.println("\n==============================================");
            System.out.println("重置密码链接（仅开发环境）：");
            System.out.println(domain + "/reset-password?token=" + resetToken);
            System.out.println("==============================================\n");
        }
        
        map.put("success", true);
        return map;
    }
    
    @Override
    public Map<String, Object> resetPassword(String token, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        
        // 验证令牌
        if (StringUtils.isBlank(token)) {
            map.put("tokenMsg", "令牌无效！");
            return map;
        }
        
        // 验证密码
        if (StringUtils.isBlank(newPassword)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        
        if (newPassword.length() < 6) {
            map.put("passwordMsg", "密码长度不能少于6位！");
            return map;
        }
        
        // 从 Redis 获取用户 ID
        String redisKey = "reset:password:" + token;
        Integer userId = (Integer) redisTemplate.opsForValue().get(redisKey);
        
        if (userId == null) {
            map.put("tokenMsg", "令牌已过期或无效！");
            return map;
        }
        
        // 更新密码
        User user = userMapper.selectById(userId);
        String newPasswordEncrypted = ForumUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPasswordEncrypted);
        
        // 删除令牌
        redisTemplate.delete(redisKey);
        
        // 清除缓存
        clearCache(userId);
        
        map.put("success", true);
        return map;
    }
    
    // ==================== 密保问题功能 ====================
    
    @Transactional
    @Override
    public int updateSecurityQuestion(int userId, String question, String answer) {
        // 将答案加密存储（使用MD5）
        String encryptedAnswer = StringUtils.isNotBlank(answer) ? ForumUtil.md5(answer.toLowerCase().trim()) : null;
        
        // 更新数据库
        int rows = userMapper.updateSecurityQuestion(userId, question, encryptedAnswer);
        
        // 清除旧缓存
        clearCache(userId);
        
        // 主动刷新缓存，加载最新数据
        initCache(userId);
        
        return rows;
    }
    
    @Override
    public Map<String, Object> getSecurityQuestion(String username) {
        Map<String, Object> map = new HashMap<>();
        
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空！");
            return map;
        }
        
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该用户不存在！");
            return map;
        }
        
        // 检查是否设置了密保问题
        if (StringUtils.isBlank(user.getSecurityQuestion())) {
            map.put("questionMsg", "该用户未设置密保问题！");
            return map;
        }
        
        map.put("question", user.getSecurityQuestion());
        return map;
    }
    
    @Override
    public Map<String, Object> resetPasswordBySecurityQuestion(String username, String answer, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("error", "用户不存在");
            return map;
        }
        
        // 检查是否设置了密保问题
        if (StringUtils.isBlank(user.getSecurityQuestion()) || StringUtils.isBlank(user.getSecurityAnswer())) {
            map.put("error", "该用户未设置密保问题");
            return map;
        }
        
        // 加密用户输入的答案
        String encryptedAnswer = ForumUtil.md5(answer.toLowerCase().trim());
        
        // 验证答案
        if (!encryptedAnswer.equals(user.getSecurityAnswer())) {
            map.put("error", "密保答案错误");
            return map;
        }
        
        // 验证新密码
        if (StringUtils.isBlank(newPassword)) {
            map.put("passwordMsg", "新密码不能为空！");
            return map;
        }
        
        if (newPassword.length() < 6) {
            map.put("passwordMsg", "密码长度不能少于6位！");
            return map;
        }
        
        // 重置密码
        String newPasswordEncrypted = ForumUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(user.getId(), newPasswordEncrypted);
        
        clearCache(user.getId());
        
        map.put("success", true);
        return map;
    }
}