<script setup lang="ts">
import {ref, watchEffect} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {UserType} from "../models/user";
import UserCardList from "../components/UserCardList.vue";

const userList = ref<UserType[]>([])
const isMatchMode = ref(false)
let userListData = []
const loadData = async () => {
  if (isMatchMode.value) {
    const num = 10; // 匹配用户数量
    userListData = await myAxios.get('/user/match', {
      params: {
        num,
      }
    })
        .then(function (response) {
          return response?.data;
        })
    if (userListData) {
      userListData.forEach((user: any) => {
        if (user.tags) {
          user.tags = JSON.parse(user.tags);
        }
      })
      userList.value = userListData;
    }
  } else {
    userListData = await myAxios.get('/user/homePage', {
      params: {
        pageNum: 1,
        pageSize: 10,
      }
    })
        .then(function (response) {
          return response?.data;
        })
    if (userListData) {
      userListData.forEach((user: any) => {
        if (user.tags) {
          user.tags = JSON.parse(user.tags);
        }
      })
      userList.value = userListData;
    }
  }
}
watchEffect(() => {
  loadData()
})
</script>

<template>
  <div class="container">
    <div class="content">
      <van-cell center title="智能匹配">
        <template #right-icon>
          <van-switch v-model="isMatchMode" />
        </template>
      </van-cell>
      <user-card-list :user-list="userList"/>
      <van-empty v-if="!userList || userList.length < 1" description="无"/>
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
:deep(.van-cell) {
  margin-bottom: 16px;
  border-radius: 8px;
}

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