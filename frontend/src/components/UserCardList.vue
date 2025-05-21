<script setup lang="ts">
import {UserType} from "../models/user";

interface UserCardListProps {
  userList?: UserType[];
}

const props = withDefaults(defineProps<UserCardListProps>(), {
  userList: () => []
});
</script>

<template>
  <van-card v-for="user in props.userList"
            :tag="user.gender === 1 ? '男' : (user.gender === 0 ? '女' : '')"
            :desc="user.profile ? user.profile : '这个人很神秘，什么都没有写'"
            :title="user.username ? user.username : '匿名用户'"
            :thumb="user.avatarUrl"
  >
    <template #tags>
      <van-tag style="margin: 1px 2px" v-for="tag in user.tags" plain type="primary">{{ tag }}</van-tag>
    </template>
    <template #bottom>
      <div>
        电话：{{ user.phone ? user.phone : '暂未填写' }}
      </div>
      <div>
        邮箱：{{ user.email ? user.email : '暂未填写' }}
      </div>
    </template>
<!--    <template #footer>-->
<!--      <van-button size="mini" plain type="primary">联系我</van-button>-->
<!--    </template>-->
  </van-card>
</template>

<style scoped>

</style>