<template>
  <div>
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-card v-for="i in 3" :key="i" class="favorite-card" shadow="never">
          <el-skeleton-item variant="h3" style="width: 60%" />
          <el-skeleton-item variant="text" style="margin-top: 10px" />
        </el-card>
      </template>
      <template #default>
        <div v-if="favoritePosts.length > 0">
          <el-card
            v-for="post in favoritePosts"
            :key="post.id"
            class="favorite-card"
            shadow="hover"
          >
            <router-link :to="'/post/' + post.id" class="post-title">{{
              post.title
            }}</router-link>
            <div class="post-meta">
              <span>收藏于: {{ formatDate(post.favoriteTime) }}</span>
            </div>
          </el-card>
        </div>
        <el-empty v-else description="你还没有收藏任何帖子"></el-empty>
      </template>
    </el-skeleton>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
import { ElMessage } from "element-plus";

export default {
  name: "FavoriteList",
  props: {
    userId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      favoritePosts: [],
      loading: true,
    };
  },
  created() {
    this.fetchFavorites();
  },
  methods: {
    formatDate(date) {
      return dayjs(date).format("YYYY-MM-DD HH:mm");
    },
    fetchFavorites() {
      this.loading = true;
      axios
        .get(`/api/favorite/list/${this.userId}`)
        .then((response) => {
          if (response.data.code === 200) {
            this.favoritePosts = response.data.data;
          } else {
            ElMessage.error("获取收藏列表失败。");
          }
        })
        .catch((error) => {
          console.error("获取收藏列表失败:", error);
          ElMessage.error("获取收藏列表服务异常。");
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped>
.favorite-card {
  margin-bottom: 15px;
}
.post-title {
  text-decoration: none;
  color: #303133;
  font-size: 18px;
  font-weight: 500;
}
.post-title:hover {
  color: var(--el-color-primary);
}
.post-meta {
  margin-top: 10px;
  font-size: 14px;
  color: #909399;
}
</style>
