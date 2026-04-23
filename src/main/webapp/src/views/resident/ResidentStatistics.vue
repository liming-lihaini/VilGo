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

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #67c23a">
            <el-icon><Location /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.localResidentCount || 0 }}</div>
            <div class="stat-label">常住人口数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #e6a23c">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.studentCount || 0 }}</div>
            <div class="stat-label">就读学生数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #f56c6c">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.allowanceCount || 0 }}</div>
            <div class="stat-label">低保人数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #909399">
            <el-icon><Medal /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.fiveGuaranteeCount || 0 }}</div>
            <div class="stat-label">五保户人数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #9b59b6">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.nonLocalHouseholdCount || 0 }}</div>
            <div class="stat-label">非本地户籍人数</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 统计图表 -->
    <div class="statistics-charts">
      <el-row :gutter="20">
        <!-- 户籍状态分布 - 环形图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>户籍状态分布</span>
              </div>
            </template>
            <div ref="householdChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 人员类型分布 - 饼图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>人员类型分布</span>
              </div>
            </template>
            <div ref="personTypeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 保障类型分布 - 柱状图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>保障类型分布</span>
              </div>
            </template>
            <div ref="securityChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 村组分布 - 柱状图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>村组分布</span>
              </div>
            </template>
            <div ref="villageChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { User, Location, Reading, Wallet, Medal, OfficeBuilding } from '@element-plus/icons-vue'
import { residentApi } from '@/request/resident'
import * as echarts from 'echarts'

// 统计数据
const statistics = reactive({
  totalCount: 0,
  localResidentCount: 0,
  studentCount: 0,
  allowanceCount: 0,
  fiveGuaranteeCount: 0,
  nonLocalHouseholdCount: 0,
  householdStatusCount: {},
  personTypeCount: {},
  securityTypeCount: {},
  villageCount: {}
})

// 图表引用
const householdChartRef = ref(null)
const personTypeChartRef = ref(null)
const securityChartRef = ref(null)
const villageChartRef = ref(null)

let householdChart = null
let personTypeChart = null
let securityChart = null
let villageChart = null

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
  pension: '社会养老',
  worker_pension: '职工养老',
  allowance: '低保',
  five_guarantee: '五保',
  other: '其他',
  none: '无'
}

// 获取户籍状态名称
const getHouseholdStatusName = (key) => householdStatusMap[key] || key

// 获取人员类型名称
const getPersonTypeName = (key) => personTypeMap[key] || key

// 获取保障类型名称
const getSecurityTypeName = (key) => securityTypeMap[key] || key

// 颜色配置
const chartColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#9b59b6', '#00bcd4', '#ff9800']

// 初始化户籍状态图表 - 环形图
const initHouseholdChart = () => {
  if (!householdChartRef.value) return

  householdChart = echarts.init(householdChartRef.value)
  const data = Object.entries(statistics.householdStatusCount).map(([key, value]) => ({
    name: getHouseholdStatusName(key),
    value: value
  }))

  const option = {
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%', left: 'center' },
    series: [
      {
        name: '户籍状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        data: data
      }
    ]
  }
  householdChart.setOption(option)
}

// 初始化人员类型图表 - 饼图
const initPersonTypeChart = () => {
  if (!personTypeChartRef.value) return

  personTypeChart = echarts.init(personTypeChartRef.value)
  const data = Object.entries(statistics.personTypeCount).map(([key, value]) => ({
    name: getPersonTypeName(key),
    value: value
  }))

  const option = {
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%', left: 'center' },
    series: [
      {
        name: '人员类型',
        type: 'pie',
        radius: '65%',
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        data: data
      }
    ]
  }
  personTypeChart.setOption(option)
}

// 初始化保障类型图表 - 柱状图
const initSecurityChart = () => {
  if (!securityChartRef.value) return

  securityChart = echarts.init(securityChartRef.value)
  const data = Object.entries(statistics.securityTypeCount).map(([key, value]) => ({
    name: getSecurityTypeName(key),
    value: value
  }))

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: { interval: 0, rotate: 30 }
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '人数',
        type: 'bar',
        data: data.map((item, index) => ({
          value: item.value,
          itemStyle: { color: chartColors[index % chartColors.length] }
        })),
        barWidth: '50%',
        itemStyle: { borderRadius: [4, 4, 0, 0] }
      }
    ]
  }
  securityChart.setOption(option)
}

// 初始化村组图表 - 柱状图
const initVillageChart = () => {
  if (!villageChartRef.value) return

  villageChart = echarts.init(villageChartRef.value)
  const data = Object.entries(statistics.villageCount)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map(item => item[0] || '未知'),
      axisLabel: { interval: 0, rotate: 30 }
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '人数',
        type: 'bar',
        data: data.map((item, index) => ({
          value: item[1],
          itemStyle: { color: chartColors[index % chartColors.length] }
        })),
        barWidth: '50%',
        itemStyle: { borderRadius: [4, 4, 0, 0] }
      }
    ]
  }
  villageChart.setOption(option)
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const result = await residentApi.statistics()
    statistics.totalCount = result.totalCount || 0
    statistics.localResidentCount = result.localResidentCount || 0
    statistics.studentCount = result.studentCount || 0
    statistics.allowanceCount = result.allowanceCount || 0
    statistics.fiveGuaranteeCount = result.fiveGuaranteeCount || 0
    statistics.nonLocalHouseholdCount = result.nonLocalHouseholdCount || 0
    statistics.householdStatusCount = result.householdStatusCount || {}
    statistics.personTypeCount = result.personTypeCount || {}
    statistics.securityTypeCount = result.securityTypeCount || {}
    statistics.villageCount = result.villageCount || {}

    // 等待DOM更新后初始化图表
    await nextTick()
    initHouseholdChart()
    initPersonTypeChart()
    initSecurityChart()
    initVillageChart()
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 窗口大小变化时重新调整图表
const handleResize = () => {
  householdChart?.resize()
  personTypeChart?.resize()
  securityChart?.resize()
  villageChart?.resize()
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.resident-statistics {
  padding: 20px;
}

.statistics-cards {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.stat-card {
  width: 220px;
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

.chart-container {
  width: 100%;
  height: 350px;
}
</style>