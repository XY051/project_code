<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  training: {
    type: Object,
    required: true
  },
  size: {
    type: String,
    default: 'default' // 'large' | 'small' | 'default'
  }
})

const router = useRouter()

const goToDetail = () => {
  if (props.training.id) {
    router.push({
      path: '/front/activityDetail',
      query: { id: props.training.id }
    })
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<template>
  <div 
    class="training-card" 
    :class="[`size-${size}`]" 
    @click="goToDetail"
  >
    <div class="training-image-wrapper">
      <img 
        :src="training.coverImage || training.coverUrl || ''" 
        :alt="training.name || training.title" 
        class="training-image"
        @error="e => { e.target.src = ''; e.target.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }"
      />
      <div class="training-badge" v-if="training.status">
        {{ training.status === 'active' || training.status === 1 ? '进行中' : '已结束' }}
      </div>
    </div>
    <div class="training-info">
      <h3 class="training-name">{{ training.name || training.title }}</h3>
      <p class="training-desc" v-if="size === 'large' && (training.description || training.intro)">
        {{ training.description || training.intro }}
      </p>
      <div class="training-meta">
        <span class="training-date" v-if="training.startTime || training.createTime">
          📅 {{ formatDate(training.startTime || training.createTime) }}
        </span>
        <span class="training-count" v-if="training.applyCount !== undefined">
          👥 {{ training.applyCount }}人参与
        </span>
        <span class="training-count" v-if="training.viewCount !== undefined">
          👁 {{ training.viewCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.training-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.06);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  .training-image-wrapper {
    position: relative;
    width: 100%;
    overflow: hidden;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

    .training-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .training-badge {
      position: absolute;
      top: 12px;
      right: 12px;
      background: linear-gradient(135deg, #409eff, #337ecc);
      color: #fff;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
    }
  }

  .training-info {
    padding: 16px 20px;

    .training-name {
      font-size: 16px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0 0 8px 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .training-desc {
      font-size: 13px;
      color: #7f8c8d;
      line-height: 1.5;
      margin: 0 0 12px 0;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .training-meta {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
      font-size: 13px;
      color: #95a5a6;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  // Large size
  &.size-large {
    .training-image-wrapper {
      height: 240px;
    }
  }

  // Small size
  &.size-small {
    display: flex;
    
    .training-image-wrapper {
      width: 100px;
      min-width: 100px;
      height: 100px;
    }

    .training-info {
      flex: 1;
      padding: 12px 16px;
      
      .training-name {
        font-size: 15px;
      }

      .training-meta {
        gap: 8px;
      }
    }
  }
}

@media (max-width: 768px) {
  .training-card.size-large {
    .training-image-wrapper {
      height: 180px;
    }
  }
}
</style>