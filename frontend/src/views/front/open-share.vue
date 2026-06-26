<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { Calendar, Connection, DocumentChecked, Link, Tickets } from "@element-plus/icons-vue";
import http from "../../utils/http";
import { getImageUrl } from "../../utils/system";

const router = useRouter();
const resources = ref<any[]>([]);
const labs = ref<any[]>([]);
const loading = ref(false);

const availableResourceCount = computed(() => resources.value.filter((item) => Number(item.availableCount || 0) > 0).length);
const totalCapacity = computed(() => labs.value.reduce((sum, item) => sum + Number(item.capacity || 0), 0));

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
      <section class="flow-section">
        <article class="flow-card">
          <el-icon><Tickets /></el-icon>
          <h2>资源开放申请</h2>
          <p>选择带实验入口的虚拟仿真资源，提交使用目的和时间；管理员审核通过后占用资源名额。</p>
          <el-button type="primary" :icon="Link" @click="goResourceApply()">去申请</el-button>
        </article>
        <article class="flow-card">
          <el-icon><Calendar /></el-icon>
          <h2>实训室预约</h2>
          <p>先提供统一用户入口和预约数据，后续实训室模块成员可以继续接入排期冲突、容量校验等规则。</p>
          <el-button type="primary" :icon="Calendar" @click="goLabReserve()">去预约</el-button>
        </article>
        <article class="flow-card">
          <el-icon><DocumentChecked /></el-icon>
          <h2>我的共享开放</h2>
          <p>查看申请审核、预约进度和资源使用状态；资源使用完成后可以在这里归还。</p>
          <el-button type="primary" :icon="DocumentChecked" @click="router.push('/front/myOpenShare')">查看记录</el-button>
        </article>
      </section>

      <section class="section-head">
        <div>
          <h2>可申请资源</h2>
          <p>资源数为 0 时不能直接进入实验，可以先提交申请等待协调。</p>
        </div>
        <el-button @click="router.push('/front/resources')">全部资源</el-button>
      </section>
      <section class="resource-grid">
        <article v-for="item in resources.slice(0, 6)" :key="item.id" class="resource-card">
          <div class="resource-cover">
            <el-image v-if="item.coverImage" :src="safeImage(item.coverImage)" fit="cover" />
            <div v-else class="cover-fallback">{{ item.openType || "虚拟仿真" }}</div>
          </div>
          <div>
            <el-tag :type="Number(item.availableCount || 0) > 0 ? 'success' : 'warning'">
              可用 {{ item.availableCount || 0 }}
            </el-tag>
            <h3>{{ item.name }}</h3>
            <p>{{ item.intro || "暂无简介" }}</p>
          </div>
          <footer>
            <span>{{ item.openType || "开放共享" }}</span>
            <el-button type="primary" text @click="goResourceApply(item.id)">申请</el-button>
          </footer>
        </article>
      </section>

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
.flow-section { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; }
.flow-card { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 20px; min-height: 230px; display: flex; flex-direction: column; align-items: flex-start; }
.flow-card > .el-icon { width: 42px; height: 42px; border-radius: 8px; background: #eff6ff; color: #2563eb; font-size: 22px; display: flex; align-items: center; justify-content: center; }
.flow-card h2 { margin: 16px 0 10px; color: #172033; font-size: 20px; }
.flow-card p { color: #526173; line-height: 1.7; flex: 1; }
.section-head { margin: 34px 0 16px; display: flex; align-items: flex-end; justify-content: space-between; gap: 18px; }
.section-head h2 { margin: 0 0 6px; color: #172033; font-size: 24px; }
.section-head p { color: #64748b; }
.resource-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; }
.resource-card { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; overflow: hidden; min-height: 300px; display: flex; flex-direction: column; justify-content: space-between; }
.resource-card > div:not(.resource-cover), .resource-card footer { padding-left: 18px; padding-right: 18px; }
.resource-cover { height: 142px; background: #dbe7ef; }
.resource-cover :deep(.el-image) { width: 100%; height: 100%; display: block; }
.cover-fallback { height: 100%; display: flex; align-items: center; justify-content: center; color: #436579; font-weight: 600; }
.resource-card h3 { margin: 14px 0 10px; color: #172033; font-size: 17px; line-height: 1.45; }
.resource-card p { color: #526173; line-height: 1.7; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.resource-card footer { margin-top: 18px; padding-bottom: 16px; display: flex; align-items: center; justify-content: space-between; color: #64748b; }
.lab-head { align-items: center; }
.lab-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; }
.lab-card { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 16px; display: grid; grid-template-columns: 44px 1fr auto; gap: 14px; align-items: center; }
.lab-icon { width: 44px; height: 44px; border-radius: 8px; background: #ecfdf5; color: #059669; display: flex; align-items: center; justify-content: center; font-size: 22px; }
.lab-card h3 { margin: 0 0 6px; color: #172033; font-size: 16px; }
.lab-card p { margin: 0 0 4px; color: #526173; }
.lab-card span { color: #64748b; font-size: 13px; }
@media (max-width: 900px) {
  .hero-inner { grid-template-columns: 1fr; }
  .flow-section, .resource-grid, .lab-grid { grid-template-columns: 1fr; }
  .section-head { align-items: flex-start; flex-direction: column; }
}
@media (max-width: 560px) {
  .hero-inner, .content { padding-left: 14px; padding-right: 14px; }
  .hero-copy h1 { font-size: 30px; }
  .hero-metrics { grid-template-columns: 1fr; }
  .lab-card { grid-template-columns: 44px 1fr; }
  .lab-card .el-button { grid-column: 2; justify-self: flex-start; }
}
</style>
