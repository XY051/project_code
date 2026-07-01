<template>
  <div>
    <el-card style="margin-bottom: 15px">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="名称">
          <el-input v-model="searchForm.name" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="handleAdd">新增</el-button>
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
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="typeId" label="类型ID" width="100" />
        <el-table-column label="封面图" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" style="width: 60px; height: 40px" fit="cover" preview-teleported :preview-src-list="[row.coverImage]" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px" @closed="handleDialogClosed">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择类型" style="width: 100%">
            <el-option label="请选择类型" value="" disabled />
            <el-option v-for="t in typeOptions" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="请输入封面图地址" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import http from '@/utils/http'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const searchForm = reactive({ name: '' })
const form = reactive<any>({})
const formRef = ref<FormInstance>()
const tableData = ref([])
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const typeOptions = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)

const rules: FormRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

function loadData() {
  http.get('/culturalHeritage/page', { params: { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm } }).then((res: any) => {
    tableData.value = res.data.records || res.data
    total.value = res.data.total || 0
  })
}

function loadTypeOptions() {
  http.get('/ichType/list').then((res: any) => {
    typeOptions.value = res.data || res || []
  })
}

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map((item: any) => item.id)
}

function handleAdd() {
  loadTypeOptions()
  dialogTitle.value = '新增实训资源'
  Object.assign(form, { name: '', typeId: '', coverImage: '', description: '', content: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  loadTypeOptions()
  dialogTitle.value = '编辑实训资源'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

function handleDialogClosed() {
  formRef.value?.resetFields()
}

function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (!valid) return
    const apiCall = form.id ? http.put(`/culturalHeritage/edit?id=${form.id}`, form) : http.post('/culturalHeritage/add', form)
    apiCall.then(() => {
      ElMessage.success(form.id ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    })
  })
}

function handleDel(id: number) {
  ElMessageBox.confirm('确定要删除该实训资源吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
    http.delete(`/culturalHeritage/del?id=${id}`).then(() => {
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
    http.post('/culturalHeritage/batchDel', selectedIds.value).then(() => {
      ElMessage.success('批量删除成功')
      loadData()
    })
  })
}

loadData()
</script>
