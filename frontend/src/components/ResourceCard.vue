<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  resource: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const goToDetail = () => {
  if (props.resource.id) {
    router.push({
      path: '/front/resourceDetail',
      query: { id: props.resource.id }
    })
  }
}
</script>

<template>
  <div class="resource-card" @click="goToDetail">
    <div class="resource-image-wrapper">
      <img
        :src="resource.coverImage || resource.coverUrl || ''"
        :alt="resource.name || resource.title"
        class="resource-image"
        @error="e => e.target.style.display = 'none'"
      />
      <div class="resource-overlay">
        <span class="view-btn">鏌ョ湅璇︽儏</span>
      </div>
    </div>
    <div class="resource-info">
      <h3 class="resource-name">{{ resource.name || resource.title }}</h3>
      <p class="resource-category" v-if="resource.categoryName">{{ resource.categoryName }}</p>
      <div class="resource-meta">
        <span class="view-count" v-if="resource.viewCount !== undefined">
          馃憗 {{ resource.viewCount }}
        </span>
        <span class="collect-count" v-if="resource.collectCount !== undefined">
          鉂?{{ resource.collectCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.resource-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.06);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);

    .resource-overlay {
      opacity: 1;
    }
  }

  .resource-image-wrapper {
    position: relative;
    width: 100%;
    height: 220px;
    overflow: hidden;
    background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);

    .resource-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .resource-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s ease;

      .view-btn {
        color: #fff;
        padding: 8px 24px;
        border: 2px solid #fff;
        border-radius: 25px;
        font-size: 14px;
        font-weight: 600;
        transition: all 0.3s;
      }
    }
  }

  .resource-info {
    padding: 16px 20px;

    .resource-name {
      font-size: 16px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0 0 8px 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .resource-category {
      font-size: 13px;
      color: #7f8c8d;
      margin: 0 0 12px 0;
    }

    .resource-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      color: #95a5a6;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

@media (max-width: 768px) {
  .resource-card {
    .resource-image-wrapper {
      height: 180px;
    }
  }
}
</style>
