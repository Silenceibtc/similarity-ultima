<template>
  <div class="team-edit-container">
    <el-card>
      <h2 style="margin-bottom: 20px;">修改队伍信息</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="队伍名称" prop="teamName">
          <el-input v-model="form.teamName" />
        </el-form-item>

        <el-form-item label="队伍描述" prop="description">
          <el-input type="textarea" v-model="form.description" rows="4" />
        </el-form-item>

        <el-form-item label="最大人数" prop="maxNum">
          <el-input-number v-model="form.maxNum" :min="1" :max="100" />
        </el-form-item>

        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker
              v-model="form.expireTime"
              type="datetime"
              placeholder="选择过期时间"
              style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="队伍状态" prop="teamStatus">
          <el-radio-group v-model="form.teamStatus">
            <el-radio :label="0">公开</el-radio>
            <el-radio :label="1">私有</el-radio>
            <el-radio :label="2">加密</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="form.teamStatus === 2" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSubmit">提交</el-button>
          <el-button @click="onCancel" style="margin-left: 10px;">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import myAxios from '../plugins/myAxios'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)

const form = ref({
  id: null,
  teamName: '',
  description: '',
  maxNum: 10,
  expireTime: '',
  teamStatus: 0,
  password: ''
})

// 表单验证规则
const rules = {
  teamName: [{ required: true, message: '请输入队伍名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入队伍描述', trigger: 'blur' }],
  maxNum: [{ required: true, type: 'number', message: '请输入最大人数', trigger: 'change' }],
  expireTime: [{ required: true, message: '请选择过期时间', trigger: 'change' }],
  teamStatus: [{ required: true, type: 'number', message: '请选择队伍状态', trigger: 'change' }],
  password: [
    {
      validator: (rule, value, callback) => {
        if (form.value.teamStatus === 2 && !value) {
          callback(new Error('加密状态下必须填写密码'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 页面加载初始化
onMounted(() => {
  const teamStr = route.query.team
  if (teamStr) {
    try {
      const team = JSON.parse(teamStr)
      form.value = {
        ...team,
        expireTime: team.expireTime ? new Date(team.expireTime) : ''
      }
    } catch (e) {
      ElMessage.error('加载队伍数据失败')
    }
  }
})

// 提交表单
const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    const payload = {
      ...form.value,
      expireTime: form.value.expireTime.toISOString()
    }

    try {
      const response = await myAxios.post('/team/update', payload, {
        withCredentials: true
      })
      if (response.code === 0) {
        ElMessage.success('队伍信息修改成功')
        router.push('/team/manage')
      } else {
        ElMessage.error('修改失败：' + response.message)
      }
    } catch (e) {
      ElMessage.error('请求失败，请稍后重试')
    }
  })
}

// 取消修改，返回上一页
const onCancel = () => {
  router.back()
}
</script>

<style scoped>
.team-edit-container {
  padding: 20px;
  background-color: #fff;
}
</style>
