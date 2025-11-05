<template>
  <div class="admin-dashboard">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="250px" class="admin-sidebar">
        <div class="admin-logo">
          <h2>管理员后台</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          @select="handleMenuSelect"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="overview">
            <el-icon><DataBoard /></el-icon>
            <span>概览</span>
          </el-menu-item>
          <el-menu-item index="posts">
            <el-icon><Document /></el-icon>
            <span>帖子管理</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="comments">
            <el-icon><ChatDotRound /></el-icon>
            <span>评论管理</span>
          </el-menu-item>
          <el-menu-item index="reports">
            <el-icon><Warning /></el-icon>
            <span>举报管理</span>
          </el-menu-item>
          <el-menu-item index="statistics">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="leaderboard">
            <el-icon><Trophy /></el-icon>
            <span>榜单管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="admin-header">
          <div class="header-left">
            <h3>{{ currentPageTitle }}</h3>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="admin-user">
                <el-avatar :src="user.avatarUrl" size="small"></el-avatar>
                <span class="username">{{ user.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile"
                    >个人资料</el-dropdown-item
                  >
                  <el-dropdown-item command="home">返回首页</el-dropdown-item>
                  <el-dropdown-item command="logout" divided
                    >退出登录</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主要内容 -->
        <el-main class="admin-main">
          <!-- 概览页面 -->
          <div v-if="activeMenu === 'overview'" class="overview-content">
            <el-row :gutter="20" class="stats-row">
              <el-col :span="6">
                <el-card class="stat-card">
                  <div class="stat-item">
                    <div class="stat-icon user-icon">
                      <el-icon><User /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-number">{{ statistics.totalUsers }}</div>
                      <div class="stat-label">总用户数</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card">
                  <div class="stat-item">
                    <div class="stat-icon post-icon">
                      <el-icon><Document /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-number">{{ statistics.totalPosts }}</div>
                      <div class="stat-label">总帖子数</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card">
                  <div class="stat-item">
                    <div class="stat-icon comment-icon">
                      <el-icon><ChatDotRound /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-number">
                        {{ statistics.totalComments }}
                      </div>
                      <div class="stat-label">总评论数</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card">
                  <div class="stat-item">
                    <div class="stat-icon report-icon">
                      <el-icon><Warning /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-number">
                        {{ statistics.pendingReports }}
                      </div>
                      <div class="stat-label">待处理举报</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="content-row">
              <el-col :span="12">
                <el-card>
                  <template #header>
                    <span>最新帖子</span>
                  </template>
                  <div class="recent-posts">
                    <div
                      v-for="post in recentPosts"
                      :key="post.id"
                      class="recent-item"
                    >
                      <div class="item-title">{{ post.title }}</div>
                      <div class="item-meta">
                        <span>{{ post.username }}</span>
                        <span>{{ formatDate(post.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <template #header>
                    <span>系统公告</span>
                  </template>
                  <div class="system-notices">
                    <el-alert
                      title="系统运行正常"
                      type="success"
                      :closable="false"
                      show-icon
                    ></el-alert>
                    <el-alert
                      title="定期数据备份已完成"
                      type="info"
                      :closable="false"
                      show-icon
                      style="margin-top: 10px"
                    ></el-alert>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <!-- 帖子管理页面 -->
          <PostManagement v-else-if="activeMenu === 'posts'" />

          <!-- 用户管理页面 -->
          <UserManagement v-else-if="activeMenu === 'users'" />

          <!-- 评论管理页面 -->
          <CommentManagement v-else-if="activeMenu === 'comments'" />

          <!-- 榜单管理页面 -->
          <LeaderboardManagement v-else-if="activeMenu === 'leaderboard'" />

          <!-- 其他页面内容占位符 -->
          <div v-else class="placeholder-content">
            <el-empty description="功能开发中...">
              <el-button type="primary" @click="activeMenu = 'overview'"
                >返回概览</el-button
              >
            </el-empty>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapState } from "vuex";
import axios from "axios";
import { ElMessage } from "element-plus";
import PostManagement from "@/components/admin/PostManagement.vue";
import UserManagement from "@/components/admin/UserManagement.vue";
import CommentManagement from "@/components/admin/CommentManagement.vue";
import LeaderboardManagement from "@/components/admin/LeaderboardManagement.vue";
import {
  DataBoard,
  Document,
  User,
  ChatDotRound,
  Warning,
  TrendCharts,
  Trophy,
  ArrowDown,
} from "@element-plus/icons-vue";

export default {
  name: "AdminDashboard",
  components: {
    PostManagement,
    UserManagement,
    CommentManagement,
    LeaderboardManagement,
    DataBoard,
    Document,
    User,
    ChatDotRound,
    Warning,
    TrendCharts,
    Trophy,
    ArrowDown,
  },
  data() {
    return {
      activeMenu: "overview",
      statistics: {
        totalUsers: 0,
        totalPosts: 0,
        totalComments: 0,
        pendingReports: 0,
      },
      recentPosts: [],
    };
  },
  computed: {
    ...mapState(["user"]),
    currentPageTitle() {
      const titles = {
        overview: "概览",
        posts: "帖子管理",
        users: "用户管理",
        comments: "评论管理",
        reports: "举报管理",
        statistics: "数据统计",
        leaderboard: "榜单管理",
      };
      return titles[this.activeMenu] || "管理员后台";
    },
  },
  created() {
    this.checkAdminPermission();
    this.loadStatistics();
    this.loadRecentPosts();

    // 处理URL参数，自动选择对应的菜单项
    const menu = this.$route.query.menu;
    if (
      menu &&
      [
        "overview",
        "posts",
        "users",
        "comments",
        "reports",
        "statistics",
        "leaderboard",
      ].includes(menu)
    ) {
      this.activeMenu = menu;
    }
  },
  methods: {
    checkAdminPermission() {
      if (!this.user || this.user.type !== 1) {
        ElMessage.error("您没有管理员权限！");
        this.$router.push("/");
        return;
      }
    },
    handleMenuSelect(index) {
      this.activeMenu = index;
    },
    handleCommand(command) {
      switch (command) {
        case "profile":
          this.$router.push("/profile/edit");
          break;
        case "home":
          this.$router.push("/");
          break;
        case "logout":
          this.$store.dispatch("logout");
          this.$router.push("/");
          break;
      }
    },
    async loadStatistics() {
      try {
        const response = await axios.get("/api/admin/statistics");
        if (response.data.code === 200) {
          this.statistics = response.data.data;
        } else {
          console.error("获取统计数据失败:", response.data.message);
          // 如果API调用失败，使用默认值
          this.statistics = {
            totalUsers: 0,
            totalPosts: 0,
            totalComments: 0,
            pendingReports: 0,
          };
        }
      } catch (error) {
        console.error("加载统计数据失败:", error);
        // 如果网络错误，使用默认值
        this.statistics = {
          totalUsers: 0,
          totalPosts: 0,
          totalComments: 0,
          pendingReports: 0,
        };
      }
    },
    async loadRecentPosts() {
      try {
        const response = await axios.get("/api/post/list", {
          params: { userId: 0 },
        });
        if (response.data.code === 200) {
          // 只显示最新的一篇帖子
          this.recentPosts = response.data.data.slice(0, 1);
        }
      } catch (error) {
        console.error("加载最新帖子失败:", error);
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString("zh-CN");
    },
  },
};
</script>

<style scoped>
.admin-dashboard {
  height: 100vh;
  background-color: #f0f2f5;
}

.admin-sidebar {
  background-color: #304156;
  height: 100vh;
}

.admin-logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #434a50;
}

.admin-logo h2 {
  color: #fff;
  margin: 0;
  font-size: 18px;
}

.admin-menu {
  border: none;
}

.admin-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left h3 {
  margin: 0;
  color: #303133;
}

.admin-user {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.admin-user:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #606266;
}

.admin-main {
  padding: 20px;
  background-color: #f0f2f5;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-item {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: #fff;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.post-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.comment-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.report-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.content-row {
  margin-top: 20px;
}

.recent-posts {
  max-height: 300px;
  overflow-y: auto;
}

.recent-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.recent-item:last-child {
  border-bottom: none;
}

.item-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
  cursor: pointer;
}

.item-title:hover {
  color: #409eff;
}

.item-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  justify-content: space-between;
}

.system-notices {
  padding: 10px 0;
}

.placeholder-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}
</style>
