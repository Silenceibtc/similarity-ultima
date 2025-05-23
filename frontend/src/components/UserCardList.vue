<script setup lang="ts">
import {UserType} from "../models/user";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {onMounted, ref} from "vue";
import {getCurrentUserInfo} from "../services/uesr.ts";

const props = defineProps({
  userList: {type: Array as () => UserType[], default: () => []}
});

// 响应式变量
const currentUser = ref<any>({});
const showRemarkDialog = ref(false); // 控制备注对话框显示
const friendRemark = ref<string>(''); // 存储输入的备注
const targetUserId = ref<number>(0); // 记录目标用户ID

// 获取当前用户信息
const getCurrentUser = async () => {
  currentUser.value = await getCurrentUserInfo();
};
onMounted(getCurrentUser);

// 处理添加好友逻辑
const handleAddFriend = (userId: number) => {
  targetUserId.value = userId;
  friendRemark.value = ''; // 每次打开对话框清空输入
  showRemarkDialog.value = true;
};

// 发送好友请求
const sendFriendRequest = async () => {
  try {
    const res = await myAxios.post("/friend/request/send", {
      receiverId: targetUserId.value,
      remark: friendRemark.value.trim() // 传递输入的备注（去除首尾空格）
    });

    if (res.code === 0) {
      showSuccessToast("好友请求已发送");
      showRemarkDialog.value = false; // 关闭对话框
    } else {
      showFailToast(res.description);
    }
  } catch (error) {
    console.error("好友请求发送失败", error);
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
    <template #tags>
      <van-tag v-for="tag in user.tags" :key="tag" plain type="primary">{{ tag }}</van-tag>
    </template>

    <template #footer>
      <van-button
          v-if="currentUser.id !== user.id"
          size="mini"
          plain
          type="primary"
          @click="handleAddFriend(user.id)"
      >
        添加好友
      </van-button>
    </template>
  </van-card>

  <!-- 输入备注对话框 -->
  <van-dialog
      v-model:show="showRemarkDialog"
      title="添加好友"
      show-cancel-button
      @confirm="sendFriendRequest"
      @cancel="showRemarkDialog = false"
  >
    <van-field
        v-model="friendRemark"
        type="text"
        label="备注"
        placeholder="请输入好友备注（可选）"
        maxlength="20"
    />
    <div class="van-dialog__message" style="font-size: 12px; color: #969799; margin-top: 8px">
      备注将仅用于您的好友列表显示（可选，不填则使用默认名称）
    </div>
  </van-dialog>
</template>

<style scoped>
/* 可选样式调整 */
.van-field__label {
  width: 60px;
}
</style>
