<!-- src/pages/FriendList.vue -->
<script setup lang="ts">
import { useRouter } from "vue-router";
import myAxios from "../plugins/myAxios.ts";
import { showFailToast } from "vant";
import { FriendVO } from "../models/vo/FriendVO";
import {onMounted, ref} from "vue";

const router = useRouter();
const friendList = ref<FriendVO[]>([]);

// 获取好友列表
const getFriendList = async () => {
  try {
    const res = await myAxios.get("/friend/list");
    if (res.code === 0) {
      friendList.value = res.data;
    } else {
      showFailToast(res.description);
    }
  } catch (error) {
    showFailToast("获取好友列表失败");
  }
};
onMounted(getFriendList);

// 跳转聊天页
const goChat = (friendId: number, username: string) => {
  router.push({ path: `/chat`, query: { targetId: friendId, chatType: 0, name: username } });
};

// 跳转到好友请求页
const gotoRequestList = () => {
  router.push('/friend/request/list');
};
</script>
<template>
  <div class="container">
    <div class="nav-bar">
      <van-button
          type="primary"
          size="small"
          @click="gotoRequestList"
          icon="bell-o"
          block
      >
        待处理请求
      </van-button>
    </div>
    <van-list class="friend-list">
      <van-card
          v-for="friend in friendList"
          :key="friend.id"
          class="friend-card"
          :title="friend.username"
          :thumb="friend.avatarUrl"
      >
        <template #title>
          <div class="card-header">
            <span class="username">{{ friend.username }}</span>
          </div>
        </template>
        <template #footer>
          <van-button
              size="small"
              type="primary"
              icon="message-o"
              @click="goChat(friend.id, friend.username)"
          >
            发消息
          </van-button>
        </template>
      </van-card>
    </van-list>
  </div>
</template>

<style scoped>
.container {
  min-height: 100vh;
  background: #f0f2f5;
}

.nav-bar {
  padding: 12px 16px; /* 调整导航栏内边距 */
  margin-bottom: 12px; /* 减少底部间距 */
}

.friend-list {
  padding: 0 16px; /* 缩小列表左右边距 */
}

.friend-card {
  margin-bottom: 12px; /* 减少卡片间距 */
  border-radius: 12px; /* 缩小圆角 */
  box-shadow: 0 2px 8px rgba(0,0,0,0.03); /* 调整阴影更轻薄 */
  background: #fff;
  padding: 12px; /* 减少卡片内边距 */
}

.card-header {
  gap: 6px; /* 缩小元素间距 */
  margin-bottom: 6px; /* 减少底部间距 */
}

.username {
  font-size: 14px; /* 缩小用户名文字大小 */
  font-weight: 500;
  color: #1f2d3d;
}

::v-deep .van-card__title {
  margin: 0;
}

::v-deep .van-button--primary {
  padding: 6px 12px; /* 缩小按钮内边距 */
  font-size: 13px; /* 缩小按钮文字大小 */
}
</style>
