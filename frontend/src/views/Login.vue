<template>
  <div class="login-page" :style="bgStyle">
    <!-- 半透明遮罩（可选） -->
    <div class="bg-overlay"></div>

    <div class="login-wrap">
      <div class="brand">
        <h1>Campus Forum</h1>
        <span>让校园交流更简单</span>
      </div>

      <el-card class="login-card" shadow="always">
        <div class="login-title">
          <el-icon><User /></el-icon>
          <span>登录</span>
        </div>

        <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="70px"
            @keyup.enter.native="onSubmit"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model.trim="form.username" placeholder="请输入用户名" clearable />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
            />
          </el-form-item>

          <div class="login-extras">
            <el-checkbox v-model="remember">记住我</el-checkbox>
            <router-link class="link" to="/register">去注册</router-link>
          </div>

          <el-button
              class="login-btn"
              type="primary"
              :loading="loading"
              size="large"
              @click="onSubmit"
          >
            登录
          </el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
/**
 * 背景图方式：在 <script> 里引入并绑定 style，避免 url 解析问题
 * 图片路径：src/assets/登录页面.jpg
 */
import bg from '@/assets/登录页面.jpg'
import { User } from '@element-plus/icons-vue'

export default {
  name: 'Login',
  components: { User },
  data () {
    return {
      // 背景图样式绑定
      bgStyle: {
        background: `url(${bg}) center center / cover no-repeat fixed`
      },
      form: {
        username: '',
        password: ''
      },
      remember: true,
      loading: false,
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onSubmit () {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        try {
          this.loading = true
          //  使用 Vuex 的登录 action，写入 user 与 token（与小框登录一致）
          const ok = await this.$store.dispatch('login', {
            username: this.form.username,
            password: this.form.password,
            remember: this.remember
          })

          if (ok) {
            this.$message.success('登录成功')
            // 跳转到首页或重定向目标
            const redirect = this.$route.query.redirect || '/'
            this.$router.replace(redirect)
          } else {
            this.$message.error('用户名或密码错误')
          }
        } catch (err) {
          console.error(err)
          this.$message.error('网络异常，请稍后再试')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
/* 整体背景容器 */
.login-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  overflow: auto;
}

/* 半透明遮罩（可选） */
.bg-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(1px);
}

/* 内容区居中 */
.login-wrap {
  position: relative;
  z-index: 1; /* 盖住遮罩 */
  max-width: 1080px;
  margin: 0 auto;
  padding: 80px 16px 48px;
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 32px;
  align-items: start;
  min-height: 100%;
}

/* 左侧品牌区 */
.brand {
  color: #fff;
  text-shadow: 0 1px 2px rgba(0,0,0,.25);
  padding: 24px 8px;
}
.brand h1 {
  margin: 0 0 8px;
  font-size: 40px;
  line-height: 1.2;
  font-weight: 800;
}
.brand span {
  font-size: 16px;
  opacity: .9;
}

/* 登录卡片 */
.login-card {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,.12);
  background: rgba(255,255,255,.9);
  backdrop-filter: saturate(1.3) blur(3px);
}

/* 标题 */
.login-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1f2f56;
}

/* 附加项（记住我/去注册） */
.login-extras {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 6px 0 18px;
}
.login-extras .link {
  color: #409eff;
  text-decoration: none;
}
.login-extras .link:hover {
  text-decoration: underline;
}

/* 按钮 */
.login-btn {
  width: 100%;
}

/* 响应式 */
@media (max-width: 1024px) {
  .login-wrap {
    grid-template-columns: 1fr;
  }
  .brand {
    text-align: center;
    padding: 0;
  }
  .brand h1 {
    font-size: 32px;
  }
}
</style>