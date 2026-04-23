<template>
  <el-drawer v-model="drawerVisible" :title="drawerTitle" size="1000px" destroy-on-close>
    <div class="drawer-content">
      <!-- 基本信息 -->
      <div class="section">
        <div class="section-header">
          <span class="section-title">基本信息</span>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ residentData.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ residentData.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ residentData.idCard }}</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ residentData.birthDate }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ residentData.phone }}</el-descriptions-item>
          <el-descriptions-item label="人员类型" :span="2">{{ getPersonTypeName(residentData.personType) }}</el-descriptions-item>
          <el-descriptions-item label="本村户籍">
            <el-tag :type="residentData.isLocalHousehold === 1 ? 'success' : 'info'" size="small">
              {{ residentData.isLocalHousehold === 1 ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="户主">
            <el-tag :type="residentData.isHouseholdHead === 1 ? 'success' : 'info'" size="small">
              {{ residentData.isHouseholdHead === 1 ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="本村常住">
            <el-tag :type="residentData.isLocalResident === 1 ? 'success' : 'info'" size="small">
              {{ residentData.isLocalResident === 1 ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="户籍状态">
            <el-tag :type="residentData.householdStatus === 'normal' ? 'success' : 'danger'" size="small">
              {{ residentData.householdStatus === 'normal' ? '正常' : '已销户' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="住址" :span="2">{{ residentData.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="外地地址" :span="2">{{ residentData.externalAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="保障类型" :span="2">
            <el-tag v-for="type in (residentData.securityType || '').split(',')" :key="type" size="small" style="margin-right: 4px" v-show="type">
              {{ getSecurityTypeName(type) }}
            </el-tag>
            <span v-if="!residentData.securityType">-</span>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ residentData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 附件信息 -->
      <div class="section">
        <div class="section-header">
          <span class="section-title">附件信息</span>
          <el-button type="primary" size="small" @click="openAttachmentDialog">管理附件</el-button>
        </div>
        <el-tabs v-model="activeTab" class="drawer-tabs">
          <el-tab-pane v-for="category in categories" :key="category.value" :label="category.label" :name="category.value">
            <div class="attachment-grid" v-if="filterByCategory(category.value).length > 0">
              <div v-for="item in filterByCategory(category.value)" :key="item.id" class="attachment-card" @click="previewFile(item)">
                <!-- 图片预览 -->
                <div v-if="isImage(item)" class="attachment-preview">
                  <img :src="item.filePath" :alt="item.fileName" />
                </div>
                <!-- PDF预览 -->
                <div v-else-if="isPdf(item)" class="attachment-preview pdf">
                  <el-icon :size="36"><Document /></el-icon>
                  <span>PDF</span>
                </div>
                <!-- 不支持格式 -->
                <div v-else class="attachment-preview unsupported">
                  <el-icon :size="36"><Document /></el-icon>
                  <span>{{ getExtension(item.fileName) }}</span>
                </div>
                <div class="attachment-name">{{ item.fileName }}</div>
              </div>
            </div>
            <el-empty v-else description="暂无附件" :image-size="60" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 附件管理弹窗 -->
    <ResidentAttachment
      v-model:visible="attachmentDialogVisible"
      :resident-id="residentData.id"
      :resident-name="residentData.name"
      @refresh="loadAttachments"
    />

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
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { residentApi } from '@/request/resident'
import { Document, FullScreen } from '@element-plus/icons-vue'
import ResidentAttachment from './ResidentAttachment.vue'

const props = defineProps({
  visible: Boolean,
  residentId: Number
})

const emit = defineEmits(['update:visible'])

const drawerVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const drawerTitle = ref('村民档案详情')
const residentData = ref({})
const attachments = ref([])
const activeTab = ref('id_card')
const attachmentDialogVisible = ref(false)

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

const personTypeMap = {
  farmer: '农民',
  worker: '工人',
  teacher: '教师',
  doctor: '医生',
  student: '学生',
  other: '其他'
}

const securityTypeMap = {
  pension: '社会养老',
  worker_pension: '职工养老',
  allowance: '低保',
  five_guarantee: '五保',
  other: '其他',
  none: '无'
}

const getPersonTypeName = (type) => personTypeMap[type] || '-'
const getSecurityTypeName = (type) => securityTypeMap[type] || type

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

const filterByCategory = (category) => {
  return attachments.value.filter(item => item.fileCategory === category)
}

const loadData = async () => {
  if (!props.visible || !props.residentId) return

  try {
    const [resident, attachmentList] = await Promise.all([
      residentApi.get(props.residentId),
      residentApi.getAttachments(props.residentId)
    ])
    residentData.value = resident || {}
    attachments.value = attachmentList || []
    drawerTitle.value = `${resident?.name || ''} 的档案详情`
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const loadAttachments = async () => {
  if (!props.residentId) return
  try {
    const list = await residentApi.getAttachments(props.residentId)
    attachments.value = list || []
  } catch (error) {
    console.error('加载附件失败:', error)
  }
}

const openAttachmentDialog = () => {
  attachmentDialogVisible.value = true
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

watch(() => props.visible, (val) => {
  if (val) {
    loadData()
  }
})
</script>

<style scoped>
.drawer-content {
  padding: 0 20px;
}

.section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-header .section-title {
  margin-bottom: 0;
}

.drawer-tabs {
  margin-top: 12px;
}

.attachment-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.attachment-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.attachment-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.attachment-preview {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.attachment-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attachment-preview.pdf,
.attachment-preview.unsupported {
  flex-direction: column;
  color: #909399;
  gap: 4px;
}

.attachment-preview.pdf span,
.attachment-preview.unsupported span {
  font-size: 11px;
}

.attachment-name {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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