<template>
  <div class="party-activity-list">
    <!-- 查询条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="活动主题" style="width: 200px">
          <el-input v-model="queryForm.theme" placeholder="请输入活动主题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="活动类型" style="width: 150px">
          <el-select v-model="queryForm.activityType" placeholder="请选择" clearable>
            <el-option label="主题党日" value="theme_day" />
            <el-option label="组织生活会" value="meeting" />
            <el-option label="学习培训" value="study" />
            <el-option label="志愿服务" value="volunteer" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增活动</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      style="width: 100%"
    >
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="theme" label="活动主题" min-width="150" show-overflow-tooltip />
      <el-table-column prop="activityType" label="活动类型" width="120">
        <template #default="{ row }">
          {{ getActivityTypeName(row.activityType) }}
        </template>
      </el-table-column>
      <el-table-column prop="activityTime" label="活动时间" width="160" />
      <el-table-column prop="location" label="活动地点" width="150" show-overflow-tooltip />
      <el-table-column prop="content" label="活动内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="creator" label="创建人" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>

    <!-- 新增/编辑抽屉 -->
    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="1000px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="党员" prop="memberId">
              <el-select v-model="formData.memberId" placeholder="请输入姓名搜索" style="width: 100%" filterable clearable remote default-first-option :remote-method="searchMember" :loading="memberLoading" @focus="loadMembers">
                <el-option v-for="member in memberList" :key="member.id" :label="member.name" :value="member.id">
                  <span>{{ member.name }}</span>
                  <span style="color: #999; font-size: 12px; margin-left: 8px;">{{ member.idCard?.slice(-4) }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动主题" prop="theme">
              <el-input v-model="formData.theme" placeholder="请输入活动主题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动类型" prop="activityType">
              <el-select v-model="formData.activityType" placeholder="请选择" style="width: 100%">
                <el-option label="主题党日" value="theme_day" />
                <el-option label="组织生活会" value="meeting" />
                <el-option label="学习培训" value="study" />
                <el-option label="志愿服务" value="volunteer" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动时间" prop="activityTime">
              <el-date-picker v-model="formData.activityTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动地点" prop="location">
              <el-input v-model="formData.location" placeholder="请输入活动地点" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参与人员" prop="participation">
              <el-input v-model="formData.participation" type="textarea" :rows="2" placeholder="请输入参与人员，多个用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动内容" prop="content">
              <WangEditor v-model="formData.content" placeholder="请输入活动内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { partyWorkApi } from '@/request/partyWork'
import WangEditor from '@/components/WangEditor.vue'

// 查询表单
const queryForm = reactive({
  theme: '',
  activityType: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const memberList = ref([])
const memberLoading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 抽屉
const drawerVisible = ref(false)
const drawerTitle = ref('')
const formRef = ref(null)
const formData = reactive({
  id: null,
  memberId: null,
  theme: '',
  activityType: '',
  activityTime: '',
  location: '',
  participation: '',
  content: ''
})

// 表单校验
const formRules = {
  memberId: [{ required: true, message: '请选择党员', trigger: 'change' }],
  theme: [{ required: true, message: '请输入活动主题', trigger: 'blur' }]
}

// 生命周期
onMounted(() => {
  loadMembers()
  handleQuery()
})

// 方法
const loadMembers = async () => {
  try {
    const data = await partyWorkApi.listMembers({ pageSize: 1000 })
    memberList.value = data?.list || []
  } catch (error) {
    console.error('加载党员列表失败', error)
  }
}

const searchMember = async (keyword) => {
  if (!keyword) {
    if (memberList.value.length === 0) loadMembers()
    return
  }
  memberLoading.value = true
  try {
    const data = await partyWorkApi.listMembers({ name: keyword, pageSize: 20 })
    memberList.value = data?.list || []
  } catch (error) {
    console.error('搜索党员失败', error)
  } finally {
    memberLoading.value = false
  }
}

const handleQuery = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const data = await partyWorkApi.listActivities(params)
    tableData.value = data?.list || data || []
    pagination.total = data?.total || 0
  } catch (error) {
    console.error('查询失败', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.theme = ''
  queryForm.activityType = ''
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  drawerTitle.value = '新增活动'
  drawerVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(formData, row)
  drawerTitle.value = '编辑活动'
  drawerVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (formData.id) {
      await partyWorkApi.updateActivity(formData)
      ElMessage.success('更新成功')
    } else {
      await partyWorkApi.createActivity(formData)
      ElMessage.success('新增成功')
    }
    drawerVisible.value = false
    handleQuery()
  } catch (error) {
    console.error('操作失败', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该活动记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await partyWorkApi.deleteActivity(row.id)
      ElMessage.success('删除成功')
      handleQuery()
    } catch (error) {
      console.error('删除失败', error)
    }
  })
}

const resetForm = () => {
  formData.id = null
  formData.memberId = null
  formData.theme = ''
  formData.activityType = ''
  formData.activityTime = ''
  formData.location = ''
  formData.participation = ''
  formData.content = ''
}

// 工具方法
const getActivityTypeName = (type) => {
  const map = {
    theme_day: '主题党日',
    meeting: '组织生活会',
    study: '学习培训',
    volunteer: '志愿服务',
    other: '其他'
  }
  return map[type] || type
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.slice(0, 19)
}
</script>

<style scoped>
.party-activity-list {
  padding: 20px;
}

.filter-bar {
  margin-bottom: 15px;
}

.toolbar {
  margin-bottom: 15px;
}

.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>