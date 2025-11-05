<template>
  <div class="comment-management">
    <div class="management-header">
      <h3>评论管理</h3>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索评论内容或用户名"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="handleSearch"
        >
          <template #suffix>
            <el-icon @click="handleSearch" style="cursor: pointer">
              <Search />
            </el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="loadComments">刷新</el-button>
      </div>
    </div>

    <el-table
      :data="comments"
      v-loading="loading"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="300">
        <template #default="scope">
          <div class="comment-content">
            {{ scope.row.content }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="user.username" label="评论者" width="120">
        <template #default="scope">
          <div class="user-info">
            <el-avatar :size="24" :src="scope.row.user?.avatarUrl" />
            <span style="margin-left: 8px">{{ scope.row.user?.username }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="entityType" label="评论类型" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.entityType === 1 ? 'primary' : 'success'">
            {{ scope.row.entityType === 1 ? "帖子评论" : "回复评论" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="likeCount"
        label="点赞数"
        width="100"
      ></el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="viewRelatedPost(scope.row)"
          >
            查看相关帖子
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="deleteComment(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-footer">
      <div class="batch-actions">
        <el-button
          type="danger"
          :disabled="selectedComments.length === 0"
          @click="batchDelete"
        >
          批量删除 ({{ selectedComments.length }})
        </el-button>
      </div>

      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        layout="total, prev, pager, next, jumper"
      />
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";

export default {
  name: "CommentManagement",
  components: {
    Search,
  },
  data() {
    return {
      comments: [],
      loading: false,
      searchKeyword: "",
      selectedComments: [],
      currentPage: 1,
      pageSize: 20,
      total: 0,
    };
  },
  created() {
    this.loadComments();
  },
  methods: {
    async loadComments() {
      this.loading = true;
      try {
        const offset = (this.currentPage - 1) * this.pageSize;
        const params = {
          offset,
          limit: this.pageSize,
        };

        if (this.searchKeyword.trim()) {
          params.keyword = this.searchKeyword.trim();
        }

        const response = await axios.get("/api/admin/comments", { params });

        if (response.data.code === 200) {
          this.comments = response.data.data || [];
          // 注意：这里需要后端返回总数，暂时使用评论数组长度
          this.total = this.comments.length;
        } else {
          ElMessage.error(response.data.message || "获取评论列表失败");
        }
      } catch (error) {
        console.error("获取评论列表失败:", error);
        ElMessage.error("获取评论列表失败，请稍后再试");
      } finally {
        this.loading = false;
      }
    },

    handleSearch() {
      this.currentPage = 1;
      this.loadComments();
    },

    handleSelectionChange(selection) {
      this.selectedComments = selection;
    },

    handlePageChange(page) {
      this.currentPage = page;
      this.loadComments();
    },

    async deleteComment(comment) {
      try {
        await ElMessageBox.confirm(
          `确定要删除用户"${comment.user?.username}"的这条评论吗？`,
          "删除确认",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        const response = await axios.delete(
          `/api/admin/comments/${comment.id}`
        );

        if (response.data.code === 200) {
          ElMessage.success("评论删除成功");
          this.loadComments();
        } else {
          ElMessage.error(response.data.message || "删除失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("删除评论失败:", error);
          ElMessage.error("删除失败，请稍后再试");
        }
      }
    },

    async batchDelete() {
      if (this.selectedComments.length === 0) {
        ElMessage.warning("请先选择要删除的评论");
        return;
      }

      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${this.selectedComments.length} 条评论吗？`,
          "批量删除确认",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        const commentIds = this.selectedComments.map((comment) => comment.id);
        const response = await axios.delete("/api/admin/comments/batch", {
          data: commentIds,
        });

        if (response.data.code === 200) {
          ElMessage.success(`成功删除 ${this.selectedComments.length} 条评论`);
          this.selectedComments = [];
          this.loadComments();
        } else {
          ElMessage.error(response.data.message || "批量删除失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("批量删除评论失败:", error);
          ElMessage.error("批量删除失败，请稍后再试");
        }
      }
    },

    viewRelatedPost(comment) {
      if (comment.entityType === 1) {
        // 直接跳转到帖子详情
        this.$router.push(`/post/${comment.entityId}`);
      } else {
        // 如果是回复评论，需要找到原始帖子
        ElMessage.info("正在跳转到相关帖子...");
        this.$router.push(`/post/${comment.entityId}`);
      }
    },

    formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    },

    getStatusType(status) {
      switch (status) {
        case 0:
          return "success";
        case 1:
          return "danger";
        default:
          return "info";
      }
    },

    getStatusText(status) {
      switch (status) {
        case 0:
          return "正常";
        case 1:
          return "已拉黑";
        default:
          return "未知";
      }
    },
  },
};
</script>

<style scoped>
.comment-management {
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.management-header h3 {
  margin: 0;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
}

.comment-content {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}

.user-info {
  display: flex;
  align-items: center;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table .el-table__header {
  background-color: #f5f7fa;
}

.el-pagination {
  justify-content: flex-end;
}
</style>
