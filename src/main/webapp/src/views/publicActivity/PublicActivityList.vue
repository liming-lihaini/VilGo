<template>
  <div class="public-activity-list">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 活动管理 -->
      <el-tab-pane label="活动管理" name="activity">
        <div class="filter-bar">
          <el-form :inline="true" :model="activityQuery" class="query-form">
            <el-form-item label="活动名称" style="width: 150px">
              <el-input v-model="activityQuery.name" placeholder="活动名称" clearable @keyup.enter="loadActivities" />
            </el-form-item>
            <el-form-item label="活动类型" style="width: 150px">
              <el-select v-model="activityQuery.activityType" placeholder="请选择" clearable @change="loadActivities">
                <el-option label="志愿服务" value="志愿服务" />
                <el-option label="公益宣讲" value="公益宣讲" />
                <el-option label="环境整治" value="环境整治" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item label="活动状态" style="width: 150px">
              <el-select v-model="activityQuery.status" placeholder="请选择" clearable @change="loadActivities">
                <el-option label="未开始" value="pending" />
                <el-option label="进行中" value="ongoing" />
                <el-option label="已归档" value="archived" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadActivities">查询</el-button>
              <el-button @click="resetActivityQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="toolbar">
          <el-button type="primary" @click="handleAddActivity">发起活动</el-button>
        </div>

        <el-table v-loading="activityLoading" :data="activityList" stripe border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="name" label="活动名称" min-width="150" />
          <el-table-column prop="activityType" label="活动类型" width="100" align="center">
            <template #default="{ row }">{{ row.activityType || '-' }}</template>
          </el-table-column>
          <el-table-column label="活动时间" width="200">
            <template #default="{ row }">{{ row.startTime }} ~ {{ row.endTime }}</template>
          </el-table-column>
          <el-table-column prop="location" label="活动地点" width="120" />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)" size="small">{{ getStatusName(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditActivity(row)">编辑</el-button>
              <el-button link type="success" size="small" @click="handleSignupManage(row)">报名管理</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteActivity(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="activityQuery.pageNum"
            v-model:page-size="activityQuery.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="activityTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadActivities"
            @current-change="loadActivities"
          />
        </div>
      </el-tab-pane>

      <!-- 活动统计 -->
      <el-tab-pane label="活动统计" name="statistics">
        <div class="filter-bar">
          <el-form :inline="true" :model="statsQuery" class="query-form">
            <el-form-item label="时间范围" style="width: 300px">
              <el-date-picker v-model="statsQuery.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" @change="loadStatistics" />
            </el-form-item>
            <el-form-item label="活动类型" style="width: 150px">
              <el-select v-model="statsQuery.activityType" placeholder="请选择" clearable @change="loadStatistics">
                <el-option label="志愿服务" value="志愿服务" />
                <el-option label="公益宣讲" value="公益宣讲" />
                <el-option label="环境整治" value="环境整治" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadStatistics">查询</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-descriptions :column="4" border class="stats-cards">
          <el-descriptions-item label="活动总数">{{ statistics.totalActivities || 0 }}</el-descriptions-item>
          <el-descriptions-item label="报名人数">{{ statistics.totalSignups || 0 }}</el-descriptions-item>
          <el-descriptions-item label="累计工时">{{ statistics.totalWorkHours || 0 }} 小时</el-descriptions-item>
          <el-descriptions-item label="累计工值{{ statistics.totalWorkValue || 0 }} 元</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
    </el-tabs>

    <!-- 活动表单弹窗 -->
    <el-dialog v-model="activityDialogVisible" :title="activityDialogTitle" width="600px" destroy-on-close>
      <el-form ref="activityFormRef" :model="activityForm" :rules="activityRules" label-width="100px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="activityForm.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="activityForm.activityType" placeholder="请选择" style="width: 100%">
            <el-option label="志愿服务" value="志愿服务" />
            <el-option label="公益宣讲" value="公益宣讲" />
            <el-option label="环境整治" value="环境整治" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="activityForm.startTime" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="activityForm.endTime" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="activityForm.content" type="textarea" :rows="3" placeholder="请输入活动内容" />
        </el-form-item>
        <el-form-item label="报名要求">
          <el-input v-model="activityForm.requirement" type="textarea" :rows="2" placeholder="请输入报名要求" />
        </el-form-item>
        <el-form-item label="活动状态" v-if="activityForm.id">
          <el-select v-model="activityForm.status" placeholder="请选择" style="width: 100%">
            <el-option label="未开始" value="pending" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动总结" v-if="activityForm.status === 'archived'">
          <el-input v-model="activityForm.summary" type="textarea" :rows="3" placeholder="请输入活动总结" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="activityDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitActivity">确定</el-button>
      </template>
    </el-dialog>

    <!-- 报名管理弹窗 -->
    <el-dialog v-model="signupDialogVisible" :title="signupDialogTitle" width="900px" destroy-on-close>
      <div class="toolbar" style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAddSignup">添加报名</el-button>
      </div>
      <el-table v-loading="signupLoading" :data="signupList" stripe border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="residentName" label="村民姓名" width="100" align="center" />
        <el-table-column prop="idCard" label="身份证号" width="170" />
        <el-table-column prop="status" label="报名状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getSignupStatusTag(row.status)" size="small">{{ getSignupStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signInTime" label="签到时间" width="170" />
        <el-table-column prop="workHours" label="工时" width="80" align="center" />
        <el-table-column prop="workValue" label="工值" width="80" align="center" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" size="small" @click="handleSignIn(row)" v-if="row.status === 'registered'">签到</el-button>
            <el-button link type="warning" size="small" @click="handleEditSignup(row)" v-if="row.status !== 'cancelled'">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleCancelSignup(row)" v-if="row.status === 'registered'">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加报名弹窗 -->
    <el-dialog v-model="addSignupDialogVisible" title="添加报名" width="600px" destroy-on-close>
      <el-form ref="signupFormRef" :model="signupForm" :rules="signupRules" label-width="100px">
        <el-form-item label="选择村民" prop="residentId">
          <div style="display: flex; gap: 8px; width: 100%">
            <el-input v-model="signupForm.residentName" placeholder="请选择村民" readonly style="flex: 1" />
            <el-button type="primary" @click="handleSelectResident">选择村民</el-button>
          </div>
        </el-form-item>
        <el-form-item label="工时">
          <el-input-number v-model="signupForm.workHours" :min="0" :precision="1" />
        </el-form-item>
        <el-form-item label="工值">
          <el-input-number v-model="signupForm.workValue" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="signupForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addSignupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSignup">确定</el-button>
      </template>
    </el-dialog>

    <!-- 选择村民弹窗 -->
    <el-dialog v-model="residentDialogVisible" title="选择村民" width="700px" destroy-on-close>
      <div class="filter-bar" style="margin-bottom: 16px">
        <el-form :inline="true" :model="residentQuery">
          <el-form-item label="身份证号">
            <el-input v-model="residentQuery.idCard" placeholder="身份证号" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="residentQuery.name" placeholder="姓名" clearable style="width: 120px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadResidents">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="residentList" stripe border size="small" max-height="300" @row-click="handleResidentSelect" highlight-current-row>
        <el-table-column type="index" label="序号" width="50" align="center"/>
        <el-table-column prop="name" label="姓名" width="80" />
        <el-table-column prop="idCard" label="身份证号" width="170" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">{{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="address" label="住址" min-width="150" show-overflow-tooltip />
      </el-table>
      <div class="pagination" style="margin-top: 12px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="residentQuery.pageNum"
          v-model:page-size="residentQuery.pageSize"
          :total="residentTotal"
          layout="total, prev, pager, next"
          small
          @current-change="loadResidents"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { publicActivityApi } from '@/request/publicActivity'
import { activitySignupApi } from '@/request/activitySignup'
import { residentApi } from '@/request/resident'

const activeTab = ref('activity')

// 活动管理
const activityLoading = ref(false)
const activityList = ref([])
const activityTotal = ref(0)
const activityQuery = reactive({ name: '', activityType: '', status: '', pageNum: 1, pageSize: 10 })

const activityDialogVisible = ref(false)
const activityDialogTitle = ref('发起活动')
const activityFormRef = ref()
const activityForm = reactive({ id: null, name: '', activityType: '', startTime: '', endTime: '', location: '', content: '', requirement: '', status: 'pending', summary: '' })
const activityRules = { name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }], startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }], endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }] }

// 报名管理
const signupDialogVisible = ref(false)
const signupDialogTitle = ref('报名管理')
const currentActivity = ref({})
const signupLoading = ref(false)
const signupList = ref([])

const addSignupDialogVisible = ref(false)
const signupFormRef = ref()
const signupForm = reactive({ id: null, activityId: null, residentId: null, residentName: '', workHours: 0, workValue: 0, remark: '' })
const signupRules = { residentId: [{ required: true, message: '请选择村民', trigger: 'change' }] }

// 村民选择
const residentDialogVisible = ref(false)
const residentList = ref([])
const residentTotal = ref(0)
const residentQuery = reactive({ idCard: '', name: '', pageNum: 1, pageSize: 10 })

// 统计数据
const statsQuery = reactive({ dateRange: [], activityType: '' })
const statistics = ref({})

onMounted(() => { loadActivities() })

// 活动管理方法
const loadActivities = async () => {
  activityLoading.value = true
  try {
    const res = await publicActivityApi.list(activityQuery)
    activityList.value = res.list || []
    activityTotal.value = res.total || 0
  } catch (e) { ElMessage.error(e.message || '查询失败') }
  finally { activityLoading.value = false }
}

const resetActivityQuery = () => { activityQuery.name = ''; activityQuery.activityType = ''; activityQuery.status = ''; activityQuery.pageNum = 1; loadActivities() }

const handleAddActivity = () => {
  activityDialogTitle.value = '发起活动'
  Object.assign(activityForm, { id: null, name: '', activityType: '', startTime: '', endTime: '', location: '', content: '', requirement: '', status: 'pending', summary: '' })
  activityDialogVisible.value = true
}

const handleEditActivity = (row) => {
  activityDialogTitle.value = '编辑活动'
  Object.assign(activityForm, row)
  activityDialogVisible.value = true
}

const handleSubmitActivity = async () => {
  try {
    await activityFormRef.value.validate()
    if (activityForm.id) { await publicActivityApi.update(activityForm); ElMessage.success('更新成功') }
    else { await publicActivityApi.create(activityForm); ElMessage.success('创建成功') }
    activityDialogVisible.value = false
    loadActivities()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '操作失败') }
}

const handleDeleteActivity = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该活动吗？', '提示', { type: 'warning' })
    await publicActivityApi.delete(row.id)
    ElMessage.success('删除成功')
    loadActivities()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '删除失败') }
}

const handleSignupManage = (row) => {
  currentActivity.value = row
  signupDialogTitle.value = row.name + ' - 报名管理'
  signupDialogVisible.value = true
  loadSignups(row.id)
}

const loadSignups = async (activityId) => {
  signupLoading.value = true
  try {
    const res = await activitySignupApi.list({ activityId })
    signupList.value = res.list || []
  } catch (e) { ElMessage.error(e.message || '查询失败') }
  finally { signupLoading.value = false }
}

const handleAddSignup = () => {
  Object.assign(signupForm, { id: null, activityId: currentActivity.value.id, residentId: null, residentName: '', workHours: 0, workValue: 0, remark: '' })
  addSignupDialogVisible.value = true
}

const handleSelectResident = () => {
  residentQuery.idCard = ''
  residentQuery.name = ''
  residentQuery.pageNum = 1
  residentDialogVisible.value = true
  loadResidents()
}

const loadResidents = async () => {
  try {
    const res = await residentApi.list(residentQuery)
    residentList.value = res.list || []
    residentTotal.value = res.total || 0
  } catch (e) { ElMessage.error(e.message || '加载村民列表失败') }
}

const handleResidentSelect = (row) => {
  signupForm.residentId = row.id
  signupForm.residentName = row.name
  residentDialogVisible.value = false
}

const handleSubmitSignup = async () => {
  try {
    await signupFormRef.value.validate()
    await activitySignupApi.create(signupForm)
    ElMessage.success('报名成功')
    addSignupDialogVisible.value = false
    loadSignups(currentActivity.value.id)
  } catch (e) { ElMessage.error(e.message || '操作失败') }
}

const handleEditSignup = async (row) => {
  Object.assign(signupForm, { ...row, residentName: '' })
  addSignupDialogVisible.value = true
}

const handleSignIn = async (row) => {
  try {
    await activitySignupApi.signIn(row.id)
    ElMessage.success('签到成功')
    loadSignups(currentActivity.value.id)
  } catch (e) { ElMessage.error(e.message || '签到失败') }
}

const handleCancelSignup = async (row) => {
  try {
    await ElMessageBox.confirm('确认取消该报名吗？', '提示', { type: 'warning' })
    await activitySignupApi.cancel(row.id)
    ElMessage.success('取消成功')
    loadSignups(currentActivity.value.id)
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '操作失败') }
}

// 统计方法
const loadStatistics = async () => {
  try {
    const params = { activityType: statsQuery.activityType }
    if (statsQuery.dateRange && statsQuery.dateRange.length === 2) {
      params.startTime = statsQuery.dateRange[0]
      params.endTime = statsQuery.dateRange[1]
    }
    const res = await publicActivityApi.statistics(params)
    statistics.value = res || {}
  } catch (e) { ElMessage.error(e.message || '统计失败') }
}

// 状态转换
const getStatusName = (status) => { const map = { pending: '未开始', ongoing: '进行中', archived: '已归档' }; return map[status] || status }
const getStatusTag = (status) => { const map = { pending: 'info', ongoing: 'warning', archived: 'success' }; return map[status] || '' }
const getSignupStatusName = (status) => { const map = { registered: '已报名', signed_in: '已签到', cancelled: '已取消' }; return map[status] || status }
const getSignupStatusTag = (status) => { const map = { registered: 'primary', signed_in: 'success', cancelled: 'info' }; return map[status] || '' }
</script>

<style scoped>
.public-activity-list { padding: 20px; }
.filter-bar { background: #fff; padding: 20px; border-radius: 4px; margin-bottom: 16px; }
.query-form { display: flex; flex-wrap: wrap; gap: 8px; }
.toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.stats-cards { margin-top: 20px; }
</style>