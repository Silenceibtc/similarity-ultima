<script setup lang="ts">
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import qs from "qs";
import {UserType} from "../models/user";
import UserCardList from "../components/UserCardList.vue";

const route = useRoute();
const {tags} = route.query;
const userList = ref<UserType[]>([])

onMounted(async () => {
// 上述请求也可以按以下方式完成（可选）
  const userListData = await myAxios.get('/user/search/tags', {
    params: {
      tagNameList: tags,
    },
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: "repeat"});
    },

  })
      .then(function (response) {
        console.log("/user/search/tags succeed", response);
        return response?.data;
      })
      .catch(function (error) {
        console.log("/user/search/tags error", error);
      })
  if (userListData) {
    userListData.forEach((user) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
})

</script>

<template>
  <user-card-list :user-list="userList"/>
  <van-empty v-if="userList?.length < 1" description="未找到符合条件的用户" />
</template>

<style scoped>

</style>