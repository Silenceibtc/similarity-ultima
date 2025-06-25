<template>
  <div class="user-edit-container">
    <el-card class="mb-20">
      <h2 class="page-title">修改用户信息</h2>

      <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          label-width="100px"
          style="max-width: 600px; margin: 0 auto;"
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username"></el-input>
        </el-form-item>

        <!-- 账号 -->
        <el-form-item label="账号" prop="userAccount">
          <el-input v-model="form.userAccount"></el-input>
        </el-form-item>

        <!-- 性别 -->
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">女</el-radio>
            <el-radio :label="1">男</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 头像 -->
        <el-form-item label="头像">
          <el-upload
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleAvatarUpload"
              :before-upload="beforeAvatarUpload"
              class="avatar-uploader"
          >
            <el-image
                v-if="form.avatarUrl"
                :src="form.avatarUrl"
                width="100"
                height="100"
                fit="cover"
                style="border-radius: 50%;"
            ></el-image>
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div v-if="form.avatarUrl" class="avatar-tip">点击图片更换头像</div>
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" type="number"></el-input>
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" type="email"></el-input>
        </el-form-item>

        <!-- 标签 -->
        <el-form-item label="标签" prop="tags">
          <el-tag
              v-for="(tag, index) in parsedTags"
              :key="index"
              :closable="true"
              @close="handleTagClose(index)"
              class="mr-5"
          >
            {{ tag }}
          </el-tag>
          <el-input
              v-model="inputTag"
              placeholder="输入标签并按回车键添加"
              @keyup.enter.native="handleAddTag"
              style="width: 150px; margin-top: 5px;"
          ></el-input>
        </el-form-item>

        <!-- 身份 -->
        <el-form-item label="身份" prop="identity">
          <el-radio-group v-model="form.identity">
            <el-radio :label="0">管理员</el-radio>
            <el-radio :label="1">普通用户</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 简介 -->
        <el-form-item label="简介" prop="profile">
          <el-input
              v-model="form.profile"
              type="textarea"
              :rows="3"
              placeholder="请输入用户简介"
          ></el-input>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item style="text-align: center;">
          <el-button type="primary" @click="handleSubmit">保存修改</el-button>
          <el-button type="default" @click="goBack" style="margin-left: 10px;">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {useRoute, useRouter} from 'vue-router'
import myAxios from '../plugins/myAxios.js'

// 路由相关
const route = useRoute()
const router = useRouter()

// 表单数据
const form = reactive({
  id: 0,
  username: '',
  userAccount: '',
  gender: 0,
  avatarUrl: '',
  phone: '',
  email: '',
  tags: [], // 存储标签数组
  identity: 0,
  profile: ''
})

// 上传相关
const uploadUrl = '/user/upload' // 后端头像上传接口
const inputTag = ref('') // 新增标签输入框
const parsedTags = computed(() => form.tags) // 计算属性方便遍历

// 表单验证规则
const rules = reactive({
  phone: [
    { required: true, message: '请填写手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请填写邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  tags: [
    { validator: (rule, value, callback) => {
        if (value.length === 0) {
          callback(new Error('请至少添加一个标签'))
        } else {
          callback()
        }
      }, trigger: 'change' }
  ]
})

onMounted(() => {
  const userStr = route.query.user || '{}';
  const user = JSON.parse(userStr);
  form.tags = Array.isArray(user.tags) ? user.tags : [];
  Object.assign(form, user);
  console.log(form)
});

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
  }
  return isImage && isLt2M
}

// 头像上传成功处理
const handleAvatarUpload = (response, file) => {
  if (response.code === 200) {
    form.avatarUrl = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

// 添加标签
const handleAddTag = () => {
  if (inputTag.value.trim() && !parsedTags.value.includes(inputTag.value.trim())) {
    form.tags.push(inputTag.value.trim())
    inputTag.value = ''
  }
}

// 删除标签
const handleTagClose = (index) => {
  form.tags.splice(index, 1)
}

// 表单提交
const handleSubmit = async () => {
    form.tags = JSON.stringify(form.tags)
    try {
      const response = await myAxios.post('/user/update', form, {
        withCredentials: true
      })
      if (response.code === 0) {
        ElMessage.success('用户信息修改成功')
        router.back()
      } else {
        ElMessage.error(`修改失败：${response.data.message}`)
      }
    } catch (error) {
      ElMessage.error('网络错误，请重试')
      console.error(error)
    }
}

// 返回列表页
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.user-edit-container {
  padding: 30px;
  background-color: #fff;
}

.page-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 1.5em;
  color: #333;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  margin: 0 auto;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar-tip {
  text-align: center;
  color: #666;
  margin-top: 5px;
}

.el-tag {
  margin-bottom: 5px;
}
</style>