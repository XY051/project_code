<template>
  <div style="max-width: 600px; margin: 0 auto">
    <h3 style="margin-bottom: 20px">个人信息</h3>
    <el-card>
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import http from '@/utils/http'

const userStore = useUserStore()

const form = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
})

onMounted(async () => {
  try {
    const res = await http.get('/user/getLoginUserInfo')
    Object.assign(form, res.data || {})
  } catch {}
})

async function handleSave() {
  try {
    await http.post('/user/update', form)
    ElMessage.success('保存成功')
  } catch {
    ElMessage.error('保存失败')
  }
}
</script>
