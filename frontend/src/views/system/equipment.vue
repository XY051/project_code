<script setup lang="ts">
import http from "../../utils/http";
import { Plus, Search, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "../../utils/system";

const initParams = { pageNum: 1, pageSize: 10, name: "", labId: null as number | null };
const params = ref({ ...initParams });
const initForm = { name: "", model: "", code: "", labId: null, status: 0, purchaseDate: "", maintenanceCycle: null, description: "" };
const form = ref({ ...initForm });
const tableData = ref([]);
const total = ref(0);
const visible = ref(false);
const multipleSelection = ref([]);
const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);
const isEdit = computed(() => form.value.id > 0);
const dialogTitle = computed(() => isEdit.value ? "编辑" : "新增");

const statusOptions = [
  { label: "正常", value: 0, type: "success" },
  { label: "维修中", value: 1, type: "warning" },
  { label: "已报废", value: 2, type: "danger" },
];
const labOptions = ref([]);

const getLabOptions = async () => {
  const res = await http.get("/lab/list");
  if (res.code === 200) { labOptions.value = res.data; }
};
const getTableData = async () => {
  const res = await http.get(`/equipment/page?pageNum=${params.value.pageNum}&pageSize=${params.value.pageSize}&name=${params.value.name}&labId=${params.value.labId || ""}`);
  if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total; }
};
const handleSearch = async () => { await getTableData(); };
const handleReset = () => { params.value = { ...initParams }; getTableData(); };
const handleAdd = () => { form.value = { ...initForm }; visible.value = true; };
const handleEdit = (row: any) => { form.value = { ...row }; visible.value = true; };
const delRow = async (id: number) => {
  const res = await http.get(`/equipment/del?id=${id}`);
  if (res.code === 200) { ElMessage.success("删除成功"); await getTableData(); } else { ElMessage.error("删除失败"); }
};
const batchDel = async () => {
  if (multipleSelection.value.length === 0) { ElMessage.warning("请先选择要删除的设备"); return; }
  const res = await http.post("/equipment/batchDel", selectedIds.value);
  if (res.code === 200) { ElMessage.success("批量删除成功"); multipleSelection.value = []; await getTableData(); } else { ElMessage.error("批量删除失败"); }
};
const handleSelectionChange = (selection) => { multipleSelection.value = selection; };
const submit = async () => {
  if (isEdit.value) {
    const res = await http.post(`/equipment/edit?id=${form.value.id}`, form.value);
    if (res.code === 200) { ElMessage.success("编辑成功"); visible.value = false; await getTableData(); } else { ElMessage.error("编辑失败"); }
  } else {
    const res = await http.post("/equipment/add", form.value);
    if (res.code === 200) { ElMessage.success("新增成功"); visible.value = false; await getTableData(); } else { ElMessage.error("新增失败"); }
  }
};
const handleClose = () => { visible.value = false; };
const handleSizeChange = (val: number) => { params.value.pageSize = val; getTableData(); };
const handleCurrentChange = (val: number) => { params.value.pageNum = val; getTableData(); };

onMounted(async () => { await getLabOptions(); await getTableData(); });
</script>

<template>
  <div>
    <header class="flex">
      <div class="flex form-item"><p class="label-text">名称：</p><el-input v-model="params.name" placeholder="请输入名称" /></div>
      <div class="flex form-item"><p class="label-text">实验室：</p>
        <el-select v-model="params.labId" placeholder="全部" clearable style="width:180px">
          <el-option v-for="lab in labOptions" :key="lab.id" :label="lab.name" :value="lab.id" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>
    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('equipment', '新增')" :icon="Plus" type="primary" @click="handleAdd">新增</el-button>
        <el-button v-if="hasPermission('equipment', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
      </div>
      <el-table :data="tableData" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="设备名称" min-width="120" />
        <el-table-column prop="model" label="型号" width="100" />
        <el-table-column prop="code" label="编号" width="120" />
        <el-table-column label="所属实验室" min-width="120">
          <template #default="scope">{{ labOptions.find(l => l.id === scope.row.labId)?.name || "-" }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope"><el-tag :type="statusOptions[scope.row.status]?.type">{{ statusOptions[scope.row.status]?.label }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购置日期" width="100" />
        <el-table-column prop="maintenanceCycle" label="维护周期(天)" width="100" />
        <el-table-column label="操作" fixed="right" min-width="140">
          <template #default="scope">
            <el-button v-if="hasPermission('equipment', '编辑')" type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm v-if="hasPermission('equipment', '删除')" title="您确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference><el-button type="danger" :icon="Delete">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, prev, pager, next" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </main>
    <el-dialog v-model="visible" :title="dialogTitle" width="700" :before-close="handleClose">
      <el-form :model="form" label-width="100">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="设备名称" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="型号" prop="model"><el-input v-model="form.model" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="设备编号" prop="code"><el-input v-model="form.code" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="所属实验室" prop="labId">
            <el-select v-model="form.labId" placeholder="请选择" clearable style="width:100%">
              <el-option v-for="lab in labOptions" :key="lab.id" :label="lab.name" :value="lab.id" />
            </el-select>
          </el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="状态" prop="status">
            <el-select v-model="form.status" style="width:100%">
              <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item label="购置日期" prop="purchaseDate"><el-date-picker v-model="form.purchaseDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="维护周期(天)" prop="maintenanceCycle"><el-input-number v-model="form.maintenanceCycle" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="描述/备注" prop="description"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer"><el-button type="primary" @click="submit()">确认</el-button><el-button @click="visible = false">取消</el-button></div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.label-text { color: #606266; width: 80px; }
.form-item { margin-right: 20px; }
.op-box { margin: 10px 0; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
</style>
