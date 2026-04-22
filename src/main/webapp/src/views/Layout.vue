<template>
  <div class="app-layout">
    <app-header class="app-layout__header" />
    <div class="app-layout__body">
      <app-sidebar class="app-layout__sidebar" />
      <app-main class="app-layout__main" />
    </div>
    <app-footer class="app-layout__footer" />
  </div>
</template>

<script setup>
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppMain from '@/components/layout/AppMain.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.app-layout__header {
  flex-shrink: 0;
}

.app-layout__body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.app-layout__sidebar {
  flex-shrink: 0;
}

.app-layout__main {
  flex: 1;
  min-width: 0;
}

.app-layout__footer {
  flex-shrink: 0;
}
</style>