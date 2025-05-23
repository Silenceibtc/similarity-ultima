<!-- src/pages/FriendRequestList.vue -->
<script setup lang="ts">
import myAxios from "../plugins/myAxios.ts";
import { showFailToast, showSuccessToast } from "vant";
import { onMounted, ref } from "vue";
import { FriendRequest } from "../models/domain/FriendRequest"; // 需补充类型定义

const friendRequests = ref<FriendRequest[]>([]);

// 获取待处理的好友请求
const getPendingRequests = async () => {
  try {
    const res = await myAxios.get("/friend/request/pending");
    if (res.code === 0) {
      friendRequests.value = res.data;
    } else {
      showFailToast(res.description);
    }
  } catch (error) {
    showFailToast("获取好友请求失败");
  }
};

// 处理好友请求（同意/拒绝）
const handleRequest = async (requestId: number, status: number) => {
  try {
    const res = await myAxios.post("/friend/request/handle", {
      requestId,
      status
    });
    if (res.code === 0) {
      showSuccessToast(status === 1 ? "已同意" : "已拒绝");
      getPendingRequests(); // 刷新列表
    } else {
      showFailToast(res.description);
    }
  } catch (error) {
    showFailToast("操作失败");
  }
};

onMounted(getPendingRequests);
</script>

<template>
  <div class="container">
    <van-list>
      <van-card
          v-for="request in friendRequests"
          :key="request.id"
          :title="request.senderUsername"
      :thumb="request.senderAvatarUrl"
      >
      <template #desc>
        备注：{{ request.remark || '无备注' }}
      </template>
      <template #footer>
        <van-button
            size="mini"
            type="primary"
            @click="handleRequest(request.id, 1)"
        >
          同意
        </van-button>
        <van-button
            size="mini"
            type="danger"
            class="ml-4"
            @click="handleRequest(request.id, 2)"
        >
          拒绝
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
.ml-4 {
  margin-left: 8px;
}
</style>