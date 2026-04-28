<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑新闻' : '创建新闻'"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="新闻标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入新闻标题" maxlength="100" show-word-limit />
      </el-form-item>

      <el-form-item label="封面图片" prop="coverImage">
        <el-upload
          class="cover-uploader"
          :auto-upload="false"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.gif,.webp"
          :on-change="handleCoverChange"
        >
          <img v-if="form.coverImage" :src="form.coverImage" class="cover-image" />
          <el-icon v-else class="upload-icon"><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">建议尺寸: 800x450 像素，支持 jpg、png、gif、webp 格式</div>
      </el-form-item>

      <el-form-item label="新闻分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择新闻分类" style="width: 100%">
          <el-option label="政策解读" value="policy_interpretation" />
          <el-option label="活动报道" value="activity_report" />
          <el-option label="先进事迹" value="advanced_deeds" />
          <el-option label="通知公告" value="notice_announcement" />
        </el-select>
      </el-form-item>

     <el-form-item label="关键词" prop="keywords">
        <el-input v-model="form.keywords" placeholder="请输入关键词，多个用逗号分隔" />
      </el-form-item>

      <el-form-item label="新闻内容" prop="content">
        <WangEditor v-model="form.content" placeholder="请输入新闻内容，支持图片和表格" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import WangEditor from '@/components/WangEditor.vue'
import { newsApi } from '@/request/news'
import { uploadApi } from '@/request/upload'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  news: {
    type: Object,
    default: null
  },
  defaultCategory: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const isEdit = computed(() => props.news !== null)

const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  id: null,
  title: '',
  content: '',
  category: '',
  publishTime: '',
  coverImage: '',
  keywords: '',
  creator: 'admin'
})

const rules = {
  title: [
    { required: true, message: '新闻标题不能为空', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '新闻内容不能为空', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '新闻分类不能为空', trigger: 'change' }
  ]
}

watch(() => props.news, (news) => {
  if (news) {
    form.id = news.id
    form.title = news.title
    form.content = news.content
    form.category = news.category
    form.publishTime = news.publishTime
    form.coverImage = news.coverImage || ''
    form.keywords = news.keywords || ''
  } else {
    resetForm()
  }
}, { immediate: true })

watch(() => props.defaultCategory, (cat) => {
  if (cat && !props.news) {
    form.category = cat
  }
}, { immediate: true })

function resetForm() {
  form.id = null
  form.title = ''
  form.content = ''
  form.category = ''
  form.publishTime = ''
  form.coverImage = ''
  form.keywords = ''
  form.creator = 'admin'
  formRef.value?.clearValidate()
}

function handleClose() {
  dialogVisible.value = false
  resetForm()
}

// 处理封面图片上传
async function handleCoverChange(file) {
  const isImage = file.raw.type.startsWith('image/')
  const isLt10M = file.raw.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return
  }

  try {
    const url = await uploadApi.uploadImage(file.raw)
    form.coverImage = url
    ElMessage.success('封面上传成功')
  } catch (error) {
    console.error('封面上传失败:', error)
    ElMessage.error('封面上传失败')
  }
}

async function handleSubmit() {
  try {
    await formRef.value.validate()

    submitting.value = true

    if (isEdit.value) {
      await newsApi.update(form)
      ElMessage.success('新闻更新成功')
    } else {
      await newsApi.create(form)
      ElMessage.success('新闻创建成功')
    }

    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.errors) {
      return
    }
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
:deep(.el-dialog__body) {
  padding: 20px;
}

.cover-uploader {
  width: 300px;
  height: 170px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
