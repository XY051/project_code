<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { ArrowLeft, Calendar, Search } from "@element-plus/icons-vue";
import http from "../../utils/http";
import useUserStore from "../../stores/userStore";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const labs = ref<any[]>([]);
const keyword = ref("");
const submitting = ref(false);
const form = ref({
  reservationType: "实训室",
  targetId: null as number | null,
  applicantOrg: "",
  contactPhone: "",
  purpose: "",
  useDate: "",
  startTime: "",
  endTime: "",
  participantCount: 1,
});

const currentUser = computed(() => {
  try {
    return typeof userStore.userInfo === "string" ? JSON.parse(userStore.userInfo) : userStore.userInfo;
  } catch {
    return {};
  }
});

const selectedLab = computed(() => labs.value.find((item) => item.id === form.value.targetId));

const filteredLabs = computed(() => {
  const word = keyword.value.trim();
  if (!word) return labs.value;
  return labs.value.filter((item) => [item.name, item.location, item.manager].some((field) => field?.includes(word)));
});

const getLabs = async () => {
  const res = await http.get("/lab/list");
  if (res.code === 200) {
    labs.value = res.data || [];
    const id = Number(route.query.labId);
    if (id) {
      form.value.targetId = id;
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
  if (!form.value.targetId) {
    ElMessage.warning("请选择预约实训室");
    return;
  }
  if (!form.value.useDate || !form.value.startTime || !form.value.endTime) {
    ElMessage.warning("请选择预约日期和时间");
    return;
  }
  if (!form.value.purpose) {
    ElMessage.warning("请填写预约用途");
    return;
  }
  submitting.value = true;
  try {
    const res = await http.post("/reservation/add", {
      ...form.value,
      targetName: selectedLab.value?.name,
      userId: currentUser.value.id,
      userName: currentUser.value.nickName || currentUser.value.username,
    });
    if (res.code === 200) {
      ElMessage.success("预约已提交，请等待审核");
      router.push("/front/myOpenShare");
    }
  } finally {
    submitting.value = false;
  }
};

onMounted(getLabs);
</script>

<template>
  <div class="reserve-page">
    <main class="reserve-shell">
      <el-button class="back-btn" :icon="ArrowLeft" @click="router.push('/front/openShare')">共享开放</el-button>

      <section class="page-head">
        <div>
          <p>Lab Reservation</p>
          <h1>实训室开放预约</h1>
          <span>先提供统一预约样式和数据入口，后续可由实训室模块继续接排期冲突与容量校验。</span>
        </div>
        <el-button type="primary" :icon="Calendar" @click="router.push('/front/myOpenShare')">预约记录</el-button>
      </section>

      <section class="reserve-layout">
        <aside class="lab-panel">
          <div class="panel-head">
            <strong>选择实训室</strong>
            <el-input v-model="keyword" placeholder="搜索实训室" clearable :prefix-icon="Search" />
          </div>
          <div class="lab-list">
            <button
              v-for="item in filteredLabs"
              :key="item.id"
              class="lab-item"
              :class="{ active: form.targetId === item.id }"
              @click="form.targetId = item.id"
            >
              <span>{{ item.name }}</span>
              <small>{{ item.location || "暂无地点" }} · 容量 {{ item.capacity || "-" }} · {{ item.openTime || "开放时间待定" }}</small>
            </button>
            <el-empty v-if="!filteredLabs.length" description="暂无实训室" />
          </div>
        </aside>

        <section class="form-panel">
          <div class="selected-box">
            <div>
              <span>当前实训室</span>
              <strong>{{ selectedLab?.name || "请先选择实训室" }}</strong>
            </div>
            <el-tag type="success">容量 {{ selectedLab?.capacity || "-" }}</el-tag>
          </div>
          <div v-if="selectedLab" class="lab-summary">
            <p>{{ selectedLab.description || "暂无实训室说明" }}</p>
            <span>{{ selectedLab.manager || "负责人待定" }} · {{ selectedLab.managerPhone || "联系电话待补充" }}</span>
          </div>
          <el-form :model="form" label-width="96">
            <el-form-item label="预约单位">
              <el-input v-model="form.applicantOrg" placeholder="请输入预约单位" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="预约日期" required>
              <el-date-picker v-model="form.useDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
            <el-row :gutter="14">
              <el-col :span="12">
                <el-form-item label="开始时间" required>
                  <el-time-picker v-model="form.startTime" value-format="HH:mm:ss" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="结束时间" required>
                  <el-time-picker v-model="form.endTime" value-format="HH:mm:ss" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="参与人数">
              <el-input-number v-model="form.participantCount" :min="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="预约用途" required>
              <el-input v-model="form.purpose" type="textarea" :rows="5" placeholder="请说明课程、项目或实训安排" />
            </el-form-item>
          </el-form>
          <div class="submit-row">
            <el-button type="primary" size="large" :icon="Calendar" :loading="submitting" @click="submit">提交预约</el-button>
            <el-button size="large" @click="router.push('/front/openShare')">返回入口</el-button>
          </div>
        </section>
      </section>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.reserve-page { min-height: 100vh; background: #f4f7fb; padding: 24px; }
.reserve-shell { max-width: 1180px; margin: 0 auto; }
.back-btn { margin-bottom: 16px; }
.page-head { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 24px; display: flex; align-items: center; justify-content: space-between; gap: 18px; }
.page-head p { margin: 0 0 8px; color: #64748b; font-size: 13px; }
.page-head h1 { margin: 0 0 10px; color: #172033; font-size: 30px; }
.page-head span { color: #526173; }
.reserve-layout { display: grid; grid-template-columns: 380px 1fr; gap: 20px; margin-top: 20px; }
.lab-panel, .form-panel { background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 18px; }
.panel-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 14px; }
.panel-head strong { color: #1f2937; }
.panel-head :deep(.el-input) { width: 180px; }
.lab-list { max-height: 590px; overflow: auto; display: flex; flex-direction: column; gap: 10px; }
.lab-item { text-align: left; border: 1px solid #e2e8f0; background: #f8fafc; border-radius: 8px; padding: 12px; cursor: pointer; transition: border-color .2s ease, background .2s ease; }
.lab-item.active { border-color: #2563eb; background: #eff6ff; }
.lab-item span { display: block; color: #1f2937; font-weight: 600; line-height: 1.45; }
.lab-item small { display: block; color: #64748b; margin-top: 6px; }
.selected-box { display: flex; align-items: center; justify-content: space-between; gap: 14px; background: #f8fafc; border: 1px solid #edf2f7; border-radius: 8px; padding: 14px 16px; margin-bottom: 14px; }
.selected-box span { display: block; color: #64748b; font-size: 13px; margin-bottom: 4px; }
.selected-box strong { color: #172033; font-size: 16px; }
.lab-summary { border-left: 3px solid #2563eb; background: #f8fafc; padding: 12px 14px; margin-bottom: 14px; color: #526173; }
.lab-summary p { margin: 0 0 6px; line-height: 1.7; }
.lab-summary span { color: #64748b; font-size: 13px; }
.submit-row { display: flex; gap: 12px; justify-content: flex-end; }
@media (max-width: 860px) {
  .reserve-page { padding: 12px; }
  .page-head { align-items: flex-start; flex-direction: column; }
  .reserve-layout { grid-template-columns: 1fr; }
  .panel-head { align-items: stretch; flex-direction: column; }
  .panel-head :deep(.el-input) { width: 100%; }
}
</style>
