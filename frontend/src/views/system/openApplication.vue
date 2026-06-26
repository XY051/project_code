<script setup lang="ts">
import http from "../../utils/http";
import { Search, Refresh, Delete, Check, Close, RefreshLeft } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "../../utils/system";

const initParams = {
  pageNum: 1,
  pageSize: 10,
  resourceName: "",
  auditStatus: "",
  useStatus: "",
};

const params = ref({ ...initParams });
const tableData = ref<any[]>([]);
const total = ref(0);
const multipleSelection = ref<any[]>([]);
const auditVisible = ref(false);
const auditForm = ref({ id: null as number | null, auditStatus: "已通过", auditComment: "" });

const auditStatusOptions = ["待审核", "已通过", "已驳回"];
const useStatusOptions = ["待使用", "使用中", "已归还", "已驳回"];

const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);

const getAuditType = (status: string) => {
  if (status === "已通过") return "success";
  if (status === "已驳回" || status === "未通过") return "danger";
  return "warning";
};

const getUseType = (status: string) => {
  if (status === "使用中") return "primary";
  if (status === "已归还") return "success";
  if (status === "已驳回") return "danger";
  return "info";
};

const buildQuery = () => {
  const query = new URLSearchParams({
    pageNum: String(params.value.pageNum),
    pageSize: String(params.value.pageSize),
  });
  if (params.value.resourceName) query.append("resourceName", params.value.resourceName);
  if (params.value.auditStatus) query.append("auditStatus", params.value.auditStatus);
  if (params.value.useStatus) query.append("useStatus", params.value.useStatus);
  return query.toString();
};

const getTableData = async () => {
  const res = await http.get(`/openApplication/page?${buildQuery()}`);
  if (res.code === 200) {
    tableData.value = res.data.records || [];
    total.value = res.data.total || 0;
  }
};

const handleSearch = async () => {
  params.value.pageNum = 1;
  await getTableData();
};

const handleReset = () => {
  params.value = { ...initParams };
  getTableData();
};

const openAudit = (row: any, status = "已通过") => {
  auditForm.value = { id: row.id, auditStatus: status, auditComment: "" };
  auditVisible.value = true;
};

const submitAudit = async () => {
  if (!auditForm.value.id) return;
  const query = new URLSearchParams({
    id: String(auditForm.value.id),
    auditStatus: auditForm.value.auditStatus,
  });
  if (auditForm.value.auditComment) query.append("auditComment", auditForm.value.auditComment);
  const res = await http.post(`/openApplication/audit?${query.toString()}`, {});
  if (res.code === 200) {
    ElMessage.success("审核完成");
    auditVisible.value = false;
    await getTableData();
  }
};

const returnResource = async (id: number) => {
  const res = await http.post(`/openApplication/return?id=${id}`, {});
  if (res.code === 200) {
    ElMessage.success("资源已归还");
    await getTableData();
  }
};

const delRow = async (id: number) => {
  const res = await http.get(`/openApplication/del?id=${id}`);
  if (res.code === 200) {
    ElMessage.success("删除成功");
    await getTableData();
  }
};

const batchDel = async () => {
  if (!selectedIds.value.length) {
    ElMessage.warning("请先选择要删除的数据");
    return;
  }
  const res = await http.post("/openApplication/batchDel", selectedIds.value);
  if (res.code === 200) {
    ElMessage.success("批量删除成功");
    multipleSelection.value = [];
    await getTableData();
  }
};

const handleSizeChange = (val: number) => {
  params.value.pageSize = val;
  getTableData();
};

const handleCurrentChange = (val: number) => {
  params.value.pageNum = val;
  getTableData();
};

onMounted(getTableData);
</script>

<template>
  <div>
    <header class="flex filter-bar">
      <div class="flex form-item">
        <p class="label-text">资源名称：</p>
        <el-input v-model="params.resourceName" placeholder="请输入资源名称" clearable />
      </div>
      <div class="flex form-item">
        <p class="label-text">审核状态：</p>
        <el-select v-model="params.auditStatus" placeholder="全部" clearable style="width: 150px">
          <el-option v-for="item in auditStatusOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </div>
      <div class="flex form-item">
        <p class="label-text">使用状态：</p>
        <el-select v-model="params.useStatus" placeholder="全部" clearable style="width: 150px">
          <el-option v-for="item in useStatusOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>

    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('openApplication', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
      </div>

      <el-table :data="tableData" border @selection-change="multipleSelection = $event">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="applicationNo" label="申请编号" width="170" />
        <el-table-column prop="resourceName" label="资源名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="userName" label="申请人" width="100" />
        <el-table-column prop="applicantOrg" label="申请单位" min-width="140" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="openType" label="开放类型" width="120" />
        <el-table-column prop="purpose" label="使用目的" min-width="160" show-overflow-tooltip />
        <el-table-column prop="useDate" label="使用日期" width="110" />
        <el-table-column prop="participantCount" label="人数" width="70" />
        <el-table-column prop="auditStatus" label="审核" width="95">
          <template #default="scope">
            <el-tag :type="getAuditType(scope.row.auditStatus)">{{ scope.row.auditStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="useStatus" label="使用" width="95">
          <template #default="scope">
            <el-tag :type="getUseType(scope.row.useStatus)">{{ scope.row.useStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditComment" label="审核意见" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="260">
          <template #default="scope">
            <el-button v-if="hasPermission('openApplication', '审核') && scope.row.auditStatus === '待审核'" type="success" :icon="Check" @click="openAudit(scope.row, '已通过')">通过</el-button>
            <el-button v-if="hasPermission('openApplication', '审核') && scope.row.auditStatus === '待审核'" type="warning" :icon="Close" @click="openAudit(scope.row, '已驳回')">驳回</el-button>
            <el-button v-if="scope.row.useStatus === '使用中'" type="primary" :icon="RefreshLeft" @click="returnResource(scope.row.id)">归还</el-button>
            <el-popconfirm v-if="hasPermission('openApplication', '删除')" title="您确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference>
                <el-button type="danger" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, prev, pager, next" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </main>

    <el-dialog v-model="auditVisible" title="共享开放申请审核" width="520">
      <el-form :model="auditForm" label-width="100">
        <el-form-item label="审核结果">
          <el-select v-model="auditForm.auditStatus" style="width: 100%">
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.auditComment" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitAudit">确认</el-button>
        <el-button @click="auditVisible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.filter-bar { flex-wrap: wrap; gap: 10px 0; }
.label-text { color: #606266; width: 92px; }
.form-item { margin-right: 18px; }
.op-box { margin: 10px 0; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
</style>
