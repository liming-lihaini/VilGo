# Pipeline State — Issue #6 党务管理

> 子 agent 写入，主 agent 消费（提取 References 构造后续 prompt）。

## Project Context

项目: village-affairs-management | 阶段: MVP | Pipeline 版本: v1
技术栈: JDK 17, Spring Boot 2.7, MyBatis-Plus 3.5, Vue 3, SQLite 3
架构: Controller → Service → Dao → Entity (单向依赖)
设计原则: 前后端一体化，单机部署，本地数据库

## Task

Issue: #6
Title: 党务管理
Issue 类型: 新功能
一句话描述: 党员档案管理、党员发展、党务活动、党费收缴与统计
用户场景: 村级工作人员管理党员信息，记录党务活动
为什么做: 实现党务工作数字化、留痕化
来源: docs/issues/issue-6-党务管理.md (F-PAW-001 ~ F-PAW-007)

## Task Detail

### 实现顺序
实现顺序: ①数据库 → ②后端(实体/枚举/DAO → Service/Controller) → ③前端(API → 页面) → ④路由/菜单

### Task 列表

#### ① 数据库层 (Task 5-1~5-3)

| Task | 文件 | 说明 |
|------|------|------|
| 5-1 | src/main/resources/schema.sql | 添加 party_members, party_activities, party_dues 表 |
| 5-2 | - | 验证数据库表 |
| 5-3 | - | 数据库初始化验证 |

#### ② 后端实体/数据层 (Task 5-4~5-13)

| Task | 文件 | 说明 |
|------|------|------|
| 5-4 | src/main/java/com/village/entity/PartyMember.java | 党员档案实体 |
| 5-5 | src/main/java/com/village/entity/PartyActivity.java | 党务活动实体 |
| 5-6 | src/main/java/com/village/entity/PartyDues.java | 党费收缴实体 |
| 5-7 | src/main/java/com/village/enums/PartyMemberStatus.java | 党员状态枚举 |
| 5-8 | src/main/java/com/village/dto/PartyMemberDTO.java | 党员档案 DTO (创建/更新) |
| 5-9 | src/main/java/com/village/dto/PartyMemberQueryDTO.java | 党员档案查询 DTO |
| 5-10 | src/main/java/com/village/dto/PartyActivityDTO.java | 党务活动 DTO |
| 5-11 | src/main/java/com/village/dto/PartyDuesDTO.java | 党费收缴 DTO |
| 5-12 | src/main/java/com/village/dao/PartyMemberMapper.java | 党员 Mapper |
| 5-13 | src/main/java/com/village/dao/PartyActivityMapper.java | 党务活动 Mapper |
| 5-14 | src/main/java/com/village/dao/PartyDuesMapper.java | 党费收缴 Mapper |

#### ③ 后端业务层 (Task 5-15~5-18)

| Task | 文件 | 说明 |
|------|------|------|
| 5-15 | src/main/java/com/village/service/PartyWorkService.java | 党务服务接口 |
| 5-16 | src/main/java/com/village/service/impl/PartyWorkServiceImpl.java | 党务服务实现 |
| 5-17 | src/main/java/com/village/controller/PartyWorkController.java | 党务控制器 |

#### ④ 前端层 (Task 5-18~5-25)

| Task | 文件 | 说明 |
|------|------|------|
| 5-18 | src/main/webapp/src/request/partyWork.js | API 封装 |
| 5-19 | src/main/webapp/src/views/partyWork/PartyMemberList.vue | 党员档案列表页 |
| 5-20 | src/main/webapp/src/views/partyWork/PartyActivityList.vue | 党务活动列表页 |
| 5-21 | src/main/webapp/src/views/partyWork/PartyDuesList.vue | 党费收缴列表页 |
| 5-22 | src/main/webapp/src/views/partyWork/PartyStatistics.vue | 党务统计页 |
| 5-23 | - | 路由配置 (如需添加) |

#### ⑤ 交付 (Task 5-26)

| Task | 说明 |
|------|------|
| 5-26 | 功能验证、测试 |

---

### 详细文件清单

#### 后端
- Entity: PartyMember.java, PartyActivity.java, PartyDues.java
- Enum: PartyMemberStatus.java (在com.village.enums包)
- DTO: PartyMemberDTO.java, PartyMemberQueryDTO.java, PartyActivityDTO.java, PartyDuesDTO.java
- Mapper: PartyMemberMapper.java, PartyActivityMapper.java, PartyDuesMapper.java
- Service: PartyWorkService.java (接口), PartyWorkServiceImpl.java (实现)
- Controller: PartyWorkController.java

#### 前端
- API: partyWork.js
- 页面: PartyMemberList.vue, PartyActivityList.vue, PartyDuesList.vue, PartyStatistics.vue

#### 数据库
- schema.sql: party_members, party_activities, party_dues 表

## Decisions

### 决策 1: 党员状态流转
选择: Java 枚举类型 (Enum) | 原因: 类型安全、可遍历、代码可读性好，与项目其他模块保持一致
影响: 后端需创建 PartyMemberStatus 枚举类，状态比较使用枚举而非字符串
阶段: design

### 决策 2: 身份证号关联
选择: 应用层校验 + 唯一约束（不使用外键） | 原因: SQLite 外键支持有限，应用层可提供更友好错误提示，与项目其他模块保持一致
影响: Service 层需校验身份证号在 residents 表是否存在，通过 id_card 唯一约束保证数据一致性
阶段: design

### 决策 3: 党费收缴周期
选择: 按月收缴（默认） | 原因: 基层党组织最常见模式，与需求匹配，支持更细粒度统计分析
影响: party_dues 表记录每笔缴纳记录，按月汇总统计
阶段: design

## Task

1. [understand] 理解 Issue 需求，明确功能范围，确认 Boundaries
2. [design] 数据库表结构设计（party_members, party_activities, party_dues）
3. [plan] 后端实体类、DTO、DAO、Service、Controller 开发计划
4. [plan] 前端页面和组件开发计划
5. [implement] 后端实体类创建
6. [implement] 后端 DTO 创建
7. [implement] 后端 DAO 创建
8. [implement] 后端 Service 接口和实现
9. [implement] 后端 Controller
10. [implement] 前端 API 封装
11. [implement] 前端党员档案管理页面
12. [implement] 前端党务活动页面
13. [implement] 前端党费收缴页面
14. [implement] 前端统计页面
15. [implement] 路由和菜单配置
16. [deliver] 验证和测试

## Boundaries

必须遵守:
- 身份证号在人事档案中存在（外键关联 residents 表）
- 党员状态流转顺序不可逆：积极分子 → 发展对象 → 预备党员 → 正式党员
- 入党时间 <= 转正时间
- 软删除保留历史记录
- 身份证号唯一性校验

范围之内:
- F-PAW-001 党员档案管理（身份证号、入党时间、转正时间、党员状态）
- F-PAW-002 编辑党员档案
- F-PAW-003 删除党员档案（软删除）
- F-PAW-004 党员发展（状态流转记录）
- F-PAW-005 党务活动（主题党日、组织生活会）
- F-PAW-006 党费收缴（按月收缴）
- F-PAW-007 党务统计（党员数量、状态分布、党费收缴）

范围之外:
- 党组织关系转接
- 党员教育学习管理
- 党务公开内容发布

## Open Questions

🔴 阻塞: 无
🟡 不阻塞: 无
🟢 已解决: 无

## Current Stage

阶段: deliver
状态: ✅ 完成

### 产出
状态: ✅ 完成
完成时间: 2026-04-24
文件:
- .claude/pipeline-log-6.md (deliver 阶段日志)
- 数据库: schema.sql (party_members, party_activities, party_dues 表)
- 后端枚举: com.village.enums.PartyMemberStatus
- 后端实体: PartyMember, PartyActivity, PartyDues
- 后端DTO: PartyMemberDTO, PartyMemberQueryDTO, PartyActivityDTO, PartyDuesDTO, PartyDuesQueryDTO, PartyMemberOutputDTO
- 后端DAO: PartyMemberDao, PartyActivityDao, PartyDuesDao
- 后端Service: PartyWorkService, PartyWorkServiceImpl
- 后端Controller: PartyWorkController
- 前端API: partyWork.js
- 前端页面: PartyMemberList, PartyActivityList, PartyDuesList, PartyStatistics, PartyWorkList

### 功能验证
- [x] F-PAW-001 党员档案管理
- [x] F-PAW-002 编辑党员档案
- [x] F-PAW-003 删除党员档案
- [x] F-PAW-004 党员发展
- [x] F-PAW-005 党务活动
- [x] F-PAW-006 党费收缴
- [x] F-PAW-007 党务统计

### 自检
- [x] SQL 注入防护: #{} 占位符
- [x] 事务注解: @Transactional(rollbackFor = Exception.class)
- [x] 分层约束: Controller → Service → Dao → Entity
- [x] 软删除: @TableLogic 注解
- [x] API 路径: /api/partyWork/*
- [x] 党员状态流转校验: 枚举控制顺序不可逆
- [x] 身份证号关联校验: 应用层校验

### Boundaries 验证
- [x] 身份证号在人事档案中存在（应用层校验）
- [x] 党员状态流转顺序不可逆（枚举 + Service 校验）
- [x] 入党时间 <= 转正时间（Service 层校验）
- [x] 软删除保留历史（@TableLogic）
- [x] 身份证号唯一性校验（UNIQUE 约束）

### 评审
状态: 非强制触发（deliver 阶段 SR/QR均无偏差）
轮次: 0
反馈: -

## References

- docs/issues/issue-6-党务管理.md: Issue 完整需求描述
- CLAUDE.md: 项目架构约束和分层规范
- src/main/resources/schema.sql: 数据库建表语句模板
- src/main/java/com/village/entity/Resident.java: 参考实体设计（关联村民）
- src/main/webapp/src/views/resident/ResidentList.vue: 参考页面设计
- docs/knowledge/rulers/Code.md: 编码规则（JAVA-MUST-0001~0005）
- docs/knowledge/rulers/SEC.md: 安全规则（DATABASE-MUST-0001, SECURITY-MUST-0002）
- docs/decisions/adr-party-member-status.md: 党员状态流转 ADR
- docs/decisions/adr-id-card-relation.md: 身份证号关联 ADR
- docs/decisions/adr-party-dues-cycle.md: 党费收缴周期 ADR

## 知识回流

| 阶段 | 回流类型 | 文件路径 | 状态 |
|------|---------|---------|------|
| understand | Issue 摘要 | - | ✅ |
| design | ADR | docs/decisions/adr-*.md (3个) | ✅ |
| implement | Knowledge 更新 | - | ✅ |
| implement | Pipeline 文件修改 | - | ✅ |

## Understand

创建时间: 2026-04-24

## Design

创建时间: 2026-04-24

### 数据库表结构设计

#### party_members 表（党员档案）
```sql
CREATE TABLE IF NOT EXISTS party_members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    join_date VARCHAR(20),
    convert_date VARCHAR(20),
    status VARCHAR(20) DEFAULT 'activist',
    creator VARCHAR(50),
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_party_members_id_card ON party_members(id_card);
CREATE INDEX IF NOT EXISTS idx_party_members_status ON party_members(status);
CREATE INDEX IF NOT EXISTS idx_party_members_deleted ON party_members(deleted);
```

#### party_activities 表（党务活动）
```sql
CREATE TABLE IF NOT EXISTS party_activities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    theme VARCHAR(100),
    activity_time VARCHAR(50),
    participation VARCHAR(50),
    creator VARCHAR(50),
    create_time VARCHAR(50),
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES party_members(id)
);
CREATE INDEX IF NOT EXISTS idx_party_activities_member ON party_activities(member_id);
CREATE INDEX IF NOT EXISTS idx_party_activities_time ON party_activities(activity_time);
CREATE INDEX IF NOT EXISTS idx_party_activities_deleted ON party_activities(deleted);
```

#### party_dues 表（党费收缴）
```sql
CREATE TABLE IF NOT EXISTS party_dues (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    amount DECIMAL(10,2),
    pay_date VARCHAR(20),
    creator VARCHAR(50),
    create_time VARCHAR(50),
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES party_members(id)
);
CREATE INDEX IF NOT EXISTS idx_party_dues_member ON party_dues(member_id);
CREATE INDEX IF NOT EXISTS idx_party_dues_pay_date ON party_dues(pay_date);
CREATE INDEX IF NOT EXISTS idx_party_dues_deleted ON party_dues(deleted);
```

### 技术决策

| 决策项 | 选择 | 原因 |
|--------|------|------|
| 党员状态流转 | Java 枚举 | 类型安全、可遍历、与项目一致 |
| 身份证号关联 | 应用层校验+唯一约束 | SQLite 外键限制、应用层提示更友好 |
| 党费收缴周期 | 按月收缴 | 常见模式、支持细粒度统计 |