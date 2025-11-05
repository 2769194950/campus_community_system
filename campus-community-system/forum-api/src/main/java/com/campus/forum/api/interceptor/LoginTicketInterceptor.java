package com.campus.forum.api.interceptor;

import com.campus.forum.dal.domain.LoginTicket;
import com.campus.forum.dal.domain.User;
import com.campus.forum.service.UserService;
import com.campus.forum.util.CookieUtil;
import com.campus.forum.util.HostHolder;
import com.campus.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(">>> Interceptor: " + request.getMethod() + " " + request.getRequestURI());
        
        // 优先从Authorization header获取，如果没有则从cookie获取
        String ticket = request.getHeader("Authorization");
        System.out.println(">>> Authorization header: " + ticket);
        
        if (ticket == null || ticket.isEmpty()) {
            ticket = CookieUtil.getValue(request, "ticket");
            System.out.println(">>> Cookie ticket: " + ticket);
        }

        if (ticket != null) {
            // 查询凭证
            String redisKey = RedisKeyUtil.getTicketKey(ticket);
            LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
            System.out.println(">>> LoginTicket from Redis: " + loginTicket);
            
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                System.out.println(">>> Found user: " + (user != null ? user.getUsername() : "null"));
                
                // 在本次请求中持有用户
                hostHolder.setUser(user);
                
                // 构建用户认证的结果,并存入SecurityContext,以便于Security进行授权.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
                System.out.println(">>> User set to HostHolder and SecurityContext");
            } else {
                System.out.println(">>> LoginTicket is invalid or expired");
            }
        } else {
            System.out.println(">>> No ticket found");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}
