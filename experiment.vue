<script setup lang="ts">
import http from '@/utils/http'
import { Search, Refresh, Delete, Star, Download, Upload, Paperclip } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { hasPermission } from '@/utils/system'
import config from '@/config'

// ===== 查询参数 =====
const initParams = { pageNum: 1, pageSize: 10, realName: "", phone: "", status: "", trainingProjectId: null };
const params = ref({ ...initParams });
const tableData = ref([]);
const total = ref(0);
const multipleSelection = ref([]);
const selectedIds = computed(() => multipleSelection.value.map((item: any) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);

// ===== 单个批改弹窗 =====
const gradeVisible = ref(false);
const gradeForm = ref({ id: null, score: null, teacherComment: "", status: 3 });
const currentRecord = ref<any>(null);

// ===== 批量批改弹窗 =====
const batchGradeVisible = ref(false);
const batchGradeScore = ref(null);
const batchGradeComment = ref("");
const batchGradeStatus = ref(3);
const batchGradeLoading = ref(false);

// ===== 附件预览 =====
const attachmentVisible = ref(false);
const attachmentUrl = ref("");

const statusDict: Record<number, string> = { 0: "未完成", 1: "已完成", 2: "待批改", 3: "已批改" };
const getStatusType = (status: number) => ({ 0: "info", 1: "warning", 2: "danger", 3: "success" }[status] || "info");
const trainingList = ref<any[]>([]);

// ===== 数据获取 =====
const getTrainingList = async () => {
  const res = await http.get("/training/list");
  if (res.code === 200) trainingList.value = res.data || [];
};

const getTableData = async () => {
  const q = new URLSearchParams();
  q.set("pageNum", String(params.value.pageNum));
  q.set("pageSize", String(params.value.pageSize));
  if (params.value.realName) q.set("realName", params.value.realName);
  if (params.value.phone) q.set("phone", params.value.phone);
  if (params.value.status !== "") q.set("status", String(params.value.status));
  if (params.value.trainingProjectId) q.set("trainingProjectId", String(params.value.trainingProjectId));
  const res = await http.get(`/experiment/page?${q.toString()}`);
  if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total; }
};

const handleSearch = () => getTableData();
const handleReset = () => { params.value = { ...initParams }; getTableData(); };

const delRow = async (id: number) => {
  const res = await http.get(`/experiment/del?id=${id}`);
  if (res.code === 200) { ElMessage.success("删除成功"); await getTableData(); }
};

const batchDel = async () => {
  if (!hasSelected.value) { ElMessage.warning("请先选择记录"); return; }
  await ElMessageBox.confirm(`确定要删除${multipleSelection.value.length}条实验记录吗？`, "批量删除", { type: "warning" });
  const res = await http.post("/experiment/batchDel", selectedIds.value);
  if (res.code === 200) { ElMessage.success("批量删除成功"); multipleSelection.value = []; await getTableData(); }
};

// ===== 单个批改 =====
const openGradeDialog = (row: any) => {
  currentRecord.value = row;
  gradeForm.value = { id: row.id, score: row.score || null, teacherComment: row.teacherComment || "", status: row.status >= 2 ? row.status : 3 };
  gradeVisible.value = true;
};

const submitGrade = async () => {
  const res = await http.post(`/experiment/grade?id=${gradeForm.value.id}&score=${gradeForm.value.score || ''}&teacherComment=${encodeURIComponent(gradeForm.value.teacherComment || '')}&status=${gradeForm.value.status}`);
  if (res.code === 200) { ElMessage.success("批改成功"); gradeVisible.value = false; await getTableData(); }
};

// ===== 批量批改 =====
const openBatchGrade = () => {
  if (!hasSelected.value) { ElMessage.warning("请先选择实验记录"); return; }
  batchGradeScore.value = null;
  batchGradeComment.value = "";
  batchGradeStatus.value = 3;
  batchGradeVisible.value = true;
};

const submitBatchGrade = async () => {
  batchGradeLoading.value = true;
  try {
    const gradeList = selectedIds.value.map((id: number) => ({
      id,
      score: batchGradeScore.value,
      teacherComment: batchGradeComment.value,
      status: batchGradeStatus.value
    }));
    const res = await http.post("/experiment/batchGrade", gradeList);
    if (res.code === 200) {
      ElMessage.success(`成功批改 ${res.data} 条记录`);
      batchGradeVisible.value = false;
      multipleSelection.value = [];
      await getTableData();
    }
  } finally { batchGradeLoading.value = false; }
};

// ===== 附件预览 =====
const previewAttachment = (row: any) => {
  if (!row.submitFile) { ElMessage.warning("该记录无附件"); return; }
  attachmentUrl.value = config.baseUrl + row.submitFile;
  attachmentVisible.value = true;
};

// ===== 导出 =====
const handleExport = async () => {
  const q = new URLSearchParams();
  if (params.value.realName) q.set("realName", params.value.realName);
  if (params.value.status !== "") q.set("status", String(params.value.status));
  try {
    const res = await http.get(`/experiment/export?${q.toString()}`, { responseType: "blob" });
    const blob = new Blob([res], { type: "text/csv;charset=utf-8" });
    const link = document.createElement("a"); link.href = URL.createObjectURL(blob); link.download = "实验记录.csv"; link.click();
    ElMessage.success("导出成功");
  } catch { ElMessage.error("导出失败"); }
};

const handleSelectionChange = (selection: any) => { multipleSelection.value = selection; };
const handleSizeChange = (val: number) => { params.value.pageSize = val; getTableData(); };
const handleCurrentChange = (val: number) => { params.value.pageNum = val; getTableData(); };

onMounted(() => { getTableData(); getTrainingList(); });
</script>

<template>
  <div>
    <!-- 搜索区域 -->
    <header class="flex">
      <div class="flex form-item"><p class="label-text">姓名：</p><el-input v-model="params.realName" placeholder="请输入姓名" clearable style="width:140px" /></div>
      <div class="flex form-item"><p class="label-text">手机号：</p><el-input v-model="params.phone" placeholder="请输入手机号" clearable style="width:140px" /></div>
      <div class="flex form-item"><p class="label-text">状态：</p>
        <el-select v-model="params.status" placeholder="全部状态" clearable style="width:120px">
          <el-option v-for="(label, val) in statusDict" :key="val" :label="label" :value="String(val)" />
        </el-select>
      </div>
      <div class="flex form-item"><p class="label-text">实训项目：</p>
        <el-select v-model="params.trainingProjectId" placeholder="全部项目" clearable style="width:180px">
          <el-option v-for="t in trainingList" :key="t.id" :label="t.title" :value="t.id" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>

    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('experiment', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
        <el-button type="warning" :icon="Star" :disabled="!hasSelected" @click="openBatchGrade">批量批改</el-button>
        <el-button type="success" :icon="Download" @click="handleExport">批量导出</el-button>
      </div>

      <el-table :data="tableData" border @selection-change="handleSelectionChange" empty-text="暂无实验记录">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="学生账号" width="120" />
        <el-table-column prop="realName" label="学生姓名" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="trainingProjectTitle" label="实训项目" show-overflow-tooltip min-width="160" />
        <el-table-column prop="score" label="得分" width="80">
          <template #default="scope">{{ scope.row.score != null ? scope.row.score : '-' }}</template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="90" />
        <el-table-column prop="teacherComment" label="教师评语" show-overflow-tooltip width="150">
          <template #default="scope">{{ scope.row.teacherComment || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope"><el-tag :type="getStatusType(scope.row.status)">{{ statusDict[scope.row.status] || '未知' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="附件" width="70">
          <template #default="scope">
            <el-button v-if="scope.row.submitFile" :icon="Paperclip" size="small" text @click="previewAttachment(scope.row)" title="查看附件" />
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="scope">
            <el-button type="warning" :icon="Star" size="small" @click="openGradeDialog(scope.row)">批改</el-button>
            <el-popconfirm v-if="hasPermission('experiment', '删除')" title="确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference><el-button type="danger" :icon="Delete" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, total, prev, pager, next" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </main>

    <!-- 单个批改弹窗 -->
    <el-dialog v-model="gradeVisible" title="批改实验" width="500px">
      <el-form :model="gradeForm" label-width="100px">
        <el-form-item label="学生姓名"><el-input :model-value="currentRecord?.realName" disabled /></el-form-item>
        <el-form-item label="实训项目"><el-input :model-value="currentRecord?.trainingProjectTitle" disabled /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="得分"><el-input-number v-model="gradeForm.score" :min="0" :max="100" :precision="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态">
            <el-select v-model="gradeForm.status" style="width:100%">
              <el-option label="已批改" :value="3" /><el-option label="待批改" :value="2" /><el-option label="已完成" :value="1" />
            </el-select>
          </el-form-item></el-col>
        </el-row>
        <el-form-item label="教师评语"><el-input v-model="gradeForm.teacherComment" type="textarea" :rows="3" placeholder="请输入评语" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitGrade">确认批改</el-button><el-button @click="gradeVisible = false">取消</el-button></div></template>
    </el-dialog>

    <!-- 批量批改弹窗 -->
    <el-dialog v-model="batchGradeVisible" title="批量批改实验" width="500px">
      <el-alert :title="`已选择 ${multipleSelection.length} 条记录进行批量批改`" type="info" show-icon :closable="false" style="margin-bottom:16px" />
      <el-form label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="统一得分"><el-input-number v-model="batchGradeScore" :min="0" :max="100" :precision="1" style="width:100%" placeholder="可选" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="统一状态">
            <el-select v-model="batchGradeStatus" style="width:100%">
              <el-option label="已批改" :value="3" /><el-option label="待批改" :value="2" />
            </el-select>
          </el-form-item></el-col>
        </el-row>
        <el-form-item label="统一评语"><el-input v-model="batchGradeComment" type="textarea" :rows="3" placeholder="可选，将应用于所选全部记录" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" :loading="batchGradeLoading" @click="submitBatchGrade">确认批量批改</el-button><el-button @click="batchGradeVisible = false">取消</el-button></div></template>
    </el-dialog>

    <!-- 附件预览弹窗 -->
    <el-dialog v-model="attachmentVisible" title="附件预览" width="70%" :close-on-click-modal="true">
      <div v-if="attachmentUrl" style="text-align:center">
        <el-link :href="attachmentUrl" target="_blank" type="primary" :underline="false">{{ attachmentUrl }}</el-link>
        <p style="margin-top:12px;color:#909399">点击链接在新窗口打开文件，或右键下载</p>
      </div>
      <div v-else class="text-muted" style="text-align:center;padding:40px">无法加载附件</div>
      <template #footer><el-button @click="attachmentVisible = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.label-text { color: #606266; width: 90px; }
.text-muted { color: #c0c4cc; }
.form-item { margin-right: 20px; }
.op-box { margin: 10px 0; gap: 10px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
</style>
