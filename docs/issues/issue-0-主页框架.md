# ISSUE-0-主页框架

## 概述

实现村庄事务管理系统主页框架，顶部展示系统名称和用户信息，左侧提供9大功能模块导航菜单（支持展开/折叠），右侧为功能内容展示区。主框架作为其他9个功能模块的容器，提供统一的布局和导航能力。

## 功能需求

### F-HOME-001 系统名称展示

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 系统名称展示 |
| 需求描述 | 顶部区域展示系统名称、Logo、版本信息 |
| 输入 | - |
| 校验 | - |
| 输出 | 系统名称显示在顶部左侧 |
| 显示内容 | 系统名称"村庄事务管理系统"、版本号V1.0 |

### F-HOME-002 用户信息展示

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 用户信息展示 |
| 需求描述 | 顶部右侧展示当前用户信息、退出按钮 |
| 输入 | - |
| 校验 | - |
| 输出 | 用户信息显示，操作入口 |
| 显示内容 | 用户名、操作员、退出按钮 |

### F-HOME-003 功能导航菜单

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 功能导航菜单 |
| 需求描述 | 左侧菜单展示9大功能模块，支持展开/折叠 |
| 输入 | - |
| 校验 | - |
| 输出 | 菜单列表，点击跳转 |
| 菜单项 | 村民档案、户籍档案、特殊人群、两委班子、公益活动、党务管理、公示栏、新闻动态、数字大屏 |
| 展开/折叠 | 支持单个菜单展开，多个菜单可同时展开 |

### F-HOME-004 菜单折叠功能

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 菜单折叠功能 |
| 需求描述 | 左侧菜单支持折叠，节省展示空间 |
| 输入 | 折叠按钮 |
| 校验 | - |
| 输出 | 菜单宽度切换（200px ↔ 60px） |
| 折叠状态 | 仅显示图标，展开显示图标+文字 |

### F-HOME-005 内容展示区

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 内容展示区 |
| 需求描述 | 右侧区域展示当前选中功能模块内容 |
| 输入 | 菜单路由 |
| 校验 | - |
| 输出 | 功能页面加载 |
| 路由规则 | /views/{module} |

### F-HOME-006 路由跳转

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 路由跳转 |
| 需求描述 | 点击菜单项，右侧内容区加载对应功能页面 |
| 输入 | 菜单项 |
| 校验 | - |
| 输出 | 路由切换，内容更新 |
| 默认页面 | 村民档案列表（/views/resident） |

## 技术方案

### 前端界面设计

#### 页面结构

```
┌────────────────────────────────────────────────────────────┐
│  [Logo] 村庄事务管理系统 V1.0           用户：管理员 [退出] │
├───────────┬──────────────────────────────────────────────────┤
│  村民档案 │                                           │
│  ├ 档案列表│                                           │
│  ├ 新增档案│                                           │
│  └ 档案统计│                                          │
│  户籍档案 │                                           │
│  ├ 家庭户 │                                           │
│  ├ 成员管理│                                           │
│  └ 户籍变动│                                          │
│  特殊人群 │                                           │
│  两委班子 │                                           │
│  公益活动 │                                           │
│  党务管理 │                                           │
│  公示栏   │                                           │
│  新闻动态 │                                           │
│  数字大屏 │                                           │
│  ─────── │                                           │
│  [折叠] │                                           │
├───────────┴──────────────────────────────────────────────────┤
│  [Footer: 版权所有]                                     │
└─────────────────────────────────────────────────────┘
```

#### 展开状态布局

```
┌────────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────────┐   │
│  │  村庄事务管理系统 V1.0              用户：管理员    │   │
│  └─────────────────────────────────────────────────────┘   │
│  ┌──────┬──────────────────────────────────────────┐   │
│  │ 村民 │  ┌─────────────────────────────────────┐   │   │
│  │ ├列表│  │ 档案列表                            │   │   │
│  │ ├新增│  │                                     │   │   │
│  │ └统计│  │                                     │   │   │
│  │ 户籍 │  │                                     │   │   │
│  │ 特殊 │  │                                     │   │   │
│  │ 两委 │  │                                     │   │   │
│  │ 活动 │  │                                     │   │   │
│  │ 党务 │  │                                     │   │   │
│  │ 公示 │  │                                     │   │   │
│  │ 新闻 │  │                                     │   │   │
│  │ 大屏 │  │                                     │   │   │
│  │ [折叠]│  └─────────────────────────────────────┘   │   │
│  └──────┴──────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
```

#### 折叠状态布局

```
┌────────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────────┐   │
│  │  村庄事务管理系统 V1.0              用户：管理员    │   │
│  └─────────────────────────────────────────────────────┘   │
│  ┌──┬───────────────────────────────────────────────┐   │
│  │📂│  ┌─────────────────────────────────────┐   │   │
│  │📂│  │ 档案列表                            │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │📂│  │                                     │   │   │
│  │[展开]│ └─────────────────────────────────────┘   │   │
│  └──┴───────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
```

#### 关键组件

| 组件 | 描述 | 事件 |
|------|------|------|
| `AppHeader` | 顶部系统名称栏 | - |
| `AppSidebar` | 左侧导航菜单 | `select`, `collapse`, `expand` |
| `MenuItem` | 菜单项组件 | `click` |
| `SubMenu` | 子菜单组件 | `click` |
| `CollapseButton` | 折叠按钮 | `click` |
| `AppMain` | 右侧内容区 | `route-change` |
| `AppFooter` | 底部版权栏 | - |

### 路由设计

| 路由 | 组件 | 说明 |
|------|------|------|
| / | Redirect to /views/resident | 首页重定向 |
| /views/resident | ResidentList | 村民档案 |
| /views/household | HouseholdList | 户籍档案 |
| /views/specialGroup | SpecialGroupList | 特殊人群 |
| /views/twoCommittee | TwoCommitteeList | 两委班子 |
| /views/publicActivity | PublicActivityList | 公益活动 |
| /views/partyWork | PartyWorkList | 党务管理 |
| /views/notice | NoticeList | 村委公示栏 |
| /views/news | NewsList | 新闻动态 |
| /views/screen | ScreenDashboard | 数字大屏 |

### 菜单配置

```javascript
const menuItems = [
  {
    id: 'resident',
    title: '村民档案',
    icon: 'user',
    path: '/views/resident',
    children: [
      { id: 'resident-list', title: '档案列表', path: '/views/resident' },
      { id: 'resident-create', title: '新增档案', path: '/views/resident/create' },
      { id: 'resident-stat', title: '档案统计', path: '/views/resident/statistics' }
    ]
  },
  {
    id: 'household',
    title: '户籍档案',
    icon: 'home',
    path: '/views/household',
    children: [
      { id: 'household-list', title: '家庭户', path: '/views/household' },
      { id: 'household-member', title: '成员管理', path: '/views/household/member' },
      { id: 'household-change', title: '户籍变动', path: '/views/household/change' }
    ]
  },
  { id: 'specialGroup', title: '特殊人群', icon: 'heart', path: '/views/specialGroup' },
  { id: 'twoCommittee', title: '两委班子', icon: 'people', path: '/views/twoCommittee' },
  { id: 'publicActivity', title: '公益活动', icon: 'activity', path: '/views/publicActivity' },
  { id: 'partyWork', title: '党务管理', icon: 'star', path: '/views/partyWork' },
  { id: 'notice', title: '村委公示栏', icon: 'notice', path: '/views/notice' },
  { id: 'news', title: '新闻动态', icon: 'news', path: '/views/news' },
  { id: 'screen', title: '数字大屏', icon: 'screen', path: '/views/screen' }
]
```

### 布局样式

```css
/* 顶部栏 */
.app-header {
  height: 60px;
  background: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

/* 左侧菜单 */
.app-sidebar {
  width: 200px;  /* 展开态 */
  width: 60px;   /* 折叠态 */
  background: #fff;
  border-right: 1px solid #e8e8e8;
  transition: width 0.3s;
}

/* 右侧内容区 */
.app-main {
  flex: 1;
  background: #f5f5f5;
  padding: 16px;
  overflow: auto;
}

/* 底部版权栏 */
.app-footer {
  height: 40px;
  background: #fff;
  border-top: 1px solid #e8e8e8;
  text-align: center;
  line-height: 40px;
  color: #999;
}
```

### 项目结构

```
src/main/webapp/src/
├── views/
│   └── Layout.vue          # 主页框架
├── components/
│   └── layout/
│       ├── AppHeader.vue  # 顶部栏
│       ├── AppSidebar.vue  # 左侧菜单
│       ├── AppMain.vue    # 内容区
│       └── AppFooter.vue  # 底部栏
├── router/
│   └── index.js          # 路由配置
└── App.vue              # 根组件
```

### 业务规则

1. 菜单折叠状态持久化到本地存储
2. 首次登录默认展开全部菜单
3. 当前激活菜单高亮显示
4. 路由变化时菜单同步激活状态
5. 响应式布局，最小宽度1200px

## 验收条件

- [ ] 系统名称正确显示
- [ ] 用户信息显示正确
- [ ] 9大菜单项完整显示
- [ ] 点击菜单跳转正确
- [ ] 菜单展开/折叠正常
- [ ] 折叠状态保留
- [ ] 路由变化菜单同步
- [ ] 默认页面正确加载
- [ ] 全屏模式下数字大屏正常
- [ ] 响应式布局正常

## 依赖

- Vue Router 路由模块
- Element Plus 组件库
- 各功能模块组件（ISSUE-1~9）

## 状态

- [ ] 待开发
- [ ] 开发中
- [ ] 待测试
- [ ] 完成