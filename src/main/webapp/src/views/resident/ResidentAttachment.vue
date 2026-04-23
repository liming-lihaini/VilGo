<template>
  <el-dialog v-model="dialogVisible" title="村民附件管理" width="700px" destroy-on-close>
    <div class="attachment-container">
      <!-- 上传区域 -->
      <div class="upload-section">
        <el-select v-model="uploadCategory" placeholder="选择附件类型" style="width: 180px; margin-right: 10px">
          <el-option label="身份证照片" value="id_card" />
          <el-option label="个人照片" value="personal_photo" />
          <el-option label="身份证复印件" value="copy" />
          <el-option label="社保卡复印件" value="social_security_copy" />
          <el-option label="户口本复印件" value="household_register_copy" />
          <el-option label="其他" value="other" />
        </el-select>
        <el-upload
          :action="uploadUrl"
          :data="uploadData"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :show-file-list="false"
          :disabled="!uploadCategory"
          accept="image/*,.pdf"
        >
          <el-button type="primary" :disabled="!uploadCategory">上传附件</el-button>
        </el-upload>
        <span class="upload-tip">支持 jpg、png、pdf 格式</span>
      </div>

      <!-- 附件列表 - 按分类展示 -->
      <el-tabs v-model="activeTab" class="attachment-tabs">
        <el-tab-pane v-for="category in categories" :key="category.value" :label="category.label" :name="category.value">
          <div class="attachment-grid" v-if="filterByCategory(category.value).length > 0">
            <div v-for="item in filterByCategory(category.value)" :key="item.id" class="attachment-card">
              <div class="attachment-preview" @click="previewFile(item)">
                <!-- 图片预览 -->
                <img v-if="isImage(item)" :src="item.filePath" :alt="item.fileName" class="preview-img" />
                <!-- PDF预览 -->
                <div v-else-if="isPdf(item)" class="pdf-preview">
                  <el-icon :size="40"><Document /></el-icon>
                  <span>PDF</span>
                </div>
                <!-- 不支持的格式 -->
                <div v-else class="unsupported-preview">
                  <el-icon :size="40"><Document /></el-icon>
                  <span>{{ getExtension(item.fileName) }}</span>
                </div>
              </div>
              <div class="attachment-name">{{ item.fileName }}</div>
              <div class="attachment-actions">
                <el-button type="primary" link @click="previewFile(item)">预览</el-button>
                <el-button type="danger" link @click="handleDelete(item)">删除</el-button>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无附件" :image-size="80" />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="附件预览" :width="isFullscreen ? '100%' : '800px'" :fullscreen="isFullscreen" append-to-body destroy-on-close>
      <template #header>
        <div class="preview-header">
          <span>{{ previewName }}</span>
          <el-button type="primary" link @click="toggleFullscreen">
            <el-icon><FullScreen /></el-icon>
            {{ isFullscreen ? '退出全屏' : '全屏' }}
          </el-button>
        </div>
      </template>
      <div class="preview-container" :class="{ fullscreen: isFullscreen }">
        <img v-if="previewType === 'image'" :src="previewUrl" :alt="previewName" class="preview-image" />
        <iframe v-else-if="previewType === 'pdf'" :src="previewUrl" class="preview-iframe"></iframe>
        <div v-else class="preview-unsupported">
          <el-icon :size="60"><Document /></el-icon>
          <p>暂不支持此文件格式的预览</p>
          <el-button type="primary" @click="downloadFile">下载文件</el-button>
        </div>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, FullScreen } from '@element-plus/icons-vue'
import { residentApi } from '@/request/resident'

const props = defineProps({
  visible: Boolean,
  residentId: Number,
  residentName: String
})

const emit = defineEmits(['update:visible', 'refresh'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const attachments = ref([])
const uploadCategory = ref('')
const activeTab = ref('id_card')
const uploadUrl = '/api/resident/attachment/upload'

// 附件分类
const categories = [
  { label: '身份证照片', value: 'id_card' },
  { label: '个人照片', value: 'personal_photo' },
  { label: '身份证复印件', value: 'copy' },
  { label: '社保卡复印件', value: 'social_security_copy' },
  { label: '户口本复印件', value: 'household_register_copy' },
  { label: '其他', value: 'other' }
]

// 预览相关
const previewVisible = ref(false)
const previewUrl = ref('')
const previewType = ref('')
const previewName = ref('')
const isFullscreen = ref(false)

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const uploadData = computed(() => ({
  residentId: props.residentId,
  fileCategory: uploadCategory.value
}))

watch(() => props.visible, (val) => {
  if (val && props.residentId) {
    loadAttachments()
  }
})

const loadAttachments = async () => {
  try {
    const res = await residentApi.getAttachments(props.residentId)
    attachments.value = res || []
  } catch (error) {
    console.error('加载附件失败:', error)
  }
}

const filterByCategory = (category) => {
  return attachments.value.filter(item => item.fileCategory === category)
}

const getExtension = (filename) => {
  if (!filename) return ''
  const ext = filename.split('.').pop()?.toLowerCase()
  return ext || ''
}

const isImage = (item) => {
  if (!item.fileType) return false
  const imageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp', 'image/bmp']
  return imageTypes.includes(item.fileType.toLowerCase()) || ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'].includes(getExtension(item.fileName))
}

const isPdf = (item) => {
  if (!item.fileType) return false
  return item.fileType.toLowerCase() === 'application/pdf' || getExtension(item.fileName).toLowerCase() === 'pdf'
}

const getPreviewType = (item) => {
  if (isImage(item)) return 'image'
  if (isPdf(item)) return 'pdf'
  return 'unsupported'
}

const beforeUpload = (file) => {
  if (!uploadCategory.value) {
    ElMessage.warning('请先选择附件类型')
    return false
  }
  // 限制格式
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp', 'image/bmp', 'application/pdf']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.warning('仅支持 jpg、png、pdf 格式')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  ElMessage.success('上传成功')
  loadAttachments()
  emit('refresh')
}

const handleUploadError = (error) => {
  ElMessage.error('上传失败: ' + error.message)
}

const previewFile = (item) => {
  previewUrl.value = item.filePath
  previewType.value = getPreviewType(item)
  previewName.value = item.fileName
  previewVisible.value = true
}

const downloadFile = () => {
  const link = document.createElement('a')
  link.href = previewUrl.value
  link.download = previewName.value
  link.click()
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除该附件吗？', '提示', { type: 'warning' })
    await residentApi.deleteAttachment(item.id)
    ElMessage.success('删除成功')
    loadAttachments()
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}
</script>

<style scoped>
.attachment-container {
  min-height: 400px;
}

.upload-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.upload-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.attachment-tabs {
  margin-top: 15px;
}

.attachment-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.attachment-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
  text-align: center;
  transition: all 0.3s;
}

.attachment-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
}

.attachment-preview {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pdf-preview, .unsupported-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  gap: 4px;
}

.pdf-preview span, .unsupported-preview span {
  font-size: 12px;
}

.attachment-name {
  font-size: 12px;
  color: #606266;
  margin: 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attachment-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.preview-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.preview-container.fullscreen {
  min-height: calc(100vh - 80px);
  background: #000;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-container.fullscreen .preview-image {
  max-height: calc(100vh - 120px);
}

.preview-iframe {
  width: 100%;
  height: 70vh;
  border: none;
}

.preview-container.fullscreen .preview-iframe {
  height: calc(100vh - 120px);
}

.preview-unsupported {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.preview-unsupported p {
  margin: 16px 0;
}
</style>