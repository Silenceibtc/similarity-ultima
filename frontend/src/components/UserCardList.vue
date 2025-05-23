<!-- src/components/UserCardList.vue -->
<script setup lang="ts">
import {UserType} from "../models/user";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUserInfo} from "../services/uesr.ts";
import {onMounted, ref} from "vue";

const props = defineProps({
  userList: {type: Array as () => UserType[], default: () => []}
});

// 获取当前用户信息
const currentUser = ref<any>({});
const getCurrentUser = async () => {
  currentUser.value = await getCurrentUserInfo();
};
onMounted(getCurrentUser);

// 发送好友请求
const sendFriendRequest = async (userId: number) => {
  try {
    const res = await myAxios.post("/friend/request/send", {
      receiverId: userId,

    });
    if (res.code === 0) {
      showSuccessToast("好友请求已发送");
    } else {
      showFailToast(res.description);
    }
  } catch (error) {
    showFailToast("请求发送失败");
  }
};
</script>

<template>
  <van-card
      v-for="user in props.userList"
      :key="user.id"
      :title="user.username || '匿名用户'"
      :thumb="user.avatarUrl"
      :desc="user.profile || '这个人很神秘，什么都没有写'"
  >
    <!-- 原有标签 -->
    <template #tags>
      <van-tag v-for="tag in user.tags" :key="tag" plain type="primary">{{ tag }}</van-tag>
    </template>

    <!-- 新增添加好友按钮 -->
    <template #footer>
      <van-button v-if="currentUser.id !== user.id"
                  size="mini"
                  plain
                  type="primary"
                  @click="sendFriendRequest(user.id)"
      >
        添加好友
      </van-button>
    </template>
  </van-card>
</template>