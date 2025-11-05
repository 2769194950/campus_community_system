package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 评论实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer entityType;
    private Integer entityId;
    private Integer targetId;
    private String content;
    private Integer status;
    private Integer likeCount;
    private Date createTime;
    private User user; // 关联用户信息
}

