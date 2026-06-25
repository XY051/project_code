<script setup lang="ts">
import http from "../../utils/http";
import { Plus, Search, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "../../utils/system";

const initParams = { pageNum: 1, pageSize: 10, equipmentId: null as number | null };
const params = ref({ ...initParams });
const initForm = { equipmentId: null, maintenanceDate: "", maintenancePerson: "", type: 0, content: "", cost: null, nextMaintenanceDate: "", remark: "" };
const form = ref({ ...initForm });
const tableData = ref([]);
const total = ref(0);
const visible = ref(false);
const multipleSelection = ref([]);
const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);
const isEdit = computed(() => form.value.id > 0);
const dialogTitle = computed(() => isEdit.value ? "编辑" : "新增");

const typeOptions = [
  { label: "定期维护", value: 0 },
  { label: "故障维修", value: 1 },
];
const equipmentOptions = ref([]);

const getEquipmentOptions = async () => {
  const res = await http.get("/equipment/list");
  if (res.code === 200) { equipmentOptions.value = res.data; }
};
const getTableData = async () => {
  const res = await http.get(`/maintenanceRecord/page?pageNum=${params.value.pageNum}&pageSize=${params.value.pageSize}&equipmentId=${params.value.equipmentId || ""}`);
  if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total; }
};
const handleSearch = async () => { await getTableData(); };
const handleReset = () => { params.value = { ...initParams }; getTableData(); };
const handleAdd = () => { form.value = { ...initForm }; visible.value = true; };
const handleEdit = (row: any) => { form.value = { ...row }; visible.value = true; };
const delRow = async (id: number) => {
  const res = await http.get(`/maintenanceRecord/del?id=${id}`);
  if (res.code === 200) { ElMessage.success("删除成功"); await getTableData(); } else { ElMessage.error("删除失败"); }
};
const batchDel = async () => {
  if (multipleSelection.value.length === 0) { ElMessage.warning("请先选择要删除的记录"); return; }
  const res = await http.post("/maintenanceRecord/batchDel", selectedIds.value);
  if (res.code === 200) { ElMessage.success("批量删除成功"); multipleSelection.value = []; await getTableData(); } else { ElMessage.error("批量删除失败"); }
};
const handleSelectionChange = (selection) => { multipleSelection.value = selection; };
const submit = async () => {
  if (isEdit.value) {
    const res = await http.post(`/maintenanceRecord/edit?id=${form.value.id}`, form.value);
    if (res.code === 200) { ElMessage.success("编辑成功"); visible.value = false; await getTableData(); } else { ElMessage.error("编辑失败"); }
  } else {
    const res = await http.post("/maintenanceRecord/add", form.value);
    if (res.code === 200) { ElMessage.success("新增成功"); visible.value = false; await getTableData(); } else { ElMessage.error("新增失败"); }
  }
};
const handleClose = () => { visible.value = false; };
const handleSizeChange = (val: number) => { params.value.pageSize = val; getTableData(); };
const handleCurrentChange = (val: number) => { params.value.pageNum = val; getTableData(); };

onMounted(async () => { await getEquipmentOptions(); await getTableData(); });
</script>

<template>
  <div>
    <header class="flex">
      <div class="flex form-item"><p class="label-text">设备：</p>
        <el-select v-model="params.equipmentId" placeholder="全部" clearable style="width:200px">
          <el-option v-for="eq in equipmentOptions" :key="eq.id" :label="eq.name" :value="eq.id" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>
    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('equipmentMaintenance', '新增')" :icon="Plus" type="primary" @click="handleAdd">新增</el-button>
        <el-button v-if="hasPermission('equipmentMaintenance', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
      </div>
      <el-table :data="tableData" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="设备" min-width="120">
          <template #default="scope">{{ equipmentOptions.find(e => e.id === scope.row.equipmentId)?.name || scope.row.equipmentId }}</template>
        </el-table-column>
        <el-table-column prop="maintenanceDate" label="维护日期" width="100" />
        <el-table-column prop="maintenancePerson" label="维护人员" width="90" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="scope"><el-tag :type="scope.row.type === 0 ? 'primary' : 'danger'">{{ typeOptions[scope.row.type]?.label }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="content" label="维护内容" min-width="160" />
        <el-table-column prop="cost" label="费用" width="80" />
        <el-table-column prop="nextMaintenanceDate" label="下次维护" width="100" />
        <el-table-column label="操作" fixed="right" min-width="140">
          <template #default="scope">
            <el-button v-if="hasPermission('equipmentMaintenance', '编辑')" type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm v-if="hasPermission('equipmentMaintenance', '删除')" title="您确定要删除吗?" @confirm="delRow(scope.row.id)">
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
      <el-form :model="form" label-width="110">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="设备" prop="equipmentId">
            <el-select v-model="form.equipmentId" placeholder="请选择设备" style="width:100%">
              <el-option v-for="eq in equipmentOptions" :key="eq.id" :label="eq.name" :value="eq.id" />
            </el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维护类型" prop="type">
            <el-select v-model="form.type" style="width:100%">
              <el-option v-for="opt in typeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="维护日期" prop="maintenanceDate"><el-date-picker v-model="form.maintenanceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维护人员" prop="maintenancePerson"><el-input v-model="form.maintenancePerson" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="维护费用" prop="cost"><el-input-number v-model="form.cost" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="下次维护日期" prop="nextMaintenanceDate"><el-date-picker v-model="form.nextMaintenanceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="维护内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
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
