<template>
  <div class="leaderboard">
    <el-card>
      <template #header>
        <h2>热度榜单</h2>
      </template>

      <el-tabs v-model="activeTab" class="leaderboard-tabs">
        <el-tab-pane label="周榜" name="weekly">
          <HotPostsList :limit="10" :period="'weekly'" />
        </el-tab-pane>
        <el-tab-pane label="月榜" name="monthly">
          <HotPostsList :limit="10" :period="'monthly'" />
        </el-tab-pane>
        <el-tab-pane label="总榜" name="all">
          <HotPostsList :limit="20" :period="'all'" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import HotPostsList from "@/components/HotPostsList.vue";

export default {
  name: "Leaderboard",
  components: {
    HotPostsList,
  },
  data() {
    return {
      activeTab: "weekly",
    };
  },
};
</script>

<style scoped>
.leaderboard {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.leaderboard-tabs {
  margin-top: 20px;
}
</style>
