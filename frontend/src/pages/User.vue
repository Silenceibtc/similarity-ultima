<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUserInfo, uploadAvatar} from "../services/uesr.ts";
import {showFailToast, showImagePreview, showLoadingToast, showSuccessToast} from 'vant';
import {UserType} from "../models/user";
import myAxios from "../plugins/myAxios.ts";
import {BaseResponse} from "../models/response";
import {setCurrentUser} from "../global/UserState.ts";

const currentUser = ref<UserType>({});

onMounted(async () => {
  currentUser.value = await getCurrentUserInfo();
  if (currentUser.value.tags) {
    currentUser.value.tags = currentUser.value.tags ? JSON.parse(currentUser.value.tags) : [];
  }
})

const router = useRouter()
const doEdit = (editKey: string, editName: string, currentValue: any) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue,
    }
  })
}

const previewAvatar = () => {
  if (currentUser.value?.avatarUrl) {
    showImagePreview([currentUser.value.avatarUrl]);
  }
}
// 修改性别
const gender = ref();
const showPicker = ref(false);
const columns = [
  {text: '男', value: 1},
  {text: '女', value: 0},
];

const onConfirm = async ({selectedOptions}) => {
  gender.value = selectedOptions[0]?.value;
  showPicker.value = false;
  const res: BaseResponse = await myAxios.post('/user/update', {
    id: currentUser.value.id,
    gender: gender.value,
  })
  if (res.code === 0 && res.data > 0) {
    showSuccessToast('更新成功')
    currentUser.value.gender = gender.value;
  } else {
    showFailToast('更新失败')
  }
};

// 头像上传处理
const uploading = ref(false);
const handleAvatarUpload = async (file: File) => {
  if (!file.type.startsWith('image/')) {
    showFailToast('请选择图片文件');
    return;
  }
  uploading.value = true;
  const toast = showLoadingToast({
    message: '上传中...',
    forbidClick: true,
    duration: 0,
  });

  try {
    // 上传头像
    const avatarUrl = await uploadAvatar(file);
    // 更新本地数据
    currentUser.value.avatarUrl = avatarUrl;
    showSuccessToast('头像更新成功');
  } catch (error) {
    showFailToast('头像上传失败');
  } finally {
    uploading.value = false;
  }
};

// 修改后的头像单元格点击处理
const handleAvatarClick = () => {
  // 创建隐藏的file input
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.onchange = (e: Event) => {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) {
      handleAvatarUpload(file);
    }
  };
  input.click();
};
// 退出登录
const logout = async () => {
  setCurrentUser(null)
  const res: BaseResponse = await myAxios.post('/user/logout');
  if (res.code === 0 && res.data) {
    showSuccessToast('退出登录成功')
    router.push('/user/login')
  }
}
</script>

<template>
  <div class="user-container" v-if="currentUser">
    <!-- 顶部个人信息卡片 -->
    <div class="profile-card">
      <van-image
          round
          fit="cover"
          width="80px"
          height="80px"
          :src="currentUser.avatarUrl"
          @click="previewAvatar"
          class="user-avatar"
      />
      <div class="profile-info">
        <div class="name-section">
          <h2 class="username">{{ currentUser.username ? currentUser.username : '暂无昵称' }}</h2>
          <van-tag
              :color="currentUser.gender === 1 ? '#4a90e2' : '#ff7ac6'"
              class="gender-tag"
          >
            {{ currentUser.gender === 1 ? '♂ 男' : '♀ 女' }}
          </van-tag>
        </div>
        <p class="account">@{{ currentUser.userAccount }}</p>
        <p class="join-date">注册于 {{ currentUser.createTime }}</p>
      </div>
    </div>

    <!-- 详细信息区域 -->
    <div class="detail-section">
      <h3 class="section-title">账户信息</h3>
      <van-cell-group inset>
        <van-cell title="手机号码"
                  :value="currentUser.phone ? currentUser.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未绑定'"
                  is-link @click="doEdit('phone', '手机号码', currentUser.phone)"/>
        <van-cell title="电子邮箱"
                  :value="currentUser.email ? currentUser.email.replace(/(.{2}).*@/, '$1****@') : '未绑定'"
                  is-link @click="doEdit('email', '电子邮箱', currentUser.email)"/>
      </van-cell-group>

      <h3 class="section-title" style="margin-top: 24px;">个人资料</h3>
      <van-cell-group inset>
        <van-cell title="用户头像" is-link @click="handleAvatarClick">
          <template #value>
            <div class="avatar-preview">
              <van-image
                  round
                  fit="cover"
                  width="36px"
                  height="36px"
                  :src="currentUser.avatarUrl"
              />
              <!-- 上传状态指示 -->
              <van-loading v-if="uploading" class="upload-indicator"/>
            </div>
          </template>
        </van-cell>
        <van-cell title="用户昵称" :value="currentUser.username ? currentUser.username : '暂无昵称'" is-link
                  @click="doEdit('username', '昵称', currentUser.username)"/>
        <van-cell title="个人简介" :value="currentUser.profile || '暂无简介'" is-link
                  @click="doEdit('profile', '个人简介', currentUser.profile)"/>
        <van-cell
            v-model="gender"
            is-link
            readonly
            name="gender"
            title="用户性别"
            :value="currentUser.gender === 1 ? '男' : currentUser.gender === 0 ? '女' : '未填写'"
            @click="showPicker = true"
        />
        <van-popup v-model:show="showPicker" destroy-on-close position="bottom">
          <van-picker
              :columns="columns"
              @confirm="onConfirm"
              @cancel="showPicker = false"
          />
        </van-popup>
        <van-cell title="用户标签" is-link @click="doEdit('tags', '用户标签', currentUser.tags)">
          <template #value>
            <van-tag v-for="(tag, index) in currentUser.tags" :key="index" type="primary" size="small"
                     style="margin: 2px">
              {{ tag }}
            </van-tag>
            <span v-if="!currentUser.tags?.length">暂无标签</span>
          </template>
        </van-cell>
      </van-cell-group>
      <h3 class="section-title" style="margin-top: 24px;">队伍</h3>
      <van-cell-group inset>
        <van-cell title="我创建的队伍" is-link to="/user/teamCreate"/>
        <van-cell title="我加入的队伍" is-link to="/user/teamJoin"/>
      </van-cell-group>

      <!-- 新增我的好友入口 -->
      <h3 class="section-title" style="margin-top: 24px;">社交</h3>
      <van-cell-group inset>
        <van-cell
            title="我的好友"
            is-link
            to="/friend/list"
        />
      </van-cell-group>
    </div>


    <!-- 安全退出 -->
    <div class="logout-section">
      <van-button round block type="default" class="logout-btn" @click="logout">退出登录</van-button>
    </div>
  </div>
</template>

<style scoped>
.user-container {
  padding: 16px;
  background: #f5f6f8;
  min-height: 100vh;
}

.profile-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-avatar {
  border: 3px solid #f0f2f5;
  margin-right: 16px;
}

.profile-info {
  flex: 1;
}

.name-section {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.username {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #1a1a1a;
}

.gender-tag {
  margin-left: 8px;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
}

.account {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.join-date {
  margin: 4px 0 0;
  font-size: 12px;
  color: #999;
}

.detail-section {
  margin-top: 16px;
}

.section-title {
  color: #909399;
  font-size: 14px;
  font-weight: normal;
  padding: 0 16px 8px;
}

.logout-section {
  margin: 40px 16px 0;
}

.logout-btn {
  color: #ff4444;
  border: 1px solid #ffe0e0;
}
</style>