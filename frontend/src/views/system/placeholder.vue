<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import { DataAnalysis, Finished, Monitor, Setting, Tools } from "@element-plus/icons-vue";

const route = useRoute();

const pageMeta = computed(() => {
  const title = route.meta.title || "功能模块";
  const map = {
    数据导出: {
      icon: DataAnalysis,
      desc: "用于汇总平台资源、实训、用户和开放申请数据，后续可接入 Excel、PDF 等导出能力。",
      tags: ["报表导出", "数据筛选", "权限留痕"],
    },
    师资管理: {
      icon: Finished,
      desc: "用于维护教师、指导老师、外聘专家等师资信息，并与课程、实训项目建立关联。",
      tags: ["教师档案", "课程关联", "开放共享"],
    },
    教学计划: {
      icon: DataAnalysis,
      desc: "用于配置课程周次、实训任务、教学目标和考核安排，支撑过程化教学管理。",
      tags: ["计划编排", "任务分配", "考核安排"],
    },
    资源监控: {
      icon: Monitor,
      desc: "用于查看实训资源访问、申请、占用和归还情况，辅助管理员判断资源开放状态。",
      tags: ["访问统计", "资源占用", "异常提醒"],
    },
    教学监控: {
      icon: Monitor,
      desc: "用于跟踪课程实训进度、实验完成情况和学生参与情况，后续可接入实时看板。",
      tags: ["进度跟踪", "完成情况", "课堂状态"],
    },
    平台设置: {
      icon: Setting,
      desc: "用于配置平台基础参数、首页展示、开放规则和系统提示信息。",
      tags: ["基础配置", "开放规则", "页面展示"],
    },
    系统日志: {
      icon: Tools,
      desc: "用于记录登录、数据变更、审核、导出等关键操作，便于审计和问题追踪。",
      tags: ["登录日志", "操作审计", "异常追踪"],
    },
  };
  return map[title] || {
    icon: Tools,
    desc: "该后台模块已预留入口，后续可按业务需要接入数据表和管理流程。",
    tags: ["模块预留", "待接入", "后台管理"],
  };
});
</script>

<template>
  <div class="placeholder-page">
    <section class="placeholder-card">
      <div class="icon-box">
        <el-icon><component :is="pageMeta.icon" /></el-icon>
      </div>
      <div class="content">
        <p>后台模块</p>
        <h1>{{ route.meta.title }}</h1>
        <span>{{ pageMeta.desc }}</span>
        <div class="tag-row">
          <el-tag v-for="tag in pageMeta.tags" :key="tag" type="primary" effect="plain">{{ tag }}</el-tag>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.placeholder-page {
  min-height: calc(100vh - 130px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fb;
}

.placeholder-card {
  width: min(760px, 100%);
  background: #fff;
  border: 1px solid #e5edf6;
  border-radius: 8px;
  padding: 34px;
  display: grid;
  grid-template-columns: 74px 1fr;
  gap: 22px;
  box-shadow: 0 10px 24px rgba(20, 45, 80, .06);
}

.icon-box {
  width: 74px;
  height: 74px;
  border-radius: 8px;
  background: #eaf5ff;
  color: #1478d4;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
}

.content p {
  margin: 0 0 8px;
  color: #7b8794;
  font-size: 14px;
}

.content h1 {
  margin: 0 0 12px;
  color: #172033;
  font-size: 28px;
}

.content span {
  display: block;
  color: #526173;
  line-height: 1.8;
}

.tag-row {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 640px) {
  .placeholder-card {
    grid-template-columns: 1fr;
    padding: 24px;
  }
}
</style>
