<script setup lang="ts">
import { useRouter } from "vue-router";
import TeamCardList from "../components/TeamCardList.vue";
import { onMounted, ref } from "vue";
import myAxios from "../plugins/myAxios.ts";
import { showFailToast, showSuccessToast } from "vant";
import { BaseResponse } from "../models/response";

const router = useRouter();
const teamList = ref([]);
const listTeams = async (val) => {
  const res: BaseResponse = await myAxios.get('/team/list/joinedTeam', {
    params: {
      searchText: val,
    }
  });
  if (res.code === 0) {
    teamList.value = res.data;
    showSuccessToast('获取队伍列表成功');
  } else {
    showFailToast('获取队伍列表失败，请刷新重试');
  }
}
onMounted(() => {
  listTeams();
});
</script>

<template>
  <div class="container">
    <!-- 队伍列表 -->
    <div class="content">
      <team-card-list :team-list="teamList" @update-team="listTeams('')"/>
      <van-empty v-if="teamList?.length < 1" description="队伍数据为空"/>
    </div>
  </div>
</template>

<style scoped>
.container {
  padding: 16px;
  min-height: 100vh;
  background: #f2f2f6;
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