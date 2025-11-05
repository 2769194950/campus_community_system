<template>
  <div class="post-detail-container" v-if="post">
    <el-card class="post-card">
      <template #header>
        <h2 class="post-title">{{ post.title }}</h2>
        <div class="post-meta">
          <!-- 这里换成可跳转的 router-link -->
          <span>
            作者：
            <router-link :to="'/profile/' + (post.userId || post.user.id)">
              {{ post.user.username }}
            </router-link>
          </span>
          <span>发布于: {{ formatDate(post.createTime) }}</span>
          <span><i class="el-icon-view"></i> {{ post.viewCount }}</span>
          <el-button
            type="primary"
            size="small"
            @click="showSendMessageModal = true"
            v-if="user && user.id !== post.user.id"
            style="margin-left: 10px"
            >发私信</el-button
          >
        </div>
      </template>
      <div class="post-content" v-html="post.content"></div>
      <div class="post-actions">
        <el-button
          :type="likeStatus === 1 ? 'primary' : 'default'"
          @click="handleLike"
        >
          <i class="el-icon-thumb"></i> 点赞 {{ likeCount }}
        </el-button>
        <el-button
          :type="isFavorited ? 'warning' : 'default'"
          @click="handleFavorite"
        >
          <i :class="isFavorited ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
          {{ isFavorited ? "已收藏" : "收藏" }}
        </el-button>
        <el-button
          v-if="user && user.type === 1"
          type="danger"
          @click="handleDeletePost"
        >
          <i class="el-icon-delete"></i> 删除帖子
        </el-button>
      </div>
    </el-card>

    <CommentList :entityType="1" :entityId="post.id" />

    <el-dialog title="发送私信" v-model="showSendMessageModal" width="30%">
      <el-input
        type="textarea"
        :rows="5"
        placeholder="请输入私信内容..."
        v-model="messageContent"
      ></el-input>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSendMessageModal = false">取 消</el-button>
          <el-button type="primary" @click="sendMessage">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <el-skeleton v-else :rows="10" animated />
</template>

<script>
import axios from "axios";
import { mapState } from "vuex";
import dayjs from "dayjs";
import { ElMessage } from "element-plus";
import CommentList from "@/components/CommentList.vue";

export default {
  name: "PostDetail",
  props: ["id"],
  components: {
    CommentList,
  },
  data() {
    return {
      post: null,
      likeCount: 0,
      likeStatus: 0,
      isFavorited: false,
      showSendMessageModal: false,
      messageContent: "",
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  created() {
    this.fetchPostDetail();
  },
  methods: {
    formatDate(date) {
      return dayjs(date).format("YYYY-MM-DD HH:mm");
    },
    fetchPostDetail() {
      axios.get(`/api/post/detail/${this.id}`).then((response) => {
        if (response.data.code === 200) {
          this.post = response.data.data;
          this.likeCount = this.post.likeCount;
          if (this.user) {
            this.fetchLikeStatus();
            this.fetchFavoriteStatus();
          }
        }
      });
    },
    fetchLikeStatus() {
      axios
        .get(`/api/like/status`, {
          params: { entityType: 1, entityId: this.post.id },
        })
        .then((response) => {
          if (response.data.code === 200) {
            this.likeStatus = response.data.data;
          }
        });
    },
    handleLike() {
      if (!this.user) {
        ElMessage.warning("请先登录再点赞！");
        return;
      }
      axios
        .post("/api/like", {
          entityType: 1,
          entityId: this.post.id,
          entityUserId: this.post.userId,
        })
        .then((response) => {
          if (response.data.code === 200) {
            this.likeCount = response.data.data.likeCount;
            this.likeStatus = response.data.data.likeStatus;
            ElMessage.success(
              this.likeStatus === 1 ? "点赞成功！" : "取消点赞成功！"
            );
          }
        })
        .catch((error) => {
          console.error("点赞失败:", error);
          ElMessage.error("操作失败，请稍后再试。");
        });
    },
    fetchFavoriteStatus() {
      axios
        .get(`/api/favorite/status`, { params: { postId: this.post.id } })
        .then((response) => {
          if (response.data.code === 200) {
            this.isFavorited = response.data.data;
          }
        });
    },
    handleFavorite() {
      if (!this.user) {
        ElMessage.warning("请先登录再收藏！");
        return;
      }
      axios
        .post("/api/favorite", { postId: this.post.id })
        .then((response) => {
          if (response.data.code === 200) {
            this.isFavorited = response.data.data.isFavorited;
            // Optionally update a favorite count if you add it to the post model
            ElMessage.success(
              this.isFavorited ? "收藏成功！" : "取消收藏成功！"
            );
          }
        })
        .catch((error) => {
          console.error("收藏操作失败:", error);
          ElMessage.error("操作失败，请稍后再试。");
        });
    },
    sendMessage() {
      if (!this.messageContent.trim()) {
        ElMessage.warning("私信内容不能为空！");
        return;
      }
      axios
        .post("/api/message/letter/send", {
          toName: this.post.user.username,
          content: this.messageContent,
        })
        .then((response) => {
          if (response.data.code === 200) {
            ElMessage.success("私信发送成功！");
            this.showSendMessageModal = false;
            this.messageContent = "";
          } else {
            ElMessage.error(response.data.message || "发送失败，请稍后再试。");
          }
        })
        .catch((error) => {
          console.error("发送私信失败:", error);
          ElMessage.error("服务异常，请联系管理员。");
        });
    },
    handleDeletePost() {
      this.$confirm("确定要删除这个帖子吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          axios
            .delete(`/api/post/delete/${this.post.id}`)
            .then((response) => {
              if (response.data.code === 200) {
                ElMessage.success("帖子删除成功！");
                this.$router.push("/");
              } else {
                ElMessage.error(response.data.message || "删除失败！");
              }
            })
            .catch((error) => {
              console.error("删除帖子失败:", error);
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
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
}
.post-card {
  margin-bottom: 20px;
}
.post-title {
  margin: 0;
}
.post-meta {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}
.post-meta span {
  margin-right: 15px;
}
.post-content {
  line-height: 1.8;
  padding: 20px 0;
}
.post-actions {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>
