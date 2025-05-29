<!-- src/pages/Message.vue -->
<script setup lang="ts">
import { useRouter } from "vue-router";
import myAxios from "../plugins/myAxios.ts";
import { showFailToast } from "vant";
import {onMounted, ref} from "vue";
import {ChatSessionVO} from "../models/vo/ChatSessionVO"; // 假设已定义ChatSessionVO

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
          <van-image :src="session.avatarUrl" round width="40px" height="40px" />
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