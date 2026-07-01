<script setup lang="ts">
import http from '@/utils/http'
import { Plus, Search, Refresh, Edit, Delete, Upload, Download, Warning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type UploadProps } from 'element-plus'
import { getImageUrl, hasPermission } from '@/utils/system'
import config from '@/config'
import { Picture as IconPicture } from "@element-plus/icons-vue";
import { computed, onMounted, ref } from "vue";

// ===== 查询参数 =====
const initParams = { pageNum: 1, pageSize: 10, title: "", difficulty: "", applicableMajor: "", status: null };
const params = ref({ ...initParams });
const initForm = { title: "", coverImage: "", content: "", startTime: "", endTime: "", address: "", maxPeople: 0, status: 1, viewCount: 0, process: "", requirement: "", experimentResult: "", difficulty: "初级", classHours: 0, applicableMajor: "", labId: null, equipmentIds: "" };
const form = ref({ ...initForm });
const tableData = ref([]);
const total = ref(0);
const visible = ref(false);
const multipleSelection = ref([]);
const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);
const isEdit = computed(() => form.value.id > 0);
const dialogTitle = computed(() => isEdit.value ? "编辑实训项目" : "新增实训项目");
const uploadUrl = ref(`${config.baseUrl}/file/upload`);
const uploadData = { authorization: `${localStorage.getItem("token")}` };
const imageUrl = ref("");

// ===== 批量导入弹窗 =====
const importVisible = ref(false);
const importFile = ref(null);
const importUploadUrl = ref(`${config.baseUrl}/training/import`);

// ===== 常量 =====
const statusDict: Record<number, string> = { 0: "未开始", 1: "进行中", 2: "已结束", 3: "已下架" };
const difficultyDict = ["初级", "中级", "高级"];
const getStatusType = (s: number) => ({ 0: "info", 1: "success", 2: "warning", 3: "danger" }[s] || "info");

// ===== 数据获取 =====
const getTableData = async () => {
  const q = new URLSearchParams();
  q.set("pageNum", String(params.value.pageNum));
  q.set("pageSize", String(params.value.pageSize));
  if (params.value.title) q.set("title", params.value.title);
  if (params.value.difficulty) q.set("difficulty", params.value.difficulty);
  if (params.value.applicableMajor) q.set("applicableMajor", params.value.applicableMajor);
  if (params.value.status != null) q.set("status", String(params.value.status));
  const res = await http.get(`/training/page?${q.toString()}`);
  if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total; }
};

const handleSearch = () => getTableData();
const handleReset = () => { params.value = { ...initParams }; getTableData(); };
const handleAdd = () => { form.value = { ...initForm }; imageUrl.value = ""; visible.value = true; };
const handleEdit = (row: any) => { imageUrl.value = row.coverImage ? getImageUrl(row.coverImage) : ""; form.value = { ...row }; visible.value = true; };

const delRow = async (id: number) => {
  const res = await http.get(`/training/del?id=${id}`);
  if (res.code === 200) { ElMessage.success("删除成功"); await getTableData(); }
};

const batchDel = async () => {
  if (!hasSelected.value) { ElMessage.warning("请先选择实训项目"); return; }
  await ElMessageBox.confirm(`确定要删除${multipleSelection.value.length}个实训项目吗？`, "批量删除", { type: "warning" });
  const res = await http.post("/training/batchDel", selectedIds.value);
  if (res.code === 200) { ElMessage.success("批量删除成功"); multipleSelection.value = []; await getTableData(); }
};

const submit = async () => {
  if (isEdit.value) {
    const res = await http.post(`/training/edit?id=${form.value.id}`, form.value);
    if (res.code === 200) { ElMessage.success("编辑成功"); visible.value = false; await getTableData(); }
  } else {
    const res = await http.post("/training/add", form.value);
    if (res.code === 200) { ElMessage.success("新增成功"); visible.value = false; await getTableData(); }
  }
};

const handleUpdateStatus = async (row: any, status: number) => {
  const label = status === 3 ? "下架" : "上架";
  await ElMessageBox.confirm(`确认${label}「${row.title}」吗？`, `实训${label}`);
  const res = await http.post(`/training/updateStatus?id=${row.id}&status=${status}`);
  if (res.code === 200) { ElMessage.success(`${label}成功`); await getTableData(); }
};

// ===== 导入导出 =====
const openImport = () => { importFile.value = null; importVisible.value = true; };

const handleImportSuccess: UploadProps["onSuccess"] = (response: any) => {
  if (response.code === 200) { ElMessage.success(`成功导入 ${response.data} 条实训项目`); importVisible.value = false; getTableData(); }
  else ElMessage.error(response.msg || "导入失败");
};

const handleExport = async () => {
  const q = new URLSearchParams();
  if (params.value.title) q.set("title", params.value.title);
  if (params.value.difficulty) q.set("difficulty", params.value.difficulty);
  const res = await http.get(`/training/export?${q.toString()}`, { responseType: "blob" });
  const blob = new Blob([res], { type: "text/csv;charset=utf-8" });
  const link = document.createElement("a"); link.href = URL.createObjectURL(blob); link.download = "实训项目.csv"; link.click();
  ElMessage.success("导出成功");
};

const handleSelectionChange = (selection: any) => { multipleSelection.value = selection; };

const beforeAvatarUpload: UploadProps["beforeUpload"] = (rawFile) => {
  if (!rawFile.type.includes("image")) { ElMessage.error("只允许上传图片文件"); return false; }
  if (rawFile.size / 1024 / 1024 > 5) { ElMessage.error("图片超过5MB限制"); return false; }
  return true;
};

const handleAvatarSuccess: UploadProps["onSuccess"] = (response: any) => {
  if (response.code === 200) { form.value.coverImage = response.data; imageUrl.value = `${config.baseUrl}${response.data}`; }
};

const handleSizeChange = (val: number) => { params.value.pageSize = val; getTableData(); };
const handleCurrentChange = (val: number) => { params.value.pageNum = val; getTableData(); };

onMounted(() => getTableData());
</script>

<template>
  <div>
    <!-- 搜索区域 -->
    <header class="flex">
      <div class="flex form-item"><p class="label-text">实训名称：</p><el-input v-model="params.title" placeholder="请输入实训名称" clearable style="width:160px" /></div>
      <div class="flex form-item"><p class="label-text">难度：</p>
        <el-select v-model="params.difficulty" placeholder="全部难度" clearable style="width:120px">
          <el-option v-for="d in difficultyDict" :key="d" :label="d" :value="d" />
        </el-select>
      </div>
      <div class="flex form-item"><p class="label-text">适用专业：</p><el-input v-model="params.applicableMajor" placeholder="请输入专业" clearable style="width:140px" /></div>
      <div class="flex form-item"><p class="label-text">状态：</p>
        <el-select v-model="params.status" placeholder="全部状态" clearable style="width:120px">
          <el-option v-for="(label, val) in statusDict" :key="val" :label="label" :value="Number(val)" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>

    <!-- 操作按钮 -->
    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('training', '新增')" :icon="Plus" type="primary" @click="handleAdd">新增</el-button>
        <el-button v-if="hasPermission('training', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
        <el-button :icon="Upload" type="warning" @click="openImport">批量导入</el-button>
        <el-button :icon="Download" type="success" @click="handleExport">批量导出</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border @selection-change="handleSelectionChange" empty-text="暂无实训项目数据">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="实训名称" width="180" show-overflow-tooltip />
        <el-table-column prop="coverImage" label="封面" width="80">
          <template #default="scope">
            <el-image v-if="scope.row.coverImage" :src="getImageUrl(scope.row.coverImage)" :preview-src-list="[getImageUrl(scope.row.coverImage)]" :preview-teleported="true" fit="cover">
              <template #error><div class="image-slot"><el-icon><icon-picture /></el-icon></div></template>
            </el-image>
            <span v-else class="text-muted">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.difficulty" :type="scope.row.difficulty === '初级' ? 'success' : scope.row.difficulty === '中级' ? 'warning' : 'danger'" size="small">{{ scope.row.difficulty }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="classHours" label="课时" width="70" />
        <el-table-column prop="applicableMajor" label="适用专业" width="120" show-overflow-tooltip />
        <el-table-column prop="address" label="实训地点" width="120" show-overflow-tooltip />
        <el-table-column prop="maxPeople" label="限人数" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ statusDict[scope.row.status] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="300" min-width="300">
          <template #default="scope">
            <el-button v-if="hasPermission('training', '编辑')" type="primary" :icon="Edit" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="hasPermission('training', '上下架') && scope.row.status !== 3" type="warning" size="small" @click="handleUpdateStatus(scope.row, 3)">下架</el-button>
            <el-button v-if="hasPermission('training', '上下架') && scope.row.status === 3" type="success" size="small" @click="handleUpdateStatus(scope.row, 1)">上架</el-button>
            <el-popconfirm v-if="hasPermission('training', '删除')" title="确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference><el-button type="danger" :icon="Delete" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, total, prev, pager, next" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </main>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="visible" :title="dialogTitle" width="900" :before-close="() => visible = false">
      <el-form :model="form" label-width="120">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="实训名称"><el-input v-model="form.title" placeholder="请输入实训名称" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="难度"><el-select v-model="form.difficulty" style="width:100%"><el-option v-for="d in difficultyDict" :key="d" :label="d" :value="d" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="实训内容"><el-input type="textarea" :autosize="{ minRows: 3, maxRows: 8 }" v-model="form.content" placeholder="请输入实训内容" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="实训地点"><el-input v-model="form.address" placeholder="请输入地点" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="人数限制"><el-input-number v-model="form.maxPeople" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="课时"><el-input-number v-model="form.classHours" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="适用专业"><el-input v-model="form.applicableMajor" placeholder="多个专业用逗号分隔" /></el-form-item>
        <el-form-item label="实训流程"><el-input type="textarea" :autosize="{ minRows: 2, maxRows: 5 }" v-model="form.process" placeholder="请输入实训流程" /></el-form-item>
        <el-form-item label="实训要求"><el-input type="textarea" :autosize="{ minRows: 2, maxRows: 5 }" v-model="form.requirement" placeholder="请输入实训要求" /></el-form-item>
        <el-form-item label="实验结果要求"><el-input type="textarea" :autosize="{ minRows: 2, maxRows: 5 }" v-model="form.experimentResult" placeholder="请输入实验结果要求" /></el-form-item>
        <el-form-item label="实验设备ID"><el-input v-model="form.equipmentIds" placeholder="多个设备ID逗号分隔" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">未开始</el-radio><el-radio :value="1">进行中</el-radio><el-radio :value="2">已结束</el-radio><el-radio :value="3">已下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload class="avatar-uploader" :action="uploadUrl" :headers="uploadData" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
            <el-image v-if="imageUrl" :src="imageUrl" fit="cover" style="width:178px;height:178px" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submit()">确认</el-button><el-button @click="visible = false">取消</el-button></div></template>
    </el-dialog>

    <!-- 批量导入弹窗 -->
    <el-dialog v-model="importVisible" title="批量导入实训项目" width="500px">
      <el-upload class="import-uploader" :action="importUploadUrl" :headers="uploadData" :on-success="handleImportSuccess" accept=".xlsx,.xls" drag>
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">拖拽Excel文件到此处 或 <em>点击上传</em></div>
        <template #tip><div class="el-upload__tip">仅支持 .xlsx / .xls 格式，第一行为表头</div></template>
      </el-upload>
      <template #footer><el-button @click="importVisible = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.label-text { color: #606266; width: 90px; }
.text-muted { color: #c0c4cc; }
.form-item { margin-right: 20px; }
.op-box { margin: 10px 0; gap: 10px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
.avatar-uploader { width: 178px; height: 178px; overflow: hidden; border: 1px dashed var(--el-border-color); border-radius: 6px; &:hover { border-color: var(--el-color-primary); } }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 178px; height: 178px; text-align: center; display: flex; align-items: center; justify-content: center; }
.import-uploader { width: 100%; }
:deep(.el-image) { width: 55px; height: 55px; border-radius: 4px; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: var(--el-fill-color-light); color: var(--el-text-color-secondary); font-size: 30px; }
</style>
