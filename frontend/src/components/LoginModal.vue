<template>
  <div>
    <div class="modal-backdrop">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">登录</h5>
            <el-button type="text" class="close-button" @click="$emit('close')"
              >&times;</el-button
            >
          </div>
          <div class="modal-body">
            <el-form @submit.prevent="handleLogin" label-position="top">
              <el-form-item label="用户名">
                <el-input v-model="username" placeholder="请输入用户名" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input
                  v-model="password"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  native-type="submit"
                  style="width: 100%"
                  >登录</el-button
                >
              </el-form-item>
              <el-form-item style="text-align: center">
                <el-link type="primary" @click="showForgotPassword"
                  >忘记密码？</el-link
                >
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <ForgotPasswordModal
      :visible="forgotPasswordVisible"
      @close="forgotPasswordVisible = false"
    />
  </div>
</template>

<script>
import { ElMessage } from "element-plus";
import { mapActions } from "vuex";
import ForgotPasswordModal from "./ForgotPasswordModal.vue";

export default {
  name: "LoginModal",
  components: {
    ForgotPasswordModal,
  },
  data() {
    return {
      username: "",
      password: "",
      forgotPasswordVisible: false,
    };
  },
  methods: {
    ...mapActions(["login"]),
    showForgotPassword() {
      this.forgotPasswordVisible = true;
    },
    async handleLogin() {
      if (!this.username || !this.password) {
        ElMessage.error("用户名和密码不能为空！");
        return;
      }
      const success = await this.login({
        username: this.username,
        password: this.password,
      });
      if (success) {
        this.$emit("close");
        // 刷新页面以确保状态更新
        window.location.reload();
      }
    },
  },
};
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}
.modal-dialog {
  width: 400px;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
}
.modal-content {
  display: flex;
  flex-direction: column;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e9ecef;
}
.modal-title {
  font-size: 1.25rem;
  margin: 0;
}
.close-button {
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1;
  color: #000;
  text-shadow: 0 1px 0 #fff;
  opacity: 0.5;
  padding: 0;
}
.close-button:hover {
  opacity: 0.75;
}
.modal-body {
  padding: 20px;
}
</style>
