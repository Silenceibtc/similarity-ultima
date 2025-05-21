<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUserInfo} from "../services/uesr.ts";

const currentUser = ref()

onMounted(async () => {
  currentUser.value = await getCurrentUserInfo();
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
</script>

<template>
  <template v-if="currentUser">
    <van-cell title="昵称" :value="currentUser.username" is-link to="/user/edit"
              @click="doEdit('username', '昵称', currentUser.username)"/>
    <van-cell title="性别" :value="currentUser.gender === 1 ? '男' : (currentUser.gender === 0 ? '女' : '')" is-link to="/user/edit"
              @click="doEdit('gender', '性别', currentUser.gender)"/>
    <van-cell title="头像" is-link to="/user/edit">
      <van-image
          round
          fit="cover"
          width="3rem"
          height="3rem"
          :src="currentUser.avatarUrl"
      />
    </van-cell>
    <van-cell title="账号" :value="currentUser.userAccount"
              @click="doEdit('userAccount', '账号', currentUser.userAccount)"/>
    <van-cell title="电话" :value="currentUser.phone" is-link to="/user/edit"
              @click="doEdit('phone', '电话', currentUser.phone)"/>
    <van-cell title="邮箱" :value="currentUser.email" is-link to="/user/edit"
              @click="doEdit('email', '邮箱', currentUser.email)"/>
    <van-cell title="注册时间" :value="currentUser.createTime.split('T')[0]"/>
  </template>
</template>

<style scoped>

</style>