<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { Search, Refresh, Link, View } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import http from "../../utils/http";
import { getImageUrl } from "../../utils/system";

const router = useRouter();

const initParams = {
  pageNum: 1,
  pageSize: 12,
  name: "",
  categoryId: null as number | null,
  openType: "",
};

const params = ref({ ...initParams });
const resourceList = ref<any[]>([]);
const categoryList = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const openTypeOptions = ["校内实训", "跨专业共享", "跨院校共享", "社会开放"];

const hasData = computed(() => resourceList.value.length > 0);

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
  if (params.value.openType) query.append("openType", params.value.openType);
  return query.toString();
};

const getCategoryList = async () => {
  const res = await http.get("/resourceCategory/list");
  if (res.code === 200) {
    categoryList.value = res.data || [];
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
  await getCategoryList();
  await getResourceList();
});
</script>

<template>
  <div class="resource-page">
    <section class="hero-band">
      <div class="hero-inner">
        <div>
          <p class="eyebrow">Virtual Simulation Resource Center</p>
          <h1>虚拟仿真资源中心</h1>
          <p class="hero-desc">面向课程实训、跨专业共享和社会开放的在线实验资源。</p>
        </div>
        <div class="hero-stats">
          <div><strong>{{ total }}</strong><span>资源总数</span></div>
          <div><strong>{{ categoryList.length }}</strong><span>资源分类</span></div>
        </div>
      </div>
    </section>

    <section class="search-section">
      <div class="search-form">
        <el-input v-model="params.name" placeholder="搜索资源名称、简介或说明" clearable class="search-input" />
        <el-select v-model="params.categoryId" placeholder="资源分类" clearable class="search-select">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="params.openType" placeholder="开放类型" clearable class="search-select">
          <el-option v-for="item in openTypeOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-button type="primary" :icon="Search" :loading="loading" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </section>

    <main class="content-section">
      <div v-if="loading" class="state-box">加载中...</div>
      <div v-else-if="hasData" class="resource-grid">
        <article v-for="item in resourceList" :key="item.id" class="resource-card" @click="goDetail(item.id)">
          <div class="cover">
            <el-image v-if="item.coverImageUrl" :src="item.coverImageUrl" fit="cover">
              <template #error>
                <div class="cover-fallback">{{ item.categoryName || "虚拟仿真" }}</div>
              </template>
            </el-image>
            <div v-else class="cover-fallback">{{ item.categoryName || "虚拟仿真" }}</div>
            <el-tag class="open-tag" type="success">{{ item.openType || "开放共享" }}</el-tag>
          </div>
          <div class="card-body">
            <div class="meta-line">
              <span>{{ item.categoryName || "未分类" }}</span>
              <span><el-icon><View /></el-icon>{{ item.viewCount || 0 }}</span>
            </div>
            <h3>{{ item.name }}</h3>
            <p>{{ item.intro || "暂无简介" }}</p>
            <div class="card-footer">
              <el-tag :type="item.availableCount > 0 ? 'primary' : 'info'">可用 {{ item.availableCount || 0 }}</el-tag>
              <span class="detail-link">查看详情 <el-icon><Link /></el-icon></span>
            </div>
          </div>
        </article>
      </div>
      <el-empty v-else description="暂无匹配的虚拟仿真资源" />
    </main>

    <div v-if="hasData" class="pagination-section">
      <el-pagination v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[12, 24, 36, 48]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.resource-page { min-height: 100vh; background: #f4f7fb; }
.hero-band { background: linear-gradient(135deg, #17324d 0%, #285c7a 55%, #2f7d68 100%); color: #fff; }
.hero-inner { max-width: 1200px; margin: 0 auto; min-height: 220px; padding: 44px 24px; display: flex; align-items: center; justify-content: space-between; gap: 32px; }
.eyebrow { margin: 0 0 10px; font-size: 13px; letter-spacing: 0; opacity: .82; }
h1 { margin: 0; font-size: 34px; line-height: 1.25; }
.hero-desc { margin: 14px 0 0; font-size: 15px; opacity: .9; }
.hero-stats { display: flex; gap: 14px; }
.hero-stats div { min-width: 120px; padding: 18px; border: 1px solid rgba(255,255,255,.22); border-radius: 8px; background: rgba(255,255,255,.1); }
.hero-stats strong { display: block; font-size: 30px; line-height: 1; }
.hero-stats span { display: block; margin-top: 8px; font-size: 13px; opacity: .86; }
.search-section { background: #fff; border-bottom: 1px solid #e8eef5; }
.search-form { max-width: 1200px; margin: 0 auto; padding: 18px 24px; display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.search-input { width: 320px; }
.search-select { width: 170px; }
.content-section { max-width: 1200px; margin: 0 auto; padding: 28px 24px; }
.state-box { height: 320px; display: flex; align-items: center; justify-content: center; color: #64748b; }
.resource-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
.resource-card { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; overflow: hidden; cursor: pointer; transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease; }
.resource-card:hover { transform: translateY(-3px); box-shadow: 0 12px 24px rgba(21, 47, 74, .12); border-color: #8fb5d1; }
.cover { position: relative; height: 170px; background: #dbe7ef; }
.cover :deep(.el-image) { width: 100%; height: 100%; }
.cover-fallback { height: 100%; display: flex; align-items: center; justify-content: center; color: #f8fafc; font-weight: 600; background: linear-gradient(135deg, #17324d 0%, #2563eb 55%, #2f7d68 100%); }
.open-tag { position: absolute; left: 12px; top: 12px; }
.card-body { padding: 16px; }
.meta-line { display: flex; justify-content: space-between; align-items: center; color: #64748b; font-size: 13px; margin-bottom: 10px; }
.meta-line span { display: flex; align-items: center; gap: 4px; }
h3 { margin: 0 0 10px; color: #1f2937; font-size: 17px; line-height: 1.4; min-height: 48px; }
.card-body p { margin: 0; color: #64748b; line-height: 1.6; font-size: 14px; min-height: 44px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { margin-top: 16px; display: flex; align-items: center; justify-content: space-between; }
.detail-link { color: #2563eb; font-size: 13px; display: flex; align-items: center; gap: 4px; }
.pagination-section { display: flex; justify-content: center; padding: 8px 24px 46px; }
@media (max-width: 760px) {
  .hero-inner { flex-direction: column; align-items: flex-start; }
  .hero-stats { width: 100%; }
  .hero-stats div { flex: 1; }
  .search-input, .search-select { width: 100%; }
}
</style>
