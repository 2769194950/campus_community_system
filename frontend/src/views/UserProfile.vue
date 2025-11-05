<template>
  <div class="user-profile">
    <el-card>
      <div class="profile-header">
        <el-avatar :size="100" :src="profileUser.avatarUrl"></el-avatar>
        <div class="profile-info">
          <h2>{{ profileUser.username }}</h2>
          <p class="introduction">{{ profileUser.introduction || '这个人很懒，什么都没有留下...' }}</p>
          <div class="user-stats">
            <span v-if="profileUser.email">
              <el-icon><Message /></el-icon> {{ profileUser.email }}
            </span>
          </div>
        </div>
        <div class="profile-actions" v-if="user && user.id === profileUser.id">
          <el-button type="primary" @click="editProfile">
            <el-icon><Edit /></el-icon>
            编辑个人资料
          </el-button>
        </div>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="我发布的帖子" name="posts">
        <PostList v-if="activeTab === 'posts'" :user-id="profileUser.id" />
      </el-tab-pane>
      <el-tab-pane label="我的收藏" name="favorites">
        <FavoriteList
          v-if="activeTab === 'favorites'"
          :user-id="profileUser.id"
        />
      </el-tab-pane>
      <el-tab-pane label="我的评论" name="comments">
        <p v-if="activeTab === 'comments'">我的评论功能正在开发中...</p>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { mapState } from "vuex";
import axios from "axios";
import PostList from "@/components/PostList.vue";
import FavoriteList from "@/components/FavoriteList.vue";
import { Edit, Message } from "@element-plus/icons-vue";

export default {
  name: "UserProfile",
  components: {
    PostList,
    FavoriteList,
    Edit,
    Message,
  },
  data() {
    return {
      profileUser: {},
      activeTab: "posts",
    };
  },
  computed: {
    ...mapState(["user"]),
    profileId() {
      // Allow viewing other profiles via route param, or default to current user
      return this.$route.params.id || (this.user ? this.user.id : null);
    },
  },
  watch: {
    profileId: {
      immediate: true,
      handler(id) {
        if (id) {
          this.fetchUserProfile(id);
        }
      },
    },
  },
  methods: {
    fetchUserProfile(userId) {
      axios
        .get(`/api/user/profile/${userId}`)
        .then((response) => {
          if (response.data.code === 200) {
            this.profileUser = response.data.data;
          }
        })
        .catch((error) => {
          console.error("获取用户信息失败:", error);
        });
    },
    editProfile() {
      // Navigate to profile edit page (to be created)
      this.$router.push("/profile/edit");
    },
  },
};
</script>

<style scoped>
.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.profile-info {
  flex-grow: 1;
}

.profile-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.introduction {
  margin: 10px 0;
  color: #606266;
  font-size: 14px;
}

.user-stats {
  display: flex;
  gap: 20px;
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

.user-stats span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.profile-actions {
  flex-shrink: 0;
}

.profile-tabs {
  margin-top: 20px;
}
</style>
