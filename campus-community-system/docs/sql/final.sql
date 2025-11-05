-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: campus_forum
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `campus_forum`
--

/*!40000 DROP DATABASE IF EXISTS `campus_forum`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `campus_forum` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `campus_forum`;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` int NOT NULL COMMENT '评论者ID',
  `entity_type` int NOT NULL COMMENT '对什么评论 1-帖子 2-评论',
  `entity_id` int NOT NULL COMMENT '被评论的ID',
  `target_id` int DEFAULT '0' COMMENT '回复目标ID',
  `content` text NOT NULL COMMENT '内容',
  `status` int DEFAULT '0' COMMENT '状态 0-正常 1-拉黑',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_entity` (`entity_type`,`entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,2,1,1,0,'同感，感觉一年比一年卷了。',0,3,'2025-03-01 02:05:00'),(2,3,1,1,0,'我还在复习高数，进度好慢啊。',0,1,'2025-03-01 02:15:00'),(3,4,1,1,0,'建议多做真题，掌握出题规律。',0,5,'2025-03-01 03:00:00'),(4,1,1,4,0,'感谢分享！非常有帮助！',0,2,'2025-04-01 04:30:00'),(5,5,1,4,0,'请问学长绩点多少呀？',0,0,'2025-04-02 01:00:00'),(6,6,1,4,0,'面试官主要关注哪些方面？',0,1,'2025-04-02 02:00:00'),(7,7,1,4,0,'项目经历重要吗？',0,3,'2025-04-02 03:00:00'),(8,1,1,9,0,'感谢分享，正在准备ACM。',0,4,'2025-05-15 09:00:00'),(9,2,1,9,0,'刷题平台推荐哪个？',0,2,'2025-05-15 10:00:00'),(10,3,1,9,0,'算法基础怎么打？',0,1,'2025-05-15 11:00:00'),(11,4,1,9,0,'时间复杂度分析有技巧吗？',0,3,'2025-05-15 12:00:00'),(12,5,1,9,0,'动态规划怎么入门？',0,2,'2025-05-15 13:00:00'),(13,6,1,9,0,'图论算法推荐看哪些？',0,1,'2025-05-15 14:00:00'),(14,7,1,9,0,'比赛策略有什么建议？',0,4,'2025-05-16 00:00:00'),(15,1,1,13,0,'味道不错，价格也还行，可以一试。',0,2,'2025-07-15 11:00:00'),(16,2,1,13,0,'汤底很香，推荐！',0,1,'2025-07-15 12:00:00'),(17,3,1,13,0,'分量有点少，不过味道确实可以。',0,0,'2025-07-15 13:00:00'),(18,1,1,15,0,'一起加油！',0,3,'2025-07-25 13:00:00'),(19,2,1,15,0,'复习计划很详细，参考了！',0,2,'2025-07-25 14:00:00'),(20,3,1,15,0,'时间安排很合理。',0,1,'2025-07-26 00:00:00'),(21,4,1,15,0,'有什么好的复习资料推荐吗？',0,0,'2025-07-26 01:00:00'),(22,5,1,15,0,'考试重点怎么把握？',0,2,'2025-07-26 02:00:00'),(24,11,1,15,NULL,'真的可以发表评论',0,NULL,'2025-10-29 06:04:58'),(25,11,1,15,NULL,'@user1 嗯嗯一起加油！',0,NULL,'2025-10-29 07:50:09');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`,`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,2,1,'2025-03-01 02:30:00'),(2,3,1,'2025-03-01 03:00:00'),(3,4,1,'2025-03-01 04:00:00'),(4,5,1,'2025-03-01 05:00:00'),(5,6,1,'2025-03-01 06:00:00'),(6,7,1,'2025-03-01 07:00:00'),(7,8,1,'2025-03-01 08:00:00'),(8,9,1,'2025-03-01 09:00:00'),(9,1,3,'2025-03-05 07:00:00'),(10,2,3,'2025-03-05 08:00:00'),(11,4,3,'2025-03-05 09:00:00'),(12,5,3,'2025-03-05 10:00:00'),(13,6,3,'2025-03-05 11:00:00'),(14,7,3,'2025-03-05 12:00:00'),(15,8,3,'2025-03-05 13:00:00'),(16,9,3,'2025-03-05 14:00:00'),(17,10,3,'2025-03-06 00:00:00'),(18,1,4,'2025-04-01 05:00:00'),(19,2,4,'2025-04-01 06:00:00'),(20,3,4,'2025-04-01 07:00:00'),(21,5,4,'2025-04-01 08:00:00'),(22,6,4,'2025-04-01 09:00:00'),(23,7,4,'2025-04-01 10:00:00'),(24,8,4,'2025-04-01 11:00:00'),(25,9,4,'2025-04-01 12:00:00'),(26,1,6,'2025-04-08 08:00:00'),(27,2,6,'2025-04-08 09:00:00'),(28,3,6,'2025-04-08 10:00:00'),(29,4,6,'2025-04-08 11:00:00'),(30,5,6,'2025-04-08 12:00:00'),(31,7,6,'2025-04-08 13:00:00'),(32,8,6,'2025-04-08 14:00:00'),(33,9,6,'2025-04-09 00:00:00'),(34,10,6,'2025-04-09 01:00:00'),(35,1,9,'2025-05-15 09:00:00'),(36,2,9,'2025-05-15 10:00:00'),(37,3,9,'2025-05-15 11:00:00'),(38,4,9,'2025-05-15 12:00:00'),(39,5,9,'2025-05-15 13:00:00'),(40,6,9,'2025-05-15 14:00:00'),(41,7,9,'2025-05-16 00:00:00'),(42,8,9,'2025-05-16 01:00:00'),(43,10,9,'2025-05-16 02:00:00'),(44,1,15,'2025-07-25 13:00:00'),(45,2,15,'2025-07-25 14:00:00'),(46,3,15,'2025-07-26 00:00:00'),(47,4,15,'2025-07-26 01:00:00'),(48,5,15,'2025-07-26 02:00:00'),(51,11,15,'2025-10-29 06:07:03'),(52,11,12,'2025-10-29 07:07:29'),(53,11,9,'2025-10-29 07:07:32');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend`
--

DROP TABLE IF EXISTS `friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '好友关系ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `friend_id` int NOT NULL COMMENT '好友ID',
  `status` int DEFAULT '0' COMMENT '状态 0-待同意 1-已同意 2-已拒绝',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_friend` (`user_id`,`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend`
--

LOCK TABLES `friend` WRITE;
/*!40000 ALTER TABLE `friend` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leaderboard`
--

DROP TABLE IF EXISTS `leaderboard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leaderboard` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '榜单ID',
  `title` varchar(100) NOT NULL COMMENT '榜单标题',
  `description` varchar(500) DEFAULT NULL COMMENT '榜单描述',
  `type` varchar(20) NOT NULL COMMENT '榜单类型 post-帖子榜 user-用户榜 favorite-收藏榜 like-点赞榜',
  `period` varchar(20) NOT NULL COMMENT '统计周期 daily-日榜 weekly-周榜 monthly-月榜 all-总榜 latest-最新',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用 0-禁用 1-启用',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `display_count` int DEFAULT '10' COMMENT '显示数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='榜单配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leaderboard`
--

LOCK TABLES `leaderboard` WRITE;
/*!40000 ALTER TABLE `leaderboard` DISABLE KEYS */;
INSERT INTO `leaderboard` VALUES (1,'热门帖子榜','根据点赞、评论、收藏综合排名的热门帖子','post','all',1,1,10,'2025-11-04 05:00:02','2025-11-04 05:00:02'),(2,'活跃用户榜','根据发帖和评论活跃度排名的用户','user','monthly',1,2,10,'2025-11-04 05:00:02','2025-11-04 05:00:02'),(3,'收藏排行榜','根据收藏数量排名的帖子','favorite','weekly',1,3,10,'2025-11-04 05:00:02','2025-11-04 05:00:02'),(4,'点赞排行榜','根据点赞数量排名的帖子','like','all',1,4,10,'2025-11-04 05:00:02','2025-11-05 07:27:50'),(5,'月度收藏榜','月度收藏数量排名','favorite','monthly',1,5,10,'2025-11-04 05:00:02','2025-11-04 05:00:02'),(6,'月度点赞榜','月度点赞数量排名','like','monthly',1,6,10,'2025-11-04 05:00:02','2025-11-04 05:00:02');
/*!40000 ALTER TABLE `leaderboard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `like`
--

DROP TABLE IF EXISTS `like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `like` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
  `user_id` int NOT NULL COMMENT '点赞用户ID',
  `entity_type` int NOT NULL COMMENT '点赞实体类型 1-帖子 2-评论',
  `entity_id` int NOT NULL COMMENT '点赞实体ID',
  `entity_user_id` int NOT NULL COMMENT '被点赞用户ID',
  `status` int DEFAULT '1' COMMENT '状态 1-已点赞 0-取消赞',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_entity` (`user_id`,`entity_type`,`entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='点赞记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like`
--

LOCK TABLES `like` WRITE;
/*!40000 ALTER TABLE `like` DISABLE KEYS */;
INSERT INTO `like` VALUES (1,2,1,1,1,1,'2025-03-01 02:30:00','2025-10-27 10:17:40'),(2,3,1,1,1,1,'2025-03-01 03:00:00','2025-10-27 10:17:40'),(3,4,1,1,1,1,'2025-03-01 04:00:00','2025-10-27 10:17:40'),(4,5,1,1,1,1,'2025-03-01 05:00:00','2025-10-27 10:17:40'),(5,6,1,1,1,1,'2025-03-01 06:00:00','2025-10-27 10:17:40'),(6,7,1,1,1,1,'2025-03-01 07:00:00','2025-10-27 10:17:40'),(7,8,1,1,1,1,'2025-03-01 08:00:00','2025-10-27 10:17:40'),(8,9,1,1,1,1,'2025-03-01 09:00:00','2025-10-27 10:17:40'),(9,10,1,1,1,1,'2025-03-01 10:00:00','2025-10-27 10:17:40'),(10,1,1,3,3,1,'2025-03-05 07:00:00','2025-10-27 10:17:40'),(11,2,1,3,3,1,'2025-03-05 08:00:00','2025-10-27 10:17:40'),(12,4,1,3,3,1,'2025-03-05 09:00:00','2025-10-27 10:17:40'),(13,5,1,3,3,1,'2025-03-05 10:00:00','2025-10-27 10:17:40'),(14,6,1,3,3,1,'2025-03-05 11:00:00','2025-10-27 10:17:40'),(15,7,1,3,3,1,'2025-03-05 12:00:00','2025-10-27 10:17:40'),(16,8,1,3,3,1,'2025-03-05 13:00:00','2025-10-27 10:17:40'),(17,9,1,3,3,1,'2025-03-05 14:00:00','2025-10-27 10:17:40'),(18,10,1,3,3,1,'2025-03-06 00:00:00','2025-10-27 10:17:40'),(19,1,1,4,4,1,'2025-04-01 05:00:00','2025-10-27 10:17:40'),(20,2,1,4,4,1,'2025-04-01 06:00:00','2025-10-27 10:17:40'),(21,3,1,4,4,1,'2025-04-01 07:00:00','2025-10-27 10:17:40'),(22,5,1,4,4,1,'2025-04-01 08:00:00','2025-10-27 10:17:40'),(23,6,1,4,4,1,'2025-04-01 09:00:00','2025-10-27 10:17:40'),(24,7,1,4,4,1,'2025-04-01 10:00:00','2025-10-27 10:17:40'),(25,8,1,4,4,1,'2025-04-01 11:00:00','2025-10-27 10:17:40'),(26,9,1,4,4,1,'2025-04-01 12:00:00','2025-10-27 10:17:40'),(27,10,1,4,4,1,'2025-04-01 13:00:00','2025-10-27 10:17:40'),(28,1,1,6,6,1,'2025-04-08 08:00:00','2025-10-27 10:17:40'),(29,2,1,6,6,1,'2025-04-08 09:00:00','2025-10-27 10:17:40'),(30,3,1,6,6,1,'2025-04-08 10:00:00','2025-10-27 10:17:40'),(31,4,1,6,6,1,'2025-04-08 11:00:00','2025-10-27 10:17:40'),(32,5,1,6,6,1,'2025-04-08 12:00:00','2025-10-27 10:17:40'),(33,7,1,6,6,1,'2025-04-08 13:00:00','2025-10-27 10:17:40'),(34,8,1,6,6,1,'2025-04-08 14:00:00','2025-10-27 10:17:40'),(35,9,1,6,6,1,'2025-04-09 00:00:00','2025-10-27 10:17:40'),(36,10,1,6,6,1,'2025-04-09 01:00:00','2025-10-27 10:17:40'),(37,1,1,9,9,1,'2025-05-15 09:00:00','2025-10-27 10:17:40'),(38,2,1,9,9,1,'2025-05-15 10:00:00','2025-10-27 10:17:40'),(39,3,1,9,9,1,'2025-05-15 11:00:00','2025-10-27 10:17:40'),(40,4,1,9,9,1,'2025-05-15 12:00:00','2025-10-27 10:17:40'),(41,5,1,9,9,1,'2025-05-15 13:00:00','2025-10-27 10:17:40'),(42,6,1,9,9,1,'2025-05-15 14:00:00','2025-10-27 10:17:40'),(43,7,1,9,9,1,'2025-05-16 00:00:00','2025-10-27 10:17:40'),(44,8,1,9,9,1,'2025-05-16 01:00:00','2025-10-27 10:17:40'),(45,10,1,9,9,1,'2025-05-16 02:00:00','2025-10-27 10:17:40'),(46,1,1,15,6,1,'2025-07-25 13:00:00','2025-10-27 10:17:40'),(47,2,1,15,6,1,'2025-07-25 14:00:00','2025-10-27 10:17:40'),(48,3,1,15,6,1,'2025-07-26 00:00:00','2025-10-27 10:17:40'),(49,4,1,15,6,1,'2025-07-26 01:00:00','2025-10-27 10:17:40'),(50,5,1,15,6,1,'2025-07-26 02:00:00','2025-10-27 10:17:40'),(51,1,2,1,2,1,'2025-03-01 02:10:00','2025-10-27 10:17:40'),(52,3,2,1,2,1,'2025-03-01 02:20:00','2025-10-27 10:17:40'),(53,4,2,1,2,1,'2025-03-01 02:30:00','2025-10-27 10:17:40'),(54,1,2,3,4,1,'2025-03-01 03:05:00','2025-10-27 10:17:40'),(55,2,2,3,4,1,'2025-03-01 03:10:00','2025-10-27 10:17:40'),(56,3,2,3,4,1,'2025-03-01 03:15:00','2025-10-27 10:17:40'),(57,5,2,3,4,1,'2025-03-01 03:20:00','2025-10-27 10:17:40'),(58,6,2,3,4,1,'2025-03-01 03:25:00','2025-10-27 10:17:40'),(60,11,1,15,6,1,'2025-10-28 18:59:58','2025-10-28 19:29:04'),(62,11,1,14,5,1,'2025-10-28 19:24:34','2025-10-28 19:29:10'),(63,11,1,9,9,0,'2025-10-28 19:25:17','2025-10-28 19:29:37'),(64,11,1,13,4,1,'2025-10-28 19:29:13','2025-10-28 19:29:13'),(65,11,1,12,3,1,'2025-10-28 19:29:19','2025-10-28 19:29:20'),(66,11,1,11,2,1,'2025-10-28 19:29:24','2025-10-28 19:29:26'),(67,11,1,10,1,1,'2025-10-28 19:29:29','2025-10-28 19:29:32'),(68,11,1,8,8,1,'2025-10-28 19:29:42','2025-10-28 19:29:42'),(69,11,1,7,7,1,'2025-10-28 19:29:46','2025-10-28 19:29:46'),(70,11,1,6,6,1,'2025-10-28 19:29:53','2025-10-28 19:29:53'),(71,11,1,5,5,1,'2025-10-28 19:29:57','2025-10-28 19:29:57'),(72,11,1,4,4,1,'2025-10-28 19:30:02','2025-10-28 19:30:02'),(73,11,1,3,3,1,'2025-10-28 19:30:08','2025-10-28 19:30:08'),(74,11,1,2,2,1,'2025-10-28 19:30:15','2025-10-28 19:30:15'),(75,11,1,1,1,0,'2025-10-28 19:30:19','2025-10-29 02:00:20'),(76,10,1,16,11,1,'2025-11-04 20:38:01','2025-11-04 20:38:01');
/*!40000 ALTER TABLE `like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '私信ID',
  `from_id` int NOT NULL COMMENT '发送用户ID',
  `to_id` int NOT NULL COMMENT '接收用户ID',
  `conversation_id` varchar(50) NOT NULL COMMENT '会话ID',
  `content` text NOT NULL COMMENT '内容',
  `status` int DEFAULT '0' COMMENT '状态 0-未读 1-已读 2-删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_from_id` (`from_id`),
  KEY `idx_to_id` (`to_id`),
  KEY `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='私信表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,2,1,'2_1','你好，关于考研数学的问题想请教一下',0,'2025-03-01 12:00:00'),(2,1,2,'2_1','好的，有什么问题尽管问',0,'2025-03-01 12:05:00'),(3,3,4,'3_4','保研面试经验很实用，谢谢分享',0,'2025-04-01 13:00:00'),(4,4,3,'3_4','不客气，希望能帮到你',0,'2025-04-01 13:10:00'),(5,5,6,'5_6','保研材料准备有什么建议吗？',0,'2025-04-08 14:00:00'),(6,6,5,'5_6','建议提前准备，材料要齐全',0,'2025-04-08 14:15:00'),(7,11,6,'6_11','hi，可以认识一下吗',0,'2025-11-05 07:31:53');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `notifier_id` int NOT NULL COMMENT '发送通知用户ID',
  `receiver_id` int NOT NULL COMMENT '接收通知用户ID',
  `outer_id` int NOT NULL COMMENT '外部实体ID',
  `type` int NOT NULL COMMENT '通知类型 1-评论 2-点赞 3-关注',
  `status` int DEFAULT '0' COMMENT '状态 0-未读 1-已读 2-删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,2,1,1,1,0,'2025-03-01 02:05:00'),(2,3,1,1,1,0,'2025-03-01 02:15:00'),(3,4,1,1,1,0,'2025-03-01 03:00:00'),(4,2,1,1,2,0,'2025-03-01 02:30:00'),(5,3,1,1,2,0,'2025-03-01 03:00:00'),(6,4,1,1,2,0,'2025-03-01 04:00:00'),(7,5,1,1,2,0,'2025-03-01 05:00:00'),(8,6,1,1,2,0,'2025-03-01 06:00:00'),(9,7,1,1,2,0,'2025-03-01 07:00:00'),(10,8,1,1,2,0,'2025-03-01 08:00:00'),(11,9,1,1,2,0,'2025-03-01 09:00:00'),(12,10,1,1,2,0,'2025-03-01 10:00:00'),(13,1,4,4,1,0,'2025-04-01 04:30:00'),(14,5,4,4,1,0,'2025-04-02 01:00:00'),(15,6,4,4,1,0,'2025-04-02 02:00:00'),(16,7,4,4,1,0,'2025-04-02 03:00:00'),(17,1,4,4,2,0,'2025-04-01 05:00:00'),(18,2,4,4,2,0,'2025-04-01 06:00:00'),(19,3,4,4,2,0,'2025-04-01 07:00:00'),(20,5,4,4,2,0,'2025-04-01 08:00:00'),(21,6,4,4,2,0,'2025-04-01 09:00:00'),(22,7,4,4,2,0,'2025-04-01 10:00:00'),(23,8,4,4,2,0,'2025-04-01 11:00:00'),(24,9,4,4,2,0,'2025-04-01 12:00:00'),(25,10,4,4,2,0,'2025-04-01 13:00:00');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partition`
--

DROP TABLE IF EXISTS `partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `partition` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分区ID',
  `name` varchar(50) NOT NULL COMMENT '分区名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分区描述',
  `post_count` int DEFAULT '0' COMMENT '帖子数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分区表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partition`
--

LOCK TABLES `partition` WRITE;
/*!40000 ALTER TABLE `partition` DISABLE KEYS */;
INSERT INTO `partition` VALUES (1,'考研','考研经验分享、资料推荐、复习计划讨论',3,'2024-12-31 16:00:00'),(2,'保研','保研夏令营、推免经验、面试技巧分享',3,'2024-12-31 16:00:00'),(3,'竞赛','各类学科竞赛、创新创业大赛组队交流',3,'2024-12-31 16:00:00'),(4,'二手交易','二手物品交易、闲置物品转让',3,'2024-12-31 16:00:00'),(5,'日常','校园生活、学习交流、日常闲聊',3,'2024-12-31 16:00:00');
/*!40000 ALTER TABLE `partition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `partition_id` int NOT NULL COMMENT '分区ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` int DEFAULT '0' COMMENT '类型 0-普通 1-置顶',
  `status` int DEFAULT '0' COMMENT '状态 0-正常 1-精华 2-拉黑',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `favorite_count` int DEFAULT '0' COMMENT '收藏数',
  `score` double DEFAULT '0' COMMENT '分数（热度）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,1,1,'2026考研数学一难度会如何？','感觉今年的数学特别难，不知道明年会不会简单一点，大家有什么看法？',0,0,120,3,9,8,0,'2025-03-01 02:00:00'),(2,2,1,'求一份靠谱的政治考研复习资料','市面上的资料太多了，求推荐！',0,0,85,0,1,5,0,'2025-03-02 03:00:00'),(3,3,1,'英语一阅读理解技巧分享','经过一年的复习，总结了一些阅读理解的技巧，希望对大家有帮助。',0,0,200,0,10,12,0,'2025-03-05 06:00:00'),(4,4,2,'计算机专业保研夏令营面试经验','分享一下面试中被问到的问题，希望能帮助到大家。',0,0,150,4,10,9,0,'2025-04-01 04:00:00'),(5,5,2,'保研边缘人，如何最大化自己的机会？','成绩在保研边缘，有点焦虑，求技巧分享。',0,0,95,0,1,7,0,'2025-04-05 05:00:00'),(6,6,2,'保研材料准备清单','整理了一份保研材料准备清单，需要的同学可以看看。',0,0,181,0,10,15,0,'2025-04-08 07:00:00'),(7,7,3,'\"挑战杯\"竞赛组队，来一个会做PPT的大佬','项目技术人员已齐，缺一个审美在线、会做商业计划书和PPT的大佬。',0,0,60,0,1,3,0,'2025-05-10 06:00:00'),(8,8,3,'数学建模美赛要开始了，你准备好了吗？','美赛要肝三天三夜，大家开始找队友、刷真题了吗？',0,0,111,0,1,6,0,'2025-05-12 07:00:00'),(9,9,3,'ACM竞赛刷题经验分享','从零基础到区域赛银牌，分享一下我的刷题经验。',0,0,252,7,9,10,0,'2025-05-15 08:00:00'),(10,1,4,'【出】九成新Kindle Paperwhite 4','买了没怎么用，价格可小刀，走闲鱼。',0,0,46,0,1,2,0,'2025-06-01 08:00:00'),(11,2,4,'【收】一辆二手自行车，代步用','新校区太大，没车寸步难行，求一辆便宜皮实的二手自行车。',0,0,30,0,1,1,0,'2025-06-03 09:00:00'),(12,3,4,'【出】考研英语真题集','2020-2024年真题，有详细解析，几乎全新。',0,0,26,0,1,1,0,'2025-06-05 10:00:00'),(13,4,5,'学校东门新开的那家麻辣烫味道怎么样？','路过好几次了，有没有去吃过的同学来说说味道如何？',0,0,80,3,1,4,0,'2025-07-15 10:00:00'),(14,5,5,'有人知道图书馆预约系统又崩了吗？','早上起来想约个座位，结果一直转圈圈，是我的问题还是系统的问题？',0,0,65,0,1,2,0,'2025-07-20 11:00:00'),(15,6,5,'期末考试复习计划分享','还有一个月就期末考试了，分享一下我的复习计划，大家一起加油！',0,0,172,7,6,6,0,'2025-07-25 12:00:00'),(16,11,5,'手游同好','这个游戏好冷门啊，有没有一起玩的',0,0,8,0,1,0,0,'2025-10-29 07:48:30');
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_tag`
--

DROP TABLE IF EXISTS `post_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_tag` (
  `post_id` int NOT NULL COMMENT '帖子ID',
  `tag_id` int NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`post_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_tag`
--

LOCK TABLES `post_tag` WRITE;
/*!40000 ALTER TABLE `post_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `salt` varchar(50) DEFAULT NULL COMMENT '盐值',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `type` int DEFAULT '0' COMMENT '账号类型 0-普通用户 1-超级管理员',
  `status` int DEFAULT '0' COMMENT '状态 0-未激活 1-已激活',
  `activation_code` varchar(100) DEFAULT NULL COMMENT '激活码',
  `avatar_url` varchar(200) DEFAULT 'http://images.nowcoder.com/head/1t.png' COMMENT '头像地址',
  `introduction` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `security_question` varchar(200) DEFAULT NULL COMMENT '密保问题',
  `security_answer` varchar(200) DEFAULT NULL COMMENT '密保答案',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'user1','ce934f2d733561a4f585d5f470701b56','a1b2c','user1@example.com',0,1,NULL,'http://images.nowcoder.com/head/101t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-10 02:00:00'),(2,'user2','d729a6b5d39d82e1e4cf19131a393cf2','d3e4f','user2@example.com',0,1,NULL,'http://images.nowcoder.com/head/102t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-11 03:00:00'),(3,'user3','26b567269151833ea52d7e3557e98a3f','g5h6i','user3@example.com',0,1,NULL,'http://images.nowcoder.com/head/103t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-12 04:00:00'),(4,'user4','3a97711c5b811de9f8d16854f0a055d7','j7k8l','user4@example.com',0,1,NULL,'http://images.nowcoder.com/head/104t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-13 05:00:00'),(5,'user5','f4955a6c97a25530a21319323758b2e1','m9n0o','user5@example.com',0,1,NULL,'http://images.nowcoder.com/head/105t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-14 06:00:00'),(6,'user6','b8830883b448655259968a355f532a31','p1q2r','user6@example.com',0,1,NULL,'http://images.nowcoder.com/head/106t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-15 07:00:00'),(7,'user7','18b104036e55181b553e46c7604557a5','s3t4u','user7@example.com',0,1,NULL,'http://images.nowcoder.com/head/107t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-16 08:00:00'),(8,'user8','d35b542e77e384e062a439c21f8a8e9e','v5w6x','user8@example.com',0,1,NULL,'http://images.nowcoder.com/head/108t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-17 09:00:00'),(9,'user9','7d8c545f126f555c963a7f804561e15b','y7z8a','user9@example.com',0,1,NULL,'http://images.nowcoder.com/head/109t.png','这个人很懒，什么都没有留下...',NULL,NULL,'2025-01-18 10:00:00'),(10,'admin','8358116249eb715f13b578b1315e057e','b9c0d','admin@example.com',1,1,NULL,'http://images.nowcoder.com/head/110t.png','系统管理员',NULL,NULL,'2025-01-19 11:00:00'),(11,'user11','dd50f3a484c1d3eade99f3e4eeada672','6d771','2769194950@qq.com',0,1,'229021a99a1a4123a94a060514f9b0bc','http://images.nowcoder.com/head/389t.png','这个人很懒','test','098f6bcd4621d373cade4e832627b4f6','2025-10-27 10:32:12');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'campus_forum'
--

--
-- Dumping routines for database 'campus_forum'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-05 22:43:36
