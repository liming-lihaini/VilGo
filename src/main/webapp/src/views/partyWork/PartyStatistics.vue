<template>
  <div class="party-statistics">
    <el-row :gutter="20">
      <!-- 党员统计卡片 -->
      <el-col :span="12">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>党员统计</span>
            </div>
          </template>
          <div v-loading="memberLoading" class="stat-content">
            <div class="stat-item">
              <div class="stat-label">党员总数</div>
              <div class="stat-value">{{ memberStats.totalCount || 0 }}</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item-group">
              <div class="stat-item" v-for="(count, status) in memberStats.statusCount" :key="status">
                <div class="stat-label">{{ getStatusName(status) }}</div>
                <div class="stat-value">{{ count || 0 }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 党费收缴统计卡片 -->
      <el-col :span="12">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>党费收缴统计</span>
            </div>
          </template>
          <div v-loading="duesLoading" class="stat-content">
            <div class="stat-item">
              <div class="stat-label">已缴总额</div>
              <div class="stat-value">￥{{ (duesStats.paidTotal || 0).toFixed(2) }}</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-label">欠缴笔数</div>
              <div class="stat-value danger">{{ duesStats.unpaidCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 按月份收缴趋势 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>按月收缴趋势</span>
            </div>
          </template>
          <div class="monthly-chart">
            <el-table :data="monthlyData" stripe border style="width: 100%">
              <el-table-column prop="month" label="月份" width="150" />
              <el-table-column prop="amount" label="收缴金额">
                <template #default="{ row }">
                  ￥{{ (row.amount || 0).toFixed(2) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { partyWorkApi } from '@/request/partyWork'

// 数据
const memberLoading = ref(false)
const duesLoading = ref(false)
const memberStats = ref({ totalCount: 0, statusCount: {} })
const duesStats = ref({ paidTotal: 0, unpaidCount: 0, monthlyCount: {} })
const monthlyData = ref([])

// 生命周期
onMounted(() => {
  loadStatistics()
})

// 方法
const loadStatistics = async () => {
  memberLoading.value = true
  duesLoading.value = true

  try {
    // 加载党员统计
    const memberRes = await partyWorkApi.partyMemberStatistics()
    if (memberRes.code === 0) {
      memberStats.value = memberRes.data || {}
    }
  } catch (error) {
    console.error('加载党员统计失败', error)
  } finally {
    memberLoading.value = false
  }

  try {
    // 加载党费统计
    const duesRes = await partyWorkApi.partyDuesStatistics()
    if (duesRes.code === 0) {
      duesStats.value = duesRes.data || {}

      // 转换月度数据
      const monthlyCount = duesStats.value.monthlyCount || {}
      monthlyData.value = Object.entries(monthlyCount).map(([month, amount]) => ({
        month,
        amount
      })).sort((a, b) => b.month.localeCompare(a.month))
    }
  } catch (error) {
    console.error('加载党费统计失败', error)
  } finally {
    duesLoading.value = false
  }
}

// 工具方法
const getStatusName = (status) => {
  const map = {
    activist: '积极分子',
    developing: '发展对象',
    probationary: '预备党员',
    formal: '正式党员'
  }
  return map[status] || status
}
</script>

<style scoped>
.party-statistics {
  padding: 20px;
}

.stat-card {
  min-height: 200px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}

.stat-content {
  min-height: 150px;
}

.stat-item {
  text-align: center;
  padding: 10px 0;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-value.danger {
  color: #f56c6c;
}

.stat-divider {
  height: 1px;
  background: #ebeef5;
  margin: 15px 0;
}

.stat-item-group {
  display: flex;
  justify-content: space-around;
}

.stat-item-group .stat-item {
  flex: 1;
  text-align: center;
}

.monthly-chart {
  max-height: 300px;
  overflow-y: auto;
}
</style>