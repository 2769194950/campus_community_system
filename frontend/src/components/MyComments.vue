<template>
  <div>
    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-empty v-if="comments.length === 0" description="暂无评论" />
        <el-card
          v-for="c in comments"
          :key="c.id"
          class="comment-card"
          shadow="hover"
        >
          <div class="meta">
            <el-avatar :size="24" :src="c.user?.avatarUrl" />
            <span class="u">{{ c.user?.username }}</span>
            <span class="t">{{
              dayjs(c.createTime).format("YYYY-MM-DD HH:mm")
            }}</span>
          </div>
          <div class="content">{{ c.content }}</div>
          <div class="link">
            <router-link :to="'/post/' + c.entityId" v-if="c.entityType === 1">
              查看被评论的帖子 →
            </router-link>
          </div>
        </el-card>
      </template>
    </el-skeleton>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
export default {
  name: "MyComments",
  props: { userId: { type: Number, required: true } },
  data() {
    return { comments: [], loading: true, dayjs };
  },
  watch: {
    userId: {
      immediate: true,
      handler(id) {
        if (id) this.fetch();
      },
    },
  },
  methods: {
    fetch() {
      this.loading = true;
      axios
        .get(`/api/user/${this.userId}/comments`)
        .then((res) => {
          if (res.data.code === 200) this.comments = res.data.data || [];
        })
        .finally(() => (this.loading = false));
    },
  },
};
</script>

<style scoped>
.comment-card {
  margin-bottom: 12px;
}
.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
  font-size: 12px;
}
.content {
  margin-top: 6px;
  font-size: 14px;
}
.link {
  margin-top: 8px;
}
.u {
  font-weight: 600;
  color: #333;
}
.t {
  margin-left: auto;
}
</style>
