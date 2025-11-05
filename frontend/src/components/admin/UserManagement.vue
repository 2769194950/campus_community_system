<template>
  <div class="user-management">
    <div class="management-header">
      <h3>用户管理</h3>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名或邮箱"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="handleSearch"
        >
          <template #suffix>
            <el-icon @click="handleSearch" style="cursor: pointer">
              <Search />
            </el-icon>
          </template>
        </el-input>
        <el-select
          v-model="filterType"
          placeholder="用户类型"
          style="width: 120px; margin-right: 10px"
          @change="handleSearch"
        >
          <el-option label="全部" :value="null"></el-option>
          <el-option label="普通用户" :value="0"></el-option>
          <el-option label="管理员" :value="1"></el-option>
          <el-option label="版主" :value="2"></el-option>
        </el-select>
        <el-button type="primary" @click="loadUsers">刷新</el-button>
      </div>
    </div>

    <el-table
      :data="users"
      v-loading="loading"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="avatarUrl" label="头像" width="80">
        <template #default="scope">
          <el-avatar :size="40" :src="scope.row.avatarUrl"></el-avatar>
        </template>
      </el-table-column>
      <el-table-column
        prop="username"
        label="用户名"
        min-width="120"
      ></el-table-column>
      <el-table-column
        prop="email"
        label="邮箱"
        min-width="180"
      ></el-table-column>
      <el-table-column prop="type" label="用户类型" width="100">
        <template #default="scope">
          <el-tag :type="getUserTypeColor(scope.row.type)">
            {{ getUserTypeText(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="postCount" label="帖子数" width="100">
        <template #default="scope">
          {{ scope.row.postCount || 0 }}
        </template>
      </el-table-column>
      <el-table-column prop="commentCount" label="评论数" width="100">
        <template #default="scope">
          {{ scope.row.commentCount || 0 }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180">
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
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="viewUserProfile(scope.row.id)"
          >
            查看
          </el-button>
          <el-button
            v-if="scope.row.type !== 1"
            type="warning"
            size="small"
            @click="changeUserType(scope.row)"
          >
            {{ scope.row.type === 2 ? "取消版主" : "设为版主" }}
          </el-button>
          <el-button
            v-if="scope.row.id !== currentUserId"
            type="danger"
            size="small"
            @click="toggleUserStatus(scope.row)"
          >
            {{ scope.row.status === 0 ? "禁用" : "启用" }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-footer">
      <div class="batch-actions">
        <el-button
          type="warning"
          :disabled="selectedUsers.length === 0"
          @click="batchSetModerator"
        >
          批量设为版主 ({{ selectedUsers.length }})
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedUsers.length === 0"
          @click="batchDisableUsers"
        >
          批量禁用 ({{ selectedUsers.length }})
        </el-button>
      </div>

      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="userDetailVisible" title="用户详情" width="600px">
      <div v-if="selectedUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{
            selectedUser.id
          }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{
            selectedUser.username
          }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{
            selectedUser.email
          }}</el-descriptions-item>
          <el-descriptions-item label="用户类型">
            <el-tag :type="getUserTypeColor(selectedUser.type)">
              {{ getUserTypeText(selectedUser.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{
            formatDate(selectedUser.createTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedUser.status)">
              {{ getStatusText(selectedUser.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="帖子数量">{{
            selectedUser.postCount || 0
          }}</el-descriptions-item>
          <el-descriptions-item label="评论数量">{{
            selectedUser.commentCount || 0
          }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { mapState } from "vuex";

export default {
  name: "UserManagement",
  components: {
    Search,
  },
  data() {
    return {
      users: [],
      loading: false,
      searchKeyword: "",
      filterType: null,
      selectedUsers: [],
      currentPage: 1,
      pageSize: 20,
      total: 0,
      userDetailVisible: false,
      selectedUser: null,
    };
  },
  computed: {
    ...mapState(["user"]),
    currentUserId() {
      return this.user ? this.user.id : null;
    },
  },
  created() {
    this.loadUsers();
  },
  methods: {
    async loadUsers() {
      this.loading = true;
      try {
        const params = {
          offset: (this.currentPage - 1) * this.pageSize,
          limit: this.pageSize,
        };

        // 调用管理员用户列表API
        const response = await axios.get("/api/admin/users", { params });

        if (response.data.code === 200) {
          this.users = response.data.data || [];
          // 由于后端没有返回总数，我们估算一个值
          // 如果返回的数据少于pageSize，说明是最后一页
          if (this.users.length < this.pageSize) {
            this.total =
              (this.currentPage - 1) * this.pageSize + this.users.length;
          } else {
            // 估算总数，实际应该由后端提供
            this.total = this.currentPage * this.pageSize + 1;
          }
        } else {
          ElMessage.error(response.data.message || "获取用户列表失败");
          this.users = [];
          this.total = 0;
        }
      } catch (error) {
        console.error("加载用户列表失败:", error);
        ElMessage.error("加载用户列表失败");
        this.users = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.currentPage = 1;
      this.loadUsers();
    },
    handleSelectionChange(selection) {
      this.selectedUsers = selection;
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.loadUsers();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadUsers();
    },
    viewUserProfile(userId) {
      // 在新标签页打开用户主页
      const routeData = this.$router.resolve(`/profile/${userId}`);
      window.open(routeData.href, "_blank");
    },
    async changeUserType(user) {
      const newType = user.type === 2 ? 0 : 2;
      const actionText = newType === 2 ? "设为版主" : "取消版主";

      try {
        await ElMessageBox.confirm(
          `确定要将用户"${user.username}"${actionText}吗？`,
          "权限变更确认",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        // 调用管理员API更新用户类型
        const response = await axios.put(`/api/admin/users/${user.id}/type`, {
          type: newType,
        });

        if (response.data.code === 200) {
          ElMessage.success(`${actionText}成功`);
          user.type = newType;
        } else {
          ElMessage.error(response.data.message || `${actionText}失败`);
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("更新用户类型失败:", error);
          ElMessage.error("更新用户类型失败");
        }
      }
    },
    async toggleUserStatus(user) {
      const newStatus = user.status === 0 ? 1 : 0;
      const actionText = newStatus === 1 ? "禁用" : "启用";

      try {
        await ElMessageBox.confirm(
          `确定要${actionText}用户"${user.username}"吗？`,
          "状态变更确认",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        // 调用管理员API更新用户状态
        const response = await axios.put(`/api/admin/users/${user.id}/status`, {
          status: newStatus,
        });

        if (response.data.code === 200) {
          ElMessage.success(`${actionText}用户成功`);
          user.status = newStatus;
        } else {
          ElMessage.error(response.data.message || `${actionText}用户失败`);
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("更新用户状态失败:", error);
          ElMessage.error("更新用户状态失败");
        }
      }
    },
    async batchSetModerator() {
      if (this.selectedUsers.length === 0) {
        ElMessage.warning("请选择要设为版主的用户");
        return;
      }

      try {
        await ElMessageBox.confirm(
          `确定要将选中的 ${this.selectedUsers.length} 个用户设为版主吗？`,
          "批量设置版主确认",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        // 调用管理员API批量更新用户类型
        const userIds = this.selectedUsers
          .filter((user) => user.type !== 1) // 不能修改管理员
          .map((user) => user.id);

        if (userIds.length === 0) {
          ElMessage.warning("没有可以设为版主的用户");
          return;
        }

        const response = await axios.put("/api/admin/users/batch/type", {
          userIds: userIds,
          type: 2,
        });

        if (response.data.code === 200) {
          this.selectedUsers.forEach((user) => {
            if (user.type !== 1) {
              // 不能修改管理员
              user.type = 2;
            }
          });
          ElMessage.success(`成功设置 ${userIds.length} 个用户为版主`);
        } else {
          ElMessage.error(response.data.message || "批量设置版主失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("批量设置版主失败:", error);
          ElMessage.error("批量设置版主失败");
        }
      }
    },
    async batchDisableUsers() {
      if (this.selectedUsers.length === 0) {
        ElMessage.warning("请选择要禁用的用户");
        return;
      }

      const validUsers = this.selectedUsers.filter(
        (user) => user.id !== this.currentUserId
      );
      if (validUsers.length === 0) {
        ElMessage.warning("不能禁用自己的账号");
        return;
      }

      try {
        await ElMessageBox.confirm(
          `确定要禁用选中的 ${validUsers.length} 个用户吗？`,
          "批量禁用确认",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        // 调用管理员API批量禁用用户
        const userIds = validUsers.map((user) => user.id);

        const response = await axios.put("/api/admin/users/batch/status", {
          userIds: userIds,
          status: 1,
        });

        if (response.data.code === 200) {
          validUsers.forEach((user) => {
            user.status = 1;
          });
          ElMessage.success(`成功禁用 ${validUsers.length} 个用户`);
        } else {
          ElMessage.error(response.data.message || "批量禁用用户失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("批量禁用用户失败:", error);
          ElMessage.error("批量禁用用户失败");
        }
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString("zh-CN");
    },
    getUserTypeText(type) {
      const typeMap = {
        0: "普通用户",
        1: "管理员",
        2: "版主",
      };
      return typeMap[type] || "未知";
    },
    getUserTypeColor(type) {
      const colorMap = {
        0: "",
        1: "danger",
        2: "warning",
      };
      return colorMap[type] || "";
    },
    getStatusType(status) {
      const statusMap = {
        0: "success",
        1: "danger",
      };
      return statusMap[status] || "info";
    },
    getStatusText(status) {
      const statusMap = {
        0: "正常",
        1: "禁用",
      };
      return statusMap[status] || "未知";
    },
  },
};
</script>

<style scoped>
.user-management {
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

.user-detail {
  padding: 20px 0;
}
</style>
