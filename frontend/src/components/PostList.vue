<template>
  <div>
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-card v-for="i in 5" :key="i" class="post-card" shadow="never">
          <el-skeleton-item
            variant="h3"
            style="width: 50%; margin-bottom: 10px"
          />
          <el-skeleton-item variant="text" style="margin-bottom: 15px" />
          <el-skeleton-item variant="text" style="width: 30%" />
        </el-card>
      </template>
      <template #default>
        <el-card
          v-for="post in posts"
          :key="post.id"
          class="post-card"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <router-link :to="'/post/' + post.id" class="post-title">{{
                post.title
              }}</router-link>
            </div>
          </template>
          <div class="post-meta">
            <span>作者: {{ post.user ? post.user.username : "未知用户" }}</span>
            |
            <span>发布于: {{ formatDate(post.createTime) }}</span>
            |
            <span>评论: {{ post.commentCount || 0 }}</span>
          </div>
        </el-card>
      </template>
    </el-skeleton>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";

export default {
  name: "PostList",
  props: {
    partitionId: {
      type: Number,
      default: null,
    },
    userId: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      posts: [],
      loading: true,
    };
  },
  watch: {
    partitionId() {
      this.fetchPosts();
    },
    userId() {
      this.fetchPosts();
    },
  },
  created() {
    this.fetchPosts();
  },
  methods: {
    formatDate(date) {
      if (!date) return "未知时间";
      try {
        return dayjs(date).format("YYYY-MM-DD HH:mm");
      } catch (error) {
        console.error("日期格式化错误:", error, "原始日期:", date);
        return "时间格式错误";
      }
    },
    fetchPosts() {
      this.loading = true;
      const params = {};

      // 只有当userId prop存在时才添加userId参数
      if (this.userId) {
        params.userId = this.userId;
      } else {
        params.userId = 0; // 0 indicates all users
      }

      if (this.partitionId) {
        params.partitionId = this.partitionId;
      }

      axios
        .get("/api/post/list", { params })
        .then((response) => {
          if (response.data.code === 200) {
            this.posts = response.data.data;
          }
        })
        .catch((error) => {
          console.error("获取帖子列表失败:", error);
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped>
.post-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-title {
  text-decoration: none;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
}

.post-title:hover {
  color: var(--el-color-primary);
}

.post-meta {
  color: #909399;
  font-size: 14px;
}

.post-meta span {
  margin-right: 15px;
}
</style>
