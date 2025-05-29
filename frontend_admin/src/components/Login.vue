<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">管理员登录</h2>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入管理员账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">立即登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const router = useRouter();
const loginForm = ref({
  username: '',
  password: ''
});

const rules = ref({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
});

const handleLogin = async () => {
  try {
    const res = await axios.post('/api/admin/login', loginForm.value);
    if (res.data.code === 0) {
      // 存储token到localStorage
      localStorage.setItem('admin-token', res.data.data.token);
      // 跳转管理首页
      router.push('/dashboard');
    } else {
      ElMessage.error(res.data.message);
    }
  } catch (error) {
    ElMessage.error('登录失败，请检查网络');
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.login-card {
  width: 400px;
  padding: 32px;
}
.title {
  text-align: center;
  margin-bottom: 24px;
  color: #1989fa;
}
</style>