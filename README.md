# 校园社区论坛系统

本项目是一个基于 Java + Spring Boot + Vue.js 技术栈开发的现代化校园社区论坛。系统采用多模块、分层架构设计，实现了问答、博客、社交互动、标签分类和搜索等多种功能，旨在为用户提供一个功能完善、易扩展的互动交流平台。

## 一、项目背景

随着网络社区的发展，用户对论坛系统的需求已经不仅仅停留在简单的发帖与回复，而是希望拥有集成问答、博客、社交互动、标签分类和搜索等多种功能的现代化平台。本项目旨在打造一个功能完善、易扩展的开源社区系统，满足现代用户在内容创作、互动交流和信息管理方面的需求。

## 二、技术栈

### 后端
- **核心框架**: Spring Boot 2.7.5
- **数据访问**: MyBatis, PageHelper (分页)
- **数据库**: MySQL 8.0
- **数据库连接池**: HikariCP
- **缓存**: Redis (用于点赞、用户缓存等)
- **安全**: Spring Security
- **API文档**: Swagger (通过 springdoc-openapi)
- **邮件服务**: Spring Mail
- **模板引擎**: Thymeleaf (用于邮件模板)

### 前端
- **框架**: Vue.js 2
- **路由**: Vue Router
- **状态管理**: Vuex
- **UI库**: Bootstrap 4
- **富文本编辑**: mavon-editor (Markdown 编辑器)
- **HTTP客户端**: Axios

## 三、模块设计

项目采用分层与模块化设计，共分为五个后端模块和一个前端模块：

1.  `forum-common`: 公共工具模块，封装日志、异常处理、统一响应格式等。
2.  `forum-dal`: 数据访问层，包含数据库实体 (Entity) 和 MyBatis Mapper 接口。
3.  `forum-service`: 核心业务模块，处理所有业务逻辑。
4.  `forum-api`: 接口服务模块，提供 RESTful API 接口供前端调用。
5.  `forum-starter`: 应用启动模块，整合所有模块并作为程序入口。
6.  `frontend`: 用户交互模块，基于 Vue.js 构建的单页面应用。

## 四、功能列表

- **账号与身份**: 注册、登录、退出、个人资料查看与编辑。
- **内容发布与浏览**: 发布帖子、分页列表、详情页展示。
- **互动功能**: 对帖子和评论进行评论/回复、点赞、收藏。
- **分类与检索**: 按分区浏览帖子。
- **消息与通知**: 站内私信功能。
- **个人中心**: 查看“我发布的帖子”、“我的评论”、“我的收藏”。
- **管理功能**: (基础) 内容审核接口预留。

## 五、如何运行

### 第一步：启动后端服务

1.  **环境准备**:
    *   确保已安装 Java (JDK 1.8+), Maven, MySQL 8.0+, Redis。
    *   在 MySQL 中创建一个名为 `campus_forum` 的数据库。
    *   将 `campus-community-system/docs/sql/schema.sql` 文件导入到 `campus_forum` 数据库中。

2.  **修改配置**:
    *   打开后端配置文件：`campus-community-system/forum-starter/src/main/resources/application.properties`。
    *   根据你的本地环境，修改以下配置：
        *   `spring.datasource.username` 和 `spring.datasource.password` (数据库用户名和密码)。
        *   `spring.redis.host` 和 `spring.redis.port` (Redis 连接信息)。
        *   `spring.mail.username` 和 `spring.mail.password` (用于发送激活邮件的邮箱账号和授权码)。

3.  **启动应用**:
    *   在你的 IDE (如 IntelliJ IDEA) 中，找到 `forum-starter` 模块下的 `CampusCommunitySystemApplication.java` 文件。
    *   右键点击该文件，选择 “Run ‘CampusCommunitySystemApplication’”。
    *   当控制台显示 Spring Boot 启动成功的信息时，表示后端已成功运行在 `http://localhost:8080/community`。

### 第二步：启动前端页面

1.  **环境准备**:
    *   确保已安装 Node.js (14.0+)。

2.  **启动应用**:
    *   在文件浏览器中，进入到项目根目录下的 `frontend` 文件夹。
    *   在该文件夹的地址栏输入 `cmd` 并回车，以在此处打开终端。
    *   在打开的终端中，首次运行请先执行 `npm install` 来安装项目依赖。
    *   安装完成后，执行 `npm run serve` 来启动前端开发服务器。
    *   当终端显示 `App running at: - Local: http://localhost:8081/` 时，表示前端已成功启动。

现在，你可以打开浏览器访问 `http://localhost:8081` 来体验你的校园论坛了。

## 六、成员分工与贡献说明

本项目由团队合作完成，以下是成员分工及代码贡献说明：

- **郗佳仪｜前端负责人**: 负责页面结构与样式、表单校验与交互；对接接口、集成 Markdown 编辑器。
- **范晓蓁｜数据库与数据层**:
  - **你的工作**: 在本次开发中，你主要负责数据库与数据层的设计与实现。
  - **具体贡献**:
    1.  **数据库结构设计**: 你设计了整个项目的核心数据表结构，包括用户表(`user`)、帖子表(`post`)、评论表(`comment`)、分区表(`partition`)、私信表(`message`)以及点赞、收藏、好友关系等关联表。你充分考虑了表之间的关系、字段类型和索引优化，为项目打下了坚实的数据基础。
    2.  **SQL脚本编写**: 你编写了位于 `docs/sql/schema.sql` 的数据库初始化脚本，该脚本包含了所有表的创建语句和初始数据的插入，保证了项目环境的一致性和可重复部署性。
    3.  **数据访问层实现**: 你在 `forum-dal` 模块中，定义了所有与数据库表对应的Java实体类 (Domain)，并编写了所有的MyBatis Mapper接口及其对应的XML文件，实现了对数据库的增、删、改、查等原子操作，为上层业务逻辑提供了稳定可靠的数据服务。
- **王永涛｜后端主负责人**: 统筹后端整体设计与核心业务实现；主导发帖/评论/问答/通知等主流程与数据校验。
- **张静雯｜后端（兼顾接口）**: 共同实现业务逻辑；重点完善接口服务模块（REST 设计、错误码与返回体规范）。
- **王征远｜后端（兼顾测试）**: 实现基础支撑与公共模块（数据库访问、缓存、文件存储抽象、日志/异常）。

---
*该项目由AI编程助手辅助生成。*

## 常见问题排查 (Troubleshooting)

如果在本地部署和运行项目时遇到问题，请尝试以下解决方案：

### 1. 登录提示“账号未激活”或首页不显示帖子

这两个问题通常都指向**数据库状态不正确**。

- **原因**: 数据库中的数据是旧的，或者没有正确导入项目提供的最新模拟数据。
- **解决方案**: 重新初始化数据库，确保使用的是最新的表结构和模拟数据。
  1.  在 MySQL 客户端中，彻底删除旧数据库:
      ```sql
      DROP DATABASE IF EXISTS campus_forum;
      ```
  2.  重新创建一个干净的数据库:
      ```sql
      CREATE DATABASE campus_forum;
      ```
  3.  切换到新创建的数据库:
      ```sql
      USE campus_forum;
      ```
  4.  依次导入项目 `docs/sql/` 目录下的两个 SQL 文件:
      -   首先导入 `schema.sql` 来创建所有数据表。
      -   然后导入 `mock_data.sql` 来填充用户、帖子等模拟数据。

完成以上步骤后，所有模拟账号（如 `user1`/`123456`）都将是**已激活**状态，首页也能正确加载帖子。

### 2. 注册/发帖/点赞等功能失败，后端报错

如果在进行某些操作时，前端提示“服务异常”，同时后端日志出现 `SQLSyntaxErrorException` 或 `Name for argument ... not specified` 等错误，通常是后端代码 Bug 导致的。

我们已经修复了以下两个关键问题：

- **SQL 语法错误**:
  - **问题**: `partition` 是 MySQL 的保留关键字，不能直接作为表名。
  - **修复**: 在 `forum-dal/src/main/resources/mapper/PartitionMapper.xml` 文件中，已将所有 `from partition` 或 `into partition` 修改为 ``from `partition` `` 和 ``into `partition` ``。

- **Spring MVC 参数绑定失败**:
  - **问题**: Controller 方法的参数缺少 `@RequestParam` 注解，导致 Spring MVC 无法将前端请求中的参数（如 `userId`）与方法参数对应起来。
  - **修复**: 已为 `PostController`, `LikeController`, `MessageController` 中所有需要从 URL 请求参数中获取值的方法，都添加了 `@RequestParam` 注解。

如果遇到类似问题，请确保你的后端代码包含了以上修复。

