<template>
  <div class="leaderboard-container">
    <div class="header">
      <h1>排行榜</h1>
      <p>查看校园社区的各类排行榜</p>
    </div>

    <div class="leaderboard-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane
          v-for="leaderboard in leaderboards"
          :key="leaderboard.id"
          :label="leaderboard.title"
          :name="leaderboard.id.toString()"
        >
          <div class="leaderboard-content">
            <div class="leaderboard-info">
              <h3>{{ leaderboard.title }}</h3>
              <p>{{ leaderboard.description }}</p>
              <span class="period-badge">{{
                getPeriodText(leaderboard.period)
              }}</span>
            </div>

            <div v-if="loading" class="loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载中...</span>
            </div>

            <div v-else-if="currentData.length === 0" class="empty-state">
              <el-empty description="暂无数据" />
            </div>

            <div v-else class="leaderboard-list">
              <!-- 帖子类型榜单 -->
              <div v-if="leaderboard.type === 'post'" class="post-leaderboard">
                <div
                  v-for="(item, index) in currentData"
                  :key="item.id"
                  class="leaderboard-item"
                  @click="goToPost(item.id)"
                >
                  <div class="rank">
                    <span class="rank-number" :class="getRankClass(index)">{{
                      index + 1
                    }}</span>
                  </div>
                  <div class="content">
                    <h4 class="title">{{ item.title }}</h4>
                    <div class="meta">
                      <span class="author"
                        >作者: {{ item.user?.username || item.username }}</span
                      >
                      <span class="stats">
                        <el-icon><Star /></el-icon> {{ item.likeCount }}
                        <el-icon><ChatDotRound /></el-icon>
                        {{ item.commentCount }}
                        <el-icon><Collection /></el-icon>
                        {{ item.favoriteCount }}
                      </span>
                    </div>
                  </div>
                  <div class="score">
                    <span class="score-value">{{
                      (item.likeCount || 0) +
                      (item.commentCount || 0) +
                      (item.favoriteCount || 0)
                    }}</span>
                    <span class="score-label">热度</span>
                  </div>
                </div>
              </div>

              <!-- 用户类型榜单 -->
              <div v-if="leaderboard.type === 'user'" class="user-leaderboard">
                <div
                  v-for="(item, index) in currentData"
                  :key="item.id"
                  class="leaderboard-item"
                  @click="goToUserProfile(item.id)"
                >
                  <div class="rank">
                    <span class="rank-number" :class="getRankClass(index)">{{
                      index + 1
                    }}</span>
                  </div>
                  <div class="user-info">
                    <el-avatar :src="item.avatarUrl" :size="50">
                      <el-icon><User /></el-icon>
                    </el-avatar>
                    <div class="user-details">
                      <h4 class="username">{{ item.username }}</h4>
                      <div class="user-stats">
                        <span>帖子: {{ item.postCount }}</span>
                        <span>评论: {{ item.commentCount }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="score">
                    <span class="score-value">{{ item.score }}</span>
                    <span class="score-label">活跃度</span>
                  </div>
                </div>
              </div>

              <!-- 收藏类型榜单 -->
              <div
                v-if="leaderboard.type === 'favorite'"
                class="favorite-leaderboard"
              >
                <div
                  v-for="(item, index) in currentData"
                  :key="item.id"
                  class="leaderboard-item"
                  @click="goToPost(item.id)"
                >
                  <div class="rank">
                    <span class="rank-number" :class="getRankClass(index)">{{
                      index + 1
                    }}</span>
                  </div>
                  <div class="content">
                    <h4 class="title">{{ item.title }}</h4>
                    <div class="meta">
                      <span class="author"
                        >作者: {{ item.user?.username || item.username }}</span
                      >
                    </div>
                  </div>
                  <div class="score">
                    <span class="score-value">{{ item.favoriteCount }}</span>
                    <span class="score-label">收藏数</span>
                  </div>
                </div>
              </div>

              <!-- 点赞类型榜单 -->
              <div v-if="leaderboard.type === 'like'" class="like-leaderboard">
                <div
                  v-for="(item, index) in currentData"
                  :key="item.id"
                  class="leaderboard-item"
                  @click="goToPost(item.id)"
                >
                  <div class="rank">
                    <span class="rank-number" :class="getRankClass(index)">{{
                      index + 1
                    }}</span>
                  </div>
                  <div class="content">
                    <h4 class="title">{{ item.title }}</h4>
                    <div class="meta">
                      <span class="author"
                        >作者: {{ item.user?.username || item.username }}</span
                      >
                    </div>
                  </div>
                  <div class="score">
                    <span class="score-value">{{ item.likeCount }}</span>
                    <span class="score-label">点赞数</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  Loading,
  Star,
  ChatDotRound,
  Collection,
  User,
} from "@element-plus/icons-vue";
import axios from "axios";

export default {
  name: "Leaderboard",
  components: {
    Loading,
    Star,
    ChatDotRound,
    Collection,
    User,
  },
  setup() {
    const router = useRouter();
    const activeTab = ref("1");
    const leaderboards = ref([]);
    const leaderboardData = ref({});
    const loading = ref(false);

    // 获取榜单列表
    const fetchLeaderboards = async () => {
      try {
        const response = await axios.get("/api/admin/public/leaderboards");
        if (response.data.code === 200) {
          leaderboards.value = response.data.data;
          if (leaderboards.value.length > 0) {
            activeTab.value = leaderboards.value[0].id.toString();
            await fetchLeaderboardData(leaderboards.value[0].id);
          }
        }
      } catch (error) {
        console.error("获取榜单列表失败:", error);
        ElMessage.error("获取榜单列表失败");
      }
    };

    // 获取榜单数据（增加 ID 解析与 NaN 防御）
    const fetchLeaderboardData = async (leaderboardId) => {
      loading.value = true;
      try {
        const idNum = Number(leaderboardId);
        if (Number.isNaN(idNum)) {
          console.warn("Invalid leaderboardId:", leaderboardId);
          ElMessage.error("榜单ID异常，无法加载数据");
          return;
        }
        const response = await axios.get(
          `/api/admin/public/leaderboards/${idNum}/data`
        );
        if (response.data.code === 200) {
          leaderboardData.value[idNum] =
            response.data.data.items || response.data.data;
        }
      } catch (error) {
        console.error("获取榜单数据失败:", error);
        ElMessage.error("获取榜单数据失败");
      } finally {
        loading.value = false;
      }
    };

    // 当前显示的数据（对 activeTab 做 NaN 防御）
    const currentData = computed(() => {
      const idNum = Number(activeTab.value);
      if (Number.isNaN(idNum)) return [];
      return leaderboardData.value[idNum] || [];
    });

    // 监听标签变化：在 v-model 更新后拉取数据，避免需要点两次
    watch(activeTab, async (newVal) => {
      // 防抖：初始化设置 activeTab 后可能已有请求在进行
      if (loading.value) return;
      const idNum = Number(newVal);
      if (Number.isNaN(idNum)) {
        console.warn("Invalid tab pane name:", newVal);
        ElMessage.error("榜单ID异常，无法加载数据");
        return;
      }
      if (!leaderboardData.value[idNum]) {
        await fetchLeaderboardData(idNum);
      }
    });

    // 获取时间周期文本
    const getPeriodText = (period) => {
      const periodMap = {
        latest: "最新",
        daily: "日榜",
        weekly: "周榜",
        monthly: "月榜",
        all: "总榜",
      };
      return periodMap[period] || "总榜";
    };

    // 获取排名样式
    const getRankClass = (index) => {
      if (index === 0) return "rank-first";
      if (index === 1) return "rank-second";
      if (index === 2) return "rank-third";
      return "";
    };

    // 跳转到帖子详情
    const goToPost = (postId) => {
      router.push(`/post/${postId}`);
    };

    // 跳转到用户资料
    const goToUserProfile = (userId) => {
      router.push(`/user/${userId}`);
    };

    onMounted(() => {
      fetchLeaderboards();
    });

    return {
      activeTab,
      leaderboards,
      currentData,
      loading,
      getPeriodText,
      getRankClass,
      goToPost,
      goToUserProfile,
    };
  },
};
</script>

<style scoped>
.leaderboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 10px;
}

.header p {
  color: #666;
  font-size: 1.1rem;
}

.leaderboard-tabs {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.leaderboard-content {
  padding: 20px;
}

.leaderboard-info {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.leaderboard-info h3 {
  margin: 0 0 8px 0;
  color: #333;
}

.leaderboard-info p {
  margin: 0 0 10px 0;
  color: #666;
}

.period-badge {
  display: inline-block;
  padding: 4px 12px;
  background: #409eff;
  color: white;
  border-radius: 12px;
  font-size: 0.9rem;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
}

.loading span {
  margin-left: 8px;
}

.leaderboard-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.leaderboard-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.leaderboard-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.rank {
  margin-right: 16px;
}

.rank-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-weight: bold;
  color: white;
  background: #999;
}

.rank-first {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #333;
}

.rank-second {
  background: linear-gradient(135deg, #c0c0c0, #e5e5e5);
  color: #333;
}

.rank-third {
  background: linear-gradient(135deg, #cd7f32, #daa520);
}

.content {
  flex: 1;
  margin-right: 16px;
}

.title {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #333;
  line-height: 1.4;
}

.meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #666;
  font-size: 0.9rem;
}

.stats {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stats .el-icon {
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
  margin-right: 16px;
}

.user-details {
  margin-left: 12px;
}

.username {
  margin: 0 0 4px 0;
  color: #333;
}

.user-stats {
  display: flex;
  gap: 12px;
  color: #666;
  font-size: 0.9rem;
}

.score {
  text-align: center;
}

.score-value {
  display: block;
  font-size: 1.5rem;
  font-weight: bold;
  color: #409eff;
}

.score-label {
  font-size: 0.8rem;
  color: #666;
}

.empty-state {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .leaderboard-container {
    padding: 10px;
  }

  .header h1 {
    font-size: 2rem;
  }

  .leaderboard-item {
    padding: 12px;
  }

  .meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>
