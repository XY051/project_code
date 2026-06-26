<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Star, StarFilled, Link, Tickets, Back } from "@element-plus/icons-vue";
import http from "../../utils/http";
import { getImageUrl } from "../../utils/system";
import useUserStore from "../../stores/userStore";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const detail = ref<any>({});
const isCollected = ref(false);
const collectLoading = ref(false);
const activeApplication = ref<any>(null);
const comments = ref<any[]>([]);
const commentLoading = ref(false);
const newComment = ref("");
const commentSubmitting = ref(false);

const currentUser = computed(() => {
  try {
    return typeof userStore.userInfo === "string" ? JSON.parse(userStore.userInfo) : userStore.userInfo;
  } catch {
    return {};
  }
});

const hasUseAccess = computed(() => !!activeApplication.value && activeApplication.value.useStatus === "使用中");
const canEnterLab = computed(() => hasUseAccess.value && detail.value.accessUrl);

const safeImage = (url?: string) => {
  if (!url) return "";
  return url.startsWith("http") ? url : getImageUrl(url);
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("zh-CN");
};

const getDetail = async () => {
  const id = route.query.id;
  if (!id) {
    ElMessage.error("资源参数错误");
    return;
  }
  loading.value = true;
  try {
    const res = await http.get(`/trainingResource/getById?id=${id}`);
    if (res.code === 200) {
      detail.value = res.data || {};
      await checkCollectStatus();
      await checkUseAccess();
      await getComments();
    }
  } finally {
    loading.value = false;
  }
};

const checkUseAccess = async () => {
  activeApplication.value = null;
  if (!currentUser.value?.id || !route.query.id) return;
  const query = new URLSearchParams({
    pageNum: "1",
    pageSize: "20",
    userId: String(currentUser.value.id),
    resourceId: String(route.query.id),
    auditStatus: "已通过",
  });
  const res = await http.get(`/openApplication/page?${query.toString()}`);
  if (res.code === 200) {
    activeApplication.value = (res.data.records || []).find((item: any) => item.useStatus === "使用中") || null;
  }
};

const checkCollectStatus = async () => {
  if (!currentUser.value?.id || !route.query.id) return;
  const res = await http.get(`/collect/isCollected?userId=${currentUser.value.id}&resourceId=${route.query.id}`);
  if (res.code === 200) {
    isCollected.value = !!res.data;
  }
};

const toggleCollect = async () => {
  if (!currentUser.value?.id) {
    ElMessage.warning("请先登录");
    return;
  }
  collectLoading.value = true;
  try {
    if (isCollected.value) {
      const collectRes = await http.get(`/collect/pageByUserId?pageNum=1&pageSize=1000&userId=${currentUser.value.id}`);
      if (collectRes.code === 200) {
        const record = (collectRes.data.records || []).find((item: any) => item.resourceId === detail.value.id || item.heritageId === detail.value.id);
        if (record) {
          await http.get(`/collect/del?id=${record.id}`);
        }
      }
      isCollected.value = false;
      ElMessage.success("已取消收藏");
    } else {
      const res = await http.post("/collect/add", {
        resourceId: detail.value.id,
        userId: currentUser.value.id,
      });
      if (res.code === 200) {
        isCollected.value = true;
        ElMessage.success("收藏成功");
      }
    }
  } finally {
    collectLoading.value = false;
  }
};

const openApply = () => {
  if (!currentUser.value?.id) {
    ElMessage.warning("请先登录");
    return;
  }
  router.push({ path: "/front/resourceApply", query: { resourceId: detail.value.id } });
};

const getComments = async () => {
  if (!detail.value?.id) return;
  commentLoading.value = true;
  try {
    const res = await http.get(`/comment/selectByHeritageId/${detail.value.id}`);
    if (res.code === 200) {
      comments.value = res.data || [];
    }
  } finally {
    commentLoading.value = false;
  }
};

const submitComment = async () => {
  if (!currentUser.value?.id) {
    ElMessage.warning("请先登录");
    return;
  }
  if (!newComment.value.trim()) {
    ElMessage.warning("请输入评论内容");
    return;
  }
  commentSubmitting.value = true;
  try {
    const res = await http.post("/comment/add", {
      resourceId: detail.value.id,
      userId: currentUser.value.id,
      content: newComment.value.trim(),
    });
    if (res.code === 200) {
      ElMessage.success("评论成功");
      newComment.value = "";
      await getComments();
    }
  } finally {
    commentSubmitting.value = false;
  }
};

const enterExperiment = () => {
  if (!detail.value.accessUrl) {
    ElMessage.warning("该资源暂未配置实验入口");
    return;
  }
  if (hasUseAccess.value) {
    window.open(detail.value.accessUrl, "_blank");
    return;
  }
  if (Number(detail.value.availableCount || 0) <= 0) {
    ElMessage.warning("当前可用资源数为 0，请先申请开放资源");
    openApply();
    return;
  }
  ElMessage.warning("请先提交资源申请，审核通过后再进入实验");
  openApply();
};

onMounted(getDetail);
</script>

<template>
  <div class="detail-page">
    <div class="detail-shell" v-loading="loading">
      <el-button class="back-btn" :icon="Back" @click="router.back()">返回</el-button>

      <template v-if="!loading && detail.id">
        <section class="top-section">
          <div class="cover">
            <el-image v-if="detail.coverImage" :src="safeImage(detail.coverImage)" fit="cover" :preview-src-list="[safeImage(detail.coverImage)]" :preview-teleported="true" />
            <div v-else class="cover-fallback">{{ detail.categoryName || detail.resourceType || "虚拟仿真" }}</div>
          </div>
          <div class="info">
            <div class="tag-row">
              <el-tag type="primary">{{ detail.categoryName || "未分类" }}</el-tag>
              <el-tag type="success">{{ detail.openType || "开放共享" }}</el-tag>
              <el-tag :type="Number(detail.availableCount || 0) > 0 ? 'success' : 'info'">可用资源 {{ detail.availableCount || 0 }}</el-tag>
            </div>
            <h1>{{ detail.name }}</h1>
            <p class="intro">{{ detail.intro || "暂无资源简介" }}</p>
            <div class="meta-grid">
              <div><span>资源类型</span><strong>{{ detail.resourceType || "虚拟仿真" }}</strong></div>
              <div><span>资源等级</span><strong>{{ detail.resourceLevel || "在线资源" }}</strong></div>
              <div><span>浏览量</span><strong>{{ detail.viewCount || 0 }}</strong></div>
              <div><span>发布时间</span><strong>{{ formatDate(detail.createTime) }}</strong></div>
            </div>
            <div class="actions">
              <el-button type="primary" :icon="Link" size="large" :disabled="!detail.accessUrl" @click="enterExperiment">
                {{ canEnterLab ? "进入实验" : "申请后实验" }}
              </el-button>
              <el-button type="success" :icon="Tickets" size="large" @click="openApply">申请资源</el-button>
              <el-button :type="isCollected ? 'warning' : 'default'" :icon="isCollected ? StarFilled : Star" :loading="collectLoading" size="large" @click="toggleCollect">
                {{ isCollected ? "已收藏" : "收藏" }}
              </el-button>
            </div>
            <p v-if="!hasUseAccess" class="hint">需要先提交资源申请，审核通过并占用资源后才能进入实验；资源数为 0 时请等待管理员协调。</p>
          </div>
        </section>

        <section class="body-section">
          <el-tabs>
            <el-tab-pane label="资源说明">
              <div v-if="detail.description" class="rich-text" v-html="detail.description"></div>
              <el-empty v-else description="暂无资源说明" />
            </el-tab-pane>
            <el-tab-pane label="开放须知">
              <div class="notice-list">
                <p>资源申请提交后由管理员审核，审核通过会占用 1 个可用资源名额。</p>
                <p>使用结束后由管理员确认归还，归还后系统恢复可用数量。</p>
                <p>实训室开放预约已预留统一预约入口，后续可由实训室模块成员继续扩展。</p>
              </div>
            </el-tab-pane>
            <el-tab-pane label="资源评论">
              <div class="comment-box">
                <div class="comment-editor">
                  <el-input v-model="newComment" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="请输入资源使用体验或建议" />
                  <div class="comment-actions">
                    <el-button type="primary" :loading="commentSubmitting" @click="submitComment">发表评论</el-button>
                  </div>
                </div>
                <div class="comment-list" v-loading="commentLoading">
                  <div v-if="comments.length">
                    <div v-for="item in comments" :key="item.id" class="comment-item">
                      <el-avatar :src="item.avatar ? safeImage(item.avatar) : ''">{{ item.userName?.slice(0, 1) || "用" }}</el-avatar>
                      <div>
                        <div class="comment-head">
                          <strong>{{ item.userName || "匿名用户" }}</strong>
                          <span>{{ item.createTime }}</span>
                        </div>
                        <p>{{ item.content }}</p>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无评论" />
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </section>
      </template>

      <el-empty v-else-if="!loading" description="暂无资源数据" />
    </div>

  </div>
</template>

<style lang="scss" scoped>
.detail-page { min-height: 100vh; background: #f4f7fb; padding: 24px; }
.detail-shell { max-width: 1180px; margin: 0 auto; }
.back-btn { margin-bottom: 16px; }
.top-section { display: grid; grid-template-columns: 420px 1fr; gap: 26px; background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 22px; }
.cover { height: 320px; background: #dbe7ef; border-radius: 8px; overflow: hidden; }
.cover :deep(.el-image) { width: 100%; height: 100%; }
.cover-fallback { height: 100%; display: flex; align-items: center; justify-content: center; color: #436579; font-weight: 600; font-size: 18px; }
.tag-row { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 14px; }
h1 { margin: 0 0 14px; font-size: 30px; line-height: 1.35; color: #172033; }
.intro { margin: 0 0 18px; color: #526173; line-height: 1.8; }
.meta-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; margin-bottom: 20px; }
.meta-grid div { background: #f8fafc; border: 1px solid #edf2f7; border-radius: 8px; padding: 12px; }
.meta-grid span { display: block; color: #7b8794; font-size: 13px; margin-bottom: 6px; }
.meta-grid strong { color: #1f2937; font-size: 15px; }
.actions { display: flex; gap: 12px; flex-wrap: wrap; }
.hint { margin: 12px 0 0; color: #b45309; font-size: 13px; }
.body-section { margin-top: 20px; background: #fff; border: 1px solid #e6edf5; border-radius: 8px; padding: 22px; }
.rich-text { color: #303846; line-height: 1.9; font-size: 15px; }
.rich-text :deep(img) { max-width: 100%; border-radius: 8px; }
.notice-list p { margin: 0 0 12px; color: #526173; line-height: 1.8; }
.comment-editor { background: #f8fafc; border: 1px solid #edf2f7; border-radius: 8px; padding: 16px; margin-bottom: 18px; }
.comment-actions { margin-top: 12px; text-align: right; }
.comment-list { min-height: 120px; }
.comment-item { display: grid; grid-template-columns: 42px 1fr; gap: 12px; padding: 16px 0; border-bottom: 1px solid #edf2f7; }
.comment-item:last-child { border-bottom: 0; }
.comment-head { display: flex; justify-content: space-between; gap: 12px; margin-bottom: 8px; color: #1f2937; }
.comment-head span { color: #8a94a3; font-size: 12px; }
.comment-item p { margin: 0; color: #526173; line-height: 1.7; }
@media (max-width: 860px) {
  .detail-page { padding: 12px; }
  .top-section { grid-template-columns: 1fr; }
  .cover { height: 220px; }
}
</style>
