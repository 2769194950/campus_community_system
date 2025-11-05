-- 为leaderboard表添加display_count字段
ALTER TABLE `leaderboard` ADD COLUMN `display_count` int(11) DEFAULT 10 COMMENT '显示数量' AFTER `sort_order`;

-- 更新现有记录的display_count值
UPDATE `leaderboard` SET `display_count` = 10 WHERE `id` = 1;
UPDATE `leaderboard` SET `display_count` = 10 WHERE `id` = 2;