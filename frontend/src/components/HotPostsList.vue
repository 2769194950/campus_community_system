<template>
  <div>
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-card v-for="i in 5" :key="i" class="hot-post-card" shadow="never">
          <el-skeleton-item variant="h3" style="width: 60%" />
          <el-skeleton-item variant="text" style="margin-top: 10px" />
        </el-card>
      </template>
      <template #default>
        <div v-if="hotPosts.length > 0">
          <el-card
            v-for="(post, index) in hotPosts"
            :key="post.id"
            class="hot-post-card"
            shadow="hover"
          >
            <div class="post-rank">
              <el-tag :type="getRankType(index + 1)" size="large">{{
                index + 1
              }}</el-tag>
            </div>
            <div class="post-content">
              <router-link :to="'/post/' + post.id" class="post-title">{{
                post.title
              }}</router-link>
              <div class="post-meta">
                <span>作者: {{ post.user.username }}</span>
                <span>发布于: {{ formatDate(post.createTime) }}</span>
                <span><i class="el-icon-thumb"></i> {{ post.likeCount }}</span>
                <span
                  ><i class="el-icon-chat-dot-round"></i>
                  {{ post.commentCount }}</span
                >
                <span
                  ><i class="el-icon-star-on"></i>
                  {{ post.favoriteCount }}</span
                >
              </div>
            </div>
            <div class="post-score">
              <el-tag type="warning" size="small">
                热度:
                {{ post.likeCount + post.commentCount + post.favoriteCount }}
              </el-tag>
            </div>
          </el-card>
        </div>
        <el-empty v-else description="暂无数据"></el-empty>
      </template>
    </el-skeleton>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
import { ElMessage } from "element-plus";

export default {
  name: "HotPostsList",
  props: {
    limit: {
      type: Number,
      default: 10,
    },
    period: {
      type: String,
      default: "all",
    },
  },
  data() {
    return {
      hotPosts: [],
      loading: true,
    };
  },
  created() {
    this.fetchHotPosts();
  },
  methods: {
    formatDate(date) {
      return dayjs(date).format("YYYY-MM-DD HH:mm");
    },
    getRankType(rank) {
      if (rank <= 3) return "danger";
      if (rank <= 10) return "warning";
      return "info";
    },
    fetchHotPosts() {
      this.loading = true;
      axios
        .get("/api/post/hot", { params: { limit: this.limit } })
        .then((response) => {
          if (response.data.code === 200) {
            this.hotPosts = response.data.data;
          } else {
            ElMessage.error("获取榜单数据失败。");
          }
        })
        .catch((error) => {
          console.error("获取榜单数据失败:", error);
          ElMessage.error("获取榜单数据服务异常。");
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped>
.hot-post-card {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  padding: 15px;
}

.post-rank {
  margin-right: 20px;
  min-width: 50px;
  text-align: center;
}

.post-content {
  flex-grow: 1;
}

.post-title {
  text-decoration: none;
  color: #303133;
  font-size: 18px;
  font-weight: 500;
  display: block;
  margin-bottom: 10px;
}

.post-title:hover {
  color: var(--el-color-primary);
}

.post-meta {
  font-size: 14px;
  color: #909399;
}

.post-meta span {
  margin-right: 15px;
}

.post-score {
  margin-left: 20px;
  min-width: 80px;
  text-align: center;
}
</style>
