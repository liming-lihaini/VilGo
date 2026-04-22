# ADR: 村民档案前端页面结构设计

## 背景

Issue #1 村民档案管理需要前端页面支持列表展示、新增/编辑表单、查询条件栏、统计弹窗等功能。根据 CLAUDE.md 前端约束，页面放在 views/ 目录，组件放在 components/ 目录。

## 备选方案

### 方案 A: 目录式组织（views/resident/）

```
views/resident/
├── ResidentList.vue      # 列表页
├── ResidentForm.vue      # 新增/编辑弹窗
└── ResidentStatistics.vue # 统计弹窗
```

- 优势: 模块化清晰，一个功能模块一个目录；符合 CLAUDE.md views/ 目录结构要求；组件复用方便
- 劣势: 文件数量略多

### 方案 B: 单页面 + 组件

```
views/Resident.vue        # 主页面（包含列表+查询）
components/
├── ResidentForm.vue
└── ResidentStatistics.vue
```

- 优势: 文件数量少
- 劣势: 主页面代码量会很大，难以维护；不符合 views/ 目录按模块组织页面的规范

## 决策

选择方案 A（目录式组织），因为严格遵循 CLAUDE.md 前端目录结构约束；村民档案是独立功能模块，独立目录管理更清晰；Element Plus 组件化开发模式下，弹窗组件独立更易维护。

## 否决项

- 方案 B: 主页面代码膨胀，难以维护；不符合前端目录结构约束

## 影响

前端文件结构：
- `src/main/webapp/src/views/resident/ResidentList.vue` - 档案列表页（含查询工具栏）
- `src/main/webapp/src/views/resident/ResidentForm.vue` - 新增/编辑表单弹窗组件
- `src/main/webapp/src/views/resident/ResidentStatistics.vue` - 人口统计弹窗组件
- `src/main/webapp/src/api/resident.js` - API 请求封装（按 CLAUDE.md 约束）
- `src/main/webapp/src/router/index.js` - 添加路由配置（懒加载）