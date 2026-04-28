<template>
  <div class="milkdown-editor">
    <div class="editor-toolbar">
      <el-button size="small" @click="insertImage">
        <el-icon><Picture /></el-icon>
        插入图片
      </el-button>
      <el-button size="small" @click="insertTable">
        <el-icon><Grid /></el-icon>
        插入表格
      </el-button>
      <el-button size="small" @click="insertDivider">
        <el-icon><Minus /></el-icon>
        分割线
      </el-button>
    </div>
    <div v-if="!editorReady" class="editor-placeholder">编辑器加载中...</div>
    <div v-show="editorReady" ref="editorRef" class="editor-content"></div>

    <!-- 图片上传对话框 -->
    <el-dialog v-model="imageDialogVisible" title="插入图片" width="400px" append-to-body>
      <el-upload
        ref="imageUploadRef"
        class="image-uploader"
        :auto-upload="false"
        :show-file-list="false"
        accept=".jpg,.jpeg,.png,.gif,.webp"
        :on-change="handleImageChange"
      >
        <img v-if="previewImage" :src="previewImage" class="image-preview" />
        <el-icon v-else class="upload-icon"><Plus /></el-icon>
      </el-upload>
      <template #footer>
        <el-button @click="imageDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="confirmImageInsert">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, Grid, Minus, Plus } from '@element-plus/icons-vue'
import { Editor, rootCtx, defaultValueCtx } from '@milkdown/core'
import { listener, listenerCtx } from '@milkdown/plugin-listener'
import { history } from '@milkdown/plugin-history'
import { clipboard } from '@milkdown/plugin-clipboard'
import { commonmark } from '@milkdown/preset-commonmark'
import { gfm } from '@milkdown/preset-gfm'
import { nord } from '@milkdown/theme-nord'
import { uploadApi } from '@/request/upload'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入内容，支持 Markdown 格式...'
  }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = ref(null)
const editorReady = ref(false)
let editorInstance = null

// 图片上传相关
const imageDialogVisible = ref(false)
const previewImage = ref('')
const uploading = ref(false)
const selectedFile = ref(null)
const imageUploadRef = ref(null)

onMounted(async () => {
  await nextTick()
  if (!editorRef.value) return

  try {
    editorInstance = await Editor.make()
      .config((ctx) => {
        ctx.set(rootCtx, editorRef.value)
        ctx.set(defaultValueCtx, props.modelValue || '')
        ctx.get(listenerCtx).markdownUpdated((ctx, markdown, prevMarkdown) => {
          emit('update:modelValue', markdown)
        })
      })
      .use(nord)
      .use(commonmark)
      .use(gfm)
      .use(history)
      .use(clipboard)
      .use(listener)
      .create()

    editorReady.value = true
  } catch (e) {
    console.error('编辑器初始化失败:', e)
    editorReady.value = true
  }
})

// 插入图片
function insertImage() {
  previewImage.value = ''
  selectedFile.value = null
  imageDialogVisible.value = true
}

// 处理图片选择
function handleImageChange(file) {
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

  selectedFile.value = file.raw
  previewImage.value = URL.createObjectURL(file.raw)
}

// 确认插入图片
async function confirmImageInsert() {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择图片')
    return
  }

  uploading.value = true
  try {
    const url = await uploadApi.uploadImage(selectedFile.value)
    // 插入 Markdown 图片语法
    const imageMarkdown = `![图片](${url})\n`
    insertMarkdown(imageMarkdown)
    imageDialogVisible.value = false
    ElMessage.success('图片插入成功')
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  } finally {
    uploading.value = false
  }
}

// 插入 Markdown 内容
function insertMarkdown(text) {
  if (editorInstance) {
    const { state } = editorInstance
    const { tr } = state
    const { from } = state.selection

    tr.insertText(text, from, from)
    editorInstance.view.dispatch(tr)
  }
}

// 插入表格
function insertTable() {
  const tableMarkdown = `| 表头1 | 表头2 | 表头3 |
|------|------|------|
| 内容1 | 内容2 | 内容3 |
| 内容4 | 内容5 | 内容6 |
`
  insertMarkdown(tableMarkdown)
  ElMessage.success('表格插入成功')
}

// 插入分割线
function insertDivider() {
  insertMarkdown('\n---\n')
}

watch(() => props.modelValue, (newVal) => {
  if (editorInstance && editorInstance.get() !== newVal) {
    editorInstance.action((ctx) => {
      const defaultValue = ctx.get(defaultValueCtx)
      if (defaultValue !== newVal) {
        ctx.set(defaultValueCtx, newVal || '')
      }
    })
  }
})

onBeforeUnmount(() => {
  if (editorInstance) {
    editorInstance.destroy()
    editorInstance = null
  }
})
</script>

<style scoped>
.milkdown-editor {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.editor-placeholder {
  padding: 60px;
  text-align: center;
  color: #909399;
  background-color: #f5f7fa;
}

.editor-content {
  width: 100%;
  min-height: 350px;
}

.editor-content :deep(.milkdown) {
  padding: 16px;
  min-height: 350px;
}

.editor-content :deep(.milkdown:focus) {
  outline: none;
}

.editor-content :deep(.ProseMirror) {
  min-height: 300px;
  padding: 12px;
  outline: none;
  font-size: 14px;
  line-height: 1.8;
}

.editor-content :deep(.ProseMirror p) {
  margin: 0.5em 0;
}

.editor-content :deep(.ProseMirror h1) {
  font-size: 1.8em;
  font-weight: bold;
  margin: 0.8em 0 0.4em;
}

.editor-content :deep(.ProseMirror h2) {
  font-size: 1.5em;
  font-weight: bold;
  margin: 0.7em 0 0.3em;
}

.editor-content :deep(.ProseMirror h3) {
  font-size: 1.3em;
  font-weight: bold;
  margin: 0.6em 0 0.3em;
}

.editor-content :deep(.ProseMirror ul),
.editor-content :deep(.ProseMirror ol) {
  padding-left: 1.5em;
  margin: 0.5em 0;
}

.editor-content :deep(.ProseMirror code) {
  background: #f0f0f0;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: monospace;
}

.editor-content :deep(.ProseMirror pre) {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
}

.editor-content :deep(.ProseMirror pre code) {
  background: none;
  padding: 0;
}

.editor-content :deep(.ProseMirror blockquote) {
  border-left: 3px solid #409eff;
  margin: 0.5em 0;
  padding-left: 1em;
  color: #606266;
}

.editor-content :deep(.ProseMirror img) {
  max-width: 100%;
  height: auto;
}

.editor-content :deep(.ProseMirror table) {
  border-collapse: collapse;
  width: 100%;
  margin: 0.5em 0;
}

.editor-content :deep(.ProseMirror table td),
.editor-content :deep(.ProseMirror table th) {
  border: 1px solid #dcdfe6;
  padding: 8px 12px;
  text-align: left;
}

.editor-content :deep(.ProseMirror table th) {
  background: #f5f7fa;
  font-weight: bold;
}

.editor-content :deep(.ProseMirror a) {
  color: #409eff;
  text-decoration: none;
}

.editor-content :deep(.ProseMirror a:hover) {
  text-decoration: underline;
}

.editor-content :deep(.ProseMirror hr) {
  border: none;
  border-top: 1px solid #dcdfe6;
  margin: 1em 0;
}

.image-uploader {
  width: 100%;
  text-align: center;
}

.image-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 150px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.image-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
}

.image-preview {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
</style>
