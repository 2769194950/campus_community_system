<template>
  <div class="search-results">
    <el-card>
      <template #header>
        <h2>搜索结果</h2>
        <p v-if="keyword">关键词: "{{ keyword }}"</p>
      </template>

      <el-skeleton :loading="loading" animated>
        <template #template>
          <el-card
            v-for="i in 5"
            :key="i"
            class="search-result-card"
            shadow="never"
          >
            <el-skeleton-item variant="h3" style="width: 60%" />
            <el-skeleton-item variant="text" style="margin-top: 10px" />
          </el-card>
        </template>
        <template #default>
          <div v-if="searchResults.length > 0">
            <el-card
              v-for="post in searchResults"
              :key="post.id"
              class="search-result-card"
              shadow="hover"
            >
              <router-link :to="'/post/' + post.id" class="post-title">{{
                post.title
              }}</router-link>
              <div class="post-meta">
                <span>作者: {{ post.user ? post.user.username : '未知用户' }}</span>
                <span>发布于: {{ formatDate(post.createTime) }}</span>
                <span><i class="el-icon-thumb"></i> {{ post.likeCount || 0 }}</span>
                <span
                  ><i class="el-icon-chat-dot-round"></i>
                  {{ post.commentCount || 0 }}</span
                >
                <span
                  ><i class="el-icon-star-on"></i>
                  {{ post.favoriteCount || 0 }}</span
                >
              </div>
              <div
                class="post-content-preview"
                v-html="getContentPreview(post.content)"
              ></div>
            </el-card>
          </div>
          <el-empty v-else description="没有找到相关帖子"></el-empty>
        </template>
      </el-skeleton>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
import { ElMessage } from "element-plus";

export default {
  name: "SearchResults",
  data() {
    return {
      searchResults: [],
      loading: true,
      keyword: "",
    };
  },
  created() {
    this.keyword = this.$route.query.keyword || "";
    if (this.keyword) {
      this.performSearch();
    }
  },
  watch: {
    "$route.query.keyword"(newKeyword) {
      this.keyword = newKeyword || "";
      if (this.keyword) {
        this.performSearch();
      }
    },
  },
  methods: {
    formatDate(date) {
      if (!date) return '未知时间';
      try {
        return dayjs(date).format("YYYY-MM-DD HH:mm");
      } catch (error) {
        console.error('日期格式化错误:', error, '原始日期:', date);
        return '时间格式错误';
      }
    },
    getContentPreview(content) {
      // Remove HTML tags and limit length
      const textContent = content.replace(/<[^>]*>/g, "");
      return textContent.length > 200
        ? textContent.substring(0, 200) + "..."
        : textContent;
    },
    performSearch() {
      this.loading = true;
      axios
        .get("/api/post/search", { params: { keyword: this.keyword } })
        .then((response) => {
          if (response.data.code === 200) {
            this.searchResults = response.data.data;
          } else {
            ElMessage.error("搜索失败，请稍后再试。");
          }
        })
        .catch((error) => {
          console.error("搜索失败:", error);
          ElMessage.error("搜索服务异常，请稍后再试。");
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped>
.search-results {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.search-result-card {
  margin-bottom: 15px;
  padding: 20px;
}

.post-title {
  text-decoration: none;
  color: #303133;
  font-size: 20px;
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
  margin-bottom: 10px;
}

.post-meta span {
  margin-right: 15px;
}

.post-content-preview {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
}
</style>
