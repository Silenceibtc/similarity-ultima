<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">Similarity管理端登录</h2>
      <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          label-width="80px"
          class="login-form"
      >
        <el-form-item label="账号" prop="userAccount">
          <el-input
              v-model="form.userAccount"
              placeholder="请输入账号"
              prefix-icon="el-icon-user"
          />
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input
              v-model="form.userPassword"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
          />
        </el-form-item>
        <el-form-item style="margin-top: 20px;">
          <el-button
              type="primary"
              @click="handleLogin"
              class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import myAxios from "../plugins/myAxios.js";

// 表单数据
const form = ref({
  userAccount: '',
  userPassword: ''
})

// 表单验证规则
const rules = ref({
  userAccount: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  userPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const router = useRouter()

// 登录处理
const handleLogin = async () => {
  try {
    // 发送登录请求
    const response = await myAxios.post('/user/login', form.value, {
      withCredentials: true // 携带Cookie（跨域场景需要）
    })

    const user = response.data
    if (user.identity !== 0) { // 非管理员处理
      ElMessage.error('只有管理员可以登录！')
      return
    }

    // 存储用户信息（示例：存储到sessionStorage）
    sessionStorage.setItem('user', JSON.stringify(user))
    router.push('/') // 跳转到主页
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查账号或密码')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  padding: 30px 40px;
  width: 350px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #409eff;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
}
</style>