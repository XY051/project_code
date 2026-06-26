<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { ArrowLeft, Check, Search, Tickets } from "@element-plus/icons-vue";
import http from "../../utils/http";
import useUserStore from "../../stores/userStore";
import { getImageUrl } from "../../utils/system";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const resources = ref<any[]>([]);
const keyword = ref("");
const submitting = ref(false);
const form = ref({
  resourceId: null as number | null,
  applicantOrg: "",
  contactPhone: "",
  openType: "",
  purpose: "",
  useDate: "",
  participantCount: 1,
});

const currentUser = computed(() => {
  try {
    return typeof userStore.userInfo === "string" ? JSON.parse(userStore.userInfo) : userStore.userInfo;
  } catch {
    return {};
  }
});

const selectedResource = computed(() => resources.value.find((item) => item.id === form.value.resourceId));

const filteredResources = computed(() => {
  const word = keyword.value.trim();
  if (!word) return resources.value;
  return resources.value.filter((item) => [item.name, item.openType, item.resourceLevel].some((field) => field?.includes(word)));
});

const safeImage = (url?: string) => {
  if (!url) return "";
  return url.startsWith("http") ? url : getImageUrl(url);
};

watch(selectedResource, (resource) => {
  if (resource) {
    form.value.openType = resource.openType || "校内实训";
  }
});

const getResources = async () => {
  const res = await http.get("/trainingResource/list");
  if (res.code === 200) {
    resources.value = res.data || [];
    const id = Number(route.query.resourceId);
    if (id) {
      form.value.resourceId = id;
    }
  }
};

const requireLogin = () => {
  if (!currentUser.value?.id) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return false;
  }
  return true;
};

const submit = async () => {
  if (!requireLogin()) return;
  if (!form.value.resourceId) {
    ElMessage.warning("请选择申请资源");
    return;
  }
  if (!form.value.useDate || !form.value.purpose) {
    ElMessage.warning("请填写使用日期和使用目的");
    return;
  }
  submitting.value = true;
  try {
    const res = await http.post("/openApplication/add", {
      ...form.value,
      resourceName: selectedResource.value?.name,
      userId: currentUser.value.id,
      userName: currentUser.value.nickName || currentUser.value.username,
    });
    if (res.code === 200) {
      ElMessage.success("申请已提交，请等待审核");
      router.push("/front/myOpenShare");
    }
  } finally {
    submitting.value = false;
  }
};

onMounted(getResources);
</script>

<template>
  <div class="apply-page">
    <main class="apply-shell">
      <el-button class="back-btn" :icon="ArrowLeft" @click="router.push('/front/openShare')">共享开放</el-button>

      <section class="page-head">
        <div>
          <p>Resource Application</p>
          <h1>资源开放申请</h1>
          <span>选择虚拟仿真资源，提交使用用途和时间，审核通过后即可进入实验。</span>
        </div>
        <el-button type="primary" :icon="Tickets" @click="router.push('/front/myOpenShare')">我的申请</el-button>
      </section>

      <section class="apply-layout">
        <aside class="resource-panel">
          <div class="panel-head">
            <strong>选择资源</strong>
            <el-input v-model="keyword" placeholder="搜索资源" clearable :prefix-icon="Search" />
          </div>
          <div class="resource-list">
            <button
              v-for="item in filteredResources"
              :key="item.id"
              class="resource-item"
              :class="{ active: form.resourceId === item.id }"
              @click="form.resourceId = item.id"
            >
              <img v-if="item.coverImage" :src="safeImage(item.coverImage)" alt="" />
              <div class="resource-meta">
                <span>{{ item.name }}</span>
                <small>{{ item.openType || "开放共享" }} · 可用 {{ item.availableCount || 0 }}</small>
              </div>
            </button>
            <el-empty v-if="!filteredResources.length" description="暂无资源" />
          </div>
        </aside>

        <section class="form-panel">
          <div v-if="selectedResource?.coverImage" class="selected-cover">
            <el-image :src="safeImage(selectedResource.coverImage)" fit="cover" />
          </div>
          <div class="selected-box">
            <div>
              <span>当前资源</span>
              <strong>{{ selectedResource?.name || "请先选择资源" }}</strong>
            </div>
            <el-tag :type="Number(selectedResource?.availableCount || 0) > 0 ? 'success' : 'warning'">
              可用 {{ selectedResource?.availableCount || 0 }}
            </el-tag>
          </div>
          <el-alert
            v-if="selectedResource && Number(selectedResource.availableCount || 0) <= 0"
            title="该资源当前可用数量为 0，可先提交申请等待管理员协调。"
            type="warning"
            show-icon
            :closable="false"
          />
          <el-form :model="form" label-width="96">
            <el-form-item label="申请单位">
              <el-input v-model="form.applicantOrg" placeholder="请输入申请单位" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="开放类型">
              <el-input v-model="form.openType" placeholder="选择资源后自动带出" />
            </el-form-item>
            <el-row :gutter="14">
              <el-col :span="12">
                <el-form-item label="使用日期" required>
                  <el-date-picker v-model="form.useDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="参与人数">
                  <el-input-number v-model="form.participantCount" :min="1" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="使用目的" required>
              <el-input v-model="form.purpose" type="textarea" :rows="5" placeholder="请说明课程、项目或实验用途" />
            </el-form-item>
          </el-form>
          <div class="submit-row">
            <el-button type="primary" size="large" :icon="Check" :loading="submitting" @click="submit">提交申请</el-button>
            <el-button size="large" @click="router.push('/front/resources')">浏览资源</el-button>
          </div>
        </section>
      </section>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.apply-page { min-height: 100vh; background: #f4f7fb; padding: 24px; }
.apply-shell { max-width: 1180px; margin: 0 auto; }
.back-btn { margin-bottom: 16px; }
.page-head { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 24px; display: flex; align-items: center; justify-content: space-between; gap: 18px; }
.page-head p { margin: 0 0 8px; color: #64748b; font-size: 13px; }
.page-head h1 { margin: 0 0 10px; color: #172033; font-size: 30px; }
.page-head span { color: #526173; }
.apply-layout { display: grid; grid-template-columns: 380px 1fr; gap: 20px; margin-top: 20px; }
.resource-panel, .form-panel { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 18px; }
.panel-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 14px; }
.panel-head strong { color: #1f2937; }
.panel-head :deep(.el-input) { width: 180px; }
.resource-list { max-height: 590px; overflow: auto; display: flex; flex-direction: column; gap: 10px; }
.resource-item { text-align: left; border: 1px solid #e2e8f0; background: #f8fafc; border-radius: 8px; padding: 10px; cursor: pointer; transition: border-color .2s ease, background .2s ease; display: grid; grid-template-columns: 74px 1fr; gap: 10px; align-items: center; width: 100%; }
.resource-item.active { border-color: #2563eb; background: #eff6ff; }
.resource-item img { width: 74px; height: 52px; object-fit: cover; border-radius: 6px; background: #dbe7ef; }
.resource-meta { min-width: 0; }
.resource-item span { display: block; color: #1f2937; font-weight: 600; line-height: 1.45; }
.resource-item small { display: block; color: #64748b; margin-top: 6px; }
.selected-cover { height: 220px; border-radius: 8px; overflow: hidden; background: #dbe7ef; margin-bottom: 14px; }
.selected-cover :deep(.el-image) { width: 100%; height: 100%; display: block; }
.selected-box { display: flex; align-items: center; justify-content: space-between; gap: 14px; background: #f8fafc; border: 1px solid #edf2f7; border-radius: 8px; padding: 14px 16px; margin-bottom: 14px; }
.selected-box span { display: block; color: #64748b; font-size: 13px; margin-bottom: 4px; }
.selected-box strong { color: #172033; font-size: 16px; }
.form-panel :deep(.el-alert) { margin-bottom: 14px; }
.submit-row { display: flex; gap: 12px; justify-content: flex-end; }
@media (max-width: 860px) {
  .apply-page { padding: 12px; }
  .page-head { align-items: flex-start; flex-direction: column; }
  .apply-layout { grid-template-columns: 1fr; }
  .panel-head { align-items: stretch; flex-direction: column; }
  .panel-head :deep(.el-input) { width: 100%; }
}
</style>
