<script setup lang="ts">
import http from "../../utils/http";
import { Plus, Search, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "../../utils/system";

const initParams = { pageNum: 1, pageSize: 10, name: "" };
const params = ref({ ...initParams });
const initForm = { name: "", description: "" };
const form = ref({ ...initForm });
const tableData = ref([]);
const total = ref(0);
const visible = ref(false);
const multipleSelection = ref([]);
const selectedIds = computed(() => multipleSelection.value.map((item) => item.id));
const hasSelected = computed(() => multipleSelection.value.length > 0);
const isEdit = computed(() => form.value.id > 0);
const dialogTitle = computed(() => (isEdit.value ? "编辑资源分类" : "新增资源分类"));

const getTableData = async () => {
  const res = await http.get(
    `/resourceCategory/page?pageNum=${params.value.pageNum}&pageSize=${params.value.pageSize}&name=${params.value.name}`
  );
  if (res.code === 200) {
    tableData.value = res.data.records;
    total.value = res.data.total;
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
  visible.value = true;
};

const handleEdit = (row: any) => {
  form.value = { ...row };
  visible.value = true;
};

const delRow = async (id: number) => {
  const res = await http.get(`/resourceCategory/del?id=${id}`);
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
  const res = await http.post("/resourceCategory/batchDel", selectedIds.value);
  if (res.code === 200) {
    ElMessage.success("批量删除成功");
    multipleSelection.value = [];
    await getTableData();
  }
};

const submit = async () => {
  const url = isEdit.value ? `/resourceCategory/edit?id=${form.value.id}` : "/resourceCategory/add";
  const res = await http.post(url, form.value);
  if (res.code === 200) {
    ElMessage.success("保存成功");
    visible.value = false;
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
    <header class="flex">
      <div class="flex form-item">
        <p class="label-text">分类名称：</p>
        <el-input v-model="params.name" placeholder="请输入资源分类名称" clearable />
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
    </header>
    <main>
      <div class="flex op-box">
        <el-button v-if="hasPermission('resourceCategory', '新增')" :icon="Plus" type="primary" @click="handleAdd">新增</el-button>
        <el-button v-if="hasPermission('resourceCategory', '删除')" :icon="Delete" type="danger" :disabled="!hasSelected" @click="batchDel">批量删除</el-button>
      </div>
      <el-table :data="tableData" border @selection-change="multipleSelection = $event">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="资源分类" width="180" show-overflow-tooltip />
        <el-table-column prop="description" label="分类说明" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button v-if="hasPermission('resourceCategory', '编辑')" type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="您确定要删除吗?" @confirm="delRow(scope.row.id)">
              <template #reference>
                <el-button v-if="hasPermission('resourceCategory', '删除')" type="danger" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, prev, pager, next" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </main>
    <el-dialog v-model="visible" :title="dialogTitle" width="620">
      <el-form :model="form" label-width="110">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入资源分类名称" />
        </el-form-item>
        <el-form-item label="分类说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入分类说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submit">确认</el-button>
        <el-button @click="visible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.label-text { color: #606266; width: 120px; }
.form-item { margin-right: 20px; }
.op-box { margin: 10px 0; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
:deep(.el-table) { --el-table-header-bg-color: var(--el-fill-color-light); }
</style>
