<!-- src/pages/Message.vue -->
<script setup lang="ts">
import { useRouter } from "vue-router";
import myAxios from "../plugins/myAxios.ts";
import { showFailToast } from "vant";
import { ChatSessionVO } from "../models/vo/ChatSessionVO";
import {onMounted, ref} from "vue"; // 假设已定义ChatSessionVO

const router = useRouter();
const sessionList = ref<ChatSessionVO[]>([]);

// 获取会话列表
const getChatSessions = async () => {
  try {
    const res = await myAxios.get("/chat/sessions");
    if (res.code === 0) {
      sessionList.value = res.data;
    } else {
      showFailToast(res.description);
    }
  } catch (error) {
    showFailToast("获取会话列表失败");
  }
};
onMounted(getChatSessions);

// 跳转聊天页
const goChat = (session: ChatSessionVO) => {
  router.push({
    path: `/chat`,
    query: {
      targetId: session.sessionId,
      chatType: session.sessionType,
      name: session.name
    }
  });
};
</script>

<template>
  <div class="container">
    <van-list>
      <van-cell
          v-for="session in sessionList"
          :key="session.sessionId"
          @click="goChat(session)"
          :title="session.name"
          :label="session.lastMessage"
      >
        <template #icon>
          <van-image :src="session.avatarUrl" round size="40" />
        </template>
        <template #right-icon>
          <div style="font-size: 12px; color: #999;">
            {{ session.lastMessageTime | formatTime }}
          </div>
        </template>
      </van-cell>
    </van-list>
  </div>
</template>

<style scoped>
.container {
  padding: 16px;
  min-height: 100vh;
  background: #f5f6f8;
}
</style>