<template>
  <div ref="editorRef" class="wang-editor"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import wangeditor from 'wangeditor'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '请输入内容...' }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = ref(null)
let editorInstance = null

onMounted(() => {
  nextTick(() => {
    editorInstance = new wangeditor(editorRef.value)

    editorInstance.config.menus = [
      'head', 'bold', 'fontSize', 'italic', 'underline', 'strikeThrough',
      'foreColor', 'backColor', 'link', 'list', 'justify', 'quote',
      'image', 'table', 'undo', 'redo'
    ]

    editorInstance.config.placeholder = props.placeholder
    editorInstance.config.uploadImgServer = '/api/upload/image'
    editorInstance.config.uploadFileName = 'file'
    editorInstance.config.uploadImgMaxSize = 10 * 1024 * 1024
    editorInstance.config.uploadImgAccept = ['jpg', 'jpeg', 'png', 'gif', 'webp']
    editorInstance.config.showLinkImg = false
    editorInstance.config.uploadImgHooks = {
      customInsert: function (insertImg, result) {
        if ((result.code === 0 || result.code === 200) && result.data) {
          insertImg(result.data)
        } else {
          alert(result?.msg || '图片上传失败')
        }
      }
    }

    editorInstance.config.onchange = (html) => {
      emit('update:modelValue', html)
    }

    editorInstance.create()

    if (props.modelValue) {
      editorInstance.txt.html(props.modelValue)
    }
  })
})

watch(() => props.modelValue, (newVal) => {
  if (editorInstance && newVal !== editorInstance.txt.html()) {
    editorInstance.txt.html(newVal || '')
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
.wang-editor {
  width: 100%;
  min-height: 400px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

:deep(.w-e-toolbar) {
  display: flex;
  flex-wrap: wrap;
  background: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 8px;
}

:deep(.w-e-text-container) {
  min-height: 350px !important;
  background: #fff;
}

:deep(.w-e-text) {
  padding: 10px;
  min-height: 300px;
}

:deep(table) {
  border-collapse: collapse;
  width: 100%;
}

:deep(tr) {
  height: 30px;
}

:deep(td),
:deep(th) {
  border: 1px solid #333;
  min-width: 80px;
  height: 30px;
  padding: 4px 6px;
  text-align: left;
  vertical-align: top;
  background: #fff;
  box-sizing: border-box;
  position: relative;
}

:deep(th) {
  background: #f5f7fa;
  font-weight: bold;
}

:deep(td p),
:deep(th p) {
  margin: 0;
}
</style>