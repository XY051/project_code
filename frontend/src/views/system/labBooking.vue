<script setup lang="ts">
import http from "../../utils/http";
import { Search, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "../../utils/system";

const initParams = { pageNum: 1, pageSize: 10, labId: null as number | null };
const params = ref({ ...initParams });
const initForm = { labId: null, userId: null, userName: "", purpose: "", bookDate: "", startTime: "", endTime: "", status: 0, remark: "" };
const form = ref({ ...initForm });
const tableData = ref([]);
const total = ref(0);
const visible = ref(false);
const multipleSelection = ref([]);
const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);
const isEdit = computed(() => form.value.id > 0);
const dialogTitle = computed(() => isEdit.value ? "编辑" : "审核");

const statusOptions = [
  { label: "待审核", value: 0, type: "warning" },
  { label: "已通过", value: 1, type: "success" },
  { label: "已拒绝", value: 2, type: "danger" },
  { label: "已取消", value: 3, type: "info" },
];
const labOptions = ref([]);

const getLabOptions = async () => {
  const res = await http.get("/lab/list");
  if (res.code === 200) { labOptions.value = res.data; }
};

const getTableData = async () => {
  const res = await http.get(`/labBooking/page?pageNum=${params.value.pageNum}&pageSize=${params.value.pageSize}&labId=${params.value.labId || ""}`);
  if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total; }
};
const handleSearch = async () => { await getTableData(); };
const handleReset = () => { params.value = { ...initParams }; getTableData(); };
const handleEdit = (row: any) => {
  form.value = { ...row };
  visible.value = true;
};
const getStatusTag = (status: number) => {
  const opt = statusOptions.find(s => s.value === status);
  return opt || { label: "未知", type: "info" };
};

const approve = async (id: number) => {
  const req = { status: 1 };
  const res = await http.post(`/labBooking/edit?id=${id}`, req);
  if (res.code === 200) { ElMessage.success("已通过"); await getTableData(); }
};
const reject = async (id: number) => {
  const req = { status: 2 };
  const res = await http.post(`/labBooking/edit?id=${id}`, req);
  if (res.code === 200) { ElMessage.success("已拒绝"); await getTableData(); }
};
const delRow = async (id: number) => {
  const res = await http.get(`/labBooking/del?id=${id}`);
  if (res.code === 200) { ElMessage.success("删除成功"); await getTableData(); }
  else { ElMessage.error("删除失败"); }
};
const handleSelectionChange = (selection) => { multipleSelection.value = selection; };
const submit = async () => {
  const res = await http.post(`/labBooking/edit?id=${form.value.id}`, { status: form.value.status, remark: form.value.remark });
  if (res.code === 200) { ElMessage.success("操作成功"); visible.value = false; await getTableData(); }
  else { ElMessage.error("操作失败"); }
};
const handleClose = () => { visible.value = false; };
const handleSizeChange = (val: number) => { params.value.pageSize = val; getTableData(); };
const handleCurrentChange = (val: number) => { params.value.pageNum = val; getTableData(); };

onMounted(async () => { await getLabOptions(); await getTableData(); });
</script>

<template>
  <div>
    <header class="flex">
      <div class="flex form-item">
        <p class="label-text">实验室：</p>
        <el-select v-model="params.labId" placeholder="全部" clearable style="width:200px">
          <el-option v-for="lab in labOptions" :key="lab.id" :label="lab.name" :value="lab.id" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>
    <main>
      <div class="flex op-box"></div>
      <el-table :data="tableData" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userName" label="预约人" width="90" />
        <el-table-column label="实验室" min-width="120">
          <template #default="scope">{{ labOptions.find(l => l.id === scope.row.labId)?.name || scope.row.labId }}</template>
        </el-table-column>
        <el-table-column prop="purpose" label="用途" min-width="140" />
        <el-table-column prop="bookDate" label="预约日期" width="100" />
        <el-table-column prop="startTime" label="开始" width="70" />
        <el-table-column prop="endTime" label="结束" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status).type">{{ getStatusTag(scope.row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button v-if="scope.row.status === 0" type="success" @click="approve(scope.row.id)">通过</el-button>
            <el-button v-if="scope.row.status === 0" type="warning" @click="reject(scope.row.id)">拒绝</el-button>
            <el-button v-if="hasPermission('labBooking', '编辑')" type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm v-if="hasPermission('labBooking', '删除')" title="您确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference><el-button type="danger" :icon="Delete">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, prev, pager, next" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </main>
    <el-dialog v-model="visible" :title="dialogTitle" width="500" :before-close="handleClose">
      <el-form :model="form" label-width="100">
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核意见" prop="remark"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit()">确认</el-button>
          <el-button @click="visible = false">取消</el-button>
        </div>
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
