<template>
  <div class="special-group-list">
    <div class="filter-bar">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="身份证号" style="width: 200px">
          <el-input v-model="queryForm.idCard" placeholder="身份证号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="姓名" style="width: 150px">
          <el-input v-model="queryForm.name" placeholder="姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="人群类型" style="width: 150px">
          <el-select v-model="queryForm.groupType" placeholder="请选择" clearable>
            <el-option label="脱贫户" value="poverty_alleviation" />
            <el-option label="监测户" value="monitoring" />
            <el-option label="残疾人" value="disabled" />
            <el-option label="孤寡老人" value="elderly" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="帮扶状态" style="width: 150px">
          <el-select v-model="queryForm.helpStatus" placeholder="请选择" clearable>
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增登记</el-button>
      <el-button type="warning" @click="handleStatistics">统计</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" stripe border @row-click="handleRowClick" highlight-current-row style="width: 100%">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="name" label="姓名" width="160" align="center"/>
      <el-table-column prop="idCard" label="身份证号" />
      <el-table-column prop="groupType" label="人群类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getGroupTypeTag(row.groupType)" size="small">{{ getGroupTypeName(row.groupType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="helperName" label="帮扶责任人" width="100" align="center"/>
      <el-table-column prop="helpStatus" label="帮扶状态" width="90" >
        <template #default="{ row }">
          <el-tag :type="row.helpStatus === 'ongoing' ? 'warning' : 'success'" size="small">
            {{ row.helpStatus === 'ongoing' ? '进行中' : '已完成' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="helpTime" label="帮扶时间" width="120" align="center"/>
      <el-table-column prop="phone" label="联系电话" width="120" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="info" size="small" @click.stop="handleRowClick(row)">查看</el-button>
          <el-button link type="primary" size="small" @click.stop="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>

    <el-drawer v-model="drawerVisible" title="特殊人群详情" size="1000px" direction="rtl">
      <div v-if="currentGroup" class="group-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ currentGroup.name }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ currentGroup.idCard }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ currentGroup.gender === 'male' ? '男' : currentGroup.gender === 'female' ? '女' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentGroup.phone }}</el-descriptions-item>
          <el-descriptions-item label="人群类型">
            <el-tag :type="getGroupTypeTag(currentGroup.groupType)" size="small">{{ getGroupTypeName(currentGroup.groupType) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="帮扶状态">
            <el-tag :type="currentGroup.helpStatus === 'ongoing' ? 'warning' : 'success'">{{ currentGroup.helpStatus === 'ongoing' ? '进行中' : '已完成' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="帮扶责任人">{{ currentGroup.helperName }}</el-descriptions-item>
          <el-descriptions-item label="帮扶时间">{{ currentGroup.helpTime }}</el-descriptions-item>
          <el-descriptions-item label="帮扶措施" :span="2">{{ currentGroup.helpContent }}</el-descriptions-item>
          <el-descriptions-item label="帮扶成效" :span="2">{{ currentGroup.helpResult }}</el-descriptions-item>
        </el-descriptions>

        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="帮扶记录" name="records">
            <div class="tab-toolbar">
              <el-button type="primary" size="small" @click="handleAddRecord">添加记录</el-button>
            </div>
            <el-table :data="helpRecords" stripe border size="small" max-height="250">
              <el-table-column type="index" label="序号" width="50" align="center"/>
              <el-table-column prop="helpTime" label="时间" width="120" align="center"/>
              <el-table-column prop="helpContent" label="帮扶措施" min-width="150"  />
              <el-table-column prop="helpResult" label="帮扶成效" min-width="150" />
              <el-table-column label="操作" width="60">
                <template #default="{ row }">
                  <el-button link type="danger" size="small" @click="handleDeleteRecord(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="被帮扶人" prop="idCard">
          <div style="display: flex; gap: 8px; width: 100%">
            <el-input v-model="form.idCard" placeholder="请选择被帮扶人" readonly style="flex: 1" />
            <el-button type="primary" @click="handleSelectResident">选择村民</el-button>
          </div>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" :disabled="!!form.idCard" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择" :disabled="!!form.idCard">
            <el-option label="男" value="male" />
            <el-option label="女" value="female" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" :disabled="!!form.idCard" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" :disabled="!!form.idCard" />
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="form.address" placeholder="请输入住址" :disabled="!!form.idCard" />
        </el-form-item>
        <el-form-item label="人群类型" prop="groupType">
          <el-select v-model="form.groupType" placeholder="请选择" style="width: 100%">
            <el-option label="脱贫户" value="poverty_alleviation" />
            <el-option label="监测户" value="monitoring" />
            <el-option label="残疾人" value="disabled" />
            <el-option label="孤寡老人" value="elderly" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="帮扶责任人">
          <el-input v-model="form.helperName" placeholder="请输入帮扶责任人" />
        </el-form-item>
        <el-form-item label="帮扶措施">
          <el-input v-model="form.helpContent" type="textarea" :rows="2" placeholder="请输入帮扶措施" />
        </el-form-item>
        <el-form-item label="帮扶时间">
          <el-date-picker v-model="form.helpTime" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="帮扶成效">
          <el-input v-model="form.helpResult" type="textarea" :rows="2" placeholder="请输入帮扶成效" />
        </el-form-item>
        <el-form-item label="帮扶状态">
          <el-select v-model="form.helpStatus" placeholder="请选择" style="width: 100%">
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

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
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column prop="name" label="姓名" width="160" />
        <el-table-column prop="idCard" label="身份证号"/>
        <el-table-column prop="gender" label="性别" width="120">
          <template #default="{ row }">{{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="120" />
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

    <el-dialog v-model="recordDialogVisible" title="添加帮扶记录" width="500px" destroy-on-close>
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="100px">
        <el-form-item label="帮扶时间" prop="helpTime">
          <el-date-picker v-model="recordForm.helpTime" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="帮扶措施" prop="helpContent">
          <el-input v-model="recordForm.helpContent" type="textarea" :rows="3" placeholder="请输入帮扶措施" />
        </el-form-item>
        <el-form-item label="帮扶成效">
          <el-input v-model="recordForm.helpResult" type="textarea" :rows="3" placeholder="请输入帮扶成效" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRecord">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statsDialogVisible" title="统计" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="总人数">{{ stats.total || 0 }}</el-descriptions-item>
        <el-descriptions-item label="脱贫户">{{ stats.poverty_alleviation || 0 }}</el-descriptions-item>
        <el-descriptions-item label="监测户">{{ stats.monitoring || 0 }}</el-descriptions-item>
        <el-descriptions-item label="残疾人">{{ stats.disabled || 0 }}</el-descriptions-item>
        <el-descriptions-item label="孤寡老人">{{ stats.elderly || 0 }}</el-descriptions-item>
        <el-descriptions-item label="其他">{{ stats.other || 0 }}</el-descriptions-item>
        <el-descriptions-item label="帮扶进行中">{{ stats.ongoing || 0 }}</el-descriptions-item>
        <el-descriptions-item label="帮扶已完成">{{ stats.completed || 0 }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { specialGroupApi } from '@/request/specialGroup'
import { residentApi } from '@/request/resident'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentGroup = ref(null)
const drawerVisible = ref(false)
const activeTab = ref('records')
const helpRecords = ref([])

const queryForm = reactive({
  idCard: '',
  name: '',
  groupType: '',
  helpStatus: '',
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增登记')
const formRef = ref()
const form = reactive({
  id: null,
  idCard: '',
  name: '',
  gender: '',
  birthDate: '',
  phone: '',
  address: '',
  groupType: '',
  helperName: '',
  helpContent: '',
  helpTime: '',
  helpResult: '',
  helpStatus: 'ongoing'
})

const rules = {
  idCard: [{ required: true, message: '请选择被帮扶人', trigger: 'change' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  groupType: [{ required: true, message: '请选择人群类型', trigger: 'change' }]
}

const recordDialogVisible = ref(false)
const recordFormRef = ref()
const recordForm = reactive({
  specialGroupId: null,
  helpTime: '',
  helpContent: '',
  helpResult: ''
})

const recordRules = {
  helpTime: [{ required: true, message: '请选择帮扶时间', trigger: 'change' }],
  helpContent: [{ required: true, message: '请输入帮扶措施', trigger: 'blur' }]
}

const statsDialogVisible = ref(false)
const stats = ref({})

const residentDialogVisible = ref(false)
const residentList = ref([])
const residentTotal = ref(0)
const residentQuery = reactive({
  idCard: '',
  name: '',
  pageNum: 1,
  pageSize: 10
})

onMounted(() => {
  handleQuery()
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await specialGroupApi.list(queryForm)
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error(e.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.idCard = ''
  queryForm.name = ''
  queryForm.groupType = ''
  queryForm.helpStatus = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增登记'
  form.id = null
  form.idCard = ''
  form.name = ''
  form.gender = ''
  form.birthDate = ''
  form.phone = ''
  form.address = ''
  form.groupType = ''
  form.helperName = ''
  form.helpContent = ''
  form.helpTime = ''
  form.helpResult = ''
  form.helpStatus = 'ongoing'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑登记'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.id) {
      await specialGroupApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await specialGroupApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该特殊人群吗？', '提示', { type: 'warning' })
    await specialGroupApi.delete(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const handleExport = async () => {
  try {
    const res = await specialGroupApi.export(queryForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '特殊人群.xlsx'
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error(e.message || '导出失败')
  }
}

const handleStatistics = async () => {
  try {
    const res = await specialGroupApi.statistics()
    stats.value = res.data || {}
    statsDialogVisible.value = true
  } catch (e) {
    ElMessage.error(e.message || '统计失败')
  }
}

const handleRowClick = async (row) => {
  currentGroup.value = row
  drawerVisible.value = true
  activeTab.value = 'records'
  await loadHelpRecords(row.id)
}

const loadHelpRecords = async (specialGroupId) => {
  try {
    const res = await specialGroupApi.listHelpRecords(specialGroupId)
    helpRecords.value = res || []
  } catch (e) {
    ElMessage.error(e.message || '加载帮扶记录失败')
  }
}

const handleAddRecord = () => {
  recordForm.specialGroupId = currentGroup.value.id
  recordForm.helpTime = ''
  recordForm.helpContent = ''
  recordForm.helpResult = ''
  recordDialogVisible.value = true
}

const handleSubmitRecord = async () => {
  try {
    await recordFormRef.value.validate()
    await specialGroupApi.addHelpRecord(recordForm)
    ElMessage.success('添加成功')
    recordDialogVisible.value = false
    await loadHelpRecords(currentGroup.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '添加失败')
    }
  }
}

const handleDeleteRecord = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该帮扶记录吗？', '提示', { type: 'warning' })
    await specialGroupApi.deleteHelpRecord(row.id)
    ElMessage.success('删除成功')
    await loadHelpRecords(currentGroup.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
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
  } catch (e) {
    ElMessage.error(e.message || '加载村民列表失败')
  }
}

const handleResidentSelect = (row) => {
  form.idCard = row.idCard
  form.name = row.name
  form.gender = row.gender
  form.birthDate = row.birthDate
  form.phone = row.phone
  form.address = row.address
  residentDialogVisible.value = false
}

const getGroupTypeName = (type) => {
  const map = { poverty_alleviation: '脱贫户', monitoring: '监测户', disabled: '残疾人', elderly: '孤寡老人', other: '其他' }
  return map[type] || type
}

const getGroupTypeTag = (type) => {
  const map = { poverty_alleviation: 'success', monitoring: 'warning', disabled: 'danger', elderly: 'info', other: '' }
  return map[type] || ''
}
</script>

<style scoped>
.special-group-list { padding: 20px; }
.filter-bar { background: #fff; padding: 20px; border-radius: 4px; margin-bottom: 16px; }
.query-form { display: flex; flex-wrap: wrap; gap: 8px; }
.toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.group-detail { padding: 0 16px; }
.detail-tabs { margin-top: 20px; }
.tab-toolbar { margin-bottom: 12px; }
</style>