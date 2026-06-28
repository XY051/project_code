<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { Location, OfficeBuilding, Search, User } from "@element-plus/icons-vue";
import http from "../../utils/http";

const labs = ref<any[]>([]);
const keyword = ref("");
const loading = ref(false);

const filteredLabs = computed(() => {
  const word = keyword.value.trim();
  if (!word) return labs.value;
  return labs.value.filter((item) =>
    [item.name, item.location, item.manager, item.description].some((field) => field?.includes(word))
  );
});

const totalCapacity = computed(() => labs.value.reduce((sum, item) => sum + Number(item.capacity || 0), 0));

const statusText = (status: number) => {
  if (status === 0) return "开放";
  if (status === 1) return "使用中";
  return "维护中";
};

const statusType = (status: number) => {
  if (status === 0) return "success";
  if (status === 1) return "warning";
  return "info";
};

const getLabs = async () => {
  loading.value = true;
  try {
    const res = await http.get("/lab/list");
    if (res.code === 200) {
      labs.value = res.data || [];
    }
  } finally {
    loading.value = false;
  }
};

onMounted(getLabs);
</script>

<template>
  <div class="lab-page">
    <section class="hero-band">
      <div class="hero-inner">
        <div>
          <p class="eyebrow">Laboratory Resources</p>
          <h1>实验室资源</h1>
          <p class="hero-desc">展示平台实验室资源、位置、容量、开放时间和负责人信息。</p>
        </div>
        <div class="hero-stats">
          <div>
            <strong>{{ labs.length }}</strong>
            <span>实验室数量</span>
          </div>
          <div>
            <strong>{{ totalCapacity }}</strong>
            <span>总容量</span>
          </div>
        </div>
      </div>
    </section>

    <section class="search-section">
      <div class="search-inner">
        <el-input v-model="keyword" placeholder="搜索实验室名称、地点、负责人" clearable :prefix-icon="Search" />
      </div>
    </section>

    <main class="lab-content" v-loading="loading">
      <div v-if="filteredLabs.length" class="lab-grid">
        <article v-for="item in filteredLabs" :key="item.id" class="lab-card">
          <div class="lab-card-head">
            <div class="lab-icon">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="lab-title">
              <h2>{{ item.name }}</h2>
              <p><el-icon><Location /></el-icon>{{ item.location || "地点待补充" }}</p>
            </div>
            <el-tag :type="statusType(item.status)">{{ statusText(item.status) }}</el-tag>
          </div>

          <p class="lab-desc">{{ item.description || "暂无实验室说明" }}</p>

          <div class="lab-meta">
            <div>
              <span>容量</span>
              <strong>{{ item.capacity || "-" }} 人</strong>
            </div>
            <div>
              <span>面积</span>
              <strong>{{ item.area || "-" }} ㎡</strong>
            </div>
            <div>
              <span>开放时间</span>
              <strong>{{ item.openTime || "待补充" }}</strong>
            </div>
            <div>
              <span>负责人</span>
              <strong><el-icon><User /></el-icon>{{ item.manager || "待定" }}</strong>
            </div>
          </div>

          <footer class="lab-footer">
            <span>{{ item.managerPhone || "联系电话待补充" }}</span>
          </footer>
        </article>
      </div>
      <el-empty v-else description="暂无实验室资源" />
    </main>
  </div>
</template>

<style lang="scss" scoped>
.lab-page {
  min-height: 100vh;
  background: #f4f7fb;
}

.hero-band {
  background: #19384f;
  color: #fff;
}

.hero-inner {
  max-width: 1180px;
  min-height: 190px;
  margin: 0 auto;
  padding: 42px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28px;
}

.eyebrow {
  margin: 0 0 10px;
  font-size: 13px;
  opacity: .82;
}

h1 {
  margin: 0;
  font-size: 34px;
}

.hero-desc {
  margin: 14px 0 0;
  color: rgba(255, 255, 255, .88);
}

.hero-stats {
  display: flex;
  gap: 12px;
}

.hero-stats div {
  min-width: 130px;
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, .2);
  border-radius: 8px;
  background: rgba(255, 255, 255, .1);
}

.hero-stats strong {
  display: block;
  font-size: 32px;
  line-height: 1;
}

.hero-stats span {
  display: block;
  margin-top: 8px;
  font-size: 13px;
  opacity: .86;
}

.search-section {
  background: #fff;
  border-bottom: 1px solid #e8eef5;
}

.search-inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 18px 24px;
}

.search-inner :deep(.el-input) {
  width: 360px;
  max-width: 100%;
}

.lab-content {
  max-width: 1180px;
  min-height: 380px;
  margin: 0 auto;
  padding: 28px 24px 48px;
}

.lab-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(330px, 1fr));
  gap: 18px;
}

.lab-card {
  background: #fff;
  border: 1px solid #e6edf5;
  border-radius: 8px;
  padding: 18px;
}

.lab-card-head {
  display: grid;
  grid-template-columns: 48px 1fr auto;
  gap: 14px;
  align-items: center;
}

.lab-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: #ecfdf5;
  color: #059669;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.lab-title h2 {
  margin: 0 0 6px;
  color: #172033;
  font-size: 18px;
}

.lab-title p {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #64748b;
  font-size: 13px;
}

.lab-desc {
  margin: 16px 0;
  color: #526173;
  line-height: 1.7;
  min-height: 48px;
}

.lab-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.lab-meta div {
  background: #f8fafc;
  border: 1px solid #edf2f7;
  border-radius: 6px;
  padding: 10px;
}

.lab-meta span {
  display: block;
  color: #8a94a3;
  font-size: 12px;
  margin-bottom: 6px;
}

.lab-meta strong {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #1f2937;
  font-size: 14px;
}

.lab-footer {
  margin-top: 14px;
  color: #64748b;
  font-size: 13px;
}

@media (max-width: 680px) {
  .hero-inner {
    align-items: flex-start;
    flex-direction: column;
  }

  .hero-stats {
    width: 100%;
    flex-direction: column;
  }

  .lab-card-head {
    grid-template-columns: 48px 1fr;
  }

  .lab-card-head .el-tag {
    grid-column: 2;
    justify-self: flex-start;
  }
}
</style>
