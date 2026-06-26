<script setup lang="ts">
import http from "../../utils/http";
import { Search, Refresh, Delete, Check, Close, Edit } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "../../utils/system";

const initParams = {
  pageNum: 1,
  pageSize: 10,
  targetName: "",
  reservationType: "",
  status: "",
};

const params = ref({ ...initParams });
const tableData = ref<any[]>([]);
const total = ref(0);
const multipleSelection = ref<any[]>([]);
const auditVisible = ref(false);
const auditForm = ref({ id: null as number | null, status: "已通过", auditComment: "" });

const reservationTypeOptions = ["资源", "实训室"];
const statusOptions = ["待审核", "已通过", "已驳回", "已取消"];

const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);

const getStatusType = (status: string) => {
  if (status === "已通过") return "success";
  if (status === "已驳回" || status === "已取消") return "danger";
  return "warning";
};

const buildQuery = () => {
  const query = new URLSearchParams({
    pageNum: String(params.value.pageNum),
    pageSize: String(params.value.pageSize),
  });
  if (params.value.targetName) query.append("targetName", params.value.targetName);
  if (params.value.reservationType) query.append("reservationType", params.value.reservationType);
  if (params.value.status) query.append("status", params.value.status);
  return query.toString();
};

const getTableData = async () => {
  const res = await http.get(`/reservation/page?${buildQuery()}`);
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
  auditForm.value = { id: row.id, status, auditComment: row.auditComment || "" };
  auditVisible.value = true;
};

const submitAudit = async () => {
  if (!auditForm.value.id) return;
  const query = new URLSearchParams({
    id: String(auditForm.value.id),
    status: auditForm.value.status,
  });
  if (auditForm.value.auditComment) query.append("auditComment", auditForm.value.auditComment);
  const res = await http.post(`/reservation/audit?${query.toString()}`, {});
  if (res.code === 200) {
    ElMessage.success("审核完成");
    auditVisible.value = false;
    await getTableData();
  }
};

const delRow = async (id: number) => {
  const res = await http.get(`/reservation/del?id=${id}`);
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
  const res = await http.post("/reservation/batchDel", selectedIds.value);
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
        <p class="label-text">预约对象：</p>
        <el-input v-model="params.targetName" placeholder="请输入资源或实训室名称" clearable />
      </div>
      <div class="flex form-item">
        <p class="label-text">预约类型：</p>
        <el-select v-model="params.reservationType" placeholder="全部" clearable style="width: 150px">
          <el-option v-for="item in reservationTypeOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </div>
      <div class="flex form-item">
        <p class="label-text">状态：</p>
        <el-select v-model="params.status" placeholder="全部" clearable style="width: 150px">
          <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>

    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('reservation', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
      </div>

      <el-table :data="tableData" border @selection-change="multipleSelection = $event">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="reservationNo" label="预约编号" width="170" />
        <el-table-column prop="reservationType" label="类型" width="90" />
        <el-table-column prop="targetName" label="预约对象" min-width="180" show-overflow-tooltip />
        <el-table-column prop="userName" label="预约人" width="100" />
        <el-table-column prop="applicantOrg" label="预约单位" min-width="140" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="purpose" label="预约用途" min-width="160" show-overflow-tooltip />
        <el-table-column prop="useDate" label="使用日期" width="110" />
        <el-table-column prop="startTime" label="开始" width="90" />
        <el-table-column prop="endTime" label="结束" width="90" />
        <el-table-column prop="participantCount" label="人数" width="70" />
        <el-table-column prop="status" label="状态" width="95">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditComment" label="审核意见" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="scope">
            <el-button v-if="hasPermission('reservation', '审核') && scope.row.status === '待审核'" type="success" :icon="Check" @click="openAudit(scope.row, '已通过')">通过</el-button>
            <el-button v-if="hasPermission('reservation', '审核') && scope.row.status === '待审核'" type="warning" :icon="Close" @click="openAudit(scope.row, '已驳回')">驳回</el-button>
            <el-button v-if="hasPermission('reservation', '审核') && scope.row.status !== '待审核'" type="primary" :icon="Edit" @click="openAudit(scope.row, scope.row.status)">编辑</el-button>
            <el-popconfirm v-if="hasPermission('reservation', '删除')" title="您确定要删除吗?" @confirm="delRow(scope.row.id)">
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

    <el-dialog v-model="auditVisible" title="共享开放预约审核" width="520">
      <el-form :model="auditForm" label-width="100">
        <el-form-item label="预约状态">
          <el-select v-model="auditForm.status" style="width: 100%">
            <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
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
