<template>
  <div class="resident-statistics">
    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #409eff">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
            <div class="stat-label">总人口数</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 统计图表 -->
    <div class="statistics-charts">
      <el-row :gutter="20">
        <!-- 按户籍状态 -->
        <el-col :span="6">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>户籍状态分布</span>
              </div>
            </template>
            <div class="chart-content">
              <div v-if="statistics.householdStatusCount" class="stat-list">
                <div
                  v-for="(value, key) in statistics.householdStatusCount"
                  :key="key"
                  class="stat-item"
                >
                  <span class="stat-name">{{ getHouseholdStatusName(key) }}</span>
                  <span class="stat-num">{{ value }}</span>
                </div>
              </div>
              <el-empty v-else description="暂无数据" />
            </div>
          </el-card>
        </el-col>

        <!-- 按人员类型 -->
        <el-col :span="6">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>人员类型分布</span>
              </div>
            </template>
            <div class="chart-content">
              <div v-if="statistics.personTypeCount" class="stat-list">
                <div
                  v-for="(value, key) in statistics.personTypeCount"
                  :key="key"
                  class="stat-item"
                >
                  <span class="stat-name">{{ getPersonTypeName(key) }}</span>
                  <span class="stat-num">{{ value }}</span>
                </div>
              </div>
              <el-empty v-else description="暂无数据" />
            </div>
          </el-card>
        </el-col>

        <!-- 按保障类型 -->
        <el-col :span="6">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>保障类型分布</span>
              </div>
            </template>
            <div class="chart-content">
              <div v-if="statistics.securityTypeCount" class="stat-list">
                <div
                  v-for="(value, key) in statistics.securityTypeCount"
                  :key="key"
                  class="stat-item"
                >
                  <span class="stat-name">{{ getSecurityTypeName(key) }}</span>
                  <span class="stat-num">{{ value }}</span>
                </div>
              </div>
              <el-empty v-else description="暂无数据" />
            </div>
          </el-card>
        </el-col>

        <!-- 按村组 -->
        <el-col :span="6">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>村组分布</span>
              </div>
            </template>
            <div class="chart-content">
              <div v-if="statistics.villageCount" class="stat-list">
                <div
                  v-for="(value, key) in statistics.villageCount"
                  :key="key"
                  class="stat-item"
                >
                  <span class="stat-name">{{ key }}</span>
                  <span class="stat-num">{{ value }}</span>
                </div>
              </div>
              <el-empty v-else description="暂无数据" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User } from '@element-plus/icons-vue'
import { residentApi } from '@/request/resident'

// 统计数据
const statistics = reactive({
  totalCount: 0,
  householdStatusCount: {},
  personTypeCount: {},
  securityTypeCount: {},
  villageCount: {}
})

// 户籍状态映射
const householdStatusMap = {
  normal: '正常',
  cancelled: '已销户'
}

// 人员类型映射
const personTypeMap = {
  farmer: '农民',
  worker: '工人',
  teacher: '教师',
  doctor: '医生',
  student: '学生',
  other: '其他'
}

// 保障类型映射
const securityTypeMap = {
  pension: '养老保险',
  insurance: '医疗保险',
  allowance: '低保',
  none: '未参保'
}

// 获取户籍状态名称
const getHouseholdStatusName = (key) => householdStatusMap[key] || key

// 获取人员类型名称
const getPersonTypeName = (key) => personTypeMap[key] || key

// 获取保障类型名称
const getSecurityTypeName = (key) => securityTypeMap[key] || key

// 加载统计数据
const loadStatistics = async () => {
  try {
    const result = await residentApi.statistics()
    statistics.totalCount = result.totalCount || 0
    statistics.householdStatusCount = result.householdStatusCount || {}
    statistics.personTypeCount = result.personTypeCount || {}
    statistics.securityTypeCount = result.securityTypeCount || {}
    statistics.villageCount = result.villageCount || {}
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.resident-statistics {
  padding: 20px;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  width: 240px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.statistics-charts {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 500;
}

.chart-content {
  min-height: 200px;
}

.stat-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.stat-name {
  color: #666;
}

.stat-num {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}
</style>