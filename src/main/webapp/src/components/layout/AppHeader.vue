<template>
  <div class="app-header">
    <div class="header-left">
      <span class="logo">📋</span>
      <span class="title">村庄事务管理系统</span>
      <span class="version">V1.0</span>
    </div>
    <div class="header-right">
      <span class="user-name">操作员</span>
      <el-button type="danger" size="small" plain @click="handleLogout">退出</el-button>
    </div>
  </div>
</template>

<script setup>
const handleLogout = () => {
  // TODO: 退出登录逻辑
  console.log('logout')
}
</script>

<style scoped>
.app-header {
  height: 60px;
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  font-size: 28px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
}

.version {
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  font-size: 14px;
}
</style>