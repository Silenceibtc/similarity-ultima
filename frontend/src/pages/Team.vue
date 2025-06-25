<script setup lang="ts">
import {useRouter} from "vue-router";
import TeamCardList from "../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {BaseResponse} from "../models/response";

const router = useRouter();
const searchText = ref('');
const teamList = ref([]);
const activeName = ref("public");
const listTeams = async (val= '', teamStatus= 0) => {
  const res: BaseResponse = await myAxios.get('/team/list', {
    params: {
      pageNum: 1,
      pageSize: 10,
      searchText: val,
      teamStatus
    }
  });
  if (res.code === 0) {
    teamList.value = res.data.records;
    showSuccessToast('获取队伍列表成功');
  } else {
    showFailToast('获取队伍列表失败，请刷新重试');
  }
}
onMounted(() => {
  listTeams();
});
const onSearch = (val: string) => {
  listTeams(val);
}
const onTabChange = () => {
  if (activeName.value == "public") {
    listTeams('', 0);
  } else {
    listTeams('', 2);
  }
}
const createTeam = () => {
  router.push('/team/create');
}
</script>

<template>
  <div class="container">
    <van-search id="search" v-model="searchText" placeholder="请输入搜索队伍名称或队伍描述关键词" @search="onSearch"/>
    <!-- 队伍列表 -->
    <van-tabs v-model:active="activeName" @change="onTabChange">
      <van-tab title="公开" name="public"></van-tab>
      <van-tab title="加密" name="encryption"></van-tab>
    </van-tabs>
    <div class="content">
      <team-card-list :team-list="teamList" @update-team="listTeams('')"/>
      <van-empty v-if="teamList?.length < 1" description="队伍数据为空"/>
    </div>
    <!-- 悬浮创建按钮 -->
    <div class="create-btn-wrapper">
      <van-button
          round
          class="create-btn"
          type="primary"
          @click="createTeam"
      >
        <van-icon name="plus" size="24"/>
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.container {
  padding: 16px;
  min-height: 100vh;
  background: #f2f2f6;
}

.create-btn-wrapper {
  position: fixed;
  bottom: 60px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 999;
}

.create-btn {
  width: 40px;
  height: 40px;
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.van-search) {
  margin-bottom: 16px;
  border-radius: 8px;
}

/* 优化卡片样式 */
:deep(.van-card) {
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

:deep(.van-card):active {
  transform: scale(0.98);
}

:deep(.van-card__title) {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

:deep(.van-card__desc) {
  color: #666666;
  font-size: 14px;
  margin: 8px 0;
}
</style>