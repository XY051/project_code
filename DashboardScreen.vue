<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import http from '@/utils/http'

// ===== 大屏数据 =====
const dashboardData = ref<Record<string, any>>({})
const loading = ref(false)
const fullscreen = ref(false)
const autoRefresh = ref(true)
const refreshInterval = ref(30)
const timeRange = ref('all')
let timer: any = null

// ===== 图表Refs =====
const barChartRef = ref<HTMLElement | null>(null)
const pieChartRef = ref<HTMLElement | null>(null)
const scoreChartRef = ref<HTMLElement | null>(null)
let barChartInst: echarts.ECharts | null = null
let pieChartInst: echarts.ECharts | null = null
let scoreChartInst: echarts.ECharts | null = null

// ===== 数据获取 =====
const fetchDashboardData = async () => {
  loading.value = true
  try {
    const q = timeRange.value !== 'all' ? `?timeRange=${timeRange.value}` : ''
    const res = await http.get(`/statistics/dashboard${q}`)
    if (res.code === 200) {
      dashboardData.value = res.data || {}
      initCharts(res.data || {})
    }
  } catch (error) { console.error('获取大屏数据失败:', error) }
  finally { loading.value = false }
}

// ===== 图表初始化 =====
const initCharts = (data: Record<string, any>) => {
  // 难度分布柱状图
  if (barChartRef.value) {
    if (!barChartInst) barChartInst = echarts.init(barChartRef.value)
    barChartInst.setOption({
      title: { text: '实训项目难度分布', textStyle: { color: '#fff', fontSize: 16 } },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: (data.difficultyDistribution || []).map((d: any) => d.name), axisLabel: { color: '#a8d8ea' } },
      yAxis: { type: 'value', axisLabel: { color: '#a8d8ea' } },
      series: [{
        type: 'bar', barWidth: '50%',
        data: (data.difficultyDistribution || []).map((d: any) => d.value || 0),
        itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#00d4ff' }, { offset: 1, color: '#0066cc' }]) }
      }]
    }, true)
  }

  // 成绩分布饼图
  if (pieChartRef.value) {
    if (!pieChartInst) pieChartInst = echarts.init(pieChartRef.value)
    pieChartInst.setOption({
      title: { text: '实验成绩分布', textStyle: { color: '#fff', fontSize: 16 } },
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', left: 'left', top: 'middle', textStyle: { color: '#a8d8ea' } },
      series: [{
        type: 'pie', radius: ['40%', '70%'], center: ['60%', '50%'],
        data: (data.scoreDistribution || []).map((d: any) => ({ name: d.name, value: d.value || 0 })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
        itemStyle: { borderColor: '#0a1628', borderWidth: 2 }
      }]
    }, true)
  }

  // 参与统计柱状图
  if (scoreChartRef.value) {
    if (!scoreChartInst) scoreChartInst = echarts.init(scoreChartRef.value)
    scoreChartInst.setOption({
      title: { text: '实验参与统计', textStyle: { color: '#fff', fontSize: 16 } },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['实训项目', '实验记录', '注册用户', '已完成'], axisLabel: { color: '#a8d8ea' } },
      yAxis: { type: 'value', axisLabel: { color: '#a8d8ea' } },
      series: [{
        type: 'bar', barWidth: '50%',
        data: [data.trainingCount || 0, data.experimentCount || 0, data.userCount || 0, data.completedCount || 0],
        itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#0ea5e9' }, { offset: 1, color: '#0369a1' }]) }
      }]
    }, true)
  }
}

// ===== 全屏控制 =====
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    fullscreen.value = true
  } else {
    document.exitFullscreen()
    fullscreen.value = false
  }
}

// ===== 自动刷新 =====
const startAutoRefresh = () => {
  if (timer) clearInterval(timer)
  if (autoRefresh.value) timer = setInterval(fetchDashboardData, refreshInterval.value * 1000)
}

const handleRefreshChange = (val: number) => {
  refreshInterval.value = val
  startAutoRefresh()
}

const toggleAutoRefresh = () => {
  autoRefresh.value = !autoRefresh.value
  if (autoRefresh.value) startAutoRefresh()
  else if (timer) clearInterval(timer)
}

// ===== 时间范围切换 =====
const handleTimeRangeChange = (val: string) => {
  timeRange.value = val
  fetchDashboardData()
}

// ===== 窗口自适应 =====
const handleResize = () => {
  barChartInst?.resize()
  pieChartInst?.resize()
  scoreChartInst?.resize()
}

onMounted(async () => {
  await fetchDashboardData()
  startAutoRefresh()
  window.addEventListener('resize', handleResize)
  document.addEventListener('fullscreenchange', () => { fullscreen.value = !!document.fullscreenElement })
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  barChartInst?.dispose()
  pieChartInst?.dispose()
  scoreChartInst?.dispose()
})
</script>

<template>
  <div class="dashboard-screen">
    <!-- 顶部控制栏 -->
    <div class="dashboard-controls">
      <span class="dashboard-time">{{ new Date().toLocaleString('zh-CN') }}</span>
      <div class="control-actions">
        <!-- 时间范围筛选 -->
        <el-select :model-value="timeRange" @change="handleTimeRangeChange" size="small" style="width:130px">
          <el-option label="全部时间" value="all" />
          <el-option label="最近7天" value="7days" />
          <el-option label="最近30天" value="30days" />
          <el-option label="最近90天" value="90days" />
          <el-option label="最近一年" value="year" />
        </el-select>
        <el-select v-model="refreshInterval" @change="handleRefreshChange" size="small" style="width:130px">
          <el-option label="15秒刷新" :value="15" />
          <el-option label="30秒刷新" :value="30" />
          <el-option label="60秒刷新" :value="60" />
        </el-select>
        <el-button size="small" :type="autoRefresh ? 'success' : 'info'" @click="toggleAutoRefresh">
          {{ autoRefresh ? '自动刷新中' : '暂停刷新' }}
        </el-button>
        <el-button size="small" type="warning" @click="fetchDashboardData" :loading="loading">手动刷新</el-button>
        <el-button size="small" @click="toggleFullscreen">
          {{ fullscreen ? '退出全屏' : '全屏展示' }}
        </el-button>
      </div>
    </div>

    <!-- 标题 -->
    <div class="dashboard-title">虚拟仿真实训平台 - 数据大屏</div>

    <!-- 数据概览卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ dashboardData.trainingCount ?? 0 }}</div>
        <div class="stat-label">实训项目总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardData.experimentCount ?? 0 }}</div>
        <div class="stat-label">实验记录总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardData.userCount ?? 0 }}</div>
        <div class="stat-label">注册学生数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardData.completionRate ?? 0 }}%</div>
        <div class="stat-label">实验完成率</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardData.avgScore ?? 0 }}</div>
        <div class="stat-label">实验平均分</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-panel">
        <div ref="barChartRef" class="chart-box"></div>
      </div>
      <div class="chart-panel">
        <div ref="pieChartRef" class="chart-box"></div>
      </div>
      <div class="chart-panel chart-panel-wide">
        <div ref="scoreChartRef" class="chart-box"></div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.dashboard-screen {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a1628 0%, #0f2440 50%, #132d46 100%);
  padding: 20px;
  color: #fff;
  box-sizing: border-box;
}
.dashboard-controls {
  display: flex; justify-content: space-between; align-items: center;
  padding: 8px 16px; margin-bottom: 16px;
  background: rgba(255,255,255,0.08); border-radius: 8px; flex-wrap: wrap; gap: 8px;
  .dashboard-time { font-size: 14px; color: #a8d8ea; }
  .control-actions { display: flex; gap: 8px; flex-wrap: wrap; }
}
.dashboard-title {
  text-align: center; font-size: 36px; font-weight: 700;
  background: linear-gradient(90deg, #00d4ff, #0066cc);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 24px; letter-spacing: 4px;
}
.stats-row { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card {
  background: rgba(255,255,255,0.08); border: 1px solid rgba(0,212,255,0.3); border-radius: 12px;
  padding: 24px 16px; text-align: center; transition: all 0.3s;
  &:hover { transform: translateY(-4px); border-color: #00d4ff; box-shadow: 0 0 20px rgba(0,212,255,0.2); }
  .stat-value { font-size: 36px; font-weight: 700; color: #00d4ff; }
  .stat-label { font-size: 14px; color: #a8d8ea; margin-top: 8px; }
}
.charts-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.chart-panel {
  background: rgba(255,255,255,0.06); border: 1px solid rgba(0,212,255,0.2); border-radius: 12px; padding: 16px;
  .chart-box { width: 100%; height: 350px; }
}
.chart-panel-wide { grid-column: 1 / -1; }
@media (max-width: 1200px) {
  .stats-row { grid-template-columns: repeat(3, 1fr); }
  .charts-grid { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .dashboard-title { font-size: 24px; }
  .stat-card .stat-value { font-size: 24px; }
  .dashboard-controls { flex-direction: column; align-items: flex-start; }
}
</style>
