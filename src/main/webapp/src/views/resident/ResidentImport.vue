<template>
  <el-dialog v-model="dialogVisible" title="导入村民档案" width="500px" destroy-on-close>
    <div class="import-container">
      <!-- 步骤说明 -->
      <div class="steps">
        <el-steps :active="activeStep" finish-status="success" align-center>
          <el-step title="上传文件" />
          <el-step title="导入结果" />
        </el-steps>
      </div>

      <!-- 步骤1: 上传文件 -->
      <div v-show="activeStep === 0" class="upload-section">
        <div class="template-download">
          <el-button type="primary" link @click="handleDownloadTemplate">
            <el-icon><Download /></el-icon>
            下载导入模板
          </el-button>
        </div>

        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          :action="uploadUrl"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          :limit="1"
          accept=".xlsx,.xls"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处 或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 .xlsx, .xls 格式文件
            </div>
          </template>
        </el-upload>

        <div class="upload-tip">
          <p>• 请先下载模板，按模板格式填写数据</p>
          <p>• 身份证号必须为18位，且不能与已有数据重复</p>
          <p>• 姓名和身份证号为必填项</p>
        </div>
      </div>

      <!-- 步骤2: 导入结果 -->
      <div v-show="activeStep === 1" class="result-section">
        <div class="result-summary">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-item success">
                <div class="stat-value">{{ importResult.successCount }}</div>
                <div class="stat-label">成功</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item fail">
                <div class="stat-value">{{ importResult.failCount }}</div>
                <div class="stat-label">失败</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item total">
                <div class="stat-value">{{ importResult.totalCount }}</div>
                <div class="stat-label">总计</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 错误详情 -->
        <div v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
          <div class="error-title">错误详情</div>
          <el-table :data="importResult.errors" max-height="200" size="small">
            <el-table-column prop="rowNum" label="行号" width="80" />
            <el-table-column prop="originalData" label="原始数据" min-width="150" show-overflow-tooltip />
            <el-table-column prop="reason" label="错误原因" min-width="200" show-overflow-tooltip />
          </el-table>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="activeStep === 0" type="primary" :disabled="!selectedFile" :loading="importing" @click="handleImport">
          开始导入
        </el-button>
        <el-button v-if="activeStep === 1" type="primary" @click="handleClose">
          完成
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Download } from '@element-plus/icons-vue'
import { residentApi } from '@/request/resident'

const props = defineProps({
  visible: Boolean
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const uploadRef = ref(null)
const activeStep = ref(0)
const selectedFile = ref(null)
const importing = ref(false)

const uploadUrl = '/api/resident/import'

const importResult = ref({
  successCount: 0,
  failCount: 0,
  totalCount: 0,
  errors: []
})

watch(() => props.visible, (val) => {
  if (val) {
    // 重置状态
    activeStep.value = 0
    selectedFile.value = null
    importResult.value = {
      successCount: 0,
      failCount: 0,
      totalCount: 0,
      errors: []
    }
    if (uploadRef.value) {
      uploadRef.value.clearFiles()
    }
  }
})

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

const handleDownloadTemplate = async () => {
  try {
    const blob = await residentApi.downloadTemplate()
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = '村民档案导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('模板下载成功')
  } catch (error) {
    console.error('模板下载失败:', error)
    ElMessage.error('模板下载失败')
  }
}

const handleImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importing.value = true
  try {
    const result = await residentApi.import(selectedFile.value)
    importResult.value = result

    if (result.successCount > 0) {
      ElMessage.success(`导入成功：${result.successCount} 条`)
      emit('success')
    }

    if (result.failCount > 0) {
      ElMessage.warning(`导入完成，其中 ${result.failCount} 条失败`)
    }

    activeStep.value = 1
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error(error.message || '导入失败')
  } finally {
    importing.value = false
  }
}

const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
.import-container {
  min-height: 300px;
}

.steps {
  margin-bottom: 30px;
}

.upload-section {
  text-align: center;
}

.template-download {
  margin-bottom: 20px;
}

.upload-tip {
  margin-top: 20px;
  text-align: left;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  color: #606266;
  font-size: 13px;
}

.upload-tip p {
  margin: 5px 0;
}

.result-section {
  padding: 20px 0;
}

.result-summary {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
}

.stat-item.success {
  background: #f0f9eb;
}

.stat-item.fail {
  background: #fef0f0;
}

.stat-item.total {
  background: #f4f4f5;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
}

.stat-item.success .stat-value {
  color: #67c23a;
}

.stat-item.fail .stat-value {
  color: #f56c6c;
}

.stat-item.total .stat-value {
  color: #409eff;
}

.stat-label {
  margin-top: 8px;
  color: #909399;
}

.error-list {
  margin-top: 20px;
}

.error-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #f56c6c;
}
</style>