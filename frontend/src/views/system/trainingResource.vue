<script setup lang="ts">
import http from "../../utils/http";
import Config from "../../config/index";
import { getImageUrl, hasPermission } from "../../utils/system";
import { Plus, Search, Refresh, Edit, Delete, Picture as IconPicture } from "@element-plus/icons-vue";
import { ElMessage, type UploadProps } from "element-plus";
import { computed, onMounted, ref } from "vue";

const initParams = {
  pageNum: 1,
  pageSize: 10,
  name: "",
  categoryId: null as number | null,
  resourceType: "",
  openType: "",
};

const initForm = {
  name: "",
  categoryId: null as number | null,
  resourceType: "虚拟仿真",
  openType: "校内实训",
  resourceLevel: "在线仿真资源",
  coverImage: "",
  intro: "",
  description: "",
  accessUrl: "",
  availableCount: 0,
  status: "可用",
};

const resourceTypeOptions = ["虚拟仿真", "在线课程", "教学案例", "实验素材"];
const openTypeOptions = ["校内实训", "跨专业共享", "跨院校共享", "社会开放"];
const statusOptions = ["可用", "维护中", "停用"];

const params = ref({ ...initParams });
const form = ref<any>({ ...initForm });
const tableData = ref<any[]>([]);
const categoryList = ref<any[]>([]);
const total = ref(0);
const visible = ref(false);
const imageUrl = ref("");
const multipleSelection = ref<any[]>([]);
const uploadUrl = `${Config.baseUrl}/file/upload`;
const uploadData = { authorization: `${localStorage.getItem("token") || ""}` };

const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);
const isEdit = computed(() => Number(form.value.id) > 0);
const dialogTitle = computed(() => (isEdit.value ? "编辑虚拟仿真资源" : "新增虚拟仿真资源"));

const safeImage = (url?: string) => {
  if (!url) return "";
  return url.startsWith("http") ? url : getImageUrl(url);
};

const buildQuery = () => {
  const query = new URLSearchParams({
    pageNum: String(params.value.pageNum),
    pageSize: String(params.value.pageSize),
  });
  if (params.value.name) query.append("name", params.value.name);
  if (params.value.categoryId) query.append("categoryId", String(params.value.categoryId));
  if (params.value.resourceType) query.append("resourceType", params.value.resourceType);
  if (params.value.openType) query.append("openType", params.value.openType);
  return query.toString();
};

const getCategoryList = async () => {
  const res = await http.get("/resourceCategory/list");
  if (res.code === 200) {
    categoryList.value = res.data || [];
  }
};

const getTableData = async () => {
  const res = await http.get(`/trainingResource/page?${buildQuery()}`);
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

const handleAdd = () => {
  form.value = { ...initForm };
  imageUrl.value = "";
  visible.value = true;
};

const handleEdit = (row: any) => {
  form.value = { ...row };
  imageUrl.value = safeImage(row.coverImage);
  visible.value = true;
};

const delRow = async (id: number) => {
  const res = await http.get(`/trainingResource/del?id=${id}`);
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
  const res = await http.post("/trainingResource/batchDel", selectedIds.value);
  if (res.code === 200) {
    ElMessage.success("批量删除成功");
    multipleSelection.value = [];
    await getTableData();
  }
};

const submit = async () => {
  if (!form.value.name) {
    ElMessage.warning("请输入资源名称");
    return;
  }
  if (!form.value.categoryId) {
    ElMessage.warning("请选择资源分类");
    return;
  }
  const url = isEdit.value ? `/trainingResource/edit?id=${form.value.id}` : "/trainingResource/add";
  const res = await http.post(url, form.value);
  if (res.code === 200) {
    ElMessage.success("保存成功");
    visible.value = false;
    await getTableData();
  }
};

const beforeAvatarUpload: UploadProps["beforeUpload"] = (rawFile) => {
  if (!rawFile.type.includes("image")) {
    ElMessage.error("只允许上传图片文件");
    return false;
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error("图片不能超过 2MB");
    return false;
  }
  return true;
};

const handleAvatarSuccess: UploadProps["onSuccess"] = (response) => {
  form.value.coverImage = response.data;
  imageUrl.value = getImageUrl(response.data);
};

const handleSizeChange = (val: number) => {
  params.value.pageSize = val;
  getTableData();
};

const handleCurrentChange = (val: number) => {
  params.value.pageNum = val;
  getTableData();
};

onMounted(async () => {
  await getCategoryList();
  await getTableData();
});
</script>

<template>
  <div>
    <header class="flex filter-bar">
      <div class="flex form-item">
        <p class="label-text">资源名称：</p>
        <el-input v-model="params.name" placeholder="请输入虚拟仿真资源名称" clearable />
      </div>
      <div class="flex form-item">
        <p class="label-text">资源分类：</p>
        <el-select v-model="params.categoryId" placeholder="全部分类" clearable style="width: 180px">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </div>
      <div class="flex form-item">
        <p class="label-text">开放类型：</p>
        <el-select v-model="params.openType" placeholder="全部" clearable style="width: 160px">
          <el-option v-for="item in openTypeOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>

    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('trainingResource', '新增')" :icon="Plus" type="primary" @click="handleAdd">新增</el-button>
        <el-button v-if="hasPermission('trainingResource', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
      </div>

      <el-table :data="tableData" border @selection-change="multipleSelection = $event">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="资源名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="resourceType" label="类型" width="110" />
        <el-table-column prop="openType" label="开放类型" width="120" />
        <el-table-column prop="coverImage" label="封面" width="90">
          <template #default="scope">
            <el-image v-if="scope.row.coverImage" :src="safeImage(scope.row.coverImage)" fit="cover" :preview-src-list="[safeImage(scope.row.coverImage)]" :preview-teleported="true">
              <template #error>
                <div class="image-slot"><el-icon><icon-picture /></el-icon></div>
              </template>
            </el-image>
            <div v-else class="image-slot"><el-icon><icon-picture /></el-icon></div>
          </template>
        </el-table-column>
        <el-table-column prop="intro" label="简介" min-width="200" show-overflow-tooltip />
        <el-table-column prop="availableCount" label="可用数" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.status === '可用' ? 'success' : scope.row.status === '维护中' ? 'warning' : 'info'">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="90" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button v-if="hasPermission('trainingResource', '编辑')" type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="您确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference>
                <el-button v-if="hasPermission('trainingResource', '删除')" type="danger" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, prev, pager, next" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </main>

    <el-dialog v-model="visible" :title="dialogTitle" width="860" top="3%">
      <el-form :model="form" label-width="120">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资源名称" required>
              <el-input v-model="form.name" placeholder="请输入资源名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资源分类" required>
              <el-select v-model="form.categoryId" placeholder="请选择资源分类" style="width: 100%">
                <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资源类型">
              <el-select v-model="form.resourceType" style="width: 100%">
                <el-option v-for="item in resourceTypeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开放类型">
              <el-select v-model="form.openType" style="width: 100%">
                <el-option v-for="item in openTypeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资源等级">
              <el-input v-model="form.resourceLevel" placeholder="如：在线仿真资源" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="可用数量">
              <el-input-number v-model="form.availableCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实验入口">
              <el-input v-model="form.accessUrl" placeholder="请输入实验入口链接" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="封面图片">
              <el-upload class="avatar-uploader" :action="uploadUrl" :headers="uploadData" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                <el-image v-if="imageUrl" :src="imageUrl" style="width: 100%; height: 100%" fit="cover" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="资源简介">
              <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="请输入资源简介" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="资源说明">
              <el-input v-model="form.description" type="textarea" :rows="6" placeholder="请输入资源说明、适用课程或开放要求" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submit">确认</el-button>
        <el-button @click="visible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.filter-bar { flex-wrap: wrap; gap: 10px 0; }
.label-text { color: #606266; width: 92px; }
.form-item { margin-right: 20px; }
.op-box { margin: 10px 0; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
.image-slot { width: 55px; height: 55px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; color: #a8abb2; border-radius: 4px; }
.avatar-uploader { width: 178px; height: 120px; overflow: hidden; border: 1px dashed var(--el-border-color); border-radius: 6px; }
.avatar-uploader:hover { border-color: var(--el-color-primary); }
.el-icon.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 178px; height: 120px; text-align: center; }
:deep(.el-image) { width: 55px; height: 55px; border-radius: 4px; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
</style>
