<template>
  <div class="page page-auth">
    <el-card class="card">
      <h2>注册</h2>
      <el-form :model="form" label-width="80px" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">注册</el-button>
          <el-button link @click="$router.push('/login')"
            >已有账号？去登录</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage } from "element-plus";
export default {
  name: "Register",
  data() {
    return { form: { username: "", email: "", password: "" } };
  },
  methods: {
    async onSubmit() {
      if (!this.form.username || !this.form.email || !this.form.password) {
        ElMessage.warning("请完整填写");
        return;
      }
      const { data } = await axios.post("/api/user/register", this.form);
      if (data.code === 200) {
        ElMessage.success("注册成功");
        this.$router.push("/login");
      } else {
        ElMessage.error(data.msg || "注册失败");
      }
    },
  },
};
</script>

<style scoped>
.page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}
.card {
  width: 480px;
}
h2 {
  margin-bottom: 16px;
}
</style>
