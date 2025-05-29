<template>
  <div class="user-manage">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="模糊搜索" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadUserList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="list-card">
      <el-table :data="userList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button
                type="text"
                size="small"
                @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
                type="text"
                size="small"
                style="color: #ff4d4f"
                @click="handleDelete(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          class="pagination"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

// 搜索表单
const searchForm = reactive({
  username: ''
});

// 表格数据
const userList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 加载用户列表
const loadUserList = async () => {
  try {
    const res = await axios.get('/api/admin/user/list', {
      params: {
        ...searchForm,
        page: currentPage.value,
        pageSize: pageSize.value
      }
    });
    userList.value = res.data.data.list;
    total.value = res.data.data.total;
  } catch (error) {
    ElMessage.error('获取用户列表失败');
  }
};

// 初始化加载
loadUserList();

// 分页切换
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadUserList();
};

// 重置搜索
const resetSearch = () => {
  searchForm.username = '';
  currentPage.value = 1;
  loadUserList();
};

// 编辑用户（示例）
const handleEdit = (user: any) => {
  ElMessageBox.prompt('请输入新邮箱', '编辑用户', {
    inputValue: user.email,
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    await axios.post('/api/admin/user/update', {
      id: user.id,
      email: value
    });
    ElMessage.success('更新成功');
    loadUserList();
  });
};

// 删除用户
const handleDelete = async (userId: number) => {
  ElMessageBox.confirm(
      '确定删除该用户？删除后数据不可恢复',
      '警告',
      { type: 'warning' }
  ).then(async () => {
    await axios.post('/api/admin/user/delete', { id: userId });
    ElMessage.success('删除成功');
    loadUserList();
  });
};
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
  padding: 16px;
}
.list-card {
  padding: 16px;
}
.pagination {
  margin-top: 24px;
  text-align: right;
}
</style>