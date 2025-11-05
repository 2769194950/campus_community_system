<template>
  <div class="page">
    <el-page-header content="帖子列表" />
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="关键字搜索（前端过滤）"
        clearable
        style="max-width: 260px"
      />
      <el-button @click="fetch" type="primary">刷新</el-button>
    </div>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-empty v-if="list.length === 0" description="暂无帖子" />
        <el-card v-for="p in filtered" :key="p.id" class="post" shadow="hover">
          <router-link :to="`/post/${p.id}`"
            ><h3 class="title">{{ p.title }}</h3></router-link
          >
          <p class="meta">
            作者：
            <router-link :to="'/profile/' + (p.userId || p.user?.id)">
              {{ p.user?.username || p.authorName }}
            </router-link>
            · {{ format(p.createTime) }}
          </p>
          <p class="summary">{{ p.content?.slice(0, 120) }}</p>
        </el-card>
      </template>
    </el-skeleton>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
export default {
  name: "PostList",
  data() {
    return { list: [], loading: true, keyword: "" };
  },
  computed: {
    filtered() {
      if (!this.keyword) return this.list;
      const k = this.keyword.toLowerCase();
      return this.list.filter(
        (i) =>
          (i.title || "").toLowerCase().includes(k) ||
          (i.content || "").toLowerCase().includes(k)
      );
    },
  },
  created() {
    this.fetch();
  },
  methods: {
    format(t) {
      return t ? dayjs(t).format("YYYY-MM-DD HH:mm") : "";
    },
    async fetch() {
      this.loading = true;
      const userId = this.$route.query.userId || 0;
      const url = `/api/post/list?userId=${userId}`;
      const { data } = await axios.get(url);
      this.list = data?.data || [];
      this.loading = false;
    },
  },
};
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  margin: 12px 0;
}
.post {
  margin-bottom: 12px;
}
.title {
  margin: 0;
}
.meta {
  color: #888;
  font-size: 12px;
  margin: 6px 0 8px;
}
.summary {
  color: #555;
}
</style>
