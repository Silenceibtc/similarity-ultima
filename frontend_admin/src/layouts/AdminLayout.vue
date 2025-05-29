<template>
  <el-container>
    <el-aside width="200px" class="sidebar">
      <el-menu
          default-active="/dashboard/user"
          router
          background-color="#304156"
          text-color="#fff"
          active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/team">
          <el-icon><Users /></el-icon>  <!-- 替换为存在的图标 -->
          <span>队伍管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <!-- 其他代码保持不变 -->
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { User, Users } from '@element-plus/icons-vue';  // 导入Users图标

const router = useRouter();

const handleLogout = () => {
  localStorage.removeItem('admin-token');
  router.push('/login');
};
</script>


<style scoped>
.sidebar {
  background-color: #304156;
}

.header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 24px;
  border-bottom: 1px solid #eee;
}

.main {
  padding: 24px;
  background: #f8f9fa;
}
</style>