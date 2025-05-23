<!-- src/pages/Chat.vue -->
<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import myAxios from "../plugins/myAxios.ts";
import { ref, onMounted } from "vue";
import { showFailToast } from "vant";
import { ChatMessage } from "../models/ChatMessage";
import {getCurrentUserInfo} from "../services/uesr.ts";

const route = useRoute();
const router = useRouter();
const messages = ref<ChatMessage[]>([]);
const inputContent = ref("");

// 获取当前用户信息
const currentUser = ref<any>({});
const getCurrentUser = async () => {
  currentUser.value = await getCurrentUserInfo();
};
onMounted(getCurrentUser);

// 获取聊天记录
const getChatMessages = async () => {
  try {
    const res = await myAxios.get("/chat/message/list", {
      params: {
        targetId: route.query.targetId,
        chatType: route.query.chatType
      }
    });
    if (res.code === 0) {
      messages.value = res.data;
    }
  } catch (error) {
    showFailToast("加载消息失败");
  }
};

// 发送消息
const sendMessage = async () => {
  if (!inputContent.value.trim()) return;
  try {
    const res = await myAxios.post("/chat/message/send", {
      chatType: Number(route.query.chatType),
      receiverId: Number(route.query.targetId),
      content: inputContent.value,
      messageType: 0 // 文本消息
    });
    if (res.code === 0) {
      messages.value.push(res.data);
      inputContent.value = "";
    }
  } catch (error) {
    showFailToast("消息发送失败");
  }
};

onMounted(getChatMessages);
</script>

<template>
  <div class="chat-container">
    <!-- 消息列表 -->
    <van-list>
      <div v-for="msg in messages" :key="msg.id" class="message-item">
        <!-- 自己的消息 -->
        <div v-if="msg.senderId === currentUser.id" class="my-message">
          <van-image :src="currentUser.avatarUrl" round size="36" class="avatar" />
          <div class="content">{{ msg.content }}</div>
        </div>
        <!-- 对方的消息 -->
        <div v-else class="other-message">
          <van-image :src="msg.senderAvatar" round size="36" class="avatar" />
          <div class="content">{{ msg.content }}</div>
        </div>
      </div>
    </van-list>

    <!-- 输入框 -->
    <div class="input-bar">
      <van-field
          v-model="inputContent"
          placeholder="输入消息..."
          @keypress.enter="sendMessage"
      />
      <van-button type="primary" @click="sendMessage">发送</van-button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  min-height: 100vh;
  background: #f5f6f8;
  padding: 16px;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
}

.my-message {
  justify-content: flex-end;
}

.other-message {
  justify-content: flex-start;
}

.avatar {
  margin: 0 8px;
}

.content {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 12px;
  background: white;
  word-wrap: break-word;
}

.my-message .content {
  background: #1989fa;
  color: white;
}

.input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: white;
  display: flex;
  gap: 8px;
}
</style>