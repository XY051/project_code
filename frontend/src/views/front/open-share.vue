<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { Calendar, Connection, Document, DocumentChecked, Headset, Monitor, Tickets, VideoCamera } from "@element-plus/icons-vue";
import http from "../../utils/http";
import { getImageUrl } from "../../utils/system";

const router = useRouter();
const resources = ref<any[]>([]);
const labs = ref<any[]>([]);
const loading = ref(false);
const activeShareTab = ref<"resource" | "lab">("resource");
const activeResourceType = ref("虚拟仿真");
const showAllResources = ref(false);

const availableResourceCount = computed(() => resources.value.filter((item) => Number(item.availableCount || 0) > 0).length);
const totalCapacity = computed(() => labs.value.reduce((sum, item) => sum + Number(item.capacity || 0), 0));
const resourceTypeOptions = ["虚拟仿真", "视频", "音频", "文档"];
const resourceTypeMeta: Record<string, any> = {
  虚拟仿真: { icon: Monitor },
  视频: { icon: VideoCamera },
  音频: { icon: Headset },
  文档: { icon: Document },
};

const shareTabs = [
  { label: "实训资源", value: "resource" },
  { label: "实训室", value: "lab" },
] as const;

const resourceTypeFilters = computed(() =>
  resourceTypeOptions.map((item) => ({
    label: item,
    value: item,
    count: resources.value.filter((resource) => resource.resourceType === item).length,
    ...resourceTypeMeta[item],
  }))
);

const filteredResources = computed(() =>
  resources.value.filter((item) => item.resourceType === activeResourceType.value)
);
const visibleResources = computed(() =>
  showAllResources.value ? filteredResources.value : filteredResources.value.slice(0, 6)
);
const hasMoreResources = computed(() => filteredResources.value.length > 6);

const needsApply = (item: any) => item.resourceType === "虚拟仿真";

const goResourceDetail = (resourceId: number) => {
  router.push({ path: "/front/resourceDetail", query: { id: resourceId } });
};

const safeImage = (url?: string) => {
  if (!url) return "";
  return url.startsWith("http") ? url : getImageUrl(url);
};

const getResources = async () => {
  const res = await http.get("/trainingResource/list");
  if (res.code === 200) {
    resources.value = res.data || [];
  }
};

const getLabs = async () => {
  const res = await http.get("/lab/list");
  if (res.code === 200) {
    labs.value = (res.data || []).slice(0, 6);
  }
};

const goResourceApply = (resourceId?: number) => {
  router.push({ path: "/front/resourceApply", query: resourceId ? { resourceId } : {} });
};

const goLabReserve = (labId?: number) => {
  router.push({ path: "/front/labReserve", query: labId ? { labId } : {} });
};

const setResourceType = (value: string) => {
  activeResourceType.value = value;
  showAllResources.value = false;
};

onMounted(async () => {
  loading.value = true;
  try {
    await Promise.all([getResources(), getLabs()]);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="open-share-page">
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-copy">
          <p>Shared Open Services</p>
          <h1>共享开放服务</h1>
          <span>面向学生、教师和合作单位，提供虚拟仿真资源申请、实训室开放预约和个人进度查询。</span>
          <div class="hero-actions">
            <el-button type="primary" size="large" :icon="Tickets" @click="goResourceApply()">申请资源</el-button>
            <el-button size="large" :icon="Calendar" @click="goLabReserve()">预约实训室</el-button>
            <el-button size="large" :icon="DocumentChecked" @click="router.push('/front/myOpenShare')">我的记录</el-button>
          </div>
        </div>
        <div class="hero-metrics">
          <div><strong>{{ resources.length }}</strong><span>开放资源</span></div>
          <div><strong>{{ availableResourceCount }}</strong><span>当前可申请</span></div>
          <div><strong>{{ labs.length }}</strong><span>实训室入口</span></div>
          <div><strong>{{ totalCapacity }}</strong><span>参考容量</span></div>
        </div>
      </div>
    </section>

    <main class="content" v-loading="loading">
      <nav class="share-switch" aria-label="共享开放类型切换">
        <button
          v-for="item in shareTabs"
          :key="item.value"
          type="button"
          :class="{ active: activeShareTab === item.value }"
          @click="activeShareTab = item.value"
        >
          {{ item.label }}
        </button>
      </nav>

      <template v-if="activeShareTab === 'resource'">
        <section class="section-head resource-head">
          <div>
            <h2>{{ activeResourceType }}资源</h2>
            <p v-if="activeResourceType === '虚拟仿真'">虚拟仿真资源需要先申请，通过后才能进入使用。</p>
            <p v-else>视频、音频和文档资源可直接浏览查看，不需要提交资源使用申请。</p>
          </div>
          <nav class="resource-type-switch" aria-label="共享开放资源类型切换">
            <button
              v-for="item in resourceTypeFilters"
              :key="item.value"
              type="button"
              :class="{ active: activeResourceType === item.value }"
              @click="setResourceType(item.value)"
            >
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
              <em>{{ item.count }}</em>
            </button>
          </nav>
        </section>

        <section class="resource-grid">
          <article v-for="item in visibleResources" :key="item.id" class="resource-card">
            <div class="resource-cover">
              <el-image v-if="item.coverImage" :src="safeImage(item.coverImage)" fit="cover" />
              <div v-else class="cover-fallback">{{ item.openType || "虚拟仿真" }}</div>
            </div>
            <div>
              <el-tag v-if="needsApply(item)" :type="Number(item.availableCount || 0) > 0 ? 'success' : 'warning'">
                可用 {{ item.availableCount || 0 }}
              </el-tag>
              <el-tag v-else type="success">可直接浏览</el-tag>
              <h3>{{ item.name }}</h3>
              <p>{{ item.intro || "暂无简介" }}</p>
            </div>
            <footer>
              <span>{{ item.openType || "开放共享" }}</span>
              <el-button v-if="needsApply(item)" type="primary" text @click="goResourceApply(item.id)">申请</el-button>
              <el-button v-else type="primary" text @click="goResourceDetail(item.id)">查看</el-button>
            </footer>
          </article>
        </section>
        <div v-if="hasMoreResources" class="more-row">
          <el-button @click="showAllResources = !showAllResources">
            {{ showAllResources ? "收起资源" : `查看全部资源（${filteredResources.length}）` }}
          </el-button>
        </div>
        <el-empty v-if="!filteredResources.length" description="暂无该类型资源" />
      </template>

      <template v-else>
        <section class="section-head lab-head">
          <div>
            <h2>实训室开放入口</h2>
            <p>当前先做样式和预约入口，数据来自现有实训室模块。</p>
          </div>
        </section>
        <section class="lab-grid">
          <article v-for="item in labs" :key="item.id" class="lab-card">
            <div class="lab-icon"><el-icon><Connection /></el-icon></div>
            <div>
              <h3>{{ item.name }}</h3>
              <p>{{ item.location || "地点待补充" }} · 容量 {{ item.capacity || "-" }}</p>
              <span>{{ item.openTime || "开放时间待补充" }}</span>
            </div>
            <el-button type="primary" text @click="goLabReserve(item.id)">预约</el-button>
          </article>
        </section>
      </template>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.open-share-page { min-height: 100vh; background: #f4f7fb; }
.hero { background: #19384f; color: #fff; }
.hero-inner { max-width: 1180px; margin: 0 auto; padding: 48px 24px; display: grid; grid-template-columns: 1fr 380px; gap: 36px; align-items: center; }
.hero-copy p { margin: 0 0 10px; opacity: .82; font-size: 13px; }
.hero-copy h1 { margin: 0 0 14px; font-size: 36px; line-height: 1.25; }
.hero-copy span { display: block; max-width: 640px; color: rgba(255,255,255,.88); line-height: 1.8; }
.hero-actions { margin-top: 24px; display: flex; flex-wrap: wrap; gap: 12px; }
.hero-metrics { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.hero-metrics div { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.2); border-radius: 8px; padding: 18px; }
.hero-metrics strong { display: block; font-size: 30px; line-height: 1; }
.hero-metrics span { display: block; margin-top: 8px; font-size: 13px; opacity: .85; }
.content { max-width: 1180px; margin: 0 auto; padding: 26px 24px 48px; }
.share-switch { display: flex; justify-content: center; gap: 78px; margin: 0 0 24px; border-bottom: 1px solid #e8edf4; }
.share-switch button { position: relative; height: 56px; border: 0; background: transparent; color: #303642; font-size: 20px; font-weight: 700; cursor: pointer; }
.share-switch button.active { color: #1478d4; }
.share-switch button.active::after { content: ""; position: absolute; left: 50%; bottom: -1px; width: 68px; height: 4px; border-radius: 99px; background: #1478d4; transform: translateX(-50%); }
.section-head { margin: 0 0 16px; display: flex; align-items: flex-end; justify-content: space-between; gap: 18px; }
.section-head h2 { margin: 0 0 6px; color: #172033; font-size: 24px; }
.section-head p { margin: 0; color: #64748b; }
.resource-head { align-items: flex-start; }
.resource-type-switch { display: flex; flex-wrap: wrap; justify-content: flex-end; gap: 8px; max-width: 620px; }
.resource-type-switch button { height: 38px; padding: 0 10px; border: 1px solid #dbe5f0; border-radius: 999px; background: #fff; color: #475569; display: inline-flex; align-items: center; gap: 6px; font-size: 14px; font-weight: 700; cursor: pointer; transition: color .18s ease, border-color .18s ease, background .18s ease; }
.resource-type-switch button.active { color: #1478d4; border-color: #93c5fd; background: #eff6ff; }
.resource-type-switch em { min-width: 20px; height: 20px; padding: 0 6px; border-radius: 999px; background: #f1f5f9; color: #64748b; font-style: normal; display: inline-flex; align-items: center; justify-content: center; font-size: 12px; }
.resource-type-switch button.active em { background: #dbeafe; color: #1d4ed8; }
.resource-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; }
.resource-card { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; overflow: hidden; min-height: 300px; display: flex; flex-direction: column; justify-content: space-between; }
.resource-card > div:not(.resource-cover), .resource-card footer { padding-left: 18px; padding-right: 18px; }
.resource-cover { height: 142px; background: #dbe7ef; }
.resource-cover :deep(.el-image) { width: 100%; height: 100%; display: block; }
.cover-fallback { height: 100%; display: flex; align-items: center; justify-content: center; color: #436579; font-weight: 600; }
.resource-card h3 { margin: 14px 0 10px; color: #172033; font-size: 17px; line-height: 1.45; }
.resource-card p { color: #526173; line-height: 1.7; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.resource-card footer { margin-top: 18px; padding-bottom: 16px; display: flex; align-items: center; justify-content: space-between; color: #64748b; }
.more-row { display: flex; justify-content: center; margin-top: 22px; }
.lab-head { align-items: center; }
.lab-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; }
.lab-card { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 16px; display: grid; grid-template-columns: 44px 1fr auto; gap: 14px; align-items: center; }
.lab-icon { width: 44px; height: 44px; border-radius: 8px; background: #ecfdf5; color: #059669; display: flex; align-items: center; justify-content: center; font-size: 22px; }
.lab-card h3 { margin: 0 0 6px; color: #172033; font-size: 16px; }
.lab-card p { margin: 0 0 4px; color: #526173; }
.lab-card span { color: #64748b; font-size: 13px; }
@media (max-width: 900px) {
  .hero-inner { grid-template-columns: 1fr; }
  .resource-grid, .lab-grid { grid-template-columns: 1fr; }
  .section-head { align-items: flex-start; flex-direction: column; }
  .resource-type-switch { justify-content: flex-start; max-width: 100%; }
  .share-switch { gap: 44px; }
}
@media (max-width: 560px) {
  .hero-inner, .content { padding-left: 14px; padding-right: 14px; }
  .hero-copy h1 { font-size: 30px; }
  .hero-metrics { grid-template-columns: 1fr; }
  .lab-card { grid-template-columns: 44px 1fr; }
  .lab-card .el-button { grid-column: 2; justify-self: flex-start; }
}
</style>
