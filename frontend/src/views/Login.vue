<template>
  <div class="login-wrap">
    <div class="brand">
      <span class="logo">Campus Forum</span>
      <span class="slogan">让校园交流更简单</span>
    </div>

    <el-card class="box" :shadow="'hover'">
      <div class="box-title">
        <el-icon class="title-icon"><UserFilled/></el-icon>
        <div>
          <h2>登录</h2>
          <p class="subtitle">欢迎回来，请输入账号密码</p>
        </div>
      </div>

      <el-form :model="form" ref="formRef" :rules="rules" label-width="78px" @keyup.enter="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" maxlength="32" clearable placeholder="请输入用户名">
            <template #prefix><el-icon><User/></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password maxlength="64" placeholder="请输入密码">
            <template #prefix><el-icon><Lock/></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="remember">记住我</el-checkbox>
          <div class="flex-spacer"></div>
          <el-button link type="primary" @click="$router.push('/register')">去注册</el-button>
        </el-form-item>

        <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="loading"
            @click="onSubmit"
        >
          登录
        </el-button>
      </el-form>
    </el-card>

    <footer class="footer">© {{year}} Campus Forum</footer>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { User, Lock, UserFilled } from "@element-plus/icons-vue";

export default {
  name: "Login",
  components: { User, Lock, UserFilled },
  data() {
    return {
      form: { username: "", password: "" },
      remember: true,
      loading: false,
      year: new Date().getFullYear(),
      rules: {
        username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      },
    };
  },
  computed: {
    ...mapGetters(["isAdmin"]),
  },
  mounted() {
    // 如果本地记住了用户名，自动回填
    const cached = localStorage.getItem("last_username");
    if (cached) this.form.username = cached;
  },
  methods: {
    async onSubmit() {
      if (this.loading) return;
      this.$refs.formRef.validate(async (ok) => {
        if (!ok) return;
        this.loading = true;
        const okLogin = await this.$store.dispatch("login", this.form);
        this.loading = false;

        if (!okLogin) return;

        if (this.remember) localStorage.setItem("last_username", this.form.username);
        else localStorage.removeItem("last_username");

        // 登录后跳转：管理员 → 后台；普通用户 → 首页
        if (this.$store.getters.isAdmin) this.$router.replace("/admin");
        else this.$router.replace("/");
      });
    },
  },
};
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: grid;
  grid-template-rows: auto 1fr auto;
  background: radial-gradient(1200px 600px at 20% -20%, #eaf3ff 0%, #ffffff 50%, #ffffff 100%),
  linear-gradient(135deg, #f5f7ff 0%, #ffffff 60%);
}

.brand {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 22px 24px 0 24px;
  user-select: none;
}
.logo {
  font-weight: 800;
  font-size: 22px;
  color: #409eff;
}
.slogan {
  color: #909399;
  font-size: 13px;
}

.box {
  width: 420px;
  margin: 6vh auto 0 auto;
  border-radius: 16px;
  --el-card-padding: 24px;
}
.box-title {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 8px;
}
.title-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: inline-grid;
  place-items: center;
  color: #fff;
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
}
h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}
.subtitle {
  margin: 4px 0 0 0;
  color: #909399;
  font-size: 13px;
}

.flex-spacer {
  flex: 1;
}
.submit-btn {
  width: 100%;
  margin-top: 4px;
  height: 40px;
  font-weight: 600;
  letter-spacing: 1px;
}

.footer {
  text-align: center;
  padding: 18px 0 24px;
  color: #b1b5bd;
  font-size: 12px;
}
</style>
