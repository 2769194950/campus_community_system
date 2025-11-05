<template>
  <div class="header-container">
    <div class="logo">Campus Forum</div>

    <!-- 登录后显示头像和菜单 (在logo右边) -->
    <div v-if="user" class="user-menu-container">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-menu-trigger">
          <el-avatar :size="32" :src="user.avatarUrl"></el-avatar>
          <span class="username">{{ user.username }}</span>
          <el-icon class="el-icon--right"><arrow-down /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="messages">
              <el-badge :value="unreadCount" :hidden="unreadCount === 0">
                消息中心
              </el-badge>
            </el-dropdown-item>
            <el-dropdown-item command="profile">个人主页</el-dropdown-item>
            <el-dropdown-item v-if="user.type === 1" command="leaderboard">
              榜单管理
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div class="nav-item" :class="{ active: activeIndex === '/' }" @click="navigateTo('/')">
      首页
    </div>

    <div class="search-container">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索帖子..."
        @keyup.enter="handleSearch"
        clearable
      >
        <template #suffix>
          <el-icon @click="handleSearch" style="cursor: pointer">
            <Search />
          </el-icon>
        </template>
      </el-input>
    </div>

    <div class="flex-grow" />

    <!-- 未登录时右边直接显示登录/注册按钮 -->
    <template v-if="!user">
      <el-button type="primary" @click="$emit('show-login-modal')" class="auth-button">
        登录
      </el-button>
      <el-button @click="$emit('show-register-modal')" class="auth-button">
        注册
      </el-button>
    </template>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import axios from "axios";
import { Search, ArrowDown } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";

export default {
  name: "Header",
  components: {
    Search,
    ArrowDown,
  },
  data() {
    return {
      activeIndex: this.$route.path,
      unreadCount: 0,
      searchKeyword: "",
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  watch: {
    $route(to) {
      this.activeIndex = to.path;
    },
    user(newUser) {
      if (newUser) {
        this.fetchUnreadCount();
      }
    },
  },
  created() {
    if (this.user) {
      this.fetchUnreadCount();
    }
  },
  methods: {
    ...mapActions(["logout"]),
    navigateTo(path) {
      this.$router.push(path);
    },
    handleCommand(command) {
      switch (command) {
        case "messages":
          this.$router.push("/messages");
          break;
        case "profile":
          this.$router.push(`/profile/${this.user.id}`);
          break;
        case "leaderboard":
          this.$router.push("/leaderboard");
          break;
        case "logout":
          this.handleLogout();
          break;
      }
    },
    async handleLogout() {
      try {
        await ElMessageBox.confirm("确定要退出登录吗？", "退出确认", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        
        const success = await this.logout();
        if (success) {
          window.location.href = "/";
        }
      } catch (error) {
        if (error === "cancel") {
          return;
        }
      }
    },
    fetchUnreadCount() {
      axios.get("/api/message/unread-count").then((response) => {
        if (response.data.code === 200) {
          this.unreadCount = response.data.data;
        }
      });
    },
    handleSearch() {
      if (this.searchKeyword.trim()) {
        this.$router.push(
          `/search?keyword=${encodeURIComponent(this.searchKeyword.trim())}`
        );
        this.searchKeyword = "";
      }
    },
  },
};
</script>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  gap: 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
}

.user-menu-container {
  display: flex;
  align-items: center;
}

.user-menu-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-menu-trigger:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
}

.nav-item {
  padding: 0 15px;
  height: 60px;
  line-height: 60px;
  cursor: pointer;
  color: #303133;
  font-size: 14px;
  transition: color 0.3s, border-bottom 0.3s;
  border-bottom: 2px solid transparent;
}

.nav-item:hover {
  color: #409eff;
}

.nav-item.active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
}

.search-container {
  width: 300px;
}

.flex-grow {
  flex-grow: 1;
}

.auth-button {
  margin-left: 0;
}

.auth-button + .auth-button {
  margin-left: 10px;
}
</style>
