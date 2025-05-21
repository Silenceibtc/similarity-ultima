<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showNotify} from "vant";
import {BaseResponse} from "../models/response";
import {getCurrentUser} from "../global/UserState.ts";

const route = useRoute();
const router = useRouter();
const editUser = ref({
  editKey: route.query.editKey,
  editName: route.query.editName,
  currentValue: route.query.currentValue,
})
const currentUser = getCurrentUser();
// 处理标签数组的响应式
// 修改 tags 初始化逻辑，处理 currentValue 为 null 的情况
const tags = ref<string[]>(
    editUser.value.editKey === 'tags'
        ? (editUser.value.currentValue || []) as string[]
        : []
);
const showDialog = ref(false);
const newTag = ref('');

const removeTag = (tag: string) => {
  tags.value = tags.value.filter(t => t !== tag);
};

const addTag = () => {
  const tag = newTag.value.trim();
  if (!tag) {
    showFailToast("标签不能为空");
    return;
  }
  if (tags.value.includes(tag)) {
    showFailToast("标签已存在");
    return;
  }
  tags.value.push(tag);
  newTag.value = '';
  showDialog.value = false;
};

const onSubmit = async () => {
  if (!currentUser) {
    showFailToast("用户未登录！");
    return;
  }

  const res: BaseResponse = await myAxios.post('/user/update', {
    id: currentUser.value?.id,
    [editUser.value.editKey as any]: editUser.value.editKey === 'tags'
        ? JSON.stringify(tags.value) // 转换为JSON字符串
        : editUser.value.currentValue
  });

  if (res.code === 0 && res.data > 0) {
    showNotify({type: 'success', message: '更新成功！'});
    router.back();
  } else {
    showNotify({type: 'danger', message: '更新失败！'});
  }
};
</script>

<template>
  <van-form @submit="onSubmit">
    <template v-if="editUser.editKey === 'tags'">
      <van-space wrap>
        <van-tag
            v-for="tag in tags"
            :key="tag"
            closeable
            size="medium"
            type="primary"
            @close="removeTag(tag)"
        >
          {{ tag }}
        </van-tag>
      </van-space>
      <div style="margin: 16px;">
        <van-button
            round
            block
            type="primary"
            @click="showDialog = true"
        >
          添加标签
        </van-button>
      </div>
    </template>

    <template v-else>
      <van-field
          v-model="editUser.currentValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="`请输入${editUser.editName}`"
          :rules="[{ required: true, message: `请输入${editUser.editName}` }]"
      />
    </template>

    <div class="action-buttons" style="margin: 16px;">
      <van-button class="submit-btn" round block type="primary" native-type="submit">
        确认修改
      </van-button>
      <van-button class="cancel-btn" round block type="primary" @click="router.back">
        取消
      </van-button>
    </div>
  </van-form>

  <!-- 添加标签对话框 -->
  <van-dialog v-model:show="showDialog" title="添加新标签" show-cancel-button @confirm="addTag">
    <van-field
        v-model="newTag"
        placeholder="请输入标签内容"
        style="padding: 15px;"
        autofocus
    />
  </van-dialog>
</template>

<style scoped>
.action-buttons {
  margin: 24px 16px;

  .submit-btn {
    margin-bottom: 12px;
  }

  .cancel-btn {
    border-color: #eee;
  }
}
</style>