<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import http from '../../utils/http'
import { getImageUrl } from '../../utils/system'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const videoDetail = ref<any>({})

// 定义接口，解决 TS 类型警告
interface ApiResponse {
  code?: number
  msg?: string
  data?: {
    id?: number | string
    title?: string
    url?: string
    createTime?: string
    viewCount?: number
  }
}

// 获取视频详情
const getVideoDetail = async () => {
  const id = Array.isArray(route.query.id) ? route.query.id[0] : route.query.id

  if (!id) {
    ElMessage.error('参数错误：视频ID不存在')
    return
  }

  loading.value = true
  try {
    const res = await http.get('/video/getById', { params: { id } }) as ApiResponse

    if (res.code === 200) {
      videoDetail.value = res.data || {}
    } else {
      ElMessage.error(res.msg || '获取视频详情失败')
    }
  } catch (err) {
    console.error('获取视频异常：', err)
    ElMessage.error('网络异常或服务错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) return '时间未知'

    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return '时间格式化失败'
  }
}

// ✅ 正确监听路由变化（修复报错核心）
watch(
    () => route.query.id,
    (newId) => {
      if (newId) getVideoDetail()
    }
)

onMounted(() => {
  getVideoDetail()
})
</script>

<template>
  <div class="video-detail-container">
    <el-card v-loading="loading" class="detail-card" shadow="hover">
      <template v-if="!loading && videoDetail.id">
        <div class="title-section">
          <h1 class="video-title">{{ videoDetail.title }}</h1>
        </div>

        <div class="meta-info">
          <div class="meta-item">
            <span>{{ formatDate(videoDetail.createTime) }}</span>
          </div>
          <div class="meta-item">
            <span>播放量：{{ videoDetail.viewCount || 0 }}</span>
          </div>
        </div>

        <!-- 原生视频播放器 -->
        <div class="video-section">
          <video
              v-if="videoDetail.url"
              class="native-video"
              controls
              preload="auto"
              :src="getImageUrl(videoDetail.url)"
          />
          <div v-else class="no-video">暂无视频文件</div>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="暂无视频数据" />
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.video-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .detail-card {
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border: none;

    .title-section {
      text-align: center;
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 2px solid #f0f0f0;

      .video-title {
        font-size: 32px;
        font-weight: bold;
        color: #2c3e50;
        margin: 0;
        line-height: 1.4;
      }
    }

    .meta-info {
      display: flex;
      justify-content: center;
      gap: 40px;
      margin-bottom: 30px;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 8px;

      .meta-item {
        color: #666;
        font-size: 14px;
      }
    }

    .video-section {
      width: 100%;
      display: flex;
      justify-content: center;

      .native-video {
        width: 100%;
        height: 500px;
        object-fit: contain;
        border-radius: 8px;
        background: #000;
      }

      .no-video {
        padding: 100px 0;
        color: #999;
        font-size: 16px;
      }
    }
  }
}

// 移动端适配
@media (max-width: 768px) {
  .video-detail-container {
    padding: 10px;

    .detail-card {
      .title-section {
        .video-title {
          font-size: 24px;
        }
      }

      .meta-info {
        flex-direction: column;
        gap: 15px;
        text-align: center;
      }

      .video-section .native-video {
        height: 250px !important;
      }
    }
  }
}
</style>