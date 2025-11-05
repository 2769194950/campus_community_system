<template>
  <div class="post-management">
    <div class="management-header">
      <h3>帖子管理</h3>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索帖子标题或内容"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="handleSearch"
        >
          <template #suffix>
            <el-icon @click="handleSearch" style="cursor: pointer">
              <Search />
            </el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="loadPosts">刷新</el-button>
      </div>
    </div>

    <el-table
      :data="posts"
      v-loading="loading"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="scope">
          <el-link
            type="primary"
            @click="viewPost(scope.row.id)"
            :underline="false"
          >
            {{ scope.row.title }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        prop="username"
        label="作者"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="likeCount"
        label="点赞数"
        width="100"
      ></el-table-column>
      <el-table-column
        prop="commentCount"
        label="评论数"
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
            @click="viewPost(scope.row.id)"
          >
            查看
          </el-button>
          <el-button type="danger" size="small" @click="deletePost(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-footer">
      <div class="batch-actions">
        <el-button
          type="danger"
          :disabled="selectedPosts.length === 0"
          @click="batchDelete"
        >
          批量删除 ({{ selectedPosts.length }})
        </el-button>
      </div>

      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        @update:page-size="(val) => (pageSize = val)"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";

export default {
  name: "PostManagement",
  components: {
    Search,
  },
  data() {
    return {
      posts: [],
      loading: false,
      searchKeyword: "",
      selectedPosts: [],
      currentPage: 1,
      pageSize: 20,
      total: 0,
    };
  },
  created() {
    this.loadPosts();
  },
  methods: {
    async loadPosts() {
      this.loading = true;
      try {
        const params = {
          offset: (this.currentPage - 1) * this.pageSize,
          limit: this.pageSize,
        };

        if (this.searchKeyword) {
          params.keyword = this.searchKeyword;
        }

        // 使用管理员专用的接口
        const response = await axios.get("/api/admin/posts", { params });

        if (response.data.code === 200) {
          this.posts = response.data.data || [];
          // 由于后端接口暂时没有返回总数，我们需要单独获取或者估算
          // 这里先设置一个较大的值，后续可以优化
          if (this.posts.length < this.pageSize) {
            // 如果返回的数据少于页面大小，说明这是最后一页
            this.total =
              (this.currentPage - 1) * this.pageSize + this.posts.length;
          } else {
            // 否则估算总数（这不是最精确的方法，但可以工作）
            this.total = this.currentPage * this.pageSize + 1;
          }
        } else {
          ElMessage.error("加载帖子列表失败");
        }
      } catch (error) {
        console.error("加载帖子列表失败:", error);
        ElMessage.error("加载帖子列表失败");
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.currentPage = 1;
      this.loadPosts();
    },
    handleSelectionChange(selection) {
      this.selectedPosts = selection;
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.loadPosts();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadPosts();
    },
    viewPost(postId) {
      // 在新标签页打开帖子详情
      const routeData = this.$router.resolve(`/post/${postId}`);
      window.open(routeData.href, "_blank");
    },
    async deletePost(post) {
      try {
        await ElMessageBox.confirm(
          `确定要删除帖子"${post.title}"吗？此操作不可恢复！`,
          "删除确认",
          {
            confirmButtonText: "确定删除",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        // 使用管理员专用的删除接口
        const response = await axios.delete(`/api/admin/posts/${post.id}`);

        if (response.data.code === 200) {
          ElMessage.success("删除成功");
          this.loadPosts();
        } else {
          ElMessage.error(response.data.message || "删除失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("删除帖子失败:", error);
          ElMessage.error("删除帖子失败");
        }
      }
    },
    async batchDelete() {
      if (this.selectedPosts.length === 0) {
        ElMessage.warning("请选择要删除的帖子");
        return;
      }

      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${this.selectedPosts.length} 个帖子吗？此操作不可恢复！`,
          "批量删除确认",
          {
            confirmButtonText: "确定删除",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        // 使用管理员专用的删除接口
        const deletePromises = this.selectedPosts.map((post) =>
          axios.delete(`/api/admin/posts/${post.id}`)
        );

        await Promise.all(deletePromises);

        ElMessage.success(`成功删除 ${this.selectedPosts.length} 个帖子`);
        this.loadPosts();
      } catch (error) {
        if (error !== "cancel") {
          console.error("批量删除失败:", error);
          ElMessage.error("批量删除失败");
        }
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString("zh-CN");
    },
    getStatusType(status) {
      const statusMap = {
        0: "success", // 正常
        1: "warning", // 待审核
        2: "danger", // 已删除
      };
      return statusMap[status] || "info";
    },
    getStatusText(status) {
      const statusMap = {
        0: "正常",
        1: "待审核",
        2: "已删除",
      };
      return statusMap[status] || "未知";
    },
  },
};
</script>

<style scoped>
.post-management {
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

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.batch-actions {
  flex: 1;
}
</style>
