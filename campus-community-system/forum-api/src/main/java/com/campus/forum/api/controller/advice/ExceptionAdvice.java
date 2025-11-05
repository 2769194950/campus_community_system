package com.campus.forum.api.controller.advice;

import com.campus.forum.util.ForumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: " + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 检查响应是否已经被提交
        if (response.isCommitted()) {
            logger.error("响应已被提交，无法进一步处理");
            return;
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/json;charset=utf-8");
            try {
                PrintWriter writer = response.getWriter();
                writer.write(ForumUtil.getJSONString(1, "服务器异常!"));
                writer.flush();
            } catch (Exception ex) {
                logger.error("写入响应失败", ex);
            }
        } else {
            try {
                if (!response.isCommitted()) {
                    response.sendRedirect(request.getContextPath() + "/error");
                }
            } catch (Exception ex) {
                logger.error("重定向失败", ex);
            }
        }
    }

}
