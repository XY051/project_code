<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { Document, Headset, Link, Monitor, Refresh, Search, VideoCamera, View } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import http from "../../utils/http";
import { getImageUrl } from "../../utils/system";

const router = useRouter();

const initParams = {
  pageNum: 1,
  pageSize: 12,
  name: "",
  categoryId: null as number | null,
  resourceType: "虚拟仿真",
};

const params = ref({ ...initParams });
const resourceList = ref<any[]>([]);
const allResourceList = ref<any[]>([]);
const categoryList = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);

const resourceTypeOptions = ["虚拟仿真", "视频", "音频", "文档"];
const resourceCategoryNames = ["视频资源", "音频资源", "文档资源"];
const resourceTypeMeta: Record<string, any> = {
  虚拟仿真: { icon: Monitor, hint: "申请后进入", className: "simulation" },
  视频: { icon: VideoCamera, hint: "在线播放", className: "video" },
  音频: { icon: Headset, hint: "直接收听", className: "audio" },
  文档: { icon: Document, hint: "在线查看", className: "document" },
};

const hasData = computed(() => resourceList.value.length > 0);
const resourcesInCurrentType = computed(() =>
  allResourceList.value.filter((item) => item.resourceType === params.value.resourceType)
);
const professionalCategoryList = computed(() =>
  categoryList.value.filter((item) => !resourceCategoryNames.includes(item.name))
);

const needsApply = (item: any) => item.resourceType === "虚拟仿真";

const categoryFilters = computed(() => [
  { label: "全部", value: null, count: resourcesInCurrentType.value.length },
  ...professionalCategoryList.value.map((item) => ({
    label: item.name,
    value: item.id,
    count: resourcesInCurrentType.value.filter((resource) => resource.categoryId === item.id).length,
  })),
]);

const resourceTypeFilters = computed(() =>
  resourceTypeOptions.map((item) => ({
    label: item,
    value: item,
    count: allResourceList.value.filter((resource) => resource.resourceType === item).length,
    ...resourceTypeMeta[item],
  }))
);

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
  return query.toString();
};

const getCategoryList = async () => {
  const res = await http.get("/resourceCategory/list");
  if (res.code === 200) {
    categoryList.value = res.data || [];
  }
};

const getAllResourceList = async () => {
  const res = await http.get("/trainingResource/list");
  if (res.code === 200) {
    allResourceList.value = res.data || [];
  }
};

const getResourceList = async () => {
  loading.value = true;
  try {
    const res = await http.get(`/trainingResource/page?${buildQuery()}`);
    if (res.code === 200) {
      resourceList.value = (res.data.records || []).map((item: any) => ({
        ...item,
        coverImageUrl: safeImage(item.coverImage),
      }));
      total.value = res.data.total || 0;
    }
  } catch (error) {
    ElMessage.error("获取资源列表失败");
  } finally {
    loading.value = false;
  }
};

const setResourceType = (value: string) => {
  params.value.resourceType = value;
  params.value.categoryId = null;
  params.value.pageNum = 1;
  getResourceList();
};

const setCategory = (value: number | null) => {
  params.value.categoryId = value;
  params.value.pageNum = 1;
  getResourceList();
};

const handleSearch = async () => {
  params.value.pageNum = 1;
  await getResourceList();
};

const handleReset = () => {
  params.value = { ...initParams };
  getResourceList();
};

const goDetail = (id: number) => {
  router.push({ path: "/front/resourceDetail", query: { id } });
};

const handleSizeChange = (val: number) => {
  params.value.pageSize = val;
  params.value.pageNum = 1;
  getResourceList();
};

const handleCurrentChange = (val: number) => {
  params.value.pageNum = val;
  getResourceList();
};

onMounted(async () => {
  await Promise.all([getCategoryList(), getAllResourceList()]);
  await getResourceList();
});
</script>

<template>
  <div class="resource-page">
    <div class="top-strip"></div>

    <main class="browse-shell">
      <nav class="type-switch" aria-label="资源类型切换">
        <button
          v-for="item in resourceTypeFilters"
          :key="item.label"
          type="button"
          :class="[item.className, { active: params.resourceType === item.value }]"
          @click="setResourceType(item.value)"
        >
          <span class="type-icon"><el-icon><component :is="item.icon" /></el-icon></span>
          <span class="type-copy">
            <strong>{{ item.label }}</strong>
            <small>{{ item.hint }}</small>
          </span>
          <span class="type-count">{{ item.count }}</span>
        </button>
      </nav>

      <section class="filter-panel">
        <span class="filter-label">专业大类：</span>
        <div class="pill-list">
          <button
            v-for="item in categoryFilters"
            :key="String(item.value)"
            type="button"
            class="filter-pill"
            :class="{ active: params.categoryId === item.value }"
            @click="setCategory(item.value)"
          >
            <span>{{ item.label }}</span>
            <em>{{ item.count }}</em>
          </button>
        </div>
      </section>

      <section class="search-bar">
        <el-input
          v-model="params.name"
          placeholder="请输入关键词快速搜索"
          clearable
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" :loading="loading" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </section>

      <section class="result-head">
        <div>
          <h1>资源中心</h1>
          <p>共找到 {{ total }} 个资源，可通过上方类型与专业筛选快速切换。</p>
        </div>
      </section>

      <section class="content-section">
        <div v-if="loading" class="state-box">加载中...</div>
        <div v-else-if="hasData" class="resource-grid">
          <article v-for="item in resourceList" :key="item.id" class="resource-card" @click="goDetail(item.id)">
            <div class="cover">
              <el-image v-if="item.coverImageUrl" :src="item.coverImageUrl" fit="cover">
                <template #error>
                  <div class="cover-fallback">{{ item.categoryName || "虚拟仿真" }}</div>
                </template>
              </el-image>
              <div v-else class="cover-fallback">{{ item.categoryName || "虚拟资源" }}</div>
              <el-tag class="open-tag" type="success">{{ item.openType || "开放共享" }}</el-tag>
            </div>
            <div class="card-body">
              <div class="meta-line">
                <span>{{ item.categoryName || "未分类" }}</span>
                <span><el-icon><View /></el-icon>{{ item.viewCount || 0 }}</span>
              </div>
              <h2>{{ item.name }}</h2>
              <p>{{ item.intro || "暂无简介" }}</p>
              <div class="card-footer">
                <el-tag v-if="needsApply(item)" :type="item.availableCount > 0 ? 'primary' : 'info'">
                  需申请 · 可用 {{ item.availableCount || 0 }}
                </el-tag>
                <el-tag v-else type="success">可直接浏览</el-tag>
                <span class="detail-link">查看详情 <el-icon><Link /></el-icon></span>
              </div>
            </div>
          </article>
        </div>
        <el-empty v-else description="暂无匹配资源" />
      </section>

      <div v-if="hasData" class="pagination-section">
        <el-pagination
          v-model:current-page="params.pageNum"
          v-model:page-size="params.pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.resource-page {
  min-height: 100vh;
  background: #fff;
}

.top-strip {
  height: 34px;
  background: linear-gradient(100deg, #123075 0%, #1b70d8 45%, #47b0e8 62%, #5b2ab5 100%);
}

.browse-shell {
  max-width: 1180px;
  margin: 0 auto;
  padding: 24px 24px 48px;
}

.type-switch {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin: 0 0 20px;
}

.type-switch button {
  position: relative;
  min-height: 72px;
  padding: 12px 14px;
  border: 1px solid #e5edf6;
  border-radius: 8px;
  background: #fff;
  color: #293241;
  cursor: pointer;
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  text-align: left;
  transition: border-color .18s ease, box-shadow .18s ease, transform .18s ease;
}

.type-switch button:hover,
.type-switch button.active {
  border-color: #93c5fd;
  box-shadow: 0 10px 22px rgba(37, 99, 235, .10);
  transform: translateY(-1px);
}

.type-switch button.active::after {
  content: "";
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: -1px;
  height: 3px;
  border-radius: 99px 99px 0 0;
  background: #1478d4;
}

.type-icon {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background: #eff6ff;
  color: #1478d4;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.type-switch button.video .type-icon {
  background: #fef3c7;
  color: #b45309;
}

.type-switch button.audio .type-icon {
  background: #ecfdf5;
  color: #059669;
}

.type-switch button.document .type-icon {
  background: #f5f3ff;
  color: #7c3aed;
}

.type-copy {
  min-width: 0;
}

.type-copy strong {
  display: block;
  color: #172033;
  font-size: 17px;
  line-height: 1.2;
}

.type-copy small {
  display: block;
  margin-top: 5px;
  color: #7b8794;
  font-size: 12px;
  line-height: 1.2;
}

.type-count {
  min-width: 28px;
  height: 28px;
  padding: 0 8px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #475569;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
}

.type-switch button.active .type-count {
  background: #e0f2fe;
  color: #0369a1;
}

.filter-panel {
  display: flex;
  align-items: flex-start;
  gap: 18px;
  margin-bottom: 16px;
  padding: 16px 18px;
  border: 1px solid #e8edf4;
  border-radius: 8px;
  background: #f8fafc;
}

.filter-panel.compact {
  margin-bottom: 18px;
}

.filter-label {
  flex: 0 0 auto;
  min-width: 76px;
  padding-top: 7px;
  color: #8b94a3;
  font-size: 14px;
  font-weight: 700;
}

.pill-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 12px;
}

.filter-pill {
  min-height: 34px;
  padding: 0 12px 0 14px;
  border: 1px solid transparent;
  border-radius: 999px;
  background: #fff;
  color: #505b68;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  transition: color .18s ease, border-color .18s ease, box-shadow .18s ease;
}

.filter-pill em {
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #64748b;
  font-style: normal;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.filter-pill:hover,
.filter-pill.active {
  color: #1478d4;
  border-color: #75b4f6;
  box-shadow: 0 4px 12px rgba(20, 120, 212, .10);
}

.filter-pill.active em {
  background: #e0f2fe;
  color: #0369a1;
}

.search-bar {
  display: grid;
  grid-template-columns: 1fr 110px 96px;
  align-items: stretch;
  margin-bottom: 24px;
  border: 1px solid #e4e9f0;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.search-bar :deep(.el-input__wrapper) {
  min-height: 52px;
  border-radius: 0;
  box-shadow: none;
}

.search-bar :deep(.el-input__inner) {
  font-size: 15px;
}

.search-bar .el-button {
  height: 52px;
  border-radius: 0;
  font-size: 15px;
  font-weight: 700;
}

.result-head {
  margin-bottom: 18px;
}

.result-head h1 {
  margin: 0 0 6px;
  color: #172033;
  font-size: 24px;
  line-height: 1.3;
}

.result-head p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.content-section {
  min-height: 340px;
}

.state-box {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.resource-card {
  background: #fff;
  border: 1px solid #e6edf5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease;
}

.resource-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 24px rgba(21, 47, 74, .12);
  border-color: #8fb5d1;
}

.cover {
  position: relative;
  height: 170px;
  background: #dbe7ef;
}

.cover :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.cover-fallback {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f8fafc;
  font-weight: 700;
  background: linear-gradient(135deg, #17324d 0%, #2563eb 55%, #2f7d68 100%);
}

.open-tag {
  position: absolute;
  left: 12px;
  top: 12px;
}

.card-body {
  padding: 16px;
}

.meta-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #64748b;
  font-size: 13px;
  margin-bottom: 10px;
}

.meta-line span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-body h2 {
  margin: 0 0 10px;
  min-height: 48px;
  color: #1f2937;
  font-size: 17px;
  line-height: 1.4;
}

.card-body p {
  margin: 0;
  min-height: 44px;
  color: #64748b;
  line-height: 1.6;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  margin-top: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-link {
  color: #2563eb;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding-top: 28px;
}

@media (max-width: 760px) {
  .browse-shell {
    padding: 18px 14px 40px;
  }

  .type-switch {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .filter-panel {
    flex-direction: column;
    gap: 12px;
    padding: 18px;
  }

  .filter-label {
    min-width: 0;
    padding-top: 0;
  }

  .search-bar {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 520px) {
  .type-switch {
    grid-template-columns: 1fr;
  }
}
</style>
