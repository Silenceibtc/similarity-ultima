<template>
  <el-container style="height: 100vh; border: 1px solid #eee;">
    <!-- 顶部标题 -->
    <el-header style="background-color: #409eff; color: white; line-height: 60px; padding: 0 20px;">
      <h1 style="margin: 0; font-size: 24px;">Similarity管理端</h1>
    </el-header>

    <!-- 主体内容 -->
    <el-container>
      <!-- 左侧菜单 -->
      <el-aside width="200px" style="background-color: #f8f8f8; border-right: 1px solid #eee;">
        <el-menu
            router
            :default-active="route?.path"
            background-color="#f8f8f8"
            text-color="#333"
            active-text-color="#409eff"
        >
          <el-menu-item index="/user">
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/team">
            <span>队伍管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧内容区域 -->
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {useRoute} from 'vue-router'
import {User} from '@element-plus/icons-vue'  // 导入 Users 替代 Team

const route = useRoute()
</script>

<style scoped>
.el-header {
  padding: 0 20px;
}

.el-aside {
  border-right: 1px solid #e6e6e6;
}

.el-menu {
  border-right: none;
}
</style>
