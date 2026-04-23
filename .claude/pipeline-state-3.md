# Pipeline State — Issue #3 特殊人群管理

> 子 agent 写入，主 agent 消费（提取 References 构造后续 prompt）。

## Project Context

项目: village-affairs-management | 阶段: MVP | Pipeline 版本: v1
技术栈: JDK 17, Spring Boot 2.7, MyBatis-Plus 3.5, Vue 3, SQLite 3
架构: Controller → Service → Dao → Entity (单向依赖)
设计原则: 前后端一体化，单机部署，本地数据库

## Task

Issue: #3
Title: 特殊人群管理
Issue 类型: 新功能
一句话描述: 登记和管理脱贫户、监测户、残疾人、孤寡老人等特殊人群，记录帮扶责任人、帮扶措施、帮扶成效，支持帮扶任务提醒和统计导出
用户场景: 村级工作人员管理特殊人群档案，记录帮扶情况，到期自动提醒
为什么做: 完善村民档案管理，实现特殊人群重点关注和帮扶工作留痕
来源: docs/issues/issue-3-特殊人群管理.md (F-SPG-001 ~ F-SPG-007)

## Decisions

### 决策 1: 特殊人群数据关联方式
选择: 方案 A（关联村民档案） | 原因: 从村民档案获取基础信息，避免数据重复维护
影响: 登记时通过身份证号关联村民档案获取姓名、性别、出生日期等
阶段: design

### 决策 2: 帮扶责任人数据来源
选择: 方案 A（关联两委班子成员） | 原因: 帮扶责任人从两委班子中选择，确保责任到人
影响: 帮扶责任人ID关联 two_committee 表
阶段: design

### 决策 3: 页面布局设计
选择: 方案 A（列表+详情Tab切换） | 原因: 符合现有系统交互习惯，参考户籍档案模块设计
影响: 左侧特殊人群列表，右侧详情（含Tab切换帮扶记录/统计）
阶段: design

### 决策 4: 数据库表设计
选择: 方案 A（2张表：special_groups, help_records） | 原因: 业务清晰，职责分离
影响: schema.sql 新增2张表
阶段: design

### 决策 5: 到期提醒计算方式
选择: 方案 A（帮扶时间+计算到期天数） | 原因: 基于帮扶时间计算，距今5天内显示提醒
影响: 前端显示到期天数，后端提供提醒接口
阶段: design

### 决策 6: 导出功能实现
选择: 方案 A（使用 EasyExcel） | 原因: 与村民档案导出保持一致
影响: 后端使用 EasyExcel 生成 Excel 文件
阶段: design

## Task

1. 数据库表结构设计（2张表：special_groups, help_records）
2. 后端实体类创建（SpecialGroup, HelpRecord）
3. 后端 DTO 创建（入参/出参）
4. 后端 DAO 创建（MyBatis-Plus BaseMapper）
5. 后端 Service 接口和实现
6. 后端 Controller（9个API）
7. 前端 API 封装
8. 前端特殊人群列表页面
9. 前端帮扶记录管理组件
10. 前端统计组件
11. 路由配置
12. 菜单配置

## Boundaries

必须遵守:
- 特殊人群身份证号全局唯一
- 软删除保留帮扶记录历史
- 帮扶时间到期前5天自动提醒
- 帮扶责任人关联两委班子成员

范围之内:
- F-SPG-001 特殊人群登记（关联村民档案）
- F-SPG-002 编辑特殊人群档案
- F-SPG-003 删除特殊人群档案（软删除）
- F-SPG-004 帮扶记录管理
- F-SPG-005 帮扶任务提醒
- F-SPG-006 帮扶统计
- F-SPG-007 帮扶报表导出

范围之外:
- 批量导入特殊人群（本期不实现）
- 帮扶责任人自动分配（手动选择）

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
  - 后端: 13 个 Java 文件 (Entity/DTO/DAO/Service/Controller)
  - 前端: 2 个前端文件 (specialGroup.js, SpecialGroupList.vue)
  - API: 9 个 RESTful 接口

### 自检
- [x] Task 分解: 完成（12个Task）
- [x] 实现约束遵守: 完成
- [x] 安全约束遵守: 完成

### 评审
状态: -
轮次: 0
反馈: -

## References

- docs/issues/issue-3-特殊人群管理.md: Issue 完整需求描述
- CLAUDE.md: 项目架构约束、分层规范、技术栈
- src/main/resources/schema.sql: 数据库建表语句模板
- src/main/java/com/village/entity/Resident.java: 村民档案实体
- src/main/webapp/src/views/resident/ResidentList.vue: 村民列表页面（参考页面设计）
- src/main/webapp/src/request/resident.js: 村民API封装（参考API风格）

## 知识回流

| 阶段 | 回流类型 | 文件路径 | 状态 |
|------|---------|---------|------|
| understand | Issue 摘要 | docs/current/issues/issue-3-special-group.md | ⏳ |
| design | ADR | docs/current/decisions/adr-special-group.md | ⏳ |
| implement | Knowledge 更新 | docs/knowledge/special-group.md | ⏳ |
| implement | Pipeline 文件修改 | stages/*.md | ⏳ |

## Deliver

PR: N/A（本地开发）
创建时间: 2026-04-23