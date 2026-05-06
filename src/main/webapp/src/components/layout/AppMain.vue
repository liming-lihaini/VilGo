<template>
  <div class="app-main">
    <!-- 顶部面包屑导航 -->
    <div class="app-breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentRoute.name">{{ currentRoute.meta.title || currentRoute.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

const currentRoute = route
</script>

<style scoped>
.app-main {
  flex: 1;
  background: #f5f7fa;
  padding: 20px;
  overflow: auto;
  height: 100vh;
  min-width: 0;
}

.app-breadcrumb {
  margin-bottom: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>