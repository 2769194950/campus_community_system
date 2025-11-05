<template>
  <el-dialog
    v-model="dialogVisible"
    title="忘记密码 - 密保问题找回"
    width="450px"
    @close="handleClose"
  >
    <el-form :model="securityForm" ref="securityFormRef" label-width="100px">
      <el-form-item label="用户名">
        <el-input
          v-model="securityForm.username"
          placeholder="请输入您的用户名"
        >
          <template #append>
            <el-button
              @click="fetchSecurityQuestion"
              :loading="fetchingQuestion"
              >获取密保问题</el-button
            >
          </template>
        </el-input>
      </el-form-item>

      <!-- 显示密保问题 -->
      <div v-if="question">
        <el-divider />
        <el-form-item :label="question" required>
          <el-input v-model="securityForm.answer" placeholder="请输入答案" />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input
            v-model="securityForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码（至少6位）"
          />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input
            v-model="securityForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleSecuritySubmit"
            :loading="securityLoading"
            style="width: 100%"
          >
            重置密码
          </el-button>
        </el-form-item>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
import { ref } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";

export default {
  name: "ForgotPasswordModal",
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  emits: ["update:visible", "close"],
  setup(props, { emit }) {
    const dialogVisible = ref(props.visible);

    // 密保问题找回
    const securityFormRef = ref(null);
    const securityLoading = ref(false);
    const fetchingQuestion = ref(false);
    const securityForm = ref({
      username: "",
      answer: "",
      newPassword: "",
      confirmPassword: "",
    });
    const question = ref("");

    const handleClose = () => {
      emit("update:visible", false);
      emit("close");
      // 重置表单
      securityForm.value = {
        username: "",
        answer: "",
        newPassword: "",
        confirmPassword: "",
      };
      question.value = "";
    };

    // 获取密保问题
    const fetchSecurityQuestion = async () => {
      if (!securityForm.value.username) {
        ElMessage.warning("请输入用户名");
        return;
      }

      fetchingQuestion.value = true;
      try {
        const response = await axios.get("/api/user/security-question", {
          params: { username: securityForm.value.username },
        });

        if (response.data.code === 200) {
          question.value = response.data.data.question;
          ElMessage.success("已获取密保问题");
        } else {
          ElMessage.error(response.data.message || "获取失败");
        }
      } catch (error) {
        console.error("Fetch security question error:", error);
        ElMessage.error("获取失败，请稍后再试");
      } finally {
        fetchingQuestion.value = false;
      }
    };

    // 密保问题找回提交
    const handleSecuritySubmit = async () => {
      // 验证表单
      if (!securityForm.value.answer) {
        ElMessage.warning("请回答密保问题");
        return;
      }
      if (!securityForm.value.newPassword) {
        ElMessage.warning("请输入新密码");
        return;
      }
      if (securityForm.value.newPassword.length < 6) {
        ElMessage.warning("密码长度不能少于6位");
        return;
      }
      if (
        securityForm.value.newPassword !== securityForm.value.confirmPassword
      ) {
        ElMessage.warning("两次输入的密码不一致");
        return;
      }

      securityLoading.value = true;
      try {
        const response = await axios.post(
          "/api/user/reset-password-by-security",
          {
            username: securityForm.value.username,
            answer: securityForm.value.answer,
            newPassword: securityForm.value.newPassword,
          }
        );

        if (response.data.code === 200) {
          ElMessage.success("密码重置成功！请重新登录");
          handleClose();
        } else {
          ElMessage.error(response.data.message || "重置失败");
        }
      } catch (error) {
        console.error("Reset password by security error:", error);
        ElMessage.error("重置失败，请稍后再试");
      } finally {
        securityLoading.value = false;
      }
    };

    return {
      dialogVisible,
      securityFormRef,
      securityLoading,
      fetchingQuestion,
      securityForm,
      question,
      handleClose,
      fetchSecurityQuestion,
      handleSecuritySubmit,
    };
  },
  watch: {
    visible(newVal) {
      this.dialogVisible = newVal;
    },
  },
};
</script>

<style scoped>
.el-divider {
  margin: 20px 0;
}
</style>
