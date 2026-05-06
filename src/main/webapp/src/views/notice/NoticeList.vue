<template>
  <div class="notice-list">
    <!-- 标签页 -->
        <div class="filter-bar">
          <el-form :inline="true" :model="query" class="query-form">
            <el-form-item>
              <el-button type="primary" @click="handleAdd">发布公示</el-button>
            </el-form-item>
            <el-form-item label="公示标题">
              <el-input v-model="query.title" placeholder="公示标题" clearable @keyup.enter="loadNotices" />
            </el-form-item>
            <el-form-item label="公示类型">
              <el-select v-model="query.noticeType" placeholder="请选择" clearable @change="loadNotices" style="width: 200px;">
                <el-option label="通知" value="通知" />
                <el-option label="政策文件" value="政策文件" />
                <el-option label="财务公开" value="财务公开" />
                <el-option label="项目公示" value="项目公示" />
                <el-option label="惠民信息" value="惠民信息" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadNotices">查询</el-button>
              <el-button @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table v-loading="loading" :data="noticeList" stripe border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="title" label="公示标题" min-width="200" />
          <el-table-column prop="noticeType" label="公示类型" width="100" align="center">
            <template #default="{ row }">{{ row.noticeType || '-' }}</template>
          </el-table-column>
          <el-table-column prop="publisher" label="发布人" width="100" align="center" />
          <el-table-column label="公示期限" width="120" align="center">
            <template #default="{ row }">{{ row.expireDate || '-' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.expireDate)" size="small">{{ getStatusName(row.expireDate) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="160" align="center" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
              <el-button link type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadNotices"
            @current-change="loadNotices"
          />
        </div>
    <!-- 公示表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公示标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公示标题" />
        </el-form-item>
        <el-form-item label="公示类型">
          <el-select v-model="form.noticeType" placeholder="请选择" style="width: 100%" @change="handleNoticeTypeChange">
            <el-option label="通知" value="通知" />
            <el-option label="政策文件" value="政策文件" />
            <el-option label="财务公开" value="财务公开" />
            <el-option label="项目公示" value="项目公示" />
            <el-option label="惠民信息" value="惠民信息" />
          </el-select>
        </el-form-item>
        <el-form-item label="公示期限">
          <el-date-picker v-model="form.expireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="公示内容" prop="content">
          <WangEditor v-model="form.content" placeholder="请输入公示内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 公示详情抽屉 -->
    <el-drawer v-model="detailDrawerVisible" title="公示详情" size="1000px" destroy-on-close>
      <div class="drawer-content">
        <el-descriptions :column="2" border v-if="currentNotice">
        <el-descriptions-item label="公示标题" :span="2">{{ currentNotice.title }}</el-descriptions-item>
        <el-descriptions-item label="公示类型">{{ currentNotice.noticeType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布人">{{ currentNotice.publisher || '-' }}</el-descriptions-item>
        <el-descriptions-item label="公示期限">{{ currentNotice.expireDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentNotice.expireDate)" size="small">{{ getStatusName(currentNotice.expireDate) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{ currentNotice?.createTime?.substring(0,10) }}</el-descriptions-item>
        <el-descriptions-item label="公示内容" :span="2">
          <div class="notice-content" v-html="currentNotice.content"></div>
        </el-descriptions-item>
      </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { noticeApi } from '@/request/notice'
import WangEditor from '@/components/WangEditor.vue'

const router = useRouter()

const activeTab = ref('publish')

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  noticeType: '',
  startTime: '',
  endTime: ''
})

const noticeList = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({
  id: null,
  title: '',
  content: '',
  noticeType: '',
  publisher: '',
  expireDate: '',
  creator: 'admin'
})

// 公示类型模板
const noticeTypeTemplates = {
  '通知': '<h2>通知</h2><p>各位村民：</p><p>现将有关事项通知如下：</p><p>1. </p><p>2. </p><p>特此通知。</p>',
  '政策文件': '<h2>政策文件</h2><p>根据上级文件精神，现将有关政策内容公示如下：</p><p>一、</p><p>二、</p><p>三、</p>',
  '财务公开': '<h2>财务公开</h2><p>本期收入：</p><p>本期支出：</p><p>结余：</p><p>详细收支明细如下：</p>',
  '项目公示': '<h2>项目公示</h2><p>项目名称：</p><p>项目内容：</p><p>实施时间：</p><p>预算金额：</p>',
  '惠民信息': '<h2>惠民信息</h2><p>各位村民：</p><p>现将惠民政策相关信息通知如下：</p><p>一、补贴对象</p><p>二、补贴标准</p><p>三、申请流程</p>'
}

const handleNoticeTypeChange = (type) => {
  if (type && noticeTypeTemplates[type] && !form.content) {
    form.content = noticeTypeTemplates[type]
  }
}

const rules = {
  title: [{ required: true, message: '公示标题不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '公示内容不能为空', trigger: 'blur' }]
}

const detailDrawerVisible = ref(false)
const currentNotice = ref(null)
const feedbackList = ref([])

const feedbackDialogVisible = ref(false)
const feedbackFormRef = ref()
const feedbackForm = reactive({
  noticeId: null,
  residentId: null,
  content: ''
})

const feedbackRules = {
  content: [{ required: true, message: '反馈内容不能为空', trigger: 'blur' }]
}

const replyDialogVisible = ref(false)
const replyFormRef = ref()
const replyForm = reactive({
  id: null,
  reply: ''
})

const replyRules = {
  reply: [{ required: true, message: '回复内容不能为空', trigger: 'blur' }]
}

const getStatusTag = (expireDate) => {
  if (!expireDate) return 'info'
  const now = new Date()
  const expire = new Date(expireDate)
  return expire >= now ? 'success' : 'danger'
}

const getStatusName = (expireDate) => {
  if (!expireDate) return '未设置'
  const now = new Date()
  const expire = new Date(expireDate)
  return expire >= now ? '未过期' : '已过期'
}

const loadNotices = async () => {
  loading.value = true
  try {
    const res = await noticeApi.list(query)
    noticeList.value = res.list || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('加载公示列表失败')
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.title = ''
  query.noticeType = ''
  query.startTime = ''
  query.endTime = ''
  loadNotices()
}

const handleAdd = () => {
  dialogTitle.value = '发布公示'
  form.id = null
  form.title = ''
  form.content = ''
  form.noticeType = ''
  form.publisher = ''
  form.expireDate = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑公示'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = async (row) => {
  currentNotice.value = row
  detailDrawerVisible.value = true
  loadFeedbackList(row.id)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该公示吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await noticeApi.delete(row.id)
      ElMessage.success('删除成功')
      loadNotices()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (form.id) {
      await noticeApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await noticeApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadNotices()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const loadFeedbackList = async (noticeId) => {
  try {
    const res = await noticeApi.getFeedbackList(noticeId)
    feedbackList.value = res || []
  } catch (error) {
    ElMessage.error('加载反馈列表失败')
  }
}

const handleAddFeedback = () => {
  feedbackForm.noticeId = currentNotice.value.id
  feedbackForm.residentId = null
  feedbackForm.content = ''
  feedbackDialogVisible.value = true
}

const handleSubmitFeedback = async () => {
  await feedbackFormRef.value.validate()
  try {
    await noticeApi.submitFeedback(feedbackForm)
    ElMessage.success('提交成功')
    feedbackDialogVisible.value = false
    loadFeedbackList(currentNotice.value.id)
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  }
}

const handleReplyFeedback = (feedback) => {
  replyForm.id = feedback.id
  replyForm.reply = ''
  replyDialogVisible.value = true
}

const handleSubmitReply = async () => {
  await replyFormRef.value.validate()
  try {
    await noticeApi.replyFeedback(replyForm)
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    loadFeedbackList(currentNotice.value.id)
  } catch (error) {
    ElMessage.error(error.message || '回复失败')
  }
}

const goToNews = () => {
  router.push('/news')
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notice-list {
  padding: 20px;
}

.filter-bar {
  padding: 10px;
  margin-bottom: 20px;
  background-color: #fff;
}


.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.notice-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

.news-panel {
  padding: 40px;
  text-align: center;
}

.news-panel p {
  margin-bottom: 20px;
  color: #666;
}

.notice-tabs :deep(.el-tabs__content) {
  padding: 0 20px;
}


.feedback-list {
  max-height: 400px;
  overflow-y: auto;
}

.feedback-item {
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 10px;
}

.feedback-content {
  font-size: 14px;
  margin-bottom: 5px;
}

.feedback-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.feedback-reply {
  margin-top: 10px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.reply-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.reply-content {
  font-size: 14px;
  white-space: pre-wrap;
}
</style>
