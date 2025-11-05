-- ----------------------------
-- 数据库初始化脚本
-- ----------------------------
DROP DATABASE IF EXISTS `campus_forum`;

CREATE DATABASE `campus_forum` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_forum`;

-- ----------------------------
-- Table structure for user
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `salt` varchar(50) DEFAULT NULL COMMENT '盐值',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `type` int(11) DEFAULT '0' COMMENT '账号类型 0-普通用户 1-超级管理员',
  `status` int(11) DEFAULT '0' COMMENT '状态 0-未激活 1-已激活',
  `activation_code` varchar(100) DEFAULT NULL COMMENT '激活码',
  `avatar_url` varchar(200) DEFAULT 'http://images.nowcoder.com/head/1t.png' COMMENT '头像地址',
  `introduction` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Table structure for post
-- 帖子表
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `partition_id` int(11) NOT NULL COMMENT '分区ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` int(11) DEFAULT '0' COMMENT '类型 0-普通 1-置顶',
  `status` int(11) DEFAULT '0' COMMENT '状态 0-正常 1-精华 2-拉黑',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览量',
  `comment_count` int(11) DEFAULT '0' COMMENT '评论数',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `favorite_count` int(11) DEFAULT '0' COMMENT '收藏数',
  `score` double DEFAULT '0' COMMENT '分数（热度）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';


-- ----------------------------
-- Table structure for partition
-- 分区表
-- ----------------------------
DROP TABLE IF EXISTS `partition`;
CREATE TABLE `partition` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分区ID',
    `name` varchar(50) NOT NULL COMMENT '分区名称',
    `description` varchar(255) DEFAULT NULL COMMENT '分区描述',
    `post_count` int(11) DEFAULT '0' COMMENT '帖子数量',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='分区表';

INSERT INTO `partition` (`id`, `name`, `description`) VALUES (1, '考研', '考研交流');
INSERT INTO `partition` (`id`, `name`, `description`) VALUES (2, '保研', '保研交流');
INSERT INTO `partition` (`id`, `name`, `description`) VALUES (3, '竞赛', '竞赛交流');
INSERT INTO `partition` (`id`, `name`, `description`) VALUES (4, '二手交易', '二手交易');
INSERT INTO `partition` (`id`, `name`, `description`) VALUES (5, '日常', '日常交流');


-- ----------------------------
-- Table structure for comment
-- 评论表
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `entity_type` int(11) NOT NULL COMMENT '评论实体类型 1-帖子 2-评论',
  `entity_id` int(11) NOT NULL COMMENT '评论实体ID',
  `target_id` int(11) DEFAULT '0' COMMENT '回复目标ID',
  `content` text NOT NULL COMMENT '内容',
  `status` int(11) DEFAULT '0' COMMENT '状态 0-正常 1-拉黑',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_entity` (`entity_type`, `entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='评论表';


-- ----------------------------
-- Table structure for tag
-- 标签表
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name` varchar(50) NOT NULL COMMENT '标签名称',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- Table structure for post_tag
-- 帖子标签关联表
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag` (
    `post_id` int(11) NOT NULL COMMENT '帖子ID',
    `tag_id` int(11) NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`post_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签关联表';


-- ----------------------------
-- Table structure for like
-- 点赞记录表
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
  `user_id` int(11) NOT NULL COMMENT '点赞用户ID',
  `entity_type` int(11) NOT NULL COMMENT '点赞实体类型 1-帖子 2-评论',
  `entity_id` int(11) NOT NULL COMMENT '点赞实体ID',
  `entity_user_id` int(11) NOT NULL COMMENT '被点赞用户ID',
  `status` int(11) DEFAULT '1' COMMENT '状态 1-已点赞 0-取消赞',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_entity` (`user_id`, `entity_type`, `entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';


-- ----------------------------
-- Table structure for favorite
-- 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` int(11) NOT NULL COMMENT '用户ID',
    `post_id` int(11) NOT NULL COMMENT '帖子ID',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';


-- ----------------------------
-- Table structure for notification
-- 通知表
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `notifier_id` int(11) NOT NULL COMMENT '发送通知用户ID',
  `receiver_id` int(11) NOT NULL COMMENT '接收通知用户ID',
  `outer_id` int(11) NOT NULL COMMENT '外部实体ID',
  `type` int(11) NOT NULL COMMENT '通知类型 1-评论 2-点赞 3-关注',
  `status` int(11) DEFAULT '0' COMMENT '状态 0-未读 1-已读 2-删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='通知表';


-- ----------------------------
-- Table structure for message
-- 私信表
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '私信ID',
  `from_id` int(11) NOT NULL COMMENT '发送用户ID',
  `to_id` int(11) NOT NULL COMMENT '接收用户ID',
  `conversation_id` varchar(50) NOT NULL COMMENT '会话ID',
  `content` text NOT NULL COMMENT '内容',
  `status` int(11) DEFAULT '0' COMMENT '状态 0-未读 1-已读 2-删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_from_id` (`from_id`),
  KEY `idx_to_id` (`to_id`),
  KEY `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='私信表';

-- ----------------------------
-- Table structure for friend
-- 好友表
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '好友关系ID',
    `user_id` int(11) NOT NULL COMMENT '用户ID',
    `friend_id` int(11) NOT NULL COMMENT '好友ID',
    `status` int(11) DEFAULT '0' COMMENT '状态 0-待同意 1-已同意 2-已拒绝',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='好友表';

-- ----------------------------
-- Mock User Data (50 users)
-- ----------------------------
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user1', 'f86938a1a8c235728a5d3f2d29f3e489', '8c8e1', 'user1@example.com', 0, 1, 'http://images.nowcoder.com/head/112t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user2', '969376644f1c7d23f73a55e1c2583ddf', '1e4a6', 'user2@example.com', 0, 1, 'http://images.nowcoder.com/head/12t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user3', 'b0f7a77b830138bf7224e70e9a4f491c', '6a8b6', 'user3@example.com', 0, 1, 'http://images.nowcoder.com/head/911t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user4', '3505b822d0d62d358172828b14e10118', 'e176b', 'user4@example.com', 0, 1, 'http://images.nowcoder.com/head/195t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user5', '8c77395e54670265278c4a52c3c13e51', '2f6d1', 'user5@example.com', 0, 1, 'http://images.nowcoder.com/head/500t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user6', 'c67420f86241a798888bf133d3126305', 'b6408', 'user6@example.com', 0, 1, 'http://images.nowcoder.com/head/662t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user7', 'b534a6c82d4493375c3f309d49495096', '963d3', 'user7@example.com', 0, 1, 'http://images.nowcoder.com/head/285t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user8', '71b12574e50d165688481351187d6129', '6878b', 'user8@example.com', 0, 1, 'http://images.nowcoder.com/head/533t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user9', '7d928a49c6d320473921867808269d50', '285e2', 'user9@example.com', 0, 1, 'http://images.nowcoder.com/head/939t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user10', '1e0b535d5d6880053a47926b772c7283', '90e43', 'user10@example.com', 0, 1, 'http://images.nowcoder.com/head/72t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user11', 'e2751f33f6314f85e482a170a59754f9', 'a2f48', 'user11@example.com', 0, 1, 'http://images.nowcoder.com/head/166t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user12', '933c01c77977a452d2f7a9379685150d', '90729', 'user12@example.com', 0, 1, 'http://images.nowcoder.com/head/305t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user13', '063b469796e138a379220556201a2f64', '5998a', 'user13@example.com', 0, 1, 'http://images.nowcoder.com/head/40t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user14', '034e402e646f0490f2385155799a4e83', 'f941e', 'user14@example.com', 0, 1, 'http://images.nowcoder.com/head/515t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user15', 'a437f1747c3855a15538e3c457d1973b', 'd2c38', 'user15@example.com', 0, 1, 'http://images.nowcoder.com/head/823t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user16', '16f062d31e9c8b746a5b675549111818', '7c86a', 'user16@example.com', 0, 1, 'http://images.nowcoder.com/head/775t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user17', '769c2794c489b6f1e847e1744158914c', 'f77f5', 'user17@example.com', 0, 1, 'http://images.nowcoder.com/head/399t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user18', '6b785d03822f3e09063529b0a684b3d3', 'd603a', 'user18@example.com', 0, 1, 'http://images.nowcoder.com/head/531t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user19', '59a5578749a263158c89b2750e05698b', '53483', 'user19@example.com', 0, 1, 'http://images.nowcoder.com/head/894t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user20', '49591e57c61c773950663a8a892b1a8d', '512a8', 'user20@example.com', 0, 1, 'http://images.nowcoder.com/head/371t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user21', '8f89e13d9435b5a6c0e5a5933a39e083', '52c5c', 'user21@example.com', 0, 1, 'http://images.nowcoder.com/head/491t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user22', '3187c53d0859424c53d4c61b6ea130ea', '35061', 'user22@example.com', 0, 1, 'http://images.nowcoder.com/head/195t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user23', 'a6d05f324a35f053229712a32193b2a3', '79861', 'user23@example.com', 0, 1, 'http://images.nowcoder.com/head/866t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user24', '702e533722a48e7e11364375b630e46a', 'b3d68', 'user24@example.com', 0, 1, 'http://images.nowcoder.com/head/28t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user25', 'ba731885f833e24483863a3a2d8aa083', '3b184', 'user25@example.com', 0, 1, 'http://images.nowcoder.com/head/918t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user26', '5ab7c4c3e8083c2e170940428d09d64f', '62994', 'user26@example.com', 0, 1, 'http://images.nowcoder.com/head/72t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user27', '9239851412d262a0a33c04c552086c12', 'c38a4', 'user27@example.com', 0, 1, 'http://images.nowcoder.com/head/678t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user28', 'c3ec81c03f909193b3303666d920399f', '45d4d', 'user28@example.com', 0, 1, 'http://images.nowcoder.com/head/285t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user29', '6790917aa8c7d8b5c479e9575914e6d4', 'd94c7', 'user29@example.com', 0, 1, 'http://images.nowcoder.com/head/379t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user30', '58a8a4f9103e62243d5089e5743f5505', 'f0933', 'user30@example.com', 0, 1, 'http://images.nowcoder.com/head/293t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user31', '302a433a593e1cf3e61895a013f99e82', 'f3d89', 'user31@example.com', 0, 1, 'http://images.nowcoder.com/head/984t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user32', '801962a970e557b3260a4e3264b306b6', '627b1', 'user32@example.com', 0, 1, 'http://images.nowcoder.com/head/376t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user33', 'e7740f9316531988941510255c464c23', '0e722', 'user33@example.com', 0, 1, 'http://images.nowcoder.com/head/778t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user34', '1b05252834b998d781a77587a83445cb', '054b7', 'user34@example.com', 0, 1, 'http://images.nowcoder.com/head/305t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user35', '4e12e1a347e3b9706346927cf185368a', '47f7d', 'user35@example.com', 0, 1, 'http://images.nowcoder.com/head/374t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user36', '71b10629ab43a912f91b7d54406a77d7', '2e90c', 'user36@example.com', 0, 1, 'http://images.nowcoder.com/head/121t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user37', 'a689b9195d3a4365312b9d3752e558a3', '3b754', 'user37@example.com', 0, 1, 'http://images.nowcoder.com/head/620t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user38', '6021287943c224213cb55963b53f659d', '6f16f', 'user38@example.com', 0, 1, 'http://images.nowcoder.com/head/728t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user39', '0289a9f24b0051e7371ac904f05b958f', '30a4d', 'user39@example.com', 0, 1, 'http://images.nowcoder.com/head/102t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user40', 'e50c822709149de4210d7967b66f275c', '4b321', 'user40@example.com', 0, 1, 'http://images.nowcoder.com/head/32t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user41', '196142759e66c757d54c7d0d6118356c', 'f0687', 'user41@example.com', 0, 1, 'http://images.nowcoder.com/head/771t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user42', 'e1761d713c77d5493b841703e1c667a4', '79841', 'user42@example.com', 0, 1, 'http://images.nowcoder.com/head/740t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user43', '24188b0a514b1e5a51375a065181b53b', '0ba0c', 'user43@example.com', 0, 1, 'http://images.nowcoder.com/head/534t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user44', 'de7d6928ab8d59a2249e6f43273187b4', '22a10', 'user44@example.com', 0, 1, 'http://images.nowcoder.com/head/678t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user45', '71d9a04a821e2572186985a973c091d4', '4867c', 'user45@example.com', 0, 1, 'http://images.nowcoder.com/head/270t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user46', 'b15175211a7a0f69d212a4176495d487', '6a35a', 'user46@example.com', 0, 1, 'http://images.nowcoder.com/head/49t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user47', '21e2e3181829d58aa84b64f434070c43', '2167c', 'user47@example.com', 0, 1, 'http://images.nowcoder.com/head/10t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user48', 'e8f7725916053facc841f3e7b1c4c11b', 'f9a2b', 'user48@example.com', 0, 1, 'http://images.nowcoder.com/head/939t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user49', '04c355a18b76a33758e9912752495d03', '679c8', 'user49@example.com', 0, 1, 'http://images.nowcoder.com/head/170t.png', 'User profile', NOW());
INSERT INTO `user` (`username`, `password`, `salt`, `email`, `type`, `status`, `avatar_url`, `introduction`, `create_time`) VALUES ('user50', '212453a290327f42629b3524b8981b2a', '1097f', 'user50@example.com', 0, 1, 'http://images.nowcoder.com/head/233t.png', 'User profile', NOW());