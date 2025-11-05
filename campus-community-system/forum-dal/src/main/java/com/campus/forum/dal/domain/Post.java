package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 帖子实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class Post {
    private Integer id;
    private Integer userId;
    private Integer partitionId;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Double score;
    private Date createTime;
    private User user; // 关联用户信息
}

