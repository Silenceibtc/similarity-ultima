<script setup lang="ts">
// 逻辑层保持不变（无需修改）
import {useRoute, useRouter} from "vue-router";
import myAxios from "../plugins/myAxios.ts";
import {ref, onMounted} from "vue";
import {showFailToast} from "vant";
import {ChatMessage} from "../models/ChatMessage";
import {getCurrentUserInfo} from "../services/uesr.ts";  // 注：检查路径是否正确

const route = useRoute();
const router = useRouter();
const messages = ref<ChatMessage[]>([]);
const inputContent = ref("");

const currentUser = ref<any>({});
const getCurrentUser = async () => {
  currentUser.value = await getCurrentUserInfo();
};
onMounted(getCurrentUser);

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

const sendMessage = async () => {
  if (!inputContent.value.trim()) return;
  try {
    const res = await myAxios.post("/chat/send", {
      chatType: Number(route.query.chatType),
      receiverId: Number(route.query.targetId),
      content: inputContent.value,
      messageType: 0
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
    <van-list>
      <div v-for="msg in messages" :key="msg.id" class="message-item">
        <!-- 自己的消息（发送方） -->
        <div v-if="msg.senderId === currentUser.id" class="message-content my-message">
          <van-image :src="currentUser.avatarUrl" round width="50px" height="50px" class="avatar"/>
          <div class="content">{{ msg.content }}</div>
        </div>
        <!-- 对方的消息（接收方） -->
        <div v-else class="message-content other-message">
          <van-image :src="msg.senderAvatar" round width="50px" height="50px" class="avatar"/>
          <div class="content">{{ msg.content }}</div>
        </div>
      </div>
    </van-list>

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
}

/* 消息项外层容器 */
.message-item {
  display: flex;
  margin-bottom: 16px;
  padding: 0 12px;
}

/* 消息内容容器（关键调整点） */
.message-content {
  display: flex;
  align-items: flex-end; /* 内容与头像底部对齐 */
  max-width: 80%;
}

/* 发送方消息样式 */
.my-message {
  margin-left: auto; /* 右对齐 */
  flex-direction: row-reverse; /* 内容→头像 反转排列 */
}
.my-message .avatar {
  margin-left: 8px; /* 头像在右侧时与内容的间距 */
}
.my-message .content {
  background: #1989fa;
  color: white;
}

/* 接收方消息样式 */
.other-message {
  margin-right: auto; /* 左对齐 */
  flex-direction: row; /* 头像→内容 正常排列 */
}
.other-message .avatar {
  margin-right: 8px; /* 头像在左侧时与内容的间距 */
}
.other-message .content {
  background: white;
  color: #333;
}

/* 通用内容气泡样式 */
.content {
  padding: 8px 12px;
  border-radius: 12px;
  word-wrap: break-word;
  max-width: calc(100% - 60px); /* 预留头像宽度（50px+左右间距） */
}

/* 头像样式 */
.avatar {
  flex-shrink: 0; /* 防止头像被压缩 */
  width: 50px;
  height: 50px;
}

.input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  gap: 8px;
  padding: 10px 12px 50px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.05);
}
</style>
