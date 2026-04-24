<template>
  <div class="party-member-list">
    <!-- 查询条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="姓名" style="width: 150px">
          <el-input v-model="queryForm.name" placeholder="请输入姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="身份证号" style="width: 200px">
          <el-input v-model="queryForm.idCard" placeholder="请输入身份证号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="党员状态" style="width: 150px">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="积极分子" value="activist" />
            <el-option label="发展对象" value="developing" />
            <el-option label="预备党员" value="probationary" />
            <el-option label="正式党员" value="formal" />
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
      <el-button type="primary" @click="handleAdd">新增党员</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      style="width: 100%"
    >
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="idCard" label="身份证号" width="180" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">
          {{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="birthDate" label="出生日期" width="120" />
      <el-table-column prop="phone" label="联系电话" width="120" />
      <el-table-column prop="joinDate" label="入党时间" width="120" />
      <el-table-column prop="convertDate" label="转正时间" width="120" />
      <el-table-column prop="status" label="党员状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusName(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link @click="handleStatusChange(row)">状态变更</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" :disabled="!!formData.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="住址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入住址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入党时间" prop="joinDate">
              <el-date-picker v-model="formData.joinDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="转正时间" prop="convertDate">
              <el-date-picker v-model="formData.convertDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="党员状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
                <el-option label="积极分子" value="activist" />
                <el-option label="发展对象" value="developing" />
                <el-option label="预备党员" value="probationary" />
                <el-option label="正式党员" value="formal" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 状态变更弹窗 -->
    <el-dialog v-model="statusDialogVisible" title="党员状态变更" width="400px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(currentRow.status)">
            {{ getStatusName(currentRow.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="变更状态">
          <el-select v-model="newStatus" placeholder="请选择" style="width: 100%">
            <el-option label="积极分子" value="activist" />
            <el-option label="发展对象" value="developing" />
            <el-option label="预备党员" value="probationary" />
            <el-option label="正式党员" value="formal" />
          </el-select>
        </el-form-item>
        <el-form-item label="提示">
          <div style="color: #909399; font-size: 12px">
            状态流转顺序：积极分子 → 发展对象 → 预备党员 → 正式党员
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { partyWorkApi } from '@/request/partyWork'

// 查询表单
const queryForm = reactive({
  name: '',
  idCard: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const formData = reactive({
  id: null,
  idCard: '',
  name: '',
  gender: 'male',
  birthDate: '',
  phone: '',
  address: '',
  joinDate: '',
  convertDate: '',
  status: 'activist'
})

// 表单校验
const formRules = {
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }]
}

// 状态变更弹窗
const statusDialogVisible = ref(false)
const currentRow = ref({})
const newStatus = ref('')

// 生命周期
onMounted(() => {
  handleQuery()
})

// 方法
const handleQuery = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await partyWorkApi.listMembers(params)
    if (res.code === 0) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('查询失败', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.idCard = ''
  queryForm.status = ''
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增党员'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(formData, row)
  dialogTitle.value = '编辑党员'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    const res = formData.id ? await partyWorkApi.updateMember(formData) : await partyWorkApi.createMember(formData)
    if (res.code === 0) {
      ElMessage.success(formData.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      handleQuery()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('操作失败', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该党员档案吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await partyWorkApi.deleteMember(row.id)
      if (res.code === 0) {
        ElMessage.success('删除成功')
        handleQuery()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败', error)
    }
  })
}

const handleStatusChange = (row) => {
  currentRow.value = row
  newStatus.value = ''
  statusDialogVisible.value = true
}

const handleStatusSubmit = async () => {
  if (!newStatus.value) {
    ElMessage.warning('请选择变更状态')
    return
  }
  try {
    const res = await partyWorkApi.updateMemberStatus(currentRow.value.id, newStatus.value)
    if (res.code === 0) {
      ElMessage.success('状态变更成功')
      statusDialogVisible.value = false
      handleQuery()
    } else {
      ElMessage.error(res.msg || '状态变更失败')
    }
  } catch (error) {
    console.error('状态变更失败', error)
  }
}

const resetForm = () => {
  formData.id = null
  formData.idCard = ''
  formData.name = ''
  formData.gender = 'male'
  formData.birthDate = ''
  formData.phone = ''
  formData.address = ''
  formData.joinDate = ''
  formData.convertDate = ''
  formData.status = 'activist'
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

const getStatusType = (status) => {
  const map = {
    activist: 'info',
    developing: 'warning',
    probationary: 'success',
    formal: 'primary'
  }
  return map[status] || 'info'
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.slice(0, 19)
}
</script>

<style scoped>
.party-member-list {
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