# Pipeline State - Issue 0

## Task

实现村庄事务管理系统主页框架，包含：
- 顶部系统名称展示区（系统名称"村庄事务管理系统 V1.0"）
- 左侧功能导航菜单（9大功能模块，支持展开/折叠）
- 右侧功能内容展示区（路由内容加载）
- 数据临时使用mock方式代替

## Boundaries

### 范围之内
- 主页框架Layout组件
- 顶部AppHeader组件
- 左侧AppSidebar菜单组件（支持展开/折叠）
- 右侧AppMain内容区组件
- 底部AppFooter版权栏组件
- Vue Router路由配置
- 9大功能模块的占位组件（Mock数据）
- 菜单折叠状态持久化

### 范围之外
- 后端API接口（使用Mock代替）
- 真实数据库（使用Mock代替）
- 其他9个功能模块的具体实现
- 权限管理功能
- 用户登录功能

## Technical Stack

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端 | Vue 3 + Element Plus | 组合式API |
| 路由 | Vue Router 4 | 路由管理 |
| 构建 | Vite 5 | 开发/构建 |
| 数据 | Mock | 临时Mock数据 |

## Current Stage

deliver

## References

- docs/issues/issue-0-主页框架.md - Issue描述
- docs/knowledge/stages/understand.md - 阶段知识
- docs/knowledge/stages/design.md - 阶段知识
- docs/knowledge/stages/plan.md - 阶段知识
- docs/knowledge/stages/implement.md - 阶段知识
- docs/knowledge/rulers/Code.md - 编码规则
- docs/knowledge/rulers/SEC.md - 安全规则

## Open Questions

- 菜单折叠状态存储位置：localStorage vs sessionStorage？
- 默认激活菜单：村民档案是否正确？

## Decisions

- 数据使用Mock代替，不连接后端
- 布局使用Flexbox实现
- 菜单使用Element Plus Menu组件

## Pipeline Log

详见 `.claude/pipeline-log-0.md`