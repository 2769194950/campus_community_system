<template>
  <div class="header">
    <router-link class="logo" to="/">Campus Forum</router-link>

    <!-- 普通用户导航 -->
    <nav v-if="!isAdmin" class="nav">
      <router-link to="/">首页</router-link>
      <router-link to="/leaderboard">排行榜</router-link>
      <!-- 只有登录用户才能访问个人主页和消息中心 -->
      <router-link v-if="user" :to="`/profile/${user?.id || ''}`">个人主页</router-link>
      <router-link v-if="user" to="/messages" class="messages-link">
        消息中心
        <el-badge 
          v-if="unreadCount > 0" 
          :value="unreadCount" 
          :max="99" 
          class="unread-badge"
          type="danger"
        />
      </router-link>
    </nav>

    <!-- 管理员导航 -->
    <nav v-else class="nav">
      <router-link to="/admin">管理后台</router-link>
    </nav>

    <div class="right">
      <!-- 未登录时显示登录/注册按钮 -->
      <div v-if="!user">
        <el-button 
          type="primary" 
          size="small" 
          @click="$emit('show-login-modal')"
        >
          登录
        </el-button>
        <el-button 
          type="success" 
          size="small" 
          @click="$emit('show-register-modal')"
        >
          注册
        </el-button>
      </div>

      <!-- 登录后欢迎语和用户菜单 -->
      <template v-else>
        <span class="welcome">欢迎，{{ user.username }}</span>
        <el-dropdown trigger="click">
          <span class="user">
            <el-avatar size="small" style="margin-right:6px">
              {{ user.username[0] }}
            </el-avatar>
            {{ user.username }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="!isAdmin" @click="$router.push(`/profile/${user.id}`)">
                个人主页
              </el-dropdown-item>
              <el-dropdown-item divided @click="onLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import axios from "axios";

export default {
  name: "Header",
  data() {
    return {
      unreadCount: 0,
      unreadPolling: null
    };
  },
  computed: {
    ...mapGetters(["user", "isAdmin"]),
  },
  watch: {
    user: {
      handler(newUser) {
        if (newUser) {
          this.startUnreadPolling();
        } else {
          this.stopUnreadPolling();
        }
      },
      immediate: true
    }
  },
  beforeUnmount() {
    this.stopUnreadPolling();
  },
  methods: {
    async onLogout() {
      const ok = await this.$store.dispatch("logout");
      if (ok) this.$router.replace("/login");
    },
    
    startUnreadPolling() {
      // 立即获取一次未读消息数
      this.fetchUnreadCount();
      
      // 每隔5秒轮询一次未读消息数
      this.unreadPolling = setInterval(() => {
        this.fetchUnreadCount();
      }, 5000);
    },
    
    stopUnreadPolling() {
      if (this.unreadPolling) {
        clearInterval(this.unreadPolling);
        this.unreadPolling = null;
      }
    },
    
    async fetchUnreadCount() {
      try {
        const response = await axios.get("/api/message/unread-count");
        if (response.data && response.data.code === 200) {
          this.unreadCount = response.data.data || 0;
        }
      } catch (error) {
        console.error("获取未读消息数失败:", error);
      }
    }
  },
};
</script>

<style scoped>
.header {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.logo {
  font-weight: 700;
  color: #409eff;
  text-decoration: none;
  margin-right: 24px;
}
.nav a {
  margin-right: 16px;
  text-decoration: none;
  color: #303133;
  position: relative;
}
.nav a:hover {
  color: #409eff;
}

.right {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  gap: 12px;
}
.user {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}
.welcome {
  color: #606266;
  font-size: 14px;
}

.messages-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.unread-badge {
  position: relative;
  top: -8px;
  right: -4px;
}
</style>