# ADR: 新闻富文本编辑器选型

## 背景
Issue #8 要求新闻内容支持图片（F-NEW-001），需要富文本编辑功能。CLAUDE.md 明确技术栈约束"编辑器 Milkdown 插件化"。需要决定富文本编辑器的实现方案。

## 备选方案

### 方案 A: Milkdown（基于 CLAUDE.md 约束）
- 优势: CLAUDE.md 明确规定"编辑器 Milkdown 插件化"，遵循项目技术栈约束；插件化架构，可按需扩展（图片上传、表格、代码高亮）；基于 ProseMirror，性能和扩展性强；支持 Markdown，适合技术文档编辑
- 劣势: 学习曲线较陡，需要配置插件；Vue 3 生态中成熟度低于其他编辑器；MVP 阶段可能过度设计（功能需求仅为"支持图片"）

### 方案 B: Element Plus Tiptap（vue-tiptap）
- 优势: 与项目前端技术栈一致（Vue 3 + Element Plus）；开箱即用，无需复杂配置；支持图片上传、富文本格式化；文档完善，社区活跃
- 劣势: 与 CLAUDE.md 约束冲突（明确规定 Milkdown）；违反架构约束中的技术栈约束

### 方案 C: Element Plus el-input type="textarea" + Base64 图片
- 优势: 实现最简单，无需引入额外组件；符合 MVP 原则（Minimum Viable Product）；满足"支持图片"基本需求
- 劣势: 用户体验差，无法实时预览富文本效果；图片 Base64 存储增加数据库存储压力（SQLite 单文件限制）；不支持图片尺寸调整、对齐等富文本功能

### 方案 D: Milkdown MVP 简化版（仅基础插件）
- 优势: 遵循 CLAUDE.md 约束，符合技术栈要求；MVP 阶段仅配置必要插件（图片上传、基础格式化），降低复杂度；为后续扩展预留空间（如表格、代码高亮）；与项目长期架构一致性
- 劣势: 相比方案 B 需要更多配置工作；但可通过封装组件减少使用复杂度

## 决策
选择方案 D（Milkdown MVP 简化版），因为必须遵循 CLAUDE.md 技术栈约束（"编辑器 Milkdown 插件化"），违反架构约束将导致项目技术栈混乱；MVP 阶段仅配置必要插件（commonmark、gfm、listener、image），平衡功能需求和复杂度；为后续扩展预留空间（如新闻内容可能需要代码高亮、表格）；项目已有前后端一体化部署架构，可通过后端接口处理图片上传，避免 Base64 存储压力。

## 否决项
- 方案 B: 与 CLAUDE.md 约束冲突，违反技术栈规范；架构约束明确规定"编辑器 Milkdown 插件化"，不得随意替换技术栈
- 方案 C: 用户体验差，无法满足"支持图片"的实际需求（图片预览、尺寸调整）；Base64 存储增加 SQLite 数据库压力（单文件限制 2GB）
- 方案 A: MVP 阶段过度设计，配置所有插件增加复杂度；但不影响选择 Milkdown 的核心决策

## 影响
- 前端新建 `src/main/webapp/src/components/MilkdownEditor.vue` 组件（封装 Milkdown 编辑器）
- 配置 Milkdown 必要插件：
  - `@milkdown/ctx`: 上下文管理
  - `@milkdown/core`: 核心功能
  - `@milkdown/preset-commonmark`: CommonMark 语法支持
  - `@milkdown/preset-gfm`: GitHub Flavored Markdown（表格、删除线等）
  - `@milkdown/plugin-listener`: 事件监听（用于双向绑定）
  - `@milkdown/plugin-image`: 图片上传插件
- 后端新建 `src/main/java/com/village/controller/UploadController.java` 处理图片上传（存储至 `src/main/resources/static/uploads/` 目录）
- `News` 实体 `content` 字段存储 Markdown 文本（图片以相对路径引用，如 `/uploads/news/20260427_image.png`）
- 前端 `NewsList.vue` 表单使用 `<MilkdownEditor />` 组件（替代 `el-input type="textarea"`）

## 组件接口设计
```vue
<!-- MilkdownEditor.vue -->
<template>
  <div ref="editorRef" class="milkdown-editor"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Editor, rootCtx, defaultValueCtx } from '@milkdown/core'
import { commonmark } from '@milkdown/preset-commonmark'
import { gfm } from '@milkdown/preset-gfm'
import { listener, listenerCtx } from '@milkdown/plugin-listener'
import { image } from '@milkdown/plugin-image'

const props = defineProps({
  modelValue: String, // 双向绑定内容
  placeholder: String,
})

const emit = defineEmits(['update:modelValue'])

onMounted(() => {
  Editor.make()
    .config((ctx) => {
      ctx.set(rootCtx, editorRef.value)
      ctx.set(defaultValueCtx, props.modelValue || '')
      ctx.get(listenerCtx).markdownUpdated((ctx, markdown) => {
        emit('update:modelValue', markdown)
      })
    })
    .use(commonmark)
    .use(gfm)
    .use(listener)
    .use(image)
    .create()
})
</script>
```

## 图片上传策略
1. 前端 Milkdown 图片上传插件配置自定义上传函数
2. 调用 `POST /api/upload/image` 接口（FormData 格式）
3. 后端 `UploadController` 保存图片至 `static/uploads/news/{YYYYMMDD}_{filename}`
4. 返回图片访问路径 `/uploads/news/{YYYYMMDD}_{filename}`
5. Milkdown 编辑器插入 Markdown 图片语法 `![alt](/uploads/news/...)`

## 后续扩展路径
如果未来需要更多富文本功能，可按需添加 Milkdown 插件：
- 表格: `@milkdown/plugin-table`（GFM 已包含基础表格）
- 代码高亮: `@milkdown/plugin-prism`（需引入 Prism.js）
- 数学公式: `@milkdown/plugin-math`（KaTeX 或 MathJax）
- 协同编辑: `@milkdown/plugin-sync`（Y.js 后端）
