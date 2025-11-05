<template>
  <div id="app">
    <Header
      @show-login-modal="showLoginModal = true"
      @show-register-modal="showRegisterModal = true"
    />
    <main class="main-content">
      <el-container>
        <router-view />
      </el-container>
    </main>
    <el-footer class="footer">
      © 2025 Campus Forum. All Rights Reserved.
    </el-footer>

    <LoginModal v-if="showLoginModal" @close="showLoginModal = false" />
    <RegisterModal
      v-if="showRegisterModal"
      @close="showRegisterModal = false"
    />
  </div>
</template>

<script>
import Header from "./components/Header.vue";
import LoginModal from "./components/LoginModal.vue";
import RegisterModal from "./components/RegisterModal.vue";
import { mapActions } from "vuex";

export default {
  name: "App",
  components: {
    Header,
    LoginModal,
    RegisterModal,
  },
  data() {
    return {
      showLoginModal: false,
      showRegisterModal: false,
    };
  },
  created() {
    // 初始化用户认证状态
    this.initAuth();
  },
  methods: {
    ...mapActions(["initAuth"]),
  },
};
</script>

<style>
/* Global Styles */
body {
  margin: 0;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #303133;
  background-color: #f4f5f7;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.el-container {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.footer {
  text-align: center;
  color: #909399;
  padding: 20px 0;
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
}
</style>
