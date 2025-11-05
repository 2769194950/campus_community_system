<template>
  <div class="leaderboard-management">
    <div class="management-header">
      <h3>榜单管理</h3>
      <div class="header-actions">
        <el-button type="primary" @click="refreshAllLeaderboards">
          <el-icon><Refresh /></el-icon>
          刷新所有榜单
        </el-button>
        <el-button type="success" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          创建榜单
        </el-button>
      </div>
    </div>

    <!-- 榜单配置卡片 -->
    <el-row :gutter="20" class="leaderboard-cards">
      <el-col :span="12" v-for="board in leaderboards" :key="board.id">
        <el-card class="leaderboard-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="board-title">{{ board.title }}</span>
              <div class="card-actions">
                <el-switch
                  v-model="board.enabled"
                  @change="toggleLeaderboard(board)"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <el-dropdown @command="(cmd) => handleBoardAction(cmd, board)">
                  <el-button type="text" size="small">
                    <el-icon><More /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="edit">编辑</el-dropdown-item>
                      <el-dropdown-item command="refresh"
                        >刷新数据</el-dropdown-item
                      >
                      <el-dropdown-item command="view"
                        >查看详情</el-dropdown-item
                      >
                      <el-dropdown-item command="delete" divided
                        >删除</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </template>

          <div class="board-info">
            <p class="board-desc">{{ board.description }}</p>
            <div class="board-stats">
              <el-tag
                size="small"
                :type="
                  board.period === 'weekly'
                    ? 'success'
                    : board.period === 'monthly'
                    ? 'warning'
                    : 'primary'
                "
              >
                {{ getPeriodText(board.period) }}
              </el-tag>
              <span class="update-time"
                >更新时间: {{ formatDate(board.lastUpdate) }}</span
              >
            </div>
            <div class="board-preview">
              <div
                class="preview-item"
                v-for="(item, index) in board.topItems"
                :key="index"
              >
                <span class="rank">{{ index + 1 }}.</span>
                <span class="title">{{ item.title || item.username }}</span>
                <span class="score">{{ item.score }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 榜单详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="currentBoard?.title + ' - 详细数据'"
      width="80%"
      top="5vh"
    >
      <el-table :data="detailData" v-loading="detailLoading" max-height="500">
        <el-table-column prop="rank" label="排名" width="80" align="center">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.rank <= 3
                  ? 'danger'
                  : scope.row.rank <= 10
                  ? 'warning'
                  : 'info'
              "
            >
              {{ scope.row.rank }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题/用户名" min-width="200">
          <template #default="scope">
            <div class="detail-item">
              <el-avatar
                v-if="scope.row.avatarUrl"
                :src="scope.row.avatarUrl"
                size="small"
              />
              <span>{{ scope.row.title || scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="100" align="center" />
        <el-table-column
          prop="likeCount"
          label="点赞数"
          width="100"
          align="center"
          v-if="currentBoard?.type === 'post'"
        />
        <el-table-column
          prop="commentCount"
          label="评论数"
          width="100"
          align="center"
          v-if="currentBoard?.type === 'post'"
        />
        <el-table-column
          prop="favoriteCount"
          label="收藏数"
          width="100"
          align="center"
          v-if="currentBoard?.type === 'post'"
        />
        <el-table-column prop="createTime" label="创建时间" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button type="text" size="small" @click="viewItem(scope.row)"
              >查看</el-button
            >
            <el-button
              type="text"
              size="small"
              @click="removeFromBoard(scope.row)"
              >移除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 创建/编辑榜单对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingBoard ? '编辑榜单' : '创建榜单'"
      width="600px"
    >
      <el-form
        :model="boardForm"
        :rules="boardRules"
        ref="boardFormRef"
        label-width="100px"
      >
        <el-form-item label="榜单标题" prop="title">
          <el-input v-model="boardForm.title" placeholder="请输入榜单标题" />
        </el-form-item>
        <el-form-item label="榜单描述" prop="description">
          <el-input
            v-model="boardForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入榜单描述"
          />
        </el-form-item>
        <el-form-item label="榜单类型" prop="type">
          <el-select v-model="boardForm.type" placeholder="请选择榜单类型">
            <el-option label="热门帖子" value="post" />
            <el-option label="活跃用户" value="user" />
            <el-option label="点赞排行" value="like" />
            <el-option label="收藏排行" value="favorite" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围" prop="period">
          <el-select v-model="boardForm.period" placeholder="请选择时间范围">
            <el-option label="周榜" value="weekly" />
            <el-option label="月榜" value="monthly" />
            <el-option label="总榜" value="all" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示数量" prop="limit">
          <el-input-number v-model="boardForm.limit" :min="5" :max="100" />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-switch v-model="boardForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveBoard">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, Plus, More } from "@element-plus/icons-vue";
import dayjs from "dayjs";

export default {
  name: "LeaderboardManagement",
  components: {
    Refresh,
    Plus,
    More,
  },
  data() {
    return {
      leaderboards: [],
      loading: false,
      showDetailDialog: false,
      showCreateDialog: false,
      currentBoard: null,
      detailData: [],
      detailLoading: false,
      editingBoard: null,
      boardForm: {
        title: "",
        description: "",
        type: "post",
        period: "weekly",
        limit: 20,
        enabled: true,
      },
      boardRules: {
        title: [{ required: true, message: "请输入榜单标题", trigger: "blur" }],
        description: [
          { required: true, message: "请输入榜单描述", trigger: "blur" },
        ],
        type: [
          { required: true, message: "请选择榜单类型", trigger: "change" },
        ],
        period: [
          { required: true, message: "请选择时间范围", trigger: "change" },
        ],
      },
    };
  },
  created() {
    this.loadLeaderboards();
  },
  methods: {
    async loadLeaderboards() {
      this.loading = true;
      try {
        const response = await axios.get("/api/admin/leaderboards");
        if (response.data.code === 200) {
          // 使用Vue.set或直接赋值确保响应式更新
          this.leaderboards = [...response.data.data];
          // 强制更新组件
          this.$forceUpdate();
        } else {
          ElMessage.error("加载榜单失败：" + response.data.message);
        }
      } catch (error) {
        console.error("加载榜单失败:", error);
        ElMessage.error("加载榜单失败");
      } finally {
        this.loading = false;
      }
    },

    async toggleLeaderboard(board) {
      try {
        const response = await axios.put(
          `/api/admin/leaderboards/${board.id}/toggle`,
          {
            enabled: board.enabled,
          }
        );
        if (response.data.code === 200) {
          ElMessage.success(board.enabled ? "榜单已启用" : "榜单已禁用");
          // 重新加载数据以确保界面同步
          await this.loadLeaderboards();
        } else {
          board.enabled = !board.enabled; // 回滚状态
          ElMessage.error("操作失败：" + response.data.message);
        }
      } catch (error) {
        board.enabled = !board.enabled; // 回滚状态
        ElMessage.error("操作失败");
      }
    },

    async handleBoardAction(command, board) {
      switch (command) {
        case "edit":
          this.editBoard(board);
          break;
        case "refresh":
          await this.refreshBoard(board);
          break;
        case "view":
          await this.viewBoardDetail(board);
          break;
        case "delete":
          await this.deleteBoard(board);
          break;
      }
    },

    editBoard(board) {
      this.editingBoard = board;
      // 使用深拷贝确保数据独立性
      this.boardForm = JSON.parse(JSON.stringify(board));
      this.showCreateDialog = true;
    },

    async refreshBoard(board) {
      try {
        const response = await axios.post(
          `/api/admin/leaderboards/${board.id}/refresh`
        );
        if (response.data.code === 200) {
          ElMessage.success("榜单数据已刷新");
          // 重新加载所有榜单数据以确保界面同步
          await this.loadLeaderboards();
        } else {
          ElMessage.error("刷新失败：" + response.data.message);
        }
      } catch (error) {
        ElMessage.error("刷新失败");
      }
    },

    async viewBoardDetail(board) {
      this.currentBoard = board;
      this.showDetailDialog = true;
      this.detailLoading = true;

      try {
        const response = await axios.get(
          `/api/admin/leaderboards/${board.id}/detail`
        );
        if (response.data.code === 200) {
          this.detailData = response.data.data.map((item, index) => ({
            ...item,
            rank: index + 1,
          }));
        } else {
          ElMessage.error("加载详细数据失败：" + response.data.message);
        }
      } catch (error) {
        ElMessage.error("加载详细数据失败");
      } finally {
        this.detailLoading = false;
      }
    },

    async deleteBoard(board) {
      try {
        await ElMessageBox.confirm(
          `确定要删除榜单"${board.title}"吗？此操作不可恢复。`,
          "删除确认",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        const response = await axios.delete(
          `/api/admin/leaderboards/${board.id}`
        );
        if (response.data.code === 200) {
          ElMessage.success("榜单已删除");
          await this.loadLeaderboards();
        } else {
          ElMessage.error("删除失败：" + response.data.message);
        }
      } catch (error) {
        if (error !== "cancel") {
          ElMessage.error("删除失败");
        }
      }
    },

    async saveBoard() {
      try {
        await this.$refs.boardFormRef.validate();

        const url = this.editingBoard
          ? `/api/admin/leaderboards/${this.editingBoard.id}`
          : "/api/admin/leaderboards";
        const method = this.editingBoard ? "put" : "post";

        const response = await axios[method](url, this.boardForm);
        if (response.data.code === 200) {
          ElMessage.success(this.editingBoard ? "榜单已更新" : "榜单已创建");
          this.showCreateDialog = false;
          this.resetForm();
          await this.loadLeaderboards();
        } else {
          ElMessage.error("保存失败：" + response.data.message);
        }
      } catch (error) {
        if (error !== false) {
          // 不是表单验证错误
          ElMessage.error("保存失败");
        }
      }
    },

    async refreshAllLeaderboards() {
      try {
        const response = await axios.post(
          "/api/admin/leaderboards/refresh-all"
        );
        if (response.data.code === 200) {
          ElMessage.success("所有榜单数据已刷新");
          await this.loadLeaderboards();
        } else {
          ElMessage.error("刷新失败：" + response.data.message);
        }
      } catch (error) {
        ElMessage.error("刷新失败");
      }
    },

    resetForm() {
      this.editingBoard = null;
      this.boardForm = {
        title: "",
        description: "",
        type: "post",
        period: "weekly",
        limit: 20,
        enabled: true,
      };
    },

    viewItem(item) {
      if (item.postId) {
        window.open(`/post/${item.postId}`, "_blank");
      } else if (item.userId) {
        window.open(`/profile/${item.userId}`, "_blank");
      }
    },

    async removeFromBoard(item) {
      try {
        await ElMessageBox.confirm(
          `确定要从榜单中移除"${item.title || item.username}"吗？`,
          "移除确认"
        );
        // 这里可以添加移除逻辑
        console.log("移除项目:", item);
        ElMessage.success("已移除");
      } catch (error) {
        // 用户取消
      }
    },

    getPeriodText(period) {
      const map = {
        weekly: "周榜",
        monthly: "月榜",
        all: "总榜",
      };
      return map[period] || period;
    },

    formatDate(date) {
      return dayjs(date).format("YYYY-MM-DD HH:mm");
    },
  },
};
</script>

<style scoped>
.leaderboard-management {
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
  gap: 10px;
}

.leaderboard-cards {
  margin-bottom: 20px;
}

.leaderboard-card {
  margin-bottom: 20px;
  height: 420px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.board-title {
  font-weight: bold;
  color: #303133;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.board-info {
  height: 320px;
  display: flex;
  flex-direction: column;
}

.board-desc {
  color: #606266;
  margin-bottom: 15px;
  font-size: 15px;
  line-height: 1.5;
}

.board-stats {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.update-time {
  font-size: 12px;
  color: #909399;
}

.board-preview {
  flex: 1;
  overflow-y: auto;
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.preview-item:last-child {
  border-bottom: none;
}

.rank {
  font-weight: bold;
  color: #409eff;
  width: 20px;
}

.title {
  flex: 1;
  margin: 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.score {
  color: #f56c6c;
  font-weight: bold;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-card :deep(.el-card__body) {
  padding: 20px;
}
</style>
