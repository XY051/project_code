<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import http from '../../utils/http'

// 加载状态
const loading = ref(false)

// 综合概览数据
const overviewData = ref({
  resourceCount: 0,
  trainingCount: 0,
  userCount: 0,
  completionRate: 0,
  activeUserCount: 0
})

// 报表数据
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

// ECharts 实例
let resourceChart = null
let trainingChart = null
let userActivityChart = null

// 获取综合概览
const getOverview = async () => {
  try {
    const res = await http.get('/efficiencyAnalysis/overview')
    if (res.code === 200) {
      overviewData.value = res.data
    }
  } catch (error) {
    console.error('获取概览数据失败:', error)
  }
}

// 获取资源利用分析
const getResourceUsage = async () => {
  try {
    const res = await http.get('/efficiencyAnalysis/resourceUsage')
    if (res.code === 200) {
      const chartDom = document.getElementById('resourceChart')
      if (chartDom) {
        if (resourceChart) resourceChart.dispose()
        resourceChart = echarts.init(chartDom)
        resourceChart.setOption({
          title: { text: '资源利用分析', left: 'center' },
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
    console.error('获取资源利用数据失败:', error)
  }
}

// 获取实训效能分析
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
          title: { text: '实训效能分析', left: 'center' },
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
    console.error('获取实训效能数据失败:', error)
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
              { value: res.data.activeUsers || 0, name: '活跃用户', itemStyle: { color: '#667eea' } },
              { value: (res.data.totalUsers || 1) - (res.data.activeUsers || 0), name: '非活跃用户', itemStyle: { color: '#e0e3e9' } }
            ]
          }]
        })
      }
    }
  } catch (error) {
    console.error('获取用户活跃度失败:', error)
  }
}

// 获取报表列表
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
    console.error('获取报表列表失败:', error)
  }
}

// 保存报表
const handleSaveReport = async () => {
  // 将当前图表数据序列化
  const chartData = {
    overview: overviewData.value,
    timestamp: new Date().toISOString()
  }
  reportForm.value.dataJson = JSON.stringify(chartData)
  
  try {
    const res = await http.post('/efficiencyAnalysis/report/save', reportForm.value)
    if (res.code === 200) {
      ElMessage.success('报表保存成功')
      dialogVisible.value = false
      getReportList()
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除报表
const handleDelReport = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该报表吗？', '提示', { type: 'warning' })
    const res = await http.get(`/efficiencyAnalysis/report/del?id=${id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getReportList()
    }
  } catch (error) {
    // 用户取消
  }
}

// 查看报表
const handleViewReport = (row) => {
  try {
    const data = JSON.parse(row.dataJson)
    ElMessageBox.alert(
      `<pre style="max-height:400px;overflow:auto">${JSON.stringify(data, null, 2)}</pre>`,
      row.reportName,
      { dangerouslyUseHTMLString: true }
    )
  } catch {
    ElMessage.error('报表数据解析失败')
  }
}

// 分页
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
  'resource': '资源利用',
  'training': '实训效能',
  'user': '用户活跃',
  'comprehensive': '综合分析'
}

onMounted(async () => {
  loading.value = true
  await Promise.all([
    getOverview(),
    getReportList()
  ])
  loading.value = false
  
  // 延迟加载图表确保DOM渲染完成
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
    <!-- 综合概览 -->
    <section class="overview-section">
      <h2 class="section-title">综合数据概览</h2>
      <div class="overview-grid">
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #667eea, #764ba2)">📚</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.resourceCount }}</span>
            <span class="card-label">虚拟仿真资源</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c)">🔬</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.trainingCount }}</span>
            <span class="card-label">实训项目</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">👥</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.userCount }}</span>
            <span class="card-label">注册用户</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">📈</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.completionRate }}%</span>
            <span class="card-label">实训完成率</span>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon" style="background: linear-gradient(135deg, #fa709a, #fee140)">🔥</div>
          <div class="card-info">
            <span class="card-value">{{ overviewData.activeUserCount }}</span>
            <span class="card-label">活跃用户</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 图表区域 -->
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
          <h3>操作面板</h3>
          <el-button type="primary" @click="dialogVisible = true">保存当前报表</el-button>
          <el-button @click="getOverview(); getResourceUsage(); getTrainingEfficiency(); getUserActivity()">
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 报表列表 -->
    <section class="report-section">
      <h2 class="section-title">历史报表</h2>
      <div class="report-filter">
        <el-select v-model="reportParams.reportType" placeholder="报表类型" clearable @change="getReportList">
          <el-option v-for="(label, value) in reportTypeMap" :key="value" :label="label" :value="value" />
        </el-select>
      </div>
      <el-table :data="reportList" border stripe style="width: 100%; margin-top: 16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="reportName" label="报表名称" />
        <el-table-column prop="reportType" label="报表类型" width="120">
          <template #default="{ row }">
            {{ reportTypeMap[row.reportType] || row.reportType }}
          </template>
        </el-table-column>
        <el-table-column prop="chartType" label="图表类型" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewReport(row)">查看</el-button>
            <el-button type="danger" size="small" @click="handleDelReport(row.id)">删除</el-button>
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

    <!-- 保存报表对话框 -->
    <el-dialog v-model="dialogVisible" title="保存分析报表" width="500px">
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="报表名称">
          <el-input v-model="reportForm.reportName" placeholder="请输入报表名称" />
        </el-form-item>
        <el-form-item label="报表类型">
          <el-select v-model="reportForm.reportType" style="width: 100%">
            <el-option v-for="(label, value) in reportTypeMap" :key="value" :label="label" :value="value" />
          </el-select>
        </el-form-item>
        <el-form-item label="图表类型">
          <el-select v-model="reportForm.chartType" style="width: 100%">
            <el-option label="柱状图" value="bar" />
            <el-option label="折线图" value="line" />
            <el-option label="饼图" value="pie" />
            <el-option label="雷达图" value="radar" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveReport">保存</el-button>
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

// 概览卡片
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

// 图表
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

// 报表列表
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