<template>
  <div class="team-manage-container">
    <el-card class="mb-20">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
            v-model="searchTeamName"
            placeholder="搜索队伍名称"
            prefix-icon="el-icon-search"
            @change="handleSearch"
            style="width: 300px;"
        />
        <el-button
            type="primary"
            @click="handleDeleteSelected"
            :disabled="selectedTeamIds.length === 0"
            style="margin-left: 10px;"
        >
          批量删除
        </el-button>
      </div>

      <!-- 队伍列表 -->
      <el-table
          :data="teamList"
          stripe
          border
          row-key="id"
          @selection-change="handleSelectionChange"
          style="width: 100%; margin-top: 20px;"
      >
        <el-table-column type="selection" width="50"/>
        <el-table-column prop="id" label="队伍ID" width="80"/>
        <el-table-column prop="teamName" label="队伍名称" width="150"/>
        <el-table-column prop="description" label="队伍描述" width="200" show-overflow-tooltip/>
        <el-table-column label="队伍规模" width="120">
          <template #default="{ row }">
            {{ row.currentNum }} / {{ row.maxNum }} 人
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="180">
          <template #default="{ row }">
            {{ row.expireTime }}
          </template>
        </el-table-column>
        <el-table-column prop="leaderId" label="队长ID" width="100"/>
        <el-table-column label="队伍状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.teamStatus)">
              {{ getStatusText(row.teamStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ row.createTime }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" fixed="right">
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
import myAxios from "../plugins/myAxios.js"
import router from "../router/index.js"

// 响应式数据
const searchTeamName = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const teamList = ref([])
const selectedTeamIds = ref([])

// 初始化获取队伍列表
onMounted(() => {
  getTeams()
})

// 获取队伍状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '公开',
    1: '私有',
    2: '加密'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    0: 'success',
    1: 'info',
    2: 'warning'
  }
  return typeMap[status] || 'danger'
}

// 获取队伍列表
const getTeams = async () => {
  try {
    const response = await myAxios.get('/team/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        teamName: searchTeamName.value
      },
      withCredentials: true
    })

    if (response.code === 0) {
      teamList.value = response.data.records
      total.value = response.data.total
    } else {
      ElMessage.error('获取队伍列表失败：' + response.message)
    }
  } catch (error) {
    console.error('获取队伍列表失败:', error)
    ElMessage.error('获取队伍列表失败，请重试')
  }
}

// 搜索队伍
const handleSearch = () => {
  pageNum.value = 1
  getTeams()
}

// 编辑队伍
const handleEdit = (row) => {
  router.push({
    path: '/team/edit',
    query: {
      team: JSON.stringify(row)
    }
  })
}

// 删除单个队伍
const handleDelete = async (teamId) => {
  await ElMessageBox.confirm('确定要删除该队伍吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(async () => {
    try {
      const response = await myAxios.post(`/team/delete`, {
        teamId: teamId
      })
      if (response.code === 0) {
        ElMessage.success('删除成功')
        getTeams()
      } else {
        ElMessage.error('删除失败：' + response.message)
      }
    } catch (error) {
      ElMessage.error('删除失败，请重试')
    }
  })
}

// 批量删除
const handleDeleteSelected = async () => {
  if (selectedTeamIds.value.length === 0) {
    ElMessage.warning('请选择要删除的队伍')
    return
  }

  await ElMessageBox.confirm(`确定要删除选中的 ${selectedTeamIds.value.length} 个队伍吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 循环调用单个删除接口
      const deletePromises = selectedTeamIds.value.map(id =>
          myAxios.post('/team/delete', {teamId: id}, {withCredentials: true})
      );

      const results = await Promise.all(deletePromises)
      const successCount = results.filter(res => res.code === 0).length

      if (successCount === selectedTeamIds.value.length) {
        ElMessage.success(`成功删除 ${successCount} 个队伍`)
      } else {
        ElMessage.warning(`成功删除 ${successCount} 个队伍，${selectedTeamIds.value.length - successCount} 个删除失败`)
      }

      selectedTeamIds.value = []
      getTeams()
    } catch (error) {
      ElMessage.error('批量删除失败，请重试')
    }
  })
}

// 处理表格选中变化
const handleSelectionChange = (selection) => {
  selectedTeamIds.value = selection.map(item => item.id)
}

// 分页变化处理
const handlePageChange = (newPage) => {
  pageNum.value = newPage
  getTeams()
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  pageNum.value = 1
  getTeams()
}
</script>

<style scoped>
.team-manage-container {
  padding: 20px;
  background-color: #fff;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}
</style>