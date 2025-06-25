<template>
  <div class="user-manage-container">
    <el-card class="mb-20">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
            v-model="searchUsername"
            placeholder="搜索用户名"
            prefix-icon="el-icon-search"
            @change="handleSearch"
            style="width: 300px;"
        />
        <el-button
            type="primary"
            @click="handleDeleteSelected"
            :disabled="selectedUserIds.length === 0"
            style="margin-left: 10px;"
        >
          批量删除
        </el-button>
      </div>

      <!-- 用户列表 -->
      <el-table
          :data="userList"
          stripe
          border
          row-key="id"
          :row-selection="{ selectedKeys: selectedUserIds, onChange: handleSelectionChange }"
          style="width: 100%; margin-top: 20px;"
      >
        <el-table-column type="selection" width="50"/>
        <el-table-column prop="username" label="用户名" width="120"/>
        <el-table-column prop="userAccount" label="账号" width="120"/>
        <el-table-column prop="avatarUrl" label="头像" width="60">
          <template #default="{ row }">
            <el-image
                :src="row.avatarUrl"
                width="40"
                height="40"
                fit="cover"
                style="border-radius: 50%;"
            />
          </template>
        </el-table-column>
        <el-table-column label="性别" width="60">
          <template #default="{ row }">
            {{ row.gender === 0 ? '女' : '男' }}
          </template>
        </el-table-column>
        <el-table-column prop="profile" label="个人简介" width="180"/>
        <el-table-column prop="phone" label="手机号" width="120"/>
        <el-table-column prop="email" label="邮箱" width="180"/>
        <el-table-column prop="tags" label="标签" width="230">
          <template #default="{ row }">
            <div v-if="row.tags">
              <el-tag
                  v-for="(tag, index) in parseTags(row.tags)"
                  :key="index"
                  type="primary"
                  style="margin-right: 5px; white-space: nowrap;"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div v-else class="no-tags">无标签</div>
          </template>
        </el-table-column>
        <el-table-column label="身份" width="80">
          <template #default="{ row }">
            {{ row.identity === 0 ? '管理员' : '普通用户' }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <div style="display: flex;">
              <el-button
                  type="primary"
                  size="mini"
                  @click="handleEdit(row)"
              >
                修改
              </el-button>
              <el-button
                  type="danger"
                  size="mini"
                  @click="handleDelete(row.id)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          style="margin-top: 20px;"
      />
    </el-card>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import axios from 'axios'
import myAxios from "../plugins/myAxios.js";
import router from "../router/index.js";

// 响应式数据
const searchUsername = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userList = ref([])
const selectedUserIds = ref([])

// 初始化获取用户列表
onMounted(() => {
  getUsers()
})

// 在script部分添加标签解析方法
const parseTags = (tagsStr) => {
  try {
    if (typeof tagsStr === 'string') {
      const tags = JSON.parse(tagsStr);
      console.log(tags)
      return Array.isArray(tags) ? tags : [];
    }
  } catch (error) {
    console.error('标签解析失败:', error);
  }
  return [];
};

// 获取用户列表
const getUsers = async () => {
  try {
    const response = await myAxios.get('/user/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        username: searchUsername.value
      },
      withCredentials: true // 携带Cookie
    })
    const {records, total: totalCount} = response.data
    userList.value = records
    total.value = totalCount
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败，请重试')
  }
}

// 搜索用户
const handleSearch = () => {
  pageNum.value = 1 // 搜索后重置为第一页
  getUsers()
}

const handleEdit = (row) => {
  const user = {
    ...row,
    tags: parseTags(row.tags), // 关键修改：将字符串转为数组
  };
  router.push({
    path: '/user/edit',
    query: {
      user: JSON.stringify(user),
    }
  })
}

// 删除单个用户
const handleDelete = async (userId) => {
  await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(async () => {
    try {
      const response = await myAxios.post(`/user/delete/${userId}`, {
        withCredentials: true
      })
      if (response.code === 0) {
        ElMessage.success('删除成功')
        getUsers() // 刷新列表
      } else {
        ElMessage.error('删除失败：' + response.data.message)
      }
    } catch (error) {
      ElMessage.error('删除失败，请重试')
    }
  })
}

// 批量删除
const handleDeleteSelected = async () => {
  if (selectedUserIds.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  await ElMessageBox.confirm(`确定要删除选中的 ${selectedUserIds.value.length} 个用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.post('/user/delete', {
        id: selectedUserIds.value
      }, {
        withCredentials: true
      })
      if (response.data.code === 200) {
        ElMessage.success('批量删除成功')
        selectedUserIds.value = [] // 清空选中
        getUsers() // 刷新列表
      } else {
        ElMessage.error('批量删除失败：' + response.data.message)
      }
    } catch (error) {
      ElMessage.error('批量删除失败，请重试')
    }
  })
}

// 处理表格选中变化
const handleSelectionChange = (keys) => {
  selectedUserIds.value = keys
}

// 分页变化处理
const handlePageChange = (newPage) => {
  pageNum.value = newPage
  getUsers()
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  pageNum.value = 1 // 切换每页数量后重置为第一页
  getUsers()
}
</script>

<style scoped>
.user-manage-container {
  padding: 20px;
  background-color: #fff;
}

.no-tags {
  color: #999;
  padding-top: 6px;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
</style>