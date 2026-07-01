<script setup lang="ts">
import http from '@/utils/http'
import { Plus, Edit, Delete, Top, Bottom, CopyDocument } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, ref, computed } from "vue";

// ===== 表单 =====
const initForm = { trainingProjectId: null, taskName: "", stepOrder: 1, description: "", requirement: "", score: 0, status: 1 };
const form = ref({ ...initForm });
const tableData = ref<any[]>([]);
const projectList = ref<any[]>([]);
const selectedProjectId = ref<number | null>(null);
const visible = ref(false);
const isEdit = computed(() => form.value.id > 0);
const dialogTitle = computed(() => isEdit.value ? "编辑实验任务" : "新增实验任务");

// ===== 复制弹窗 =====
const copyVisible = ref(false);
const copySourceProjectId = ref<number | null>(null);

// ===== 数据 =====
const getProjectList = async () => {
  const res = await http.get("/training/list");
  if (res.code === 200) projectList.value = res.data || [];
};

const getTableData = async () => {
  if (!selectedProjectId.value) { tableData.value = []; return; }
  const res = await http.get(`/experimentTask/list?trainingProjectId=${selectedProjectId.value}`);
  if (res.code === 200) tableData.value = res.data || [];
};

const handleProjectChange = (val: number | null) => { selectedProjectId.value = val; getTableData(); };

// ===== CRUD =====
const handleAdd = () => { form.value = { ...initForm, trainingProjectId: selectedProjectId.value }; visible.value = true; };

const handleEdit = (row: any) => { form.value = { ...row }; visible.value = true; };

const delRow = async (id: number) => {
  const res = await http.get(`/experimentTask/del?id=${id}`);
  if (res.code === 200) { ElMessage.success("删除成功"); await getTableData(); }
};

const submit = async () => {
  if (!form.value.trainingProjectId) { ElMessage.warning("请先选择实训项目"); return; }
  if (!form.value.taskName.trim()) { ElMessage.warning("请输入任务名称"); return; }
  if (isEdit.value) {
    const res = await http.post(`/experimentTask/edit?id=${form.value.id}`, form.value);
    if (res.code === 200) { ElMessage.success("编辑成功"); visible.value = false; await getTableData(); }
  } else {
    const res = await http.post("/experimentTask/add", form.value);
    if (res.code === 200) { ElMessage.success("新增成功"); visible.value = false; await getTableData(); }
  }
};

// ===== 拖拽排序（上移/下移） =====
const moveUp = async (index: number) => {
  if (index === 0) return;
  const list = [...tableData.value];
  [list[index], list[index - 1]] = [list[index - 1], list[index]];
  const updated = list.map((item: any, i: number) => ({ ...item, stepOrder: i + 1 }));
  const res = await http.post("/experimentTask/updateSort", updated);
  if (res.code === 200) { ElMessage.success("排序已更新"); await getTableData(); }
};

const moveDown = async (index: number) => {
  if (index === tableData.value.length - 1) return;
  const list = [...tableData.value];
  [list[index], list[index + 1]] = [list[index + 1], list[index]];
  const updated = list.map((item: any, i: number) => ({ ...item, stepOrder: i + 1 }));
  const res = await http.post("/experimentTask/updateSort", updated);
  if (res.code === 200) { ElMessage.success("排序已更新"); await getTableData(); }
};

// ===== 复制任务 =====
const openCopyDialog = () => {
  if (!selectedProjectId.value) { ElMessage.warning("请先选择目标实训项目"); return; }
  copySourceProjectId.value = null;
  copyVisible.value = true;
};

const submitCopy = async () => {
  if (!copySourceProjectId.value) { ElMessage.warning("请选择来源实训项目"); return; }
  if (copySourceProjectId.value === selectedProjectId.value) { ElMessage.warning("不能从同一项目复制"); return; }
  const res = await http.post(`/experimentTask/copyFromProject?sourceProjectId=${copySourceProjectId.value}&targetProjectId=${selectedProjectId.value}`);
  if (res.code === 200) { ElMessage.success(`成功复制 ${res.data} 个任务`); copyVisible.value = false; await getTableData(); }
};

// ===== 启用/禁用 =====
const toggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1;
  const label = newStatus === 1 ? "启用" : "禁用";
  await ElMessageBox.confirm(`确认${label}「${row.taskName}」吗？`, `任务${label}`);
  const res = await http.post(`/experimentTask/updateStatus?id=${row.id}&status=${newStatus}`);
  if (res.code === 200) { ElMessage.success(`${label}成功`); await getTableData(); }
};

onMounted(() => getProjectList());
</script>

<template>
  <div>
    <header class="flex">
      <div class="flex form-item">
        <p class="label-text">实训项目：</p>
        <el-select v-model="selectedProjectId" placeholder="请选择实训项目" clearable filterable style="width: 300px" @change="handleProjectChange">
          <el-option v-for="p in projectList" :key="p.id" :label="p.title" :value="p.id" />
        </el-select>
      </div>
    </header>
    <main>
      <div class="flex op-box">
        <el-button :icon="Plus" type="primary" :disabled="!selectedProjectId" @click="handleAdd">新增任务</el-button>
        <el-button :icon="CopyDocument" type="warning" :disabled="!selectedProjectId" @click="openCopyDialog">从其他项目复制</el-button>
      </div>
      <el-table :data="tableData" border empty-text="请先选择实训项目后查看任务">
        <el-table-column prop="stepOrder" label="步骤" width="60" align="center" />
        <el-table-column prop="taskName" label="任务名称" width="200" />
        <el-table-column prop="description" label="步骤描述" show-overflow-tooltip min-width="200" />
        <el-table-column prop="requirement" label="任务要求" show-overflow-tooltip min-width="200" />
        <el-table-column prop="score" label="分数" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="scope">
            <el-button type="primary" :icon="Edit" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button :icon="Top" size="small" :disabled="scope.$index === 0" @click="moveUp(scope.$index)" title="上移" />
            <el-button :icon="Bottom" size="small" :disabled="scope.$index === tableData.length - 1" @click="moveDown(scope.$index)" title="下移" />
            <el-button size="small" @click="toggleStatus(scope.row)">{{ scope.row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-popconfirm title="确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference><el-button type="danger" :icon="Delete" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </main>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="visible" :title="dialogTitle" width="700">
      <el-form :model="form" label-width="100px">
        <el-form-item label="实训项目">
          <el-select v-model="form.trainingProjectId" placeholder="请选择" style="width:100%" disabled>
            <el-option v-for="p in projectList" :key="p.id" :label="p.title" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="16"><el-form-item label="任务名称"><el-input v-model="form.taskName" placeholder="请输入任务名称" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="步骤排序"><el-input-number v-model="form.stepOrder" :min="1" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="步骤描述"><el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4 }" v-model="form.description" placeholder="请输入步骤描述" /></el-form-item>
        <el-form-item label="任务要求"><el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4 }" v-model="form.requirement" placeholder="请输入任务要求" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="任务分数"><el-input-number v-model="form.score" :min="0" :precision="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submit">确认</el-button><el-button @click="visible = false">取消</el-button></div></template>
    </el-dialog>

    <!-- 复制任务弹窗 -->
    <el-dialog v-model="copyVisible" title="从其他实训项目复制任务" width="500px">
      <el-form label-width="120px">
        <el-form-item label="来源实训项目">
          <el-select v-model="copySourceProjectId" placeholder="请选择来源项目" filterable style="width:100%">
            <el-option v-for="p in projectList.filter(p => p.id !== selectedProjectId)" :key="p.id" :label="p.title" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-alert title="将复制来源项目的所有实验任务到当前项目中（包含任务名称、描述、要求、分数）" type="info" :closable="false" show-icon />
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitCopy">确认复制</el-button><el-button @click="copyVisible = false">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.label-text { color: #606266; width: 90px; }
.form-item { margin-right: 20px; }
.op-box { margin: 10px 0; gap: 10px; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
</style>
