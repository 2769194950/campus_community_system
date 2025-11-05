<template>
  <div class="search-results">
    <el-card>
      <template #header>
        <h2>搜索结果</h2>
        <p v-if="keyword">关键词: "{{ keyword }}"</p>
      </template>

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
          <div v-if="searchResults.length > 0">
            <el-card
              v-for="post in searchResults"
              :key="post.id"
              class="post-card"
              shadow="hover"
            >
              <template #header>
                <div class="card-header">
                  <router-link :to="'/post/' + post.id" class="post-title">
                    <span v-html="highlightKeyword(post.title)"></span>
                  </router-link>
                </div>
              </template>
              <div class="post-meta">
                <span
                  >作者: {{ post.user ? post.user.username : "未知用户" }}</span
                >
                |
                <span>发布于: {{ formatDate(post.createTime) }}</span>
                |
                <span>评论: {{ post.commentCount || 0 }}</span>
              </div>
              <div class="post-content-preview" v-if="post.content">
                <span
                  v-html="highlightKeyword(getContentPreview(post.content))"
                ></span>
              </div>
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
      if (!date) return "未知时间";
      try {
        return dayjs(date).format("YYYY-MM-DD HH:mm");
      } catch (error) {
        console.error("日期格式化错误:", error, "原始日期:", date);
        return "时间格式错误";
      }
    },
    getContentPreview(content) {
      // Remove HTML tags and limit length
      const textContent = content.replace(/<[^>]*>/g, "");
      return textContent.length > 200
        ? textContent.substring(0, 200) + "..."
        : textContent;
    },
    highlightKeyword(text) {
      if (!this.keyword || !text) {
        return text;
      }

      // 转义特殊字符，防止正则表达式错误
      const escapedKeyword = this.keyword.replace(
        /[.*+?^${}()|[\]\\]/g,
        "\\$&"
      );

      // 创建正则表达式，忽略大小写
      const regex = new RegExp(`(${escapedKeyword})`, "gi");

      // 替换匹配的关键字为高亮标签
      return text.replace(regex, '<span class="highlight">$1</span>');
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

.post-content-preview {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
  margin-top: 10px;
}
</style>

<style>
/* 全局样式，不使用scoped，确保动态生成的highlight类能正确应用 */
.highlight {
  background-color: #ffeb3b !important;
  color: #333 !important;
  font-weight: bold !important;
  padding: 2px 4px !important;
  border-radius: 3px !important;
}
</style>
