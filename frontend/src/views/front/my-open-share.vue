<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { Search, Refresh, RefreshLeft } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import http from "../../utils/http";
import useUserStore from "../../stores/userStore";

const userStore = useUserStore();
const activeTab = ref("applications");
const loading = ref(false);
const appParams = ref({ pageNum: 1, pageSize: 10, resourceName: "" });
const reservationParams = ref({ pageNum: 1, pageSize: 10, targetName: "" });
const applications = ref<any[]>([]);
const reservations = ref<any[]>([]);
const appTotal = ref(0);
const reservationTotal = ref(0);

const currentUser = computed(() => {
  try {
    return typeof userStore.userInfo === "string" ? JSON.parse(userStore.userInfo) : userStore.userInfo;
  } catch {
    return {};
  }
});

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

const requireLogin = () => {
  if (!currentUser.value?.id) {
    ElMessage.warning("请先登录");
    return false;
  }
  return true;
};

const getApplications = async () => {
  if (!requireLogin()) return;
  loading.value = true;
  try {
    const query = new URLSearchParams({
      pageNum: String(appParams.value.pageNum),
      pageSize: String(appParams.value.pageSize),
      userId: String(currentUser.value.id),
    });
    if (appParams.value.resourceName) query.append("resourceName", appParams.value.resourceName);
    const res = await http.get(`/openApplication/page?${query.toString()}`);
    if (res.code === 200) {
      applications.value = res.data.records || [];
      appTotal.value = res.data.total || 0;
    }
  } finally {
    loading.value = false;
  }
};

const getReservations = async () => {
  if (!requireLogin()) return;
  loading.value = true;
  try {
    const query = new URLSearchParams({
      pageNum: String(reservationParams.value.pageNum),
      pageSize: String(reservationParams.value.pageSize),
      userId: String(currentUser.value.id),
    });
    if (reservationParams.value.targetName) query.append("targetName", reservationParams.value.targetName);
    const res = await http.get(`/reservation/page?${query.toString()}`);
    if (res.code === 200) {
      reservations.value = res.data.records || [];
      reservationTotal.value = res.data.total || 0;
    }
  } finally {
    loading.value = false;
  }
};

const searchApplications = () => {
  appParams.value.pageNum = 1;
  getApplications();
};

const searchReservations = () => {
  reservationParams.value.pageNum = 1;
  getReservations();
};

const resetApplications = () => {
  appParams.value = { pageNum: 1, pageSize: 10, resourceName: "" };
  getApplications();
};

const resetReservations = () => {
  reservationParams.value = { pageNum: 1, pageSize: 10, targetName: "" };
  getReservations();
};

const returnResource = async (id: number) => {
  const res = await http.post(`/openApplication/return?id=${id}`, {});
  if (res.code === 200) {
    ElMessage.success("资源已归还");
    await getApplications();
  }
};

onMounted(async () => {
  await Promise.all([getApplications(), getReservations()]);
});
</script>

<template>
  <div class="my-open-page">
    <div class="page-header">
      <h1>我的共享开放</h1>
      <p>查看资源开放申请、实训室预约和审核进度。</p>
    </div>

    <div class="content-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="资源申请" name="applications">
          <header class="search-bar">
            <el-input v-model="appParams.resourceName" placeholder="搜索资源名称" clearable />
            <el-button type="primary" :icon="Search" @click="searchApplications">搜索</el-button>
            <el-button :icon="Refresh" @click="resetApplications">重置</el-button>
          </header>
          <el-table :data="applications" border v-loading="loading" empty-text="暂无资源申请记录">
            <el-table-column prop="applicationNo" label="申请编号" width="170" />
            <el-table-column prop="resourceName" label="资源名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="applicantOrg" label="申请单位" min-width="140" show-overflow-tooltip />
            <el-table-column prop="useDate" label="使用日期" width="110" />
            <el-table-column prop="participantCount" label="人数" width="70" />
            <el-table-column prop="auditStatus" label="审核状态" width="100">
              <template #default="scope"><el-tag :type="getAuditType(scope.row.auditStatus)">{{ scope.row.auditStatus }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="useStatus" label="使用状态" width="100">
              <template #default="scope"><el-tag :type="getUseType(scope.row.useStatus)">{{ scope.row.useStatus }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="auditComment" label="审核意见" min-width="160" show-overflow-tooltip />
            <el-table-column prop="createTime" label="提交时间" width="180" />
            <el-table-column label="操作" fixed="right" width="100">
              <template #default="scope">
                <el-popconfirm v-if="scope.row.useStatus === '使用中'" title="确认归还该资源吗?" @confirm="returnResource(scope.row.id)">
                  <template #reference>
                    <el-button type="primary" :icon="RefreshLeft">归还</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination v-model:current-page="appParams.pageNum" v-model:page-size="appParams.pageSize" :page-sizes="[10, 20, 50]" layout="sizes, prev, pager, next, total" :total="appTotal" @size-change="getApplications" @current-change="getApplications" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="预约记录" name="reservations">
          <header class="search-bar">
            <el-input v-model="reservationParams.targetName" placeholder="搜索预约对象" clearable />
            <el-button type="primary" :icon="Search" @click="searchReservations">搜索</el-button>
            <el-button :icon="Refresh" @click="resetReservations">重置</el-button>
          </header>
          <el-table :data="reservations" border v-loading="loading" empty-text="暂无预约记录">
            <el-table-column prop="reservationNo" label="预约编号" width="170" />
            <el-table-column prop="reservationType" label="类型" width="90" />
            <el-table-column prop="targetName" label="预约对象" min-width="180" show-overflow-tooltip />
            <el-table-column prop="useDate" label="使用日期" width="110" />
            <el-table-column prop="startTime" label="开始" width="90" />
            <el-table-column prop="endTime" label="结束" width="90" />
            <el-table-column prop="participantCount" label="人数" width="70" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope"><el-tag :type="getAuditType(scope.row.status)">{{ scope.row.status }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="auditComment" label="审核意见" min-width="160" show-overflow-tooltip />
            <el-table-column prop="createTime" label="提交时间" width="180" />
          </el-table>
          <div class="pagination-container">
            <el-pagination v-model:current-page="reservationParams.pageNum" v-model:page-size="reservationParams.pageSize" :page-sizes="[10, 20, 50]" layout="sizes, prev, pager, next, total" :total="reservationTotal" @size-change="getReservations" @current-change="getReservations" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.my-open-page { min-height: 100vh; background: #f4f7fb; padding: 24px; }
.page-header { max-width: 1180px; margin: 0 auto 18px; }
.page-header h1 { margin: 0 0 8px; color: #1f2937; font-size: 28px; }
.page-header p { margin: 0; color: #64748b; }
.content-card { max-width: 1180px; margin: 0 auto; background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 18px; }
.search-bar { display: flex; gap: 12px; margin-bottom: 14px; }
.search-bar :deep(.el-input) { width: 260px; }
.pagination-container { margin-top: 18px; display: flex; justify-content: center; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
@media (max-width: 680px) {
  .my-open-page { padding: 12px; }
  .search-bar { flex-direction: column; }
  .search-bar :deep(.el-input) { width: 100%; }
}
</style>
