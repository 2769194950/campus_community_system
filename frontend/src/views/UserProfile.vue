<template>
  <div class="user-profile">
    <el-card>
      <div class="profile-header">
        <el-avatar :size="96" :src="profileUser.avatarUrl" />
        <div class="profile-info">
          <h2>
            {{ profileUser.username }}
            <small v-if="profileUser.id">（ID: {{ profileUser.id }}）</small>
          </h2>
          <p class="intro">
            {{ profileUser.introduction || "这个人很懒，什么都没有留下..." }}
          </p>
          <div class="meta">
            <span v-if="profileUser.email">{{ profileUser.email }}</span>
            <span v-if="followCounts">
              · 粉丝 {{ followCounts.followers }} | 我的关注
              {{ followCounts.myFollowees }}</span
            >
          </div>
        </div>

        <!-- 行为区：自己主页显示“编辑”，他人主页显示“关注/私信” -->
        <div v-if="me && me.id === profileUser.id">
          <el-button type="primary" @click="$router.push('/profile/edit')"
            >编辑资料</el-button
          >
        </div>
        <div v-else>
          <el-button
            :type="followed ? 'default' : 'primary'"
            @click="toggleFollow"
          >
            {{ followed ? "取消关注" : "关注" }}
          </el-button>
          <el-button @click="openMsgDialog">私信</el-button>
        </div>
      </div>
    </el-card>

    <el-tabs v-model="active" class="tabs">
      <el-tab-pane label="我发布的帖子" name="posts">
        <PostList
          :user-id="profileUser.id"
          v-if="active === 'posts' && profileUser.id"
        />
      </el-tab-pane>

      <el-tab-pane label="我的收藏" name="favorites">
        <FavoriteList
          :user-id="profileUser.id"
          v-if="active === 'favorites' && profileUser.id"
        />
      </el-tab-pane>

      <el-tab-pane label="我的评论" name="comments">
        <MyComments
          :user-id="profileUser.id"
          v-if="active === 'comments' && profileUser.id"
        />
      </el-tab-pane>

      <el-tab-pane label="我点赞的帖子" name="likes">
        <MyLikes
          :user-id="profileUser.id"
          v-if="active === 'likes' && profileUser.id"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 私信弹窗 -->
    <el-dialog
      v-model="msgDialog"
      :title="'发送私信给：' + profileUser.username"
      width="520px"
    >
      <el-input
        v-model="msgContent"
        type="textarea"
        :rows="5"
        maxlength="500"
        show-word-limit
        placeholder="说点什么吧~"
      />
      <template #footer>
        <el-button @click="msgDialog = false">取 消</el-button>
        <el-button type="primary" @click="sendMessage">发 送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage } from "element-plus";
import { mapState } from "vuex";

import PostList from "@/components/PostList.vue";
import FavoriteList from "@/components/FavoriteList.vue";
import MyComments from "@/components/MyComments.vue";
import MyLikes from "@/components/MyLikes.vue";

export default {
  name: "UserProfile",
  components: { PostList, FavoriteList, MyComments, MyLikes },
  props: { id: { type: [String, Number], required: true } },
  data() {
    return {
      profileUser: {},
      followed: false,
      followCounts: null,
      active: "posts",
      msgDialog: false,
      msgContent: "",
    };
  },
  computed: {
    ...mapState(["user"]),
    me() {
      return this.user;
    },
  },
  watch: {
    id: {
      immediate: true,
      handler(v) {
        if (v) this.fetchProfile(v);
      },
    },
  },
  methods: {
    fetchProfile(uid) {
      axios.get(`/api/user/profile/${uid}`).then(({ data }) => {
        if (data.code === 200) {
          this.profileUser = data.data;
          this.refreshFollowInfo();
        }
      });
    },
    refreshFollowInfo() {
      axios
        .get("/api/follow/status", {
          params: { targetId: this.profileUser.id },
        })
        .then(({ data }) => {
          if (data.code === 200) this.followed = !!data.data;
        });
      axios
        .get(`/api/follow/counts/${this.profileUser.id}`)
        .then(({ data }) => {
          if (data.code === 200) this.followCounts = data.data;
        });
    },
    toggleFollow() {
      axios.post(`/api/follow/user/${this.profileUser.id}`).then(({ data }) => {
        if (data.code === 200) {
          this.followed = data.data.followed;
          this.followCounts = {
            ...(this.followCounts || {}),
            followers: data.data.followerCount,
          };
        }
      });
    },
    openMsgDialog() {
      this.msgDialog = true;
    },
    sendMessage() {
      if (!this.msgContent.trim()) return ElMessage.warning("内容不能为空");
      axios
        .post("/api/message/letter/send", {
          toName: this.profileUser.username,
          content: this.msgContent,
        })
        .then(({ data }) => {
          if (data.code === 200) {
            ElMessage.success("已发送");
            this.msgDialog = false;
            this.msgContent = "";
          } else ElMessage.error(data.msg || "发送失败");
        });
    },
  },
};
</script>

<style scoped>
.user-profile {
  max-width: 960px;
  margin: 0 auto;
}
.profile-header {
  display: flex;
  gap: 16px;
  align-items: center;
}
.profile-info {
  flex: 1;
}
.intro {
  margin: 0.25rem 0 0.5rem;
  color: #666;
}
.meta {
  color: #999;
}
.tabs {
  margin-top: 16px;
}
</style>
