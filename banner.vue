<template>
  <div>
    <el-card style="margin-bottom: 15px">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
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
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 60px; height: 40px" fit="cover" preview-teleported :preview-src-list="[row.imageUrl]" />
          </template>
        </el-table-column>
        <el-table-column prop="linkUrl" label="链接地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @closed="handleDialogClosed">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="图片地址" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片地址" />
        </el-form-item>
        <el-form-item label="链接地址" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入链接地址" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" placeholder="请输入排序号" style="width: 100%" />
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

const searchForm = reactive({ title: '' })
const form = reactive<any>({})
const formRef = ref<FormInstance>()
const tableData = ref([])
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片地址', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序号', trigger: 'blur' }],
}

function loadData() {
  http.get('/banner/page', { params: { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm } }).then((res: any) => {
    tableData.value = res.data.records || res.data
    total.value = res.data.total || 0
  })
}

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map((item: any) => item.id)
}

function handleAdd() {
  dialogTitle.value = '新增Banner'
  Object.assign(form, { title: '', imageUrl: '', linkUrl: '', sort: 0 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑Banner'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

function handleDialogClosed() {
  formRef.value?.resetFields()
}

function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (!valid) return
    const apiCall = form.id ? http.put(`/banner/edit?id=${form.id}`, form) : http.post('/banner/add', form)
    apiCall.then(() => {
      ElMessage.success(form.id ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    })
  })
}

function handleDel(id: number) {
  ElMessageBox.confirm('确定要删除该Banner吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
    http.delete(`/banner/del?id=${id}`).then(() => {
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
    http.post('/banner/batchDel', selectedIds.value).then(() => {
      ElMessage.success('批量删除成功')
      loadData()
    })
  })
}

loadData()
</script>
