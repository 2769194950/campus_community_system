-- 为用户表添加密保问题字段
USE campus_forum;

ALTER TABLE `user` 
ADD COLUMN `security_question` VARCHAR(200) NULL COMMENT '密保问题' AFTER `introduction`,
ADD COLUMN `security_answer` VARCHAR(200) NULL COMMENT '密保答案' AFTER `security_question`;

