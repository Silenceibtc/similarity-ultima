<template>
  <div class="team-manage">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="队伍名称">
          <el-input v-model="searchForm.teamName" placeholder="模糊搜索" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadTeamList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 队伍列表 -->
    <el-card class="list-card">
      <el-table :data="teamList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="teamName" label="队伍名称" />
        <el-table-column prop="leaderName" label="队长" />
        <el-table-column prop="memberCount" label="成员数" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
  teamName: ''
});

// 表格数据
const teamList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 加载队伍列表
const loadTeamList = async () => {
  try {
    const res = await axios.get('/api/admin/team/list', {
      params: {
        ...searchForm,
        page: currentPage.value,
        pageSize: pageSize.value
      }
    });
    teamList.value = res.data.data.list;
    total.value = res.data.data.total;
  } catch (error) {
    ElMessage.error('获取队伍列表失败');
  }
};

// 初始化加载
loadTeamList();

// 分页切换
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadTeamList();
};

// 重置搜索
const resetSearch = () => {
  searchForm.teamName = '';
  currentPage.value = 1;
  loadTeamList();
};

// 编辑队伍（示例）
const handleEdit = (team: any) => {
  ElMessageBox.prompt('请输入新队伍名称', '编辑队伍', {
    inputValue: team.teamName,
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    await axios.post('/api/admin/team/update', {
      id: team.id,
      teamName: value
    });
    ElMessage.success('更新成功');
    loadTeamList();
  });
};

// 删除队伍
const handleDelete = async (teamId: number) => {
  ElMessageBox.confirm(
      '确定删除该队伍？删除后数据不可恢复',
      '警告',
      { type: 'warning' }
  ).then(async () => {
    await axios.post('/api/admin/team/delete', { id: teamId });
    ElMessage.success('删除成功');
    loadTeamList();
  });
};
</script>

<style scoped>
/* 复用用户管理样式 */
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