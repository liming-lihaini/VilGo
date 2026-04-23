<template>
  <div class="household-list">
    <!-- 查询条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="家庭户编号" style="width: 180px">
          <el-input v-model="queryForm.householdNo" placeholder="编号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="户主姓名" style="width: 200px">
          <el-input v-model="queryForm.headName" placeholder="姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="身份证号" style="width: 200px">
          <el-input v-model="queryForm.headIdCard" placeholder="身份证号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="success" @click="handleSync">从村民档案同步</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      @row-click="handleRowClick"
      highlight-current-row
      style="width: 100%"
    >
      <el-table-column prop="householdNo" label="家庭户编号" width="160" />
      <el-table-column prop="headName" label="户主姓名" width="100" />
      <el-table-column prop="headIdCard" label="户主身份证号" width="180" />
      <el-table-column prop="phone" label="联系电话" width="120" />
      <el-table-column prop="address" label="家庭住址" min-width="180" show-overflow-tooltip />
      <el-table-column prop="memberCount" label="成员人数" width="90" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click.stop="handleRowClick(row)">详情</el-button>
          <el-button link type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="家庭户详情"
      size="800px"
      direction="rtl"
    >
      <div v-if="currentHousehold" class="household-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="家庭户编号">{{ currentHousehold.householdNo }}</el-descriptions-item>
          <el-descriptions-item label="户主姓名">{{ currentHousehold.headName }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ currentHousehold.headIdCard }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentHousehold.phone }}</el-descriptions-item>
          <el-descriptions-item label="户籍住址" :span="2">{{ currentHousehold.address }}</el-descriptions-item>
          <el-descriptions-item label="成员人数">{{ currentHousehold.memberCount }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentHousehold.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="家庭成员" name="members">
            <div class="tab-toolbar">
              <el-button type="primary" size="small" @click="handleAddMember">添加成员</el-button>
            </div>
            <el-table :data="members" stripe border size="small" max-height="300">
              <el-table-column prop="name" label="姓名" width="100" />
              <el-table-column prop="idCard" label="身份证号"  />
              <el-table-column prop="gender" label="性别" width="60" align="center">
                <template #default="{ row }">
                  {{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="relation" label="与户主关系" width="100" align="center"/>
              <el-table-column prop="phone" label="联系电话" width="120" />
              <el-table-column label="操作" width="80" fixed="right">
                <template #default="{ row }">
                  <el-button link type="danger" size="small" @click="handleRemoveMember(row)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="年度收入" name="income">
            <div class="tab-toolbar">
              <el-button type="primary" size="small" @click="handleAddIncome">添加收入</el-button>
            </div>
            <el-table :data="incomes" stripe border size="small" max-height="300">
              <el-table-column prop="year" label="年份" width="80" />
              <el-table-column prop="totalIncome" label="家庭总收入" width="120">
                <template #default="{ row }">
                  {{ row.totalIncome ? row.totalIncome.toLocaleString() : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="perCapitaIncome" label="人均收入" width="120">
                <template #default="{ row }">
                  {{ row.perCapitaIncome ? row.perCapitaIncome.toLocaleString() : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="incomeSource" label="收入来源" min-width="120" />
              <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip />
              <el-table-column label="操作" width="80" fixed="right">
                <template #default="{ row }">
                  <el-button link type="danger" size="small" @click="handleDeleteIncome(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="户籍变动" name="changes">
            <div class="tab-toolbar">
              <el-button type="primary" size="small" @click="handleAddChange">登记变动</el-button>
            </div>
            <el-table :data="changes" stripe border size="small" max-height="300">
              <el-table-column prop="changeType" label="变动类型" width="100">
                <template #default="{ row }">
                  <el-tag :type="getChangeTypeTag(row.changeType)" size="small">
                    {{ getChangeTypeName(row.changeType) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="changeTime" label="变动时间" width="120">
                <template #default="{ row }">
                  {{ formatDate(row.changeTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="changeReason" label="变动原因" min-width="120" show-overflow-tooltip />
              <el-table-column prop="relatedPersons" label="相关人员" min-width="100" show-overflow-tooltip />
              <el-table-column prop="beforeStatus" label="变动前" width="100" />
              <el-table-column prop="afterStatus" label="变动后" width="100" />
              <el-table-column label="操作" width="80" fixed="right">
                <template #default="{ row }">
                  <el-button link type="danger" size="small" @click="handleDeleteChange(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="家庭户编号" prop="householdNo">
          <el-input v-model="form.householdNo" placeholder="请输入家庭户编号" />
        </el-form-item>
        <el-form-item label="户主姓名" prop="headName">
          <el-input v-model="form.headName" placeholder="请输入户主姓名" />
        </el-form-item>
        <el-form-item label="户主身份证号" prop="headIdCard">
          <el-input v-model="form.headIdCard" placeholder="请输入户主身份证号" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="form.address" placeholder="请输入住址" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加成员弹窗 -->
    <el-dialog v-model="memberDialogVisible" title="添加家庭成员" width="500px" destroy-on-close>
      <el-form :model="memberForm" label-width="100px">
        <el-form-item label="选择村民">
          <el-select
            v-model="memberForm.residentId"
            filterable
            placeholder="请输入姓名搜索"
            style="width: 100%"
          >
            <el-option
              v-for="resident in residentOptions"
              :key="resident.id"
              :label="`${resident.name} - ${resident.idCard}`"
              :value="resident.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="与户主关系">
          <el-select v-model="memberForm.relation" placeholder="请选择">
            <el-option label="配偶" value="配偶" />
            <el-option label="子女" value="子女" />
            <el-option label="父母" value="父母" />
            <el-option label="儿媳" value="儿媳" />
            <el-option label="女婿" value="女婿" />
            <el-option label="孙子" value="孙子" />
            <el-option label="孙女" value="孙女" />
            <el-option label="外孙" value="外孙" />
            <el-option label="外孙女" value="外孙女" />
            <el-option label="兄弟姐妹" value="兄弟姐妹" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMember">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加收入弹窗 -->
    <el-dialog v-model="incomeDialogVisible" title="添加年度收入" width="500px" destroy-on-close>
      <el-form ref="incomeFormRef" :model="incomeForm" :rules="incomeRules" label-width="100px">
        <el-form-item label="年份" prop="year">
          <el-select v-model="incomeForm.year" placeholder="请选择">
            <el-option v-for="y in yearOptions" :key="y" :label="y" :value="y" />
          </el-select>
        </el-form-item>
        <el-form-item label="家庭总收入">
          <el-input-number v-model="incomeForm.totalIncome" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="人均收入">
          <el-input-number v-model="incomeForm.perCapitaIncome" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收入来源">
          <el-input v-model="incomeForm.incomeSource" placeholder="请输入收入来源" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="incomeForm.remark" placeholder="请输入备注" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="incomeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitIncome">确定</el-button>
      </template>
    </el-dialog>

    <!-- 登记变动弹窗 -->
    <el-dialog v-model="changeDialogVisible" title="登记户籍变动" width="500px" destroy-on-close>
      <el-form ref="changeFormRef" :model="changeForm" :rules="changeRules" label-width="100px">
        <el-form-item label="变动类型" prop="changeType">
          <el-select v-model="changeForm.changeType" placeholder="请选择" style="width: 100%">
            <el-option label="迁入" value="move_in" />
            <el-option label="迁出" value="move_out" />
            <el-option label="新生儿" value="newborn" />
            <el-option label="分户" value="split" />
            <el-option label="合户" value="merge" />
          </el-select>
        </el-form-item>
        <el-form-item label="变动时间">
          <el-date-picker v-model="changeForm.changeTime" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="变动原因">
          <el-input v-model="changeForm.changeReason" placeholder="请输入变动原因" />
        </el-form-item>
        <el-form-item label="相关人员">
          <el-input v-model="changeForm.relatedPersons" placeholder="请输入相关人员，多人用逗号分隔" />
        </el-form-item>
        <el-form-item label="变动前状态">
          <el-input v-model="changeForm.beforeStatus" placeholder="请输入变动前状态" />
        </el-form-item>
        <el-form-item label="变动后状态">
          <el-input v-model="changeForm.afterStatus" placeholder="请输入变动后状态" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="changeForm.remark" placeholder="请输入备注" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitChange">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { householdApi } from '@/request/household'
import { residentApi } from '@/request/resident'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentHousehold = ref(null)
const drawerVisible = ref(false)
const activeTab = ref('members')

const queryForm = reactive({
  householdNo: '',
  headName: '',
  headIdCard: '',
  address: '',
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增家庭户')
const formRef = ref()
const form = reactive({
  id: null,
  householdNo: '',
  headName: '',
  headIdCard: '',
  phone: '',
  address: ''
})

const rules = {
  householdNo: [{ required: true, message: '请输入家庭户编号', trigger: 'blur' }],
  headName: [{ required: true, message: '请输入户主姓名', trigger: 'blur' }],
  headIdCard: [{ required: true, message: '请输入户主身份证号', trigger: 'blur' }]
}

const memberDialogVisible = ref(false)
const memberForm = reactive({
  householdId: null,
  residentId: null,
  relation: ''
})
const residentOptions = ref([])
const members = ref([])

const incomeDialogVisible = ref(false)
const incomeFormRef = ref()
const incomeForm = reactive({
  householdId: null,
  year: null,
  totalIncome: null,
  perCapitaIncome: null,
  incomeSource: '',
  remark: ''
})
const incomeRules = {
  year: [{ required: true, message: '请选择年份', trigger: 'change' }]
}
const incomes = ref([])
const yearOptions = ref([])

const changeDialogVisible = ref(false)
const changeFormRef = ref()
const changeForm = reactive({
  householdId: null,
  changeType: '',
  changeTime: '',
  changeReason: '',
  relatedPersons: '',
  beforeStatus: '',
  afterStatus: '',
  remark: ''
})
const changeRules = {
  changeType: [{ required: true, message: '请选择变动类型', trigger: 'change' }]
}
const changes = ref([])

onMounted(() => {
  const currentYear = new Date().getFullYear()
  yearOptions.value = Array.from({ length: 10 }, (_, i) => currentYear - i)
  handleQuery()
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await householdApi.list(queryForm)
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error(e.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.householdNo = ''
  queryForm.headName = ''
  queryForm.headIdCard = ''
  queryForm.address = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增家庭户'
  form.id = null
  form.householdNo = ''
  form.headName = ''
  form.headIdCard = ''
  form.phone = ''
  form.address = ''
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑家庭户'
  form.id = row.id
  form.householdNo = row.householdNo
  form.headName = row.headName
  form.headIdCard = row.headIdCard
  form.phone = row.phone
  form.address = row.address
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.id) {
      await householdApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await householdApi.create(form)
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
    await ElMessageBox.confirm('确认删除该家庭户吗？', '提示', { type: 'warning' })
    await householdApi.delete(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const handleSync = async () => {
  try {
    await ElMessageBox.confirm('将从村民档案中所有标记为户主的人员同步为家庭户，是否继续？', '提示', { type: 'info' })
    await householdApi.syncAll()
    ElMessage.success('同步成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '同步失败')
    }
  }
}

const handleRowClick = async (row) => {
  currentHousehold.value = row
  drawerVisible.value = true
  activeTab.value = 'members'
  await loadDetailData(row.id)
}

const loadDetailData = async (householdId) => {
  try {
    const res = await householdApi.detail(householdId)
    currentHousehold.value = { ...currentHousehold.value, ...res.household }
    members.value = res.members || []
    incomes.value = res.incomes || []
    changes.value = res.changes || []
  } catch (e) {
    ElMessage.error(e.message || '加载详情失败')
  }
}

const handleAddMember = async () => {
  memberForm.householdId = currentHousehold.value.id
  memberForm.residentId = null
  memberForm.relation = ''
  try {
    const res = await residentApi.list({ pageNum: 1, pageSize: 1000 })
    residentOptions.value = res.list || []
  } catch (e) {
    ElMessage.error('加载村民列表失败')
  }
  memberDialogVisible.value = true
}

const handleSubmitMember = async () => {
  try {
    await householdApi.addMember(memberForm)
    ElMessage.success('添加成功')
    memberDialogVisible.value = false
    await loadDetailData(currentHousehold.value.id)
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

const handleRemoveMember = async (row) => {
  try {
    await ElMessageBox.confirm('确认移除该成员吗？', '提示', { type: 'warning' })
    await householdApi.removeMember(row.id)
    ElMessage.success('移除成功')
    await loadDetailData(currentHousehold.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '移除失败')
    }
  }
}

const handleAddIncome = () => {
  incomeForm.householdId = currentHousehold.value.id
  incomeForm.year = new Date().getFullYear()
  incomeForm.totalIncome = null
  incomeForm.perCapitaIncome = null
  incomeForm.incomeSource = ''
  incomeForm.remark = ''
  incomeDialogVisible.value = true
}

const handleSubmitIncome = async () => {
  try {
    await incomeFormRef.value.validate()
    await householdApi.saveIncome(incomeForm)
    ElMessage.success('保存成功')
    incomeDialogVisible.value = false
    await loadDetailData(currentHousehold.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '保存失败')
    }
  }
}

const handleDeleteIncome = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该年度收入记录吗？', '提示', { type: 'warning' })
    await householdApi.deleteIncome(row.id)
    ElMessage.success('删除成功')
    await loadDetailData(currentHousehold.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const handleAddChange = () => {
  changeForm.householdId = currentHousehold.value.id
  changeForm.changeType = ''
  changeForm.changeTime = ''
  changeForm.changeReason = ''
  changeForm.relatedPersons = ''
  changeForm.beforeStatus = ''
  changeForm.afterStatus = ''
  changeForm.remark = ''
  changeDialogVisible.value = true
}

const handleSubmitChange = async () => {
  try {
    await changeFormRef.value.validate()
    await householdApi.createChange(changeForm)
    ElMessage.success('登记成功')
    changeDialogVisible.value = false
    await loadDetailData(currentHousehold.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '登记失败')
    }
  }
}

const handleDeleteChange = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该变动记录吗？', '提示', { type: 'warning' })
    await householdApi.deleteChange(row.id)
    ElMessage.success('删除成功')
    await loadDetailData(currentHousehold.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const getChangeTypeName = (type) => {
  const map = { move_in: '迁入', move_out: '迁出', newborn: '新生儿', split: '分户', merge: '合户' }
  return map[type] || type
}

const getChangeTypeTag = (type) => {
  const map = { move_in: 'success', move_out: 'warning', newborn: 'info', split: 'danger', merge: 'success' }
  return map[type] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 10);
}
</script>

<style scoped>
.household-list {
  padding: 20px;
}

.filter-bar {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.query-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.household-detail {
  padding: 0 16px;
}

.detail-tabs {
  margin-top: 20px;
}

.tab-toolbar {
  margin-bottom: 12px;
}
</style>