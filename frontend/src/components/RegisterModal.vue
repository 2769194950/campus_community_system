<template>
  <div class="modal-backdrop">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">注册</h5>
          <el-button type="text" class="close-button" @click="$emit('close')"
            >&times;</el-button
          >
        </div>
        <div class="modal-body">
          <el-form @submit.prevent="register" label-position="top">
            <el-form-item label="用户名">
              <el-input v-model="username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="email" placeholder="请输入邮箱" type="email" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="password"
                type="password"
                placeholder="请输入密码（至少6位）"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input
                v-model="confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                show-password
              />
            </el-form-item>

            <el-divider content-position="left">设置密保问题（用于找回密码）</el-divider>

            <el-form-item label="密保问题">
              <el-input
                v-model="securityQuestion"
                placeholder="例如：您母亲的姓名是？"
              />
            </el-form-item>
            <el-form-item label="答案">
              <el-input
                v-model="securityAnswer"
                placeholder="请输入答案"
                type="password"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" native-type="submit" style="width: 100%"
                >注册</el-button
              >
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage } from "element-plus";

export default {
  name: "RegisterModal",
  data() {
    return {
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
      securityQuestion: "",
      securityAnswer: "",
    };
  },
  methods: {
    async register() {
      // 验证
      if (!this.username || !this.password || !this.email) {
        ElMessage.error("用户名、邮箱和密码不能为空！");
        return;
      }
      
      // 验证邮箱格式
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.email)) {
        ElMessage.error("请输入正确的邮箱格式！");
        return;
      }
      
      // 验证密码长度
      if (this.password.length < 6) {
        ElMessage.error("密码长度不能少于6位！");
        return;
      }
      
      // 验证两次密码是否一致
      if (this.password !== this.confirmPassword) {
        ElMessage.error("两次输入的密码不一致！");
        return;
      }

      // 验证密保问题
      if (!this.securityQuestion || !this.securityAnswer) {
        ElMessage.error("请填写密保问题和答案！");
        return;
      }
      
      try {
        // 先注册用户
        const registerResponse = await axios.post("/api/user/register", {
          username: this.username,
          email: this.email,
          password: this.password,
        });
        
        if (registerResponse.data.code === 200) {
          // 注册成功后，自动登录并设置密保问题
          const loginResponse = await axios.post("/api/user/login", {
            username: this.username,
            password: this.password,
          });

          if (loginResponse.data.code === 200) {
            const token = loginResponse.data.data.ticket;
            
            // 设置密保问题
            await axios.post(
              "/api/user/security-question",
              {
                question: this.securityQuestion,
                answer: this.securityAnswer,
              },
              {
                headers: {
                  Authorization: token,
                },
              }
            );

            ElMessage.success("注册成功！密保问题已设置");
            this.$emit("close");
            
            // 刷新页面以更新登录状态
            setTimeout(() => {
              window.location.reload();
            }, 1000);
          }
        } else {
          ElMessage.error(registerResponse.data.message || "注册失败，请稍后再试。");
        }
      } catch (error) {
        console.error("注册请求失败:", error);
        ElMessage.error("注册服务异常，请联系管理员。");
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
