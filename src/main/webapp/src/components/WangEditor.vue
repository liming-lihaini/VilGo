<template>
  <div ref="editorRef" class="wang-editor"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import wangeditor from 'wangeditor'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = ref(null)
let editorInstance = null

onMounted(() => {
  // 创建编辑器
  editorInstance = new wangeditor(editorRef.value)

  // 配置菜单
  editorInstance.config.menus = [
    'head',  // 标题
    'bold',  // 粗体
    'fontSize',  // 字号
    'italic',  // 斜体
    'underline',  // 下划线
    'strikeThrough',  // 删除线
    'foreColor',  // 文字颜色
    'backColor',  // 背景颜色
    'link',  // 链接
    'list',  // 列表
    'justify',  // 对齐方式
    'quote',  // 引用
    'image',  // 图片
    'table',  // 表格
    'undo',  // 撤销
    'redo'  // 重做
  ]

  // 配置占位符
  editorInstance.config.placeholder = props.placeholder

  // 配置图片上传
  editorInstance.config.uploadImgServer = '/api/upload/image'
  editorInstance.config.uploadFileName = 'file'
  editorInstance.config.uploadImgMaxSize = 10 * 1024 * 1024  // 10MB
  editorInstance.config.uploadImgAccept = ['jpg', 'jpeg', 'png', 'gif', 'webp']
  editorInstance.config.showLinkImg = false  // 禁用网络图片
  editorInstance.config.uploadImgHooks = {
    // 上传图片成功后的回调
    success: function (xhr) {
      console.log('图片上传成功', xhr)
    },
    error: function (xhr) {
      console.log('图片上传失败', xhr)
    },
    customInsert: function (insertImg, result, editor) {
      // result 是服务端返回的数据
      // our backend returns: {"code":0,"msg":"操作成功","data":"/uploads/xxx.jpg"}
      console.log('上传结果:', result)
      if ((result.code === 0 || result.code === 200) && result.data) {
        // 插入图片到编辑器，需要完整的 URL
        insertImg(result.data)
      } else {
        const msg = result?.msg || result?.message || '图片上传失败'
        console.error(msg)
        alert(msg)
      }
    }
  }

  // 配置 onchange 回调
  editorInstance.config.onchange = (html) => {
    emit('update:modelValue', html)
  }

  // 创建编辑器
  editorInstance.create()

  // 设置初始内容
  if (props.modelValue) {
    editorInstance.txt.html(props.modelValue)
  }
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
  overflow: hidden;
}

:deep(.w-e-toolbar) {
  display: flex;
  flex-wrap: wrap;
  background: #f5f5f5;
  border-bottom: 1px solid #dcdfe6;
}

:deep(.w-e-text-container) {
  min-height: 350px !important;
}
</style>
