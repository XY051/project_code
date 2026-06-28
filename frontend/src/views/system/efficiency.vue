<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import http from '../../utils/http'

// 加载状态
const loading = ref(false)

// 缁煎悎姒傝鏁版嵁
const overviewData = ref({
  resourceCount: 0,
  trainingCount: 0,
  userCount: 0,
  completionRate: 0,
  activeUserCount: 0
})

// 鎶ヨ〃鏁版嵁
const reportList = ref([])
const reportTotal = ref(0)
const reportParams = ref({
  pageNum: 1,
  pageSize: 10,
  reportType: ''
})

// 保存报表对话框
const dialogVisible = ref(false)
const reportForm = ref({
  reportName: '',
  reportType: 'comprehensive',
  dataJson: '',
  chartType: 'bar'
})

// ECharts 瀹炰緥
let resourceChart = null
let trainingChart = null
let userActivityChart = null

// 鑾峰彇缁煎悎姒傝
const getOverview = async () => {
  try {
    const res = await http.get('/efficiencyAnalysis/overview')
    if (res.code === 200) {
      overviewData.value = res.data
    }
  } catch (error) {
    console.error('鑾峰彇姒傝鏁版嵁澶辫触:', error)
  }
}

// 鑾峰彇璧勬簮鍒╃敤鍒嗘瀽
const getResourceUsage = async () => {
  try {
    const res = await http.get('/efficiencyAnalysis/resourceUsage')
    if (res.code === 200) {
      const chartDom = document.getElementById('resourceChart')
      if (chartDom) {
        if (resourceChart) resourceChart.dispose()
        resourceChart = echarts.init(chartDom)
        resourceChart.setOption({
          title: { text: '璧勬簮鍒╃敤鍒嗘瀽', left: 'center' },
          tooltip: { trigger: 'axis' },
          legend: { data: ['浏览量', '收藏量'], top: 30 },
          xAxis: { type: 'category', data: res.data.names || [] },
          yAxis: { type: 'value' },
          series: [
            { name: '浏览量', type: 'bar', data: res.data.viewCounts || [], itemStyle: { color: '#667eea' } },
            { name: '收藏量', type: 'bar', data: res.data.collectCounts || [], itemStyle: { color: '#f093fb' } }
          ],
          grid: { top: 70, left: 50, right: 20, bottom: 40 }
        })
      }
    }
  } catch (error) {
    console.error('鑾峰彇璧勬簮鍒╃敤鏁版嵁澶辫触:', error)
  }
}

// 鑾峰彇瀹炶鏁堣兘鍒嗘瀽
const getTrainingEfficiency = async () => {
  try {
    const res = await http.get('/efficiencyAnalysis/trainingEfficiency')
    if (res.code === 200) {
      const chartDom = document.getElementById('trainingChart')
      if (chartDom) {
        if (trainingChart) trainingChart.dispose()
        trainingChart = echarts.init(chartDom)
        const data = res.data
        trainingChart.setOption({
          title: { text: '瀹炶鏁堣兘鍒嗘瀽', left: 'center' },
          tooltip: { trigger: 'axis' },
          xAxis: {
            type: 'category',
            data: ['实训项目数', '完成数', '平均时长(分钟)', '完成率(%)']
          },
          yAxis: { type: 'value' },
          series: [{
            type: 'bar',
            data: [
              { value: data.totalExperiments || 0, itemStyle: { color: '#4facfe' } },
              { value: data.completedCount || 0, itemStyle: { color: '#43e97b' } },
              { value: data.avgDuration || 0, itemStyle: { color: '#f093fb' } },
              { value: data.completionRate || 0, itemStyle: { color: '#667eea' } }
            ]
          }],
          grid: { top: 40, left: 50, right: 20, bottom: 40 }
        })
      }
    }
  } catch (error) {
    console.error('鑾峰彇瀹炶鏁堣兘鏁版嵁澶辫触:', error)
  }
}

// 获取用户活跃度
const getUserActivity = async () => {
  try {
    const res = await http.get('/efficiencyAnalysis/userActivity')
    if (res.code === 200) {
      const chartDom = document.getElementById('userActivityChart')
      if (chartDom) {
        if (userActivityChart) userActivityChart.dispose()
        userActivityChart = echarts.init(chartDom)
        userActivityChart.setOption({
          title: { text: '用户活跃度分析', left: 'center' },
          tooltip: { trigger: 'item' },
          series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            data: [
              { value: res.data.activeUsers || 0, name: '娲昏穬鐢ㄦ埛', itemStyle: { color: '#667eea' } },
              { value: (res.data.totalUsers || 1) - (res.data.activeUsers || 0), name: '非活跃用户', itemStyle: { color: '#e0e3e9' } }
            ]
          }]
        })
      }
    }
  } catch (error) {
    console.error('鑾峰彇鐢ㄦ埛娲昏穬搴﹀け璐?', error)
  }
}

// 鑾峰彇鎶ヨ〃鍒楄〃
const getReportList = async () => {
  try {
    let url = `/efficiencyAnalysis/report/page?pageNum=${reportParams.value.pageNum}&pageSize=${reportParams.value.pageSize}`
    if (reportParams.value.reportType) url += `&reportType=${reportParams.value.reportType}`
    const res = await http.get(url)
    if (res.code === 200) {
      reportList.value = res.data.records || []
      reportTotal.value = res.data.total || 0
    }
  } catch (error) {
    console.error('鑾峰彇鎶ヨ〃鍒楄〃澶辫触:', error)
  }
}

// 淇濆瓨鎶ヨ〃
const handleSaveReport = async () => {
  // 灏嗗綋鍓嶅浘琛ㄦ暟鎹簭鍒楀寲
  const chartData = {
    overview: overviewData.value,
    timestamp: new Date().toISOString()
  }
  reportForm.value.dataJson = JSON.stringify(chartData)

  try {
    const res = await http.post('/efficiencyAnalysis/report/save', reportForm.value)
    if (res.code === 200) {
      ElMessage.success('鎶ヨ〃淇濆瓨鎴愬姛')
      dialogVisible.value = false
      getReportList()
    }
  } catch (error) {
    ElMessage.error('淇濆瓨澶辫触')
  }
}

// 鍒犻櫎鎶ヨ〃
const handleDelReport = async (id) => {
  try {
    await ElMessageBox.confirm('纭畾瑕佸垹闄よ鎶ヨ〃鍚楋紵', '鎻愮ず', { type: 'warning' })
    const res = await http.get(`/efficiencyAnalysis/report/del?id=${id}`)
    if (res.code === 200) {
      ElMessage.success('鍒犻櫎鎴愬姛')
      getReportList()
    }
  } catch (error) {
    // 鐢ㄦ埛鍙栨秷
  }
}

// 鏌ョ湅鎶ヨ〃
const handleViewReport = (row) => {
  try {
    const data = JSON.parse(row.dataJson)
    ElMessageBox.alert(
      `<pre style="max-height:400px;overflow:auto">${JSON.stringify(data, null, 2)}</pre>`,
      row.reportName,
      { dangerouslyUseHTMLString: true }
    )
  } catch {
    ElMessage.error('鎶ヨ〃鏁版嵁瑙ｆ瀽澶辫触')
  }
}

// 鍒嗛〉
const handlePageChange = (page) => {
  reportParams.value.pageNum = page
  getReportList()
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  resourceChart?.resize()
  trainingChart?.resize()
  userActivityChart?.resize()
}

const reportTypeMap = {
  'resource': '璧勬簮鍒╃敤',
  'training': '瀹炶鏁堣兘',
  'user': '鐢ㄦ埛娲昏穬',
  'comprehensive': '缁煎悎鍒嗘瀽'
}

onMounted(async () => {
  loading.value = true
  await Promise.all([
    getOverview(),
    getReportList()
  ])
  loading.value = false

  // 寤惰繜鍔犺浇鍥捐〃纭繚DOM娓叉煋瀹屾垚
  setTimeout(() => {
    getResourceUsage()
    getTrainingEfficiency()
    getUserActivity()
  }, 300)

  window.addEventListener('resize', handleResize)
})

// 组件卸载时销毁图表
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  resourceChart?.dispose()
  trainingChart?.dispose()
  userActivityChart?.dispose()
})
</script>

<template>
  <div class="efficiency-container">
    <!-- 缁煎悎姒傝 -->
    <section class="overview-section">
      <h2 class="section-title">缁煎悎鏁版嵁姒傝</h2>
      <div class="overview-grid">
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #667eea, #764ba2)">馃摎</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.resourceCount }}</span>
            <span class="card-label">铏氭嫙浠跨湡璧勬簮</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c)">馃敩</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.trainingCount }}</span>
            <span class="card-label">瀹炶椤圭洰</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">馃懃</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.userCount }}</span>
            <span class="card-label">娉ㄥ唽鐢ㄦ埛</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">馃搱</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.completionRate }}%</span>
            <span class="card-label">实训完成率</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #fa709a, #fee140)">馃敟</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.activeUserCount }}</span>
            <span class="card-label">娲昏穬鐢ㄦ埛</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 鍥捐〃鍖哄煙 -->
    <div class="charts-grid">
      <div class="chart-card">
        <div id="resourceChart" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <div id="trainingChart" class="chart-container"></div>
      </div>
    </div>
    <div class="charts-row">
      <div class="chart-card half">
        <div id="userActivityChart" class="chart-container"></div>
      </div>
      <div class="chart-card half">
        <div class="action-panel">
          <h3>鎿嶄綔闈㈡澘</h3>
          <el-button type="primary" @click="dialogVisible = true">淇濆瓨褰撳墠鎶ヨ〃</el-button>
          <el-button @click="getOverview(); getResourceUsage(); getTrainingEfficiency(); getUserActivity()">
            鍒锋柊鏁版嵁
          </el-button>
        </div>
      </div>
    </div>

    <!-- 鎶ヨ〃鍒楄〃 -->
    <section class="report-section">
      <h2 class="section-title">鍘嗗彶鎶ヨ〃</h2>
      <div class="report-filter">
        <el-select v-model="reportParams.reportType" placeholder="鎶ヨ〃绫诲瀷" clearable @change="getReportList">
          <el-option v-for="(label, value) in reportTypeMap" :key="value" :label="label" :value="value" />
        </el-select>
      </div>
      <el-table :data="reportList" border stripe style="width: 100%; margin-top: 16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="reportName" label="鎶ヨ〃鍚嶇О" />
        <el-table-column prop="reportType" label="鎶ヨ〃绫诲瀷" width="120">
          <template #default="{ row }">
            {{ reportTypeMap[row.reportType] || row.reportType }}
          </template>
        </el-table-column>
        <el-table-column prop="chartType" label="鍥捐〃绫诲瀷" width="100" />
        <el-table-column prop="createTime" label="鍒涘缓鏃堕棿" width="180" />
        <el-table-column label="鎿嶄綔" width="160">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewReport(row)">鏌ョ湅</el-button>
            <el-button type="danger" size="small" @click="handleDelReport(row.id)">鍒犻櫎</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; text-align: right">
        <el-pagination
          :current-page="reportParams.pageNum"
          :page-size="reportParams.pageSize"
          :total="reportTotal"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </section>

    <!-- 淇濆瓨鎶ヨ〃瀵硅瘽妗?-->
    <el-dialog v-model="dialogVisible" title="淇濆瓨鍒嗘瀽鎶ヨ〃" width="500px">
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="鎶ヨ〃鍚嶇О">
          <el-input v-model="reportForm.reportName" placeholder="请输入报表名称" />
        </el-form-item>
        <el-form-item label="鎶ヨ〃绫诲瀷">
          <el-select v-model="reportForm.reportType" style="width: 100%">
            <el-option v-for="(label, value) in reportTypeMap" :key="value" :label="label" :value="value" />
          </el-select>
        </el-form-item>
        <el-form-item label="鍥捐〃绫诲瀷">
          <el-select v-model="reportForm.chartType" style="width: 100%">
            <el-option label="柱状图" value="bar" />
            <el-option label="折线图" value="line" />
            <el-option label="楗煎浘" value="pie" />
            <el-option label="雷达图" value="radar" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">鍙栨秷</el-button>
        <el-button type="primary" @click="handleSaveReport">淇濆瓨</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.efficiency-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  padding-left: 12px;
  border-left: 4px solid #667eea;
}

// 姒傝鍗＄墖
.overview-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 24px;

  .overview-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 14px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .card-icon {
      width: 50px;
      height: 50px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      flex-shrink: 0;
    }

    .card-info {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .card-value {
        font-size: 24px;
        font-weight: 700;
        color: #2c3e50;
      }

      .card-label {
        font-size: 13px;
        color: #95a5a6;
      }
    }
  }
}

// 鍥捐〃
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .chart-container {
    width: 100%;
    height: 300px;
  }

  .action-panel {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 300px;
    gap: 16px;

    h3 {
      margin: 0;
      color: #2c3e50;
    }
  }
}

// 鎶ヨ〃鍒楄〃
.report-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .report-filter {
    display: flex;
    gap: 12px;
  }
}

@media (max-width: 1200px) {
  .overview-grid { grid-template-columns: repeat(3, 1fr); }
  .charts-grid, .charts-row { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  .overview-grid { grid-template-columns: repeat(2, 1fr); }
  .efficiency-container { padding: 16px; }
}
</style>
