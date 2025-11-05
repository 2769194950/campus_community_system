<template>
  <div class="profile-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>编辑个人资料</h2>
        </div>
      </template>

      <el-form
        ref="profileForm"
        :model="profileForm"
        :rules="rules"
        label-width="100px"
        class="profile-form"
      >
        <!-- 头像 -->
        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-avatar
              :size="100"
              :src="avatarPreview || profileForm.avatarUrl"
            ></el-avatar>
            <div class="avatar-actions">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :auto-upload="false"
                :on-change="handleAvatarChange"
                accept="image/*"
              >
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  选择本地图片
                </el-button>
              </el-upload>
              <el-button @click="useDefaultAvatar" style="margin-top: 10px">
                使用默认头像
              </el-button>
              <el-text type="info" size="small" style="margin-top: 5px">
                提示：支持JPG、PNG格式，建议不超过2MB
              </el-text>
            </div>
          </div>
        </el-form-item>

        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="profileForm.email"
            placeholder="请输入邮箱地址"
            type="email"
          />
        </el-form-item>

        <!-- 个人简介 -->
        <el-form-item label="个人简介" prop="introduction">
          <el-input
            v-model="profileForm.introduction"
            type="textarea"
            :rows="4"
            placeholder="介绍一下自己吧..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <!-- 按钮组 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            保存修改
          </el-button>
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="danger" plain @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 密保问题设置 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <h2>密保问题设置</h2>
          <el-text type="info" size="small">用于找回密码，请妥善保管</el-text>
        </div>
      </template>

      <el-form
        ref="securityForm"
        :model="securityForm"
        label-width="120px"
        class="security-form"
      >
        <el-form-item label="密保问题" required>
          <el-input
            v-model="securityForm.question"
            placeholder="例如：您母亲的姓名是？"
          />
        </el-form-item>
        <el-form-item label="答案" required>
          <el-input
            v-model="securityForm.answer"
            placeholder="请输入答案"
            type="password"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="submitSecurityQuestion"
            :loading="securityLoading"
          >
            保存密保问题
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { mapState } from "vuex";
import axios from "axios";
import { ElMessage } from "element-plus";
import { Upload } from "@element-plus/icons-vue";

export default {
  name: "ProfileEdit",
  components: {
    Upload,
  },
  data() {
    return {
      loading: false,
      securityLoading: false,
      avatarPreview: "", // 头像预览URL
      avatarFile: null, // 选中的图片文件
      profileForm: {
        username: "",
        email: "",
        avatarUrl: "",
        introduction: "",
      },
      originalData: {}, // 保存原始数据用于重置
      securityForm: {
        question: "",
        answer: "",
      },
      rules: {
        email: [
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: "blur",
          },
        ],
        introduction: [
          {
            max: 200,
            message: "个人简介不能超过200个字符",
            trigger: "blur",
          },
        ],
      },
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  created() {
    if (!this.user) {
      ElMessage.error("请先登录");
      this.$router.push("/");
      return;
    }
    this.loadUserProfile();
  },
  methods: {
    // 加载用户资料
    loadUserProfile() {
      axios
        .get(`/api/user/profile/${this.user.id}`)
        .then((response) => {
          if (response.data.code === 200) {
            const userData = response.data.data;
            this.profileForm = {
              username: userData.username || "",
              email: userData.email || "",
              avatarUrl: userData.avatarUrl || "",
              introduction:
                userData.introduction || "这个人很懒，什么都没有留下...",
            };
            // 保存原始数据
            this.originalData = { ...this.profileForm };
          }
        })
        .catch((error) => {
          console.error("获取用户信息失败:", error);
          ElMessage.error("获取用户信息失败");
        });
    },

    // 处理图片选择
    handleAvatarChange(file) {
      // 验证文件类型
      const isImage = file.raw.type.startsWith("image/");
      if (!isImage) {
        ElMessage.error("只能上传图片文件！");
        return;
      }

      // 验证文件大小（2MB）
      const isLt2M = file.raw.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        ElMessage.error("图片大小不能超过 2MB!");
        return;
      }

      // 保存文件对象
      this.avatarFile = file.raw;

      // 创建预览URL
      const reader = new FileReader();
      reader.onload = (e) => {
        this.avatarPreview = e.target.result;
      };
      reader.readAsDataURL(file.raw);
    },

    // 使用默认头像
    useDefaultAvatar() {
      const randomNum = Math.floor(Math.random() * 1000);
      this.avatarPreview = `http://images.nowcoder.com/head/${randomNum}t.png`;
      this.avatarFile = null; // 清除文件对象
    },

    // 提交表单
    submitForm() {
      this.$refs.profileForm.validate(async (valid) => {
        if (!valid) {
          return false;
        }

        this.loading = true;
        try {
          let newAvatarUrl = this.profileForm.avatarUrl;

          // 如果有新的头像文件或预览URL，使用它
          if (this.avatarPreview) {
            newAvatarUrl = this.avatarPreview;
          }

          console.log("开始更新用户资料...");
          console.log("原始数据:", this.originalData);
          console.log("新数据:", {
            introduction: this.profileForm.introduction,
            avatarUrl: newAvatarUrl,
            email: this.profileForm.email,
          });

          // 更新个人简介
          if (
            this.profileForm.introduction !== this.originalData.introduction
          ) {
            console.log("更新个人简介:", this.profileForm.introduction);
            const res1 = await axios.put("/api/user/profile", {
              introduction: this.profileForm.introduction,
            });
            console.log("简介更新结果:", res1.data);
          }

          // 更新头像
          if (newAvatarUrl !== this.originalData.avatarUrl) {
            console.log("更新头像:", newAvatarUrl);
            const res2 = await axios.put("/api/user/avatar", {
              avatarUrl: newAvatarUrl,
            });
            console.log("头像更新结果:", res2.data);
          }

          // 更新邮箱
          if (this.profileForm.email !== this.originalData.email) {
            console.log("更新邮箱:", this.profileForm.email);
            const res3 = await axios.put("/api/user/email", {
              email: this.profileForm.email,
            });
            console.log("邮箱更新结果:", res3.data);
          }

          // 更新用户名
          if (this.profileForm.username !== this.originalData.username) {
            console.log("更新用户名:", this.profileForm.username);
            const res4 = await axios.put("/api/user/username", {
              username: this.profileForm.username,
            });
            console.log("用户名更新结果:", res4.data);
          }

          console.log("所有更新完成");
          ElMessage.success("个人资料更新成功！");

          // 更新Vuex中的用户信息
          this.$store.commit("setUser", {
            ...this.user,
            username: this.profileForm.username,
            avatarUrl: newAvatarUrl,
            introduction: this.profileForm.introduction,
            email: this.profileForm.email,
          });

          // 跳转回个人主页
          setTimeout(() => {
            this.$router.push(`/profile/${this.user.id}`);
          }, 1000);
        } catch (error) {
          console.error("更新失败:", error);
          ElMessage.error(
            error.response?.data?.message || "更新失败，请稍后再试"
          );
        } finally {
          this.loading = false;
        }
      });
    },

    // 取消编辑
    cancelEdit() {
      this.$router.back();
    },

    // 重置表单
    resetForm() {
      this.profileForm = { ...this.originalData };
      ElMessage.info("已重置为原始数据");
    },

    // 提交密保问题
    async submitSecurityQuestion() {
      // 验证表单
      if (!this.securityForm.question || !this.securityForm.answer) {
        ElMessage.warning("请填写密保问题和答案！");
        return;
      }

      this.securityLoading = true;
      try {
        const response = await axios.post("/api/user/security-question", {
          question: this.securityForm.question,
          answer: this.securityForm.answer,
        });

        if (response.data.code === 200) {
          ElMessage.success("密保问题设置成功！");
          // 清空表单
          this.securityForm = {
            question: "",
            answer: "",
          };
        } else {
          ElMessage.error(response.data.message || "设置失败，请稍后再试");
        }
      } catch (error) {
        console.error("设置密保问题失败:", error);
        ElMessage.error("设置失败，请稍后再试");
      } finally {
        this.securityLoading = false;
      }
    },
  },
};
</script>

<style scoped>
.profile-edit {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
}

.profile-form {
  margin-top: 20px;
}

.avatar-upload {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.avatar-actions {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>
