<template>
  <div class="reset-password-container">
    <el-card class="reset-password-card">
      <template #header>
        <div class="card-header">
          <span>重置密码</span>
        </div>
      </template>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
        v-if="!resetSuccess"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="loading"
            style="width: 100%"
          >
            重置密码
          </el-button>
        </el-form-item>
      </el-form>

      <div v-else class="success-message">
        <el-result icon="success" title="密码重置成功！">
          <template #extra>
            <el-button type="primary" @click="goToLogin"> 前往登录 </el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { ElMessage } from "element-plus";

export default {
  name: "ResetPassword",
  setup() {
    const route = useRoute();
    const router = useRouter();
    const formRef = ref(null);
    const loading = ref(false);
    const resetSuccess = ref(false);
    const token = ref("");

    const form = ref({
      newPassword: "",
      confirmPassword: "",
    });

    const validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入新密码"));
      } else if (value.length < 6) {
        callback(new Error("密码长度不能少于6位"));
      } else {
        if (form.value.confirmPassword !== "") {
          formRef.value.validateField("confirmPassword");
        }
        callback();
      }
    };

    const validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== form.value.newPassword) {
        callback(new Error("两次输入密码不一致"));
      } else {
        callback();
      }
    };

    const rules = {
      newPassword: [{ validator: validatePass, trigger: "blur" }],
      confirmPassword: [{ validator: validatePass2, trigger: "blur" }],
    };

    onMounted(() => {
      // 从 URL 获取 token
      token.value = route.query.token || "";
      if (!token.value) {
        ElMessage.error("重置链接无效");
        setTimeout(() => {
          router.push("/");
        }, 2000);
      }
    });

    const handleSubmit = async () => {
      if (!formRef.value) return;

      await formRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            const response = await axios.post("/api/user/reset-password", {
              token: token.value,
              newPassword: form.value.newPassword,
            });

            if (response.data.code === 200) {
              ElMessage.success("密码重置成功！");
              resetSuccess.value = true;
            } else {
              ElMessage.error(response.data.message || "重置失败");
            }
          } catch (error) {
            console.error("Reset password error:", error);
            ElMessage.error("重置失败，请稍后再试");
          } finally {
            loading.value = false;
          }
        }
      });
    };

    const goToLogin = () => {
      router.push("/");
    };

    return {
      formRef,
      loading,
      resetSuccess,
      form,
      rules,
      handleSubmit,
      goToLogin,
    };
  },
};
</script>

<style scoped>
.reset-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.reset-password-card {
  width: 450px;
}

.card-header {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
}

.success-message {
  text-align: center;
}
</style>
