<template>
  <div class="party-dues-list">
    <!-- 查询条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="缴费月份" style="width: 150px">
          <el-date-picker v-model="queryForm.payMonth" type="month" placeholder="请选择月份" value-format="YYYY-MM" style="width: 100%" />
        </el-form-item>
        <el-form-item label="缴费状态" style="width: 150px">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="未缴" value="unpaid" />
            <el-option label="已缴" value="paid" />
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
      <el-button type="primary" @click="handleAdd">新增党费</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      style="width: 100%"
    >
      <el-table-column prop="memberId" label="党员ID" width="100" />
      <el-table-column prop="amount" label="缴费金额" width="100">
        <template #default="{ row }">
          {{ row.amount ? '￥' + row.amount.toFixed(2) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="payMonth" label="缴费月份" width="100" />
      <el-table-column prop="payDate" label="缴费日期" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'paid' ? 'success' : 'danger'">
            {{ row.status === 'paid' ? '已缴' : '未缴' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column prop="creator" label="创建人" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status !== 'paid'" type="success" link @click="handleMarkPaid(row)">
            标记已缴
          </el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="党员" prop="memberId">
              <el-select v-model="formData.memberId" placeholder="请选择党员" style="width: 100%" filterable @change="handleMemberChange">
                <el-option v-for="member in memberList" :key="member.id" :label="member.name || member.idCard" :value="member.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费金额" prop="amount">
              <el-input-number v-model="formData.amount" :min="0" :precision="2" placeholder="请输入金额" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费月份" prop="payMonth">
              <el-date-picker v-model="formData.payMonth" type="month" placeholder="请选择月份" value-format="YYYY-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
                <el-option label="未缴" value="unpaid" />
                <el-option label="已缴" value="paid" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="缴费日期" prop="payDate">
              <el-date-picker v-model="formData.payDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { partyWorkApi } from '@/request/partyWork'

// 查询表单
const queryForm = reactive({
  payMonth: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const memberList = ref([])

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
const currentMemberName = ref('')
const formData = reactive({
  id: null,
  memberId: null,
  memberName: '',
  amount: 0,
  payMonth: '',
  payDate: '',
  status: 'unpaid',
  remark: ''
})

// 表单校验
const formRules = {
  memberId: [{ required: true, message: '请选择党员', trigger: 'change' }],
  amount: [{ required: true, message: '请输入缴费金额', trigger: 'blur' }],
  payMonth: [{ required: true, message: '请选择缴费月份', trigger: 'change' }]
}

// 生命周期
onMounted(() => {
  loadMembers()
  handleQuery()
})

// 方法
const loadMembers = async () => {
  try {
    const res = await partyWorkApi.listMembers({ pageSize: 1000 })
    if (res.code === 0) {
      memberList.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载党员列表失败', error)
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
    const res = await partyWorkApi.listDues(params)
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
  queryForm.payMonth = ''
  queryForm.status = ''
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增党费'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(formData, row)
  dialogTitle.value = '编辑党费'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    const res = formData.id ? await partyWorkApi.updateDues(formData) : await partyWorkApi.createDues(formData)
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
  ElMessageBox.confirm('确定要删除该党费记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await partyWorkApi.deleteDues(row.id)
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

const handleMarkPaid = (row) => {
  ElMessageBox.confirm('确定要标记该笔党费为已缴吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await partyWorkApi.markDuesAsPaid(row.id)
      if (res.code === 0) {
        ElMessage.success('标记成功')
        handleQuery()
      } else {
        ElMessage.error(res.msg || '标记失败')
      }
    } catch (error) {
      console.error('标记失败', error)
    }
  })
}

const handleMemberChange = (memberId) => {
  const member = memberList.value.find(m => m.id === memberId)
  if (member) {
    currentMemberName.value = member.name || member.idCard
  }
}

const resetForm = () => {
  formData.id = null
  formData.memberId = null
  formData.amount = 0
  formData.payMonth = ''
  formData.payDate = ''
  formData.status = 'unpaid'
  formData.remark = ''
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.slice(0, 19)
}
</script>

<style scoped>
.party-dues-list {
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