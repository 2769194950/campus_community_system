<template>
  <div class="mc">
    <div class="mc-layout">
      <!-- ===== 左侧：会话列表 ===== -->
      <aside class="mc-sidebar">
        <div class="mc-sidebar__toolbar">
          <el-input
            v-model="q"
            size="small"
            placeholder="搜索用户/会话，回车打开"
            clearable
            @keyup.enter.native="openFirstMatch"
          />
          <el-button size="small" @click="loadConvs">刷新</el-button>
        </div>

        <el-scrollbar class="mc-convlist">
          <div
            v-for="c in filteredConvs"
            :key="c.conversationId"
            :class="['mc-conv', { active: c.conversationId === currentConvId }]"
            @click="openConv(c)"
          >
            <div class="mc-conv__avatar">
              <el-avatar :size="40" :src="c.target?.avatarUrl || ''">
                {{ (c.target?.username || "").slice(0, 1) }}
              </el-avatar>
            </div>

            <div class="mc-conv__content">
              <div class="mc-conv__row">
                <div class="mc-conv__name ellips">
                  {{ c.target?.username || "用户#" + c.target?.id }}
                </div>
                <div class="mc-conv__time">{{ fmt(c.last?.createTime) }}</div>
              </div>
              <div class="mc-conv__preview ellips">{{ c.last?.content }}</div>
            </div>

            <el-badge
              :value="c.unread"
              :hidden="!c.unread"
              class="mc-conv__badge"
            />
          </div>

          <el-empty
            v-if="!filteredConvs.length"
            description="暂无会话或未匹配到结果"
            :image-size="64"
          />
        </el-scrollbar>
      </aside>

      <!-- ===== 右侧：聊天窗 ===== -->
      <main class="mc-chat">
        <div class="mc-chat__header" v-if="currentTarget">
          <div class="mc-chat__title">
            与 <b>{{ currentTarget.username }}</b> 的会话
          </div>
          <el-button size="small" @click="loadLetters">刷新</el-button>
        </div>
        <div class="mc-chat__placeholder" v-else>
          <el-empty description="请选择左侧一个会话" />
        </div>

        <template v-if="currentTarget">
          <el-scrollbar ref="scroller" class="mc-msglist">
            <div
              v-for="m in letters"
              :key="m.id"
              :class="['mc-msg', m.fromId === me.id ? 'mine' : 'other']"
            >
              <div class="mc-msg__avatar" v-if="m.fromId !== me.id">
                <el-avatar :size="32" :src="currentTarget.avatarUrl || ''">
                  {{ (currentTarget.username || "").slice(0, 1) }}
                </el-avatar>
              </div>

              <div class="mc-msg__bubble">
                <div class="bubble">{{ m.content }}</div>
                <div class="time">{{ fmt(m.createTime) }}</div>
              </div>

              <div class="mc-msg__avatar" v-if="m.fromId === me.id">
                <el-avatar :size="32" :src="me.avatarUrl || ''">
                  {{ (me.username || "").slice(0, 1) }}
                </el-avatar>
              </div>
            </div>
          </el-scrollbar>

          <div class="mc-composer">
            <el-input
              v-model="draft"
              type="textarea"
              :rows="3"
              maxlength="500"
              show-word-limit
              placeholder="回个消息吧~"
              @keyup.enter.exact.native="send"
            />
            <div class="mc-composer__actions">
              <el-button type="primary" @click="send">发送</el-button>
            </div>
          </div>
        </template>
      </main>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
import { mapState } from "vuex";
import { ElMessage } from "element-plus";
import SockJS from "sockjs-client";
import { Client as StompClient } from "@stomp/stompjs";

export default {
  name: "MessageCenter",
  data() {
    return {
      q: "",
      convs: [],
      currentConvId: "",
      letters: [],
      draft: "",
      client: null,
      polling: null,
    };
  },
  computed: {
    ...mapState(["user"]),
    me() {
      return this.user || {};
    },
    filteredConvs() {
      const s = this.q.trim().toLowerCase();
      if (!s) return this.convs;
      return this.convs.filter(
        (c) =>
          (c.target?.username || "").toLowerCase().includes(s) ||
          (c.last?.content || "").toLowerCase().includes(s)
      );
    },
    currentTarget() {
      const c = this.convs.find((x) => x.conversationId === this.currentConvId);
      return c ? c.target : null;
    },
  },
  mounted() {
    this.loadConvs();
    this.initWs();
  },
  beforeUnmount() {
    if (this.polling) clearInterval(this.polling);
    if (this.client) this.client.deactivate();
  },
  methods: {
    fmt(t) {
      return t ? dayjs(t).format("YYYY-MM-DD HH:mm") : "";
    },

    openFirstMatch() {
      if (!this.filteredConvs.length) {
        ElMessage.info("未找到匹配的会话");
        return;
      }
      this.openConv(this.filteredConvs[0]);
    },

    async loadConvs() {
      try {
        const { data } = await axios.get("/api/message/conversations");
        this.convs = data?.data || [];
        if (!this.currentConvId && this.convs.length) {
          this.openConv(this.convs[0]);
        } else {
          const cur = this.convs.find(
            (c) => c.conversationId === this.currentConvId
          );
          if (cur) this.$nextTick(() => this.scrollBottom());
        }
      } catch (e) {
        console.error("loadConvs error", e);
      }
    },

    async openConv(c) {
      if (!c) return;
      this.currentConvId = c.conversationId;
      await this.loadLetters();
      if (this.polling) clearInterval(this.polling);
      this.polling = setInterval(this.loadLetters, 5000);
      this.$nextTick(() => this.scrollBottom());
    },

    async loadLetters() {
      if (!this.currentConvId) return;
      try {
        const { data } = await axios.get("/api/message/letters", {
          params: { conversationId: this.currentConvId, limit: 200, offset: 0 },
        });
        this.letters = data?.data || [];
        this.$nextTick(() => this.scrollBottom());
      } catch (e) {
        console.error("loadLetters error", e);
      }
    },

    async send() {
      const content = (this.draft || "").trim();
      if (!content) return ElMessage.warning("内容不能为空");
      if (!this.currentTarget) return ElMessage.error("未选择会话");

      try {
        const { data } = await axios.post("/api/message/letter/send", {
          toName: this.currentTarget.username,
          content,
        });
        if (data.code !== 200) {
          return ElMessage.error(data.msg || "发送失败");
        }
        const m = data.data.message;
        const returnedConvId = data.data.conversationId;

        if (returnedConvId !== this.currentConvId) {
          this.currentConvId = returnedConvId;
          await this.loadConvs();
          await this.loadLetters();
        } else {
          this.letters.push(m);
        }
        this.draft = "";
        this.$nextTick(() => this.scrollBottom());
      } catch (e) {
        console.error("send error", e);
        ElMessage.error("发送失败，请稍后再试");
      }
    },

    scrollBottom() {
      const s = this.$refs.scroller;
      if (s && s.wrapRef) s.wrapRef.scrollTop = s.wrapRef.scrollHeight;
    },

    initWs() {
      try {
        const url = `${location.origin.replace("http", "ws")}/ws`;
        this.client = new StompClient({
          webSocketFactory: () => new SockJS(url),
          reconnectDelay: 5000,
          onConnect: () => {
            this.client.subscribe("/user/queue/chat", (frame) => {
              const msg = JSON.parse(frame.body || "{}");
              if (msg.conversationId === this.currentConvId) {
                this.letters.push(msg);
                this.$nextTick(() => this.scrollBottom());
              }
              this.loadConvs();
            });
          },
          debug: () => {},
        });
        this.client.activate();
      } catch (e) {
        console.warn("WS init failed", e);
      }
    },
  },
};
</script>

<style scoped>
/* 布局 */
.mc {
  max-width: 1100px;
  margin: 0 auto;
  padding: 12px;
}
.mc-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 12px;
}

/* 左侧 */
.mc-sidebar {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 10px;
}
.mc-sidebar__toolbar {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  margin-bottom: 8px;
}
.mc-convlist {
  height: 76vh;
}

.mc-conv {
  display: grid;
  grid-template-columns: 48px 1fr auto;
  gap: 10px;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  position: relative;
  transition: background 0.15s ease;
}
.mc-conv:hover {
  background: #f7f8fa;
}
.mc-conv.active {
  background: #eef5ff;
}

.mc-conv__avatar {
  display: flex;
  align-items: center;
  justify-content: center;
}
.mc-conv__content {
  min-width: 0;
}
.mc-conv__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.mc-conv__name {
  font-weight: 600;
}
.mc-conv__time {
  color: #999;
  font-size: 12px;
  margin-left: 8px;
}
.mc-conv__preview {
  color: #666;
  font-size: 13px;
  margin-top: 2px;
}
.mc-conv__badge {
  position: absolute;
  right: 8px;
  top: 8px;
}
.ellips {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 右侧聊天窗 */
.mc-chat {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 10px;
  min-height: 76vh;
  display: flex;
  flex-direction: column;
}
.mc-chat__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2px 0 8px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 8px;
}
.mc-chat__title {
  font-weight: 700;
}
.mc-chat__placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 76vh;
}

.mc-msglist {
  flex: 1;
  min-height: 0;
  border: 1px solid #f1f1f1;
  border-radius: 10px;
  background: #fafbfc;
  padding: 10px;
}

.mc-msg {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  margin: 8px 0;
  max-width: 70%;
}
.mc-msg.other {
  justify-content: flex-start;
}
.mc-msg.mine {
  margin-left: auto;
  justify-content: flex-end;
}

.mc-msg__bubble {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}
.mc-msg.mine .mc-msg__bubble {
  align-items: flex-end;
}

.bubble {
  background: #fff;
  border: 1px solid #e6e6e6;
  border-radius: 10px;
  padding: 8px 12px;
  line-height: 1.6;
  word-break: break-word;
}
.mc-msg.mine .bubble {
  background: #e6f4ff;
  border-color: #cfe7ff;
}

.time {
  color: #aaa;
  font-size: 12px;
}

.mc-composer {
  margin-top: 10px;
}
.mc-composer__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
