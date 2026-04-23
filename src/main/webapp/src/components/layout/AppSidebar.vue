<template>
  <div class="app-sidebar" :class="{ 'is-collapse': isCollapse }">
    <div class="sidebar-header">
      <span class="logo">📋</span>
      <span v-show="!isCollapse" class="title">VILGO</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :router="true"
      class="sidebar-menu"
    >

      <el-sub-menu index="resident">
        <template #title>
          <el-icon><User /></el-icon>
          <span>村民管理</span>
        </template>
        <el-menu-item index="/resident">人员列表</el-menu-item>
        <el-menu-item index="/resident/statistics">数据统计</el-menu-item>
      </el-sub-menu>

      <!-- 户籍档案 -->
      <el-sub-menu index="household">
        <template #title>
          <el-icon><House /></el-icon>
          <span>户籍档案</span>
        </template>
        <el-menu-item index="/household">家庭户</el-menu-item>
        <el-menu-item index="/household/change">户籍变动</el-menu-item>
      </el-sub-menu>

      <!-- 特殊人群 -->
      <el-sub-menu index="special-group">
        <template #title>
          <el-icon><UserFilled /></el-icon>
          <span>特殊人群</span>
        </template>
        <el-menu-item index="/special-group">特殊人群管理</el-menu-item>
      </el-sub-menu>

      <!-- 党务管理 -->
      <el-sub-menu index="party-work">
        <template #title>
          <el-icon><Star /></el-icon>
          <span>党务管理</span>
        </template>
        <el-menu-item index="/party-work">党务管理</el-menu-item>
      </el-sub-menu>

      <!-- 村委公示栏 -->
      <el-sub-menu index="notice">
        <template #title>
          <el-icon><Bell /></el-icon>
          <span>村委公示栏</span>
        </template>
        <el-menu-item index="/notice">村委公示栏</el-menu-item>
      </el-sub-menu>

      <!-- 新闻动态 -->
      <el-sub-menu index="news">
        <template #title>
          <el-icon><Document /></el-icon>
          <span>新闻动态</span>
        </template>
        <el-menu-item index="/news">新闻动态</el-menu-item>
      </el-sub-menu>

      <!-- 数字大屏 -->
      <el-sub-menu index="screen">
        <template #title>
          <el-icon><DataBoard /></el-icon>
          <span>数字大屏</span>
        </template>
        <el-menu-item index="/screen">数字大屏</el-menu-item>
      </el-sub-menu>
    </el-menu>

    <div class="sidebar-footer">
      <div class="user-info">
        <el-icon><User /></el-icon>
        <span v-show="!isCollapse">操作员</span>
      </div>
    </div>

    <div class="sidebar-toggle" @click="toggleCollapse">
      <el-icon v-if="isCollapse"><DArrowRight /></el-icon>
      <el-icon v-else><DArrowLeft /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { DArrowLeft, DArrowRight, User, House, UserFilled, Avatar, TrendCharts, Star, Bell, Document, DataBoard } from '@element-plus/icons-vue'

const route = useRoute()
const STORAGE_KEY = 'sidebar_collapse'

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
  localStorage.setItem(STORAGE_KEY, isCollapse.value ? '1' : '0')
}

const handleLogout = () => {
  console.log('logout')
}

onMounted(() => {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (saved === '1') {
    isCollapse.value = true
  }
})
</script>

<style>
.app-sidebar .el-submenu {
  overflow: visible;
}

.app-sidebar .el-submenu__title {
  display: flex;
  align-items: center;
  height: 48px;
  line-height: 48px;
  padding: 0 16px !important;
  margin: 2px 8px;
  border-radius: 6px;
  transition: all 0.3s;
  color: #333;
  font-weight: 500;
  background: transparent !important;
  float: none;
}

.app-sidebar .el-submenu__title > .el-submenu__icon-arrow {
  position: absolute;
  right: 10px;
}

.app-sidebar .el-submenu__title .el-icon {
  margin-right: 8px;
  font-size: 18px;
  display: flex;
  align-items: center;
}

.app-sidebar .el-submenu__title:hover {
  background: #e8f4ff;
  color: #1890ff;
}

.app-sidebar .el-submenu.is-opened > .el-submenu__title {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: #fff;
}

.app-sidebar .el-menu--inline {
  margin: 0;
  padding: 4px 0;
  background: transparent;
}

.app-sidebar .el-menu--inline .el-menu-item {
  height: 42px;
  line-height: 42px;
  padding: 0 16px 0 48px;
  margin: 2px 8px;
  border-radius: 6px;
  transition: all 0.3s;
  color: #666;
  font-size: 13px;
  background: transparent;
  border-left: 3px solid transparent;
}

.app-sidebar .el-menu--inline .el-menu-item:hover {
  background: #e8f4ff;
  color: #1890ff;
  border-left-color: #1890ff;
}

.app-sidebar .el-menu--inline .el-menu-item.is-active {
  background: linear-gradient(90deg, #1890ff 0%, #40a9ff 100%);
  color: #fff;
  font-weight: 500;
  border-left-color: #1890ff;
}

.app-sidebar .el-menu-item {
  display: flex;
  align-items: center;
  height: 48px;
  line-height: 48px;
  padding: 0 16px;
  margin: 2px 8px;
  border-radius: 6px;
  color: #333;
  font-weight: 500;
  background: transparent;
}

.app-sidebar .el-menu-item:hover {
  background: #e8f4ff;
  color: #1890ff;
}

.app-sidebar .el-menu-item.is-active {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: #fff;
}
</style>

<style scoped>
.app-sidebar {
  width: 200px;
  height: 100vh;
  background: #fafafa;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
}

.app-sidebar.is-collapse {
  width: 64px;
}

.app-sidebar :deep(.el-menu) {
  border-right: none;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  background: transparent;
}

.app-sidebar :deep(.el-menu-item),
.app-sidebar :deep(.el-submenu) {
  display: block;
  width: 100%;
}

.app-sidebar :deep(.el-menu::-webkit-scrollbar) {
  width: 4px;
}

.app-sidebar :deep(.el-menu::-webkit-scrollbar-thumb) {
  background: #d0d0d0;
  border-radius: 2px;
}

.sidebar-header {
  height: 60px;
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 10px;
  flex-shrink: 0;
}

.sidebar-header .logo {
  font-size: 24px;
  flex-shrink: 0;
}

.sidebar-header .title {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
}

.sidebar-footer {
  height: 70px;
  border-top: 1px solid #e8e8e8;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: #fff;
}

.sidebar-footer .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 13px;
}

.sidebar-toggle {
  position: absolute;
  bottom: 80px;
  right: -12px;
  z-index: 10;
  width: 24px;
  height: 24px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  font-size: 12px;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.sidebar-toggle:hover {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}
</style>