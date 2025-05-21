<script setup lang="ts">
// BasicLayout.vue
import { useRouter } from "vue-router";
import { computed } from 'vue';

const router = useRouter();

// 定义带默认值的 props
const props = defineProps({
  meta: {
    type: Object,
    default: () => ({}) // 关键！设置默认空对象
  }
});

// 安全访问计算属性
const navTitle = computed(() => props.meta.navTitle || 'Similarity');
const showBack = computed(() => props.meta.showBack ?? true);
const showSearch = computed(() => props.meta.showSearch ?? false);

const handleBack = () => router.back();
const handleSearch = () => router.push('/search');
</script>

<template>
  <van-nav-bar
      v-if="meta.layout !== 'empty'"
      :title="navTitle"
      :left-arrow="showBack"
      @click-left="handleBack"
  >
    <template #right v-if="showSearch">
      <van-icon name="search" size="18" @click="handleSearch"/>
    </template>
  </van-nav-bar>

  <div id="content">
    <slot></slot>
  </div>

  <van-tabbar route v-if="meta.layout === 'basic'">
    <van-tabbar-item icon="home-o" name="index" to="/">主页</van-tabbar-item>
    <van-tabbar-item icon="friends-o" name="team" to="/team">队伍</van-tabbar-item>
    <van-tabbar-item icon="user-o" name="user" to="/user">个人</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
#content {
  padding-bottom: 50px;
}
</style>