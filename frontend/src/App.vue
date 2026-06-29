<script setup lang="ts">
import { onMounted } from "vue";
import { loadPermissions } from "./utils/system";
import useUserStore from "./stores/userStore";

onMounted(async () => {
  const store = useUserStore();
  if (store.token) {
    await loadPermissions();
  }
});
</script>

<template>
  <router-view />
</template>
