-- 添加更多的榜单配置
INSERT INTO `leaderboard` (`id`, `title`, `description`, `type`, `period`, `enabled`, `sort_order`, `display_count`) VALUES
(3, '收藏排行榜', '根据收藏数量排名的帖子', 'favorite', 'weekly', 1, 3, 10),
(4, '点赞排行榜', '根据点赞数量排名的帖子', 'like', 'weekly', 1, 4, 10),
(5, '月度收藏榜', '月度收藏数量排名', 'favorite', 'monthly', 1, 5, 10),
(6, '月度点赞榜', '月度点赞数量排名', 'like', 'monthly', 1, 6, 10)
ON DUPLICATE KEY UPDATE 
  title = VALUES(title), 
  description = VALUES(description), 
  type = VALUES(type), 
  period = VALUES(period), 
  enabled = VALUES(enabled), 
  sort_order = VALUES(sort_order), 
  display_count = VALUES(display_count);