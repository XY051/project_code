<template>
  <div>
    <el-card style="margin-bottom: 15px">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleBatchDel" :disabled="!selectedIds.length">批量删除</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" @selection-change="handleSelectionChange" border stripe>
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleDel(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadData"
        layout="total, prev, pager, next"
        style="margin-top: 15px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import http from '@/utils/http'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({ userId: '' })
const tableData = ref([])
const selectedIds = ref<number[]>([])
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)

function loadData() {
  http.get('/feedback/page', { params: { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm } }).then((res: any) => {
    tableData.value = res.data.records || res.data
    total.value = res.data.total || 0
  })
}

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map((item: any) => item.id)
}

function handleDel(id: number) {
  ElMessageBox.confirm('确定要删除该反馈吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
    http.delete(`/feedback/del?id=${id}`).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  })
}

function handleBatchDel() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请至少选择一条数据')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条数据吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
    http.post('/feedback/batchDel', selectedIds.value).then(() => {
      ElMessage.success('批量删除成功')
      loadData()
    })
  })
}

loadData()
</script>
