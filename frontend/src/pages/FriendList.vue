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
</script>

<template>
  <div class="container">
    <van-list>
      <van-card
          v-for="friend in friendList"
          :key="friend.id"
          :title="friend.username"
          :thumb="friend.avatarUrl"
      >
        <template #footer>
          <van-button size="mini" type="primary" @click="goChat(friend.id, friend.username)">
            私信
          </van-button>
        </template>
      </van-card>
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