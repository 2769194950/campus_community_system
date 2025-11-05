package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 分区实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class Partition {
    private Integer id;
    private String name;
    private String description;
    private Integer postCount;
    private Date createTime;
}

