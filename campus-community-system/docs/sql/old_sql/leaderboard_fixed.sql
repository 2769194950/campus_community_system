-- 榜单配置表
DROP TABLE IF EXISTS `leaderboard`;
CREATE TABLE `leaderboard` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '榜单ID',
  `title` varchar(100) NOT NULL COMMENT '榜单标题',
  `description` varchar(500) DEFAULT NULL COMMENT '榜单描述',
  `type` varchar(20) NOT NULL COMMENT '榜单类型 post-帖子榜 user-用户榜 favorite-收藏榜 like-点赞榜',
  `period` varchar(20) NOT NULL COMMENT '统计周期 daily-日榜 weekly-周榜 monthly-月榜 all-总榜 latest-最新',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用 0-禁用 1-启用',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序顺序',
  `display_count` int(11) DEFAULT 10 COMMENT '显示数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='榜单配置表';

-- 插入默认榜单配置
INSERT INTO `leaderboard` (`id`, `title`, `description`, `type`, `period`, `enabled`, `sort_order`, `display_count`) VALUES
(1, '热门帖子榜', '根据点赞、评论、收藏综合排名的热门帖子', 'post', 'all', 1, 1, 10),
(2, '活跃用户榜', '根据发帖和评论活跃度排名的用户', 'user', 'monthly', 1, 2, 10),
(3, '收藏排行榜', '根据收藏数量排名的帖子', 'favorite', 'weekly', 1, 3, 10),
(4, '点赞排行榜', '根据点赞数量排名的帖子', 'like', 'weekly', 1, 4, 10),
(5, '月度收藏榜', '月度收藏数量排名', 'favorite', 'monthly', 1, 5, 10),
(6, '月度点赞榜', '月度点赞数量排名', 'like', 'monthly', 1, 6, 10);