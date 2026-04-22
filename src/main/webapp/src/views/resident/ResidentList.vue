<template>
  <div class="resident-list">
    <!-- 查询条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="姓名">
          <el-input v-model="queryForm.name" placeholder="请输入姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="queryForm.idCard" placeholder="请输入身份证号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="queryForm.gender" placeholder="请选择" clearable>
            <el-option label="男" value="male" />
            <el-option label="女" value="female" />
          </el-select>
        </el-form-item>
        <el-form-item label="户籍状态">
          <el-select v-model="queryForm.householdStatus" placeholder="请选择" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="已销户" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="人员类型">
          <el-select v-model="queryForm.personType" placeholder="请选择" clearable>
            <el-option label="农民" value="farmer" />
            <el-option label="工人" value="worker" />
            <el-option label="教师" value="teacher" />
            <el-option label="医生" value="doctor" />
            <el-option label="学生" value="student" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属村组">
          <el-input v-model="queryForm.village" placeholder="请输入村组" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增村民</el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
        批量删除
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      @selection-change="handleSelectionChange"
      style="width: 100%"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="idCard" label="身份证号" width="180" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">
          {{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="birthDate" label="出生日期" width="120" />
      <el-table-column prop="householdStatus" label="户籍状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.householdStatus === 'normal' ? 'success' : 'danger'">
            {{ row.householdStatus === 'normal' ? '正常' : '已销户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="personType" label="人员类型" width="100">
        <template #default="{ row }">
          {{ getPersonTypeName(row.personType) }}
        </template>
      </el-table-column>
      <el-table-column prop="securityType" label="保障类型" width="100">
        <template #default="{ row }">
          {{ getSecurityTypeName(row.securityType) }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系电话" width="120" />
      <el-table-column prop="address" label="住址" min-width="150" show-overflow-tooltip />
      <el-table-column prop="village" label="所属村组" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link @click="handleCancel(row)" :disabled="row.householdStatus === 'cancelled'">
            销户
          </el-button>
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
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="人员类型" prop="personType">
          <el-select v-model="formData.personType" placeholder="请选择">
            <el-option label="农民" value="farmer" />
            <el-option label="工人" value="worker" />
            <el-option label="教师" value="teacher" />
            <el-option label="医生" value="doctor" />
            <el-option label="学生" value="student" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="保障类型" prop="securityType">
          <el-select v-model="formData.securityType" placeholder="请选择">
            <el-option label="养老保险" value="pension" />
            <el-option label="医疗保险" value="insurance" />
            <el-option label="低保" value="allowance" />
            <el-option label="未参保" value="none" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="住址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入住址" />
        </el-form-item>
        <el-form-item label="户主姓名" prop="householdHead">
          <el-input v-model="formData.householdHead" placeholder="请输入户主姓名" />
        </el-form-item>
        <el-form-item label="与户主关系" prop="relationship">
          <el-input v-model="formData.relationship" placeholder="请输入与户主关系" />
        </el-form-item>
        <el-form-item label="所属村组" prop="village">
          <el-input v-model="formData.village" placeholder="请输入所属村组" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { residentApi } from '@/request/resident'

// 查询表单
const queryForm = reactive({
  name: '',
  idCard: '',
  gender: '',
  householdStatus: '',
  personType: '',
  village: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])

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
  name: '',
  idCard: '',
  gender: 'male',
  birthDate: '',
  personType: '',
  securityType: '',
  phone: '',
  address: '',
  householdHead: '',
  relationship: '',
  village: '',
  remark: ''
})

const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }]
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

// 获取人员类型名称
const getPersonTypeName = (type) => personTypeMap[type] || '-'

// 获取保障类型名称
const getSecurityTypeName = (type) => securityTypeMap[type] || '-'

// 格式化时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

// 查询
const handleQuery = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      orderBy: 'create_time',
      sortOrder: 'desc'
    }
    const result = await residentApi.list(params)
    tableData.value = result.list || []
    pagination.total = result.total || 0
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    name: '',
    idCard: '',
    gender: '',
    householdStatus: '',
    personType: '',
    village: ''
  })
  pagination.pageNum = 1
  handleQuery()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增村民'
  Object.assign(formData, {
    id: null,
    name: '',
    idCard: '',
    gender: 'male',
    birthDate: '',
    personType: '',
    securityType: '',
    phone: '',
    address: '',
    householdHead: '',
    relationship: '',
    village: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑村民'
  try {
    const result = await residentApi.get(row.id)
    Object.assign(formData, result)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await residentApi.update(formData)
          ElMessage.success('更新成功')
        } else {
          await residentApi.create(formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        handleQuery()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该村民档案吗？', '提示', {
      type: 'warning'
    })
    await residentApi.delete(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '提示', {
      type: 'warning'
    })
    for (const row of selectedRows.value) {
      await residentApi.delete(row.id)
    }
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 销户
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要进行销户登记吗？', '提示', {
      type: 'warning'
    })
    await residentApi.cancel(row.id)
    ElMessage.success('销户成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('销户失败:', error)
    }
  }
}

// 导出
const handleExport = async () => {
  try {
    const params = { ...queryForm }
    const blob = await residentApi.export(params)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = '村民档案.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 选择行
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.resident-list {
  padding: 20px;
}

.filter-bar {
  margin-bottom: 16px;
}

.query-form {
  background: #fff;
  padding: 16px;
  border-radius: 4px;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>