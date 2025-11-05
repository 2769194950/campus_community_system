<template>
  <div>
    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-empty v-if="posts.length === 0" description="暂无点赞记录" />
        <el-card v-for="p in posts" :key="p.id" class="card" shadow="hover">
          <router-link :to="'/post/' + p.id"
            ><h3 class="title">{{ p.title }}</h3></router-link
          >
          <div class="meta">
            作者：
            <router-link :to="'/profile/' + p.userId">{{
              p.user?.username || p.authorName
            }}</router-link>
            · {{ format(p.createTime) }}
          </div>
          <p class="summary">{{ (p.content || "").slice(0, 120) }}</p>
        </el-card>
      </template>
    </el-skeleton>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
export default {
  name: "MyLikes",
  props: { userId: { type: Number, required: true } },
  data() {
    return { loading: true, posts: [] };
  },
  watch: {
    userId: {
      immediate: true,
      handler() {
        this.fetch();
      },
    },
  },
  methods: {
    format(t) {
      return t ? dayjs(t).format("YYYY-MM-DD HH:mm") : "";
    },
    async fetch() {
      this.loading = true;
      const { data } = await axios.get(`/api/user/${this.userId}/liked-posts`);
      this.posts = data?.data || [];
      this.loading = false;
    },
  },
};
</script>

<style scoped>
.card {
  margin-bottom: 12px;
}
.title {
  margin: 0;
}
.meta {
  color: #999;
  font-size: 12px;
  margin: 4px 0 8px;
}
.summary {
  color: #555;
}
</style>
