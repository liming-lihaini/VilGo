# Pipeline State — Issue #4 两委班子管理

> 子 agent 写入，主 agent 消费（提取 References 构造后续 prompt）。

## Project Context

项目: village-affairs-management | 阶段: MVP | Pipeline 版本: v1
技术栈: JDK 17, Spring Boot 2.7, MyBatis-Plus 3.5, Vue 3, SQLite 3
架构: Controller → Service → Dao → Entity (单向依赖)
设计原则: 前后端一体化，单机部署，本地数据库

## Task

Issue: #4
Title: 两委班子管理
Issue 类型: 新功能
一句话描述: 管理村两委成员信息、任务分配与跟踪、会议记录，支持班子成员查询和任务统计
用户场景: 村级工作人员管理两委成员，分配任务，记录会议
为什么做: 实现两委班子信息化管理，提升工作透明度
来源: docs/issues/issue-4-两委班子管理.md (F-TC-001 ~ F-TC-008)

## Decisions

### 决策 1: 成员职务数据来源
选择: 方案 A（固定职务列表） | 原因: 职务类型固定（书记/主任/副主任/委员）
影响: 前端使用下拉选择
阶段: design

### 决策 2: 任务状态流转
选择: 方案 A（待处理→进行中→已完成） | 原因: 状态流转不可逆，符合实际工作流程
影响: 前端状态按钮控制
阶段: design

### 决策 3: 会议决议转任务
选择: 方案 A（弹窗选择接收人和期限） | 原因: 操作直观
影响: 会议详情页添加转化按钮
阶段: design

## Task

1. 数据库表结构设计（3张表：two_committee, tasks, meetings）
2. 后端实体类创建（TwoCommittee, Task, Meeting）
3. 后端 DTO 创建
4. 后端 DAO 创建
5. 后端 Service 接口和实现
6. 后端 Controller
7. 前端 API 封装
8. 前端两委成员管理页面
9. 前端任务管理页面
10. 前端会议管理页面
11. 路由配置
12. 菜单配置

## Boundaries

必须遵守:
- 成员姓名非空，职务有效
- 任务状态流转不可逆
- 软删除保留历史关联

范围之内:
- F-TC-001 两委成员管理
- F-TC-002 编辑成员信息
- F-TC-003 删除成员
- F-TC-004 任务分配
- F-TC-005 任务进度跟踪
- F-TC-006 会议记录
- F-TC-007 决议关联任务
- F-TC-008 班子统计

范围之外:
- 任务到期自动提醒通知（仅提供数据）

## Open Questions

🔴 阻塞: 无
🟡 不阻塞: 无
🟢 已解决: 无

## Current Stage

阶段: deliver
状态: ✅ 完成

### 产出
状态: ✅ 完成
文件:
  - 后端: 15 个 Java 文件 (Entity/DTO/DAO/Service/Controller)
  - 前端: 4 个前端文件 (twoCommittee.js, task.js, meeting.js, TwoCommitteeList.vue)
  - 菜单: 已添加两委班子菜单
  - 路由: 已配置

### 自检
- [x] Task 分解: 完成（12个Task）
- [x] 实现约束遵守: 完成
- [x] 安全约束遵守: 完成

### 评审
状态: -
轮次: 0
反馈: -

## References

- docs/issues/issue-4-两委班子管理.md: Issue 完整需求描述
- CLAUDE.md: 项目架构约束和分层规范
- src/main/resources/schema.sql: 数据库建表语句模板
- src/main/java/com/village/entity/SpecialGroup.java: 参考实体设计
- src/main/webapp/src/views/specialGroup/SpecialGroupList.vue: 参考页面设计

## 知识回流

| 阶段 | 回流类型 | 文件路径 | 状态 |
|------|---------|---------|------|
| understand | Issue 摘要 | docs/current/issues/issue-4-two-committee.md | ⏳ |
| design | ADR | docs/current/decisions/adr-two-committee.md | ⏳ |
| implement | Knowledge 更新 | docs/knowledge/two-committee.md | ⏳ |
| implement | Pipeline 文件修改 | stages/*.md | ⏳ |

## Deliver

PR: N/A（本地开发）
创建时间: 2026-04-23
