<script setup lang="ts">
import http from "../../utils/http";
import { Search, Refresh } from "@element-plus/icons-vue";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "../../utils/system";

const initParams = { pageNum: 1, pageSize: 10, userId: null, trainingId: null, status: null };
const params = ref({ ...initParams });
const tableData = ref([]);
const total = ref(0);

const stats = ref({
  totalStudents: 0,
  avgProgress: 0,
  abnormalCount: 0,
  completionRate: 0,
});

const statusMap = {
  0: { label: "未开始", type: "info" },
  1: { label: "进行中", type: "warning" },
  2: { label: "已完成", type: "success" },
  3: { label: "异常", type: "danger" },
};

const fetchStats = async () => {
  try {
    const res = await http.get("/monitorRecord/list");
    if (res.code === 200 && Array.isArray(res.data)) {
      const records = res.data;
      const userIds = new Set(records.map((r) => r.userId));
      stats.value.totalStudents = userIds.size;
      stats.value.abnormalCount = records.filter((r) => r.status === 3).length;
      const total = records.length || 1;
      stats.value.completionRate = Math.round((records.filter((r) => r.status === 2).length / total) * 100);
      const totalProgress = records.reduce((sum, r) => sum + (r.progress || 0), 0);
      stats.value.avgProgress = Math.round(totalProgress / total);
    }
  } catch (e) {
    console.error("Failed to fetch stats:", e);
  }
};

const getTableData = async () => {
  let url = `/monitorRecord/page?pageNum=${params.value.pageNum}&pageSize=${params.value.pageSize}`;
  if (params.value.userId) url += `&userId=${params.value.userId}`;
  if (params.value.trainingId) url += `&trainingId=${params.value.trainingId}`;
  if (params.value.status !== null && params.value.status !== "") url += `&status=${params.value.status}`;
  const res = await http.get(url);
  if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total; }
};

const formatDuration = (minutes) => {
  if (!minutes) return "0分钟";
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return h > 0 ? `${h}小时${m}分钟` : `${m}分钟`;
};

const handleSearch = async () => { await getTableData(); };
const handleReset = () => { params.value = { ...initParams }; getTableData(); };
const handleSizeChange = (val: number) => { params.value.pageSize = val; getTableData(); };
const handleCurrentChange = (val: number) => { params.value.pageNum = val; getTableData(); };

onMounted(async () => {
  await fetchStats();
  await getTableData();
});
</script>

<template>
  <div>
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-number">{{ stats.totalStudents }}</div>
        <div class="stat-label">在训学生数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-number">{{ stats.avgProgress }}%</div>
        <div class="stat-label">平均进度</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-number danger">{{ stats.abnormalCount }}</div>
        <div class="stat-label">异常数量</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-number">{{ stats.completionRate }}%</div>
        <div class="stat-label">完成率</div>
      </el-card>
    </div>

    <header class="flex">
      <div class="flex form-item">
        <p class="label-text">学生ID：</p>
        <el-input v-model="params.userId" placeholder="学生ID" />
      </div>
      <div class="flex form-item">
        <p class="label-text">实训ID：</p>
        <el-input v-model="params.trainingId" placeholder="实训ID" />
      </div>
      <div class="flex form-item">
        <p class="label-text">状态：</p>
        <el-select v-model="params.status" placeholder="全部" clearable style="width: 140px">
          <el-option label="未开始" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="异常" :value="3" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>

    <main>
      <el-table :data="tableData" border>
        <el-table-column prop="userId" label="学生ID" width="100" />
        <el-table-column prop="trainingId" label="实训项目ID" width="120" />
        <el-table-column label="进度" width="180">
          <template #default="scope">
            <el-progress :percentage="scope.row.progress || 0" :stroke-width="16" />
          </template>
        </el-table-column>
        <el-table-column label="学习时长" width="120">
          <template #default="scope">
            {{ formatDuration(scope.row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusMap[scope.row.status]?.type || 'info'">
              {{ statusMap[scope.row.status]?.label || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abnormalInfo" label="异常信息" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="params.pageNum"
          v-model:page-size="params.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  flex: 1;
  text-align: center;
}
.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-color-primary);
}
.stat-number.danger {
  color: var(--el-color-danger);
}
.stat-label {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}
.label-text { color: #606266; width: 80px; }
.form-item { margin-right: 20px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
</style>
