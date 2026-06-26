<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../utils/http'
import { getImageUrl } from '../../utils/system'
import config from '../../config'
import ResourceCard from '../../components/ResourceCard.vue'
import TrainingCard from '../../components/TrainingCard.vue'

const router = useRouter()

// 轮播图数据
const bannerList = ref([])
// 热门资源数据
const hotResourceList = ref([])
// 热门实训项目数据
const hotTrainingList = ref([])
// 热门公告数据
const hotNewsList = ref([])
// 数据概览
const overviewData = ref({
  resourceCount: 0,
  trainingCount: 0,
  userCount: 0,
  labCount: 0
})
// 加载状态
const loading = ref(false)

// 获取轮播图数据
const getBannerList = async () => {
  try {
    const res = await http.get('/banner/list')
    if (res.code === 200) {
      bannerList.value = (res.data || []).map(t => {
        t.url = getImageUrl(t.url)
        return t
      })
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

// 获取热门虚拟仿真资源（暂用原接口，等待第2人改造后对接 /trainingResource/hot）
const getHotResourceList = async () => {
  try {
    const res = await http.get('/culturalHeritage/hot?pageNum=1&pageSize=8&orderBy=view_count&orderType=desc')
    if (res.code === 200) {
      hotResourceList.value = res.data?.records.map(t => {
        t.coverImage = getImageUrl(t.coverImage)
        return t
      }) || []
    }
  } catch (error) {
    console.error('获取热门资源失败:', error)
  }
}

// 获取热门实训项目（暂用原接口，等待第3人改造后对接 /training/hot）
const getHotTrainingList = async () => {
  try {
    const res = await http.get('/activity/hot?pageNum=1&pageSize=6&orderBy=view_count&orderType=desc')
    if (res.code === 200) {
      hotTrainingList.value = res.data?.records || []
    }
  } catch (error) {
    console.error('获取热门实训项目失败:', error)
  }
}

// 获取热门公告
const getHotNewsList = async () => {
  try {
    const res = await http.get('/article/hot?pageNum=1&pageSize=5&orderBy=view_count&orderType=desc')
    if (res.code === 200) {
      hotNewsList.value = res.data?.records.map(t => {
        t.coverUrl = getImageUrl(t.coverUrl)
        return t
      }) || []
    }
  } catch (error) {
    console.error('获取热门公告失败:', error)
  }
}

// 获取数据概览（门户配置 + 统计数据）
const getOverviewData = async () => {
  try {
    // 尝试获取统计数据概览
    const res = await http.get('/statistics/overview')
    if (res.code === 200) {
      overviewData.value = {
        resourceCount: res.data.heritageCount || 0,
        trainingCount: res.data.activityCount || 0,
        userCount: res.data.userCount || 0,
        labCount: res.data.videoCount || 0
      }
    }
  } catch (error) {
    console.error('获取数据概览失败:', error)
  }
}

// 跳转到列表页
const goToList = (path) => {
  router.push(path)
}

// 跳转到公告详情
const goToNewsDetail = (id) => {
  router.push({ path: '/front/newsDetail', query: { id } })
}

// 快捷入口
const quickEntries = [
  { icon: '📚', label: '资源中心', path: '/front/heritage', color: '#667eea' },
  { icon: '🔬', label: '实训项目', path: '/front/activity', color: '#f093fb' },
  { icon: '🏫', label: '实验室预约', path: '/front/labBooking', color: '#4facfe' },
  { icon: '📊', label: '数据统计', path: '/chart', color: '#43e97b' }
]

// 初始化数据
const initData = async () => {
  loading.value = true
  try {
    await Promise.all([
      getBannerList(),
      getHotResourceList(),
      getHotTrainingList(),
      getHotNewsList(),
      getOverviewData()
    ])
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  initData()
})
</script>

<template>
  <div class="home-container">
    <!-- 轮播图区域 -->
    <div class="banner-section">
      <el-carousel
        v-if="bannerList.length > 0"
        height="500px"
        :interval="4000"
        indicator-position="outside"
        type="card"
        arrow="hover"
      >
        <el-carousel-item v-for="item in bannerList" :key="item.id">
          <div
            class="banner-item"
            :style="{ backgroundImage: `url(${item.url})` }"
          >
            <div class="banner-content">
              <h2 class="banner-title">{{ item.title }}</h2>
              <p class="banner-desc">{{ item.description || '虚拟仿真实训平台' }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      <div v-else class="banner-placeholder">
        <div class="placeholder-content">
          <h2>{{ config.frontName }}</h2>
          <p>{{ config.description }}</p>
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-section">
      <div class="quick-grid">
        <div
          v-for="entry in quickEntries"
          :key="entry.path"
          class="quick-card"
          :style="{ '--card-color': entry.color }"
          @click="goToList(entry.path)"
        >
          <span class="quick-icon">{{ entry.icon }}</span>
          <span class="quick-label">{{ entry.label }}</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 数据概览 -->
      <section class="section overview-section">
        <div class="section-header">
          <h2 class="section-title">平台数据概览</h2>
        </div>
        <div class="overview-grid">
          <div class="overview-card">
            <div class="overview-icon" style="background: linear-gradient(135deg, #667eea, #764ba2)">
              📚
            </div>
            <div class="overview-info">
              <span class="overview-value">{{ overviewData.resourceCount }}</span>
              <span class="overview-label">仿真资源</span>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c)">
              🔬
            </div>
            <div class="overview-info">
              <span class="overview-value">{{ overviewData.trainingCount }}</span>
              <span class="overview-label">实训项目</span>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">
              👥
            </div>
            <div class="overview-info">
              <span class="overview-value">{{ overviewData.userCount }}</span>
              <span class="overview-label">注册用户</span>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">
              🏫
            </div>
            <div class="overview-info">
              <span class="overview-value">{{ overviewData.labCount }}</span>
              <span class="overview-label">实验室</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 热门虚拟仿真资源 -->
      <section class="section">
        <div class="section-header">
          <div class="section-title-wrapper">
            <div class="title-decoration left"></div>
            <h2 class="section-title">热门虚拟仿真资源</h2>
            <div class="title-decoration right"></div>
          </div>
          <el-button type="primary" class="more-btn" @click="goToList('/front/heritage')">
            查看更多
          </el-button>
        </div>
        <div class="resource-grid">
          <ResourceCard
            v-for="item in hotResourceList"
            :key="item.id"
            :resource="item"
          />
        </div>
      </section>

      <!-- 最新实训项目 -->
      <section class="section">
        <div class="section-header">
          <div class="section-title-wrapper">
            <div class="title-decoration left"></div>
            <h2 class="section-title">最新实训项目</h2>
            <div class="title-decoration right"></div>
          </div>
          <el-button type="primary" class="more-btn" @click="goToList('/front/activity')">
            更多
          </el-button>
        </div>
        <div class="training-layout">
          <div class="main-training-wrapper" v-if="hotTrainingList.length > 0">
            <TrainingCard :training="hotTrainingList[0]" size="large" />
          </div>
          <div class="training-list">
            <TrainingCard
              v-for="item in hotTrainingList.slice(1, 4)"
              :key="item.id"
              :training="item"
              size="small"
            />
          </div>
        </div>
      </section>

      <!-- 最新公告 -->
      <section class="section">
        <div class="section-header">
          <div class="section-title-wrapper">
            <div class="title-decoration left"></div>
            <h2 class="section-title">最新公告</h2>
            <div class="title-decoration right"></div>
          </div>
          <el-button type="primary" class="more-btn" @click="goToList('/front/news')">
            更多
          </el-button>
        </div>
        <div class="news-layout">
          <div class="main-news" v-if="hotNewsList.length > 0" @click="goToNewsDetail(hotNewsList[0].id)">
            <el-image
              :src="hotNewsList[0].coverUrl"
              :alt="hotNewsList[0].title"
              class="main-news-image"
              fit="cover"
            >
              <template #error>
                <div class="image-error">📰</div>
              </template>
            </el-image>
            <div class="main-news-content">
              <h3 class="news-title">{{ hotNewsList[0].title }}</h3>
              <div class="news-intro">{{ hotNewsList[0].intro }}</div>
            </div>
          </div>
          <div class="news-list">
            <div
              v-for="item in hotNewsList.slice(1, 5)"
              :key="item.id"
              class="news-item"
              @click="goToNewsDetail(item.id)"
            >
              <div class="news-cover-box">
                <img :src="item.coverUrl" alt="">
              </div>
              <div class="news-content">
                <h4 class="news-title">{{ item.title }}</h4>
                <p class="news-summary">{{ item.intro }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

// 轮播图样式
.banner-section {
  width: 100%;

  .banner-item {
    height: 500px;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    position: relative;
    cursor: pointer;
    border-radius: 0 0 24px 24px;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba(52, 152, 219, 0.3) 0%, rgba(102, 126, 234, 0.3) 100%);
    }

    .banner-content {
      position: absolute;
      bottom: 80px;
      left: 80px;
      color: white;
      z-index: 2;

      .banner-title {
        font-size: 48px;
        font-weight: 700;
        margin-bottom: 16px;
        text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5);
      }

      .banner-desc {
        font-size: 18px;
        opacity: 0.95;
        text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.5);
        font-weight: 500;
      }
    }
  }

  .banner-placeholder {
    height: 500px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    border-radius: 0 0 24px 24px;

    .placeholder-content {
      text-align: center;

      h2 {
        font-size: 48px;
        margin-bottom: 16px;
        text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
        font-weight: 700;
      }

      p {
        font-size: 18px;
        opacity: 0.9;
        font-weight: 500;
        max-width: 600px;
        line-height: 1.6;
      }
    }
  }
}

// 快捷入口
.quick-section {
  max-width: 1200px;
  margin: -40px auto 40px;
  position: relative;
  z-index: 10;
  padding: 0 20px;

  .quick-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    background: #fff;
    border-radius: 20px;
    padding: 30px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);

    .quick-card {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      padding: 24px 16px;
      border-radius: 16px;
      cursor: pointer;
      transition: all 0.3s ease;
      background: #f8f9fa;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        background: var(--card-color);
        
        .quick-icon {
          transform: scale(1.2);
        }

        .quick-label {
          color: #fff;
        }
      }

      .quick-icon {
        font-size: 36px;
        transition: transform 0.3s ease;
      }

      .quick-label {
        font-size: 16px;
        font-weight: 600;
        color: #2c3e50;
        transition: color 0.3s ease;
      }
    }
  }
}

// 主要内容区域
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 60px;
}

// 区块样式
.section {
  margin-bottom: 60px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;

    .section-title-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      flex: 1;

      .title-decoration {
        height: 2px;
        background: linear-gradient(90deg, transparent 0%, #667eea 50%, transparent 100%);
        flex: 1;

        &.left { margin-right: 30px; }
        &.right { margin-left: 30px; }
      }

      .section-title {
        font-size: 28px;
        font-weight: 700;
        color: #2c3e50;
        white-space: nowrap;
      }
    }

    .more-btn {
      border-radius: 25px;
      padding: 12px 24px;
      font-weight: 600;
    }
  }
}

// 数据概览
.overview-section {
  .section-header {
    justify-content: center;
    margin-bottom: 24px;
    .section-title { font-size: 22px; color: #606266; }
  }
  
  .overview-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .overview-card {
      background: #fff;
      border-radius: 16px;
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

      .overview-icon {
        width: 56px;
        height: 56px;
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        flex-shrink: 0;
      }

      .overview-info {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .overview-value {
          font-size: 28px;
          font-weight: 700;
          color: #2c3e50;
        }

        .overview-label {
          font-size: 14px;
          color: #95a5a6;
        }
      }
    }
  }
}

// 资源网格
.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

// 实训项目布局
.training-layout {
  display: flex;
  gap: 24px;

  .main-training-wrapper {
    flex: 1.2;
  }

  .training-list {
    flex: 0.8;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}

// 公告布局
.news-layout {
  display: flex;
  gap: 24px;

  .main-news {
    flex: 1.2;
    cursor: pointer;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    background: #fff;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
    }

    .main-news-image {
      width: 100%;
      height: 240px;
    }

    .main-news-content {
      padding: 20px;

      .news-title {
        font-size: 18px;
        font-weight: 700;
        color: #2c3e50;
        margin-bottom: 10px;
        line-height: 1.4;
      }

      .news-intro {
        font-size: 14px;
        color: #95a5a6;
      }
    }
  }

  .news-list {
    flex: 0.8;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .news-item {
      display: flex;
      background: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateX(6px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
      }

      .news-cover-box {
        width: 60px;
        min-width: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;

        img {
          width: 50px;
          height: 50px;
          object-fit: cover;
          border-radius: 8px;
        }
      }

      .news-content {
        flex: 1;
        padding: 14px 16px;

        .news-title {
          font-size: 14px;
          font-weight: 600;
          color: #2c3e50;
          margin: 0 0 6px 0;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .news-summary {
          font-size: 12px;
          color: #95a5a6;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
    }
  }
}

.image-error {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa, #e9ecef);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
}

// 响应式设计
@media (max-width: 1024px) {
  .quick-section .quick-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .overview-section .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .banner-item .banner-content {
    bottom: 40px;
    left: 40px;
    .banner-title { font-size: 32px; }
    .banner-desc { font-size: 14px; }
  }

  .banner-placeholder .placeholder-content h2 { font-size: 32px; }

  .quick-section {
    margin-top: -30px;
    .quick-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      padding: 20px;
      
      .quick-card {
        padding: 16px 12px;
        .quick-icon { font-size: 28px; }
        .quick-label { font-size: 14px; }
      }
    }
  }

  .main-content { padding: 0 16px 40px; }

  .section {
    margin-bottom: 40px;

    .section-header .section-title-wrapper .section-title {
      font-size: 22px;
    }
  }

  .resource-grid {
    grid-template-columns: 1fr;
  }

  .training-layout {
    flex-direction: column;
  }

  .news-layout {
    flex-direction: column;
  }

  .overview-section .overview-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .banner-section .banner-item, .banner-section .banner-placeholder {
    height: 300px;
  }

  .quick-section .quick-grid {
    grid-template-columns: 1fr 1fr;
  }

  .overview-section .overview-grid {
    grid-template-columns: 1fr 1fr;
  }
}
</style>