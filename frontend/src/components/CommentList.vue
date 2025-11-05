<template>
  <div>
    <el-card class="box-card comment-form" v-if="user">
      <template #header>
        <span>发表评论</span>
      </template>
      <el-input
        type="textarea"
        :rows="3"
        placeholder="请输入评论内容..."
        v-model="newComment"
      ></el-input>
      <el-button type="primary" @click="addComment" style="margin-top: 10px"
        >发表</el-button
      >
    </el-card>

    <el-card class="box-card comment-list" v-if="comments.length > 0">
      <template #header>
        <span>全部评论 ({{ comments.length }})</span>
      </template>
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <el-avatar
          :src="comment.user.avatarUrl"
          class="comment-avatar"
        ></el-avatar>
        <div class="comment-body">
          <div class="comment-header">
            <strong>{{ comment.user.username }}</strong>
            <span class="comment-time">{{
              formatDate(comment.createTime)
            }}</span>
            <el-button
              v-if="user && user.type === 1"
              type="danger"
              size="small"
              @click="handleDeleteComment(comment.id)"
              style="margin-left: 10px"
            >
              <i class="el-icon-delete"></i> 删除
            </el-button>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
        </div>
      </div>
    </el-card>

    <el-empty v-else description="暂无评论，快来抢沙发吧！"></el-empty>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
import { mapState } from "vuex";
import { ElMessage } from "element-plus";

export default {
  name: "CommentList",
  props: ["entityType", "entityId"],
  data() {
    return {
      comments: [],
      newComment: "",
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  created() {
    this.fetchComments();
  },
  methods: {
    formatDate(date) {
      return dayjs(date).format("YYYY-MM-DD HH:mm");
    },
    fetchComments() {
      axios
        .get(`/api/comment/list/${this.entityType}/${this.entityId}`)
        .then((response) => {
          this.comments = response.data.data;
        });
    },
    addComment() {
      if (!this.newComment.trim()) {
        ElMessage.warning("评论内容不能为空！");
        return;
      }
      axios
        .post("/api/comment/add", {
          entityType: this.entityType,
          entityId: this.entityId,
          content: this.newComment,
        })
        .then(() => {
          this.newComment = "";
          this.fetchComments(); // Refresh comments after adding
          ElMessage.success("评论成功！");
        })
        .catch((error) => {
          console.error("评论失败:", error);
          ElMessage.error("评论失败，请稍后再试。");
        });
    },
    handleDeleteComment(commentId) {
      this.$confirm("确定要删除这条评论吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          axios
            .delete(`/api/comment/delete/${commentId}`)
            .then((response) => {
              if (response.data.code === 200) {
                ElMessage.success("评论删除成功！");
                this.fetchComments(); // Refresh comments after deletion
              } else {
                ElMessage.error(response.data.message || "删除失败！");
              }
            })
            .catch((error) => {
              console.error("删除评论失败:", error);
              ElMessage.error("删除失败，请稍后再试。");
            });
        })
        .catch(() => {
          // User cancelled
        });
    },
  },
};
</script>

<style scoped>
.comment-form {
  margin-top: 20px;
}
.comment-list {
  margin-top: 20px;
}
.comment-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-avatar {
  margin-right: 15px;
}
.comment-body {
  flex: 1;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}
.comment-time {
  font-size: 12px;
  color: #909399;
}
.comment-content {
  font-size: 14px;
}
</style>
