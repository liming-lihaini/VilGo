<template>
  <div class="position-list">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增职位</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="name" label="职位名称" width="150" align="center" />
      <el-table-column prop="code" label="职位编码" width="150" />
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="职位名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入职位名称" />
        </el-form-item>
        <el-form-item label="职位编码">
          <el-input v-model="form.code" placeholder="请输入职位编码" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { positionApi } from '@/request/position'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增职位')
const formRef = ref()
const form = reactive({ id: null, name: '', code: '', sortOrder: 0 })
const rules = { name: [{ required: true, message: '请输入职位名称', trigger: 'blur' }] }

onMounted(() => { loadData() })

const loadData = async () => {
  loading.value = true
  try {
    const res = await positionApi.list()
    tableData.value = res || []
  } catch (e) {
    ElMessage.error(e.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增职位'
  Object.assign(form, { id: null, name: '', code: '', sortOrder: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑职位'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.id) {
      await positionApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await positionApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该职位吗？', '提示', { type: 'warning' })
    await positionApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}
</script>

<style scoped>
.position-list { padding: 20px; }
.toolbar { margin-bottom: 16px; }
</style>
