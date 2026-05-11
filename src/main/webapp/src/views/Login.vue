<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo-wrapper">
        <img src="@/assets/images/home.jpeg" alt="logo" class="logo" />
      </div>
      <div class="title">村级事务管理系统</div>
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-button" size="large" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      const result = userStore.login(form.username, form.password)
      if (result.success) {
        ElMessage.success('登录成功')
        // 登录成功后跳转到缓存的路由或默认页面
        const cachedRoute = sessionStorage.getItem('village_last_route')
        if (cachedRoute && cachedRoute !== '/') {
          router.replace(cachedRoute)
        } else {
          router.replace('/resident')
        }
      } else {
        ElMessage.error(result.message)
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.login-box {
  width: 380px;
  padding: 40px 50px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.logo-wrapper {
  text-align: center;
  margin-bottom: 16px;
}

.logo {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 32px;
  letter-spacing: 2px;
}

.login-form {
  margin-top: 10px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #dcdfe6;
  padding: 4px 12px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #409eff;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.login-button {
  width: 100%;
  font-size: 16px;
  border-radius: 8px;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border: none;
}

.login-button:hover {
  background: linear-gradient(135deg, #16213e 0%, #1a1a2e 100%);
}
</style>