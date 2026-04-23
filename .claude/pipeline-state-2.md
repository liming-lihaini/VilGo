# Pipeline State — Issue #2 户籍档案管理

> 子 agent 写入，主 agent 消费（提取 References 构造后续 prompt）。

## Project Context

项目: village-affairs-management | 阶段: MVP | Pipeline 版本: v1
技术栈: JDK 17, Spring Boot 2.7, MyBatis-Plus 3.5, Vue 3, SQLite 3
架构: Controller → Service → Dao → Entity (单向依赖)
设计原则: 前后端一体化，单机部署，本地数据库

## Task

Issue: #2
Title: 户籍档案管理
Issue 类型: 新功能
一句话描述: 以家庭户为单位进行管理，家庭户主数据来源于村民档案中标记为"户主"的人员，支持家庭成员管理、年度收入台账、户籍变动记录
用户场景: 村级工作人员管理户籍档案，以家庭户为单位维护成员信息、收入情况、户籍变动
为什么做: 完善村民档案管理，实现以户为单位的综合管理
来源: docs/issues/issue-2-户籍档案管理.md (F-HOU-001 ~ F-HOU-006)

## Decisions

### 决策 1: 家庭户数据来源
选择: 方案 A（从村民档案同步） | 原因: 户主信息已在村民档案中维护(is_household_head=1)，避免数据重复维护
否决: 方案 B（独立录入户主）— 数据冗余，同一人员维护两处
影响: 家庭户自动从村民档案同步，is_household_head 变更时自动更新
阶段: design

### 决策 2: 家庭成员关联方式
选择: 方案 A（关联村民档案） | 原因: 与村民档案保持一致性，成员信息自动同步
否决: 方案 B（独立录入成员信息）— 信息分散，难以保证一致性
影响: 家庭成员必须从村民档案中选择
阶段: design

### 决策 3: 页面布局设计
选择: 方案 A（左侧列表+右侧详情） | 原因: 符合现有系统交互习惯，参考村民档案模块设计
否决: 方案 B（单页面表格）— 家庭户信息量大，需要更丰富的展示
影响: 左侧家庭户列表，右侧详情（含Tab切换成员/收入/变动）
阶段: design

### 决策 4: 数据库表设计
选择: 方案 A（4张表：households, household_members, household_income, household_changes） | 原因: 业务清晰，职责分离，与村民档案模块风格一致
否决: 方案 B（2张表合并）— 违反单一职责原则
影响: schema.sql 新增4张表
阶段: design

### 决策 5: 年度收入年份选择
选择: 方案 A（支持多年份记录） | 原因: 历史数据查询需要，支持年度对比分析
否决: 方案 B（仅当年）— 无法查看历史数据
影响: 收入记录支持多年份存储
阶段: design

### 决策 6: 户籍变动状态保留
选择: 方案 A（记录变动前后状态） | 原因: 完整保留变更轨迹，便于追溯
否决: 方案 B（仅记录当前状态）— 丢失历史信息
影响: 变动记录包含 before_status 和 after_status
阶段: design

## Boundaries

必须遵守:
- 家庭户编号全局唯一
- 成员唯一性：同一人员只能属于一个家庭户
- 年度收入唯一性：同一家庭同一年份只能有一条记录
- 软删除保留数据

范围之内:
- F-HOU-001 家庭户同步（从村民档案自动同步户主）
- F-HOU-002 家庭户管理（基本信息维护）
- F-HOU-003 家庭成员管理（添加/移除成员）
- F-HOU-004 年度收入台账（记录家庭年度收入）
- F-HOU-005 户籍变动记录（迁入/迁出/新生儿/分户/合户）
- F-HOU-006 户籍查询统计

范围之外:
- 批量导入家庭户（本期不实现）
- 家庭户合并分拆流程自动化（仅记录变动）

## Open Questions

🔴 阻塞: 无
🟡 不阻塞: 无
🟢 已解决: 无

## Current Stage

阶段: deliver
状态: ✅ 完成

### 产出
状态: ✅
文件:
  - 后端: Household实体/DAO/Service/Controller (4个表, 14个API)
  - 前端: HouseholdList.vue, household.js
  - 路由: /household, /household/change
  - 提交: 24fd87b

### 自检
- [x] Task 分解: 完成（13个Task）
- [x] 实现约束遵守: 完成
- [x] 安全约束遵守: 完成
- [x] 代码提交: 完成
- [x] 推送到远程: 完成

### 评审
状态: ✅
轮次: 0
反馈: -

### 产出
状态: ✅
文件:
  - 后端: Household实体/DAO/Service/Controller (4个表, 14个API)
  - 前端: HouseholdList.vue, household.js
  - 路由: /household, /household/change

### 自检
- [x] Task 分解: 完成（13个Task）
- [x] 实现约束遵守: 完成
- [x] 安全约束遵守: 完成

### 评审
状态: ⏳
轮次: 0
反馈: -
- [ ] 理解 Issue 需求: 完成
- [ ] 明确数据来源: 完成（村民档案 is_household_head=1）
- [ ] 确认功能范围: 完成（6个功能点）

### 评审
状态: ⏳
轮次: 0
反馈: -

## References

> 主 agent 在 understand 完成后提取此列表，构造后续阶段的 sub-agent prompt。
> 后续阶段不通过此 section 读取文件。

- docs/issues/issue-2-户籍档案管理.md: Issue 完整需求描述
- CLAUDE.md: 项目架构约束、分层规范、技术栈
- docs/knowledge/rulers/Code.md: 编码规范约束（design/plan/implement 必读）
- docs/knowledge/rulers/SEC.md: 安全合规约束（design/plan/implement 必读）
- src/main/resources/schema.sql: 数据库建表语句模板
- src/main/java/com/village/entity/Resident.java: 村民档案实体（参考户主字段）
- src/main/java/com/village/service/ResidentService.java: 村民服务接口（参考同步方式）
- src/main/webapp/src/views/resident/ResidentList.vue: 村民列表页面（参考页面设计）
- src/main/webapp/src/request/resident.js: 村民API封装（参考API风格）
- docs/decisions/adr-residents-data-model.md: 数据模型 ADR（参考设计模式）

## 知识回流

| 阶段 | 回流类型 | 文件路径 | 状态 |
|------|---------|---------|------|
| understand | Issue 摘要 | docs/current/issues/issue-2-household.md | ⏳ |
| design | ADR | docs/current/decisions/adr-household.md | ⏳ |
| implement | Knowledge 更新 | docs/knowledge/household.md | ⏳ |
| implement | Pipeline 文件修改 | stages/*.md | ⏳ |

## Deliver

PR: N/A（本地开发）
创建时间: 2026-04-23