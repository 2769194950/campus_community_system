package com.campus.forum.dal.domain;

import java.util.Date;

/**
 * 榜单配置实体类
 * @author System
 * @date 2025/01/01
 */
public class Leaderboard {
    
    private Integer id;
    private String title;
    private String description;
    private String type;
    private String period;
    private Boolean enabled;
    private Integer sortOrder;
    private Integer displayCount; // 显示数量
    private Date createTime;
    private Date updateTime;
    
    public Leaderboard() {}
    
    public Leaderboard(String title, String description, String type, String period) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.period = period;
        this.enabled = true;
        this.sortOrder = 0;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getDisplayCount() {
        return displayCount;
    }
    
    public void setDisplayCount(Integer displayCount) {
        this.displayCount = displayCount;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public String toString() {
        return "Leaderboard{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", period='" + period + '\'' +
                ", enabled=" + enabled +
                ", sortOrder=" + sortOrder +
                ", displayCount=" + displayCount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}