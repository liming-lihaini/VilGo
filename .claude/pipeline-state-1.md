# Pipeline State — Issue #1 村民档案管理

> 子 agent 写入，主 agent 消费（提取 References 构造后续 prompt）。

## Project Context

项目: village-affairs-management | 阶段: MVP | Pipeline 版本: v1
技术栈: JDK 17, Spring Boot 2.7, MyBatis-Plus 3.5, Vue 3, SQLite 3
架构: Controller → Service → Dao → Entity (单向依赖)
设计原则: 前后端一体化，单机部署，本地数据库

## Task

Issue: #1
Title: 村民档案管理
Issue 类型: 新功能
一句话描述: 实现村民"一人一档"规范化管理，支持村民基础信息录入、编辑、删除、销户、多条件查询、人口统计和Excel导出
用户场景: 村级工作人员在单机环境下完成村民档案的全生命周期管理，身份证号为唯一标识
为什么做: 满足村级工作人员对村民信息的规范化、便捷化管理需求
来源: docs/issues/issue-1-村民档案管理.md (F-RES-001 ~ F-RES-007)

## Decisions

### 决策 1: 数据表设计
选择: 方案 A（单表 residents） | 原因: MVP 阶段开发效率优先，20个字段内 SQLite 处理无压力，与 MyBatis-Plus 单表操作天然契合
否决: 方案 B（主从表设计）— MVP 阶段过早优化，增加不必要复杂度
影响: 新建 Entity/DAO 层文件，数据库自动创建表
阶段: design

### 决策 2: Excel 导出库选型
选择: 方案 B（EasyExcel） | 原因: 阿里开源社区活跃，中文文档丰富；API 简洁开发效率高；流式写入避免 OOM
否决: 方案 A（Apache POI）— API 繁琐，内存占用高；方案 C（JXL）— 仅支持 .xls 已淘汰
影响: pom.xml 添加 easyexcel 依赖，新建 ExcelUtil 工具类
阶段: design

### 决策 3: 后端分层设计
选择: 方案 A（标准四层 Controller/Service/Dao/Entity + DTO） | 原因: 严格遵循 CLAUDE.md 分层约束；DTO 实现数据脱敏，禁止 Entity 直接暴露给前端
否决: 方案 B（三层）— 违反分层约束，Entity 直接暴露存在安全风险
影响: 新建 Controller/Service/ServiceImpl/Dao/DTO 文件
阶段: design

### 决策 4: API 接口设计
选择: 方案 A（RESTful 风格） | 原因: 符合 CLAUDE.md 接口前缀规范；资源导向设计更符合 RESTful 最佳实践
否决: 方案 B（混合风格）— 接口风格不统一，增加维护成本
影响: 8 个 API 接口路由定义
阶段: design

### 决策 5: 前端页面结构
选择: 方案 A（目录式 views/resident/） | 原因: 严格遵循 CLAUDE.md 前端目录结构约束；独立模块目录更清晰易维护
否决: 方案 B（单页面）— 主页面代码膨胀，难以维护
影响: 前端 views/resident/ 目录结构
阶段: design

## Boundaries

必须遵守:
- 身份证号全局唯一性约束（数据库UNIQUE约束）
- 软删除保留数据（deleted字段标记，保留原始数据）
- 销户状态独立于删除状态（household_status字段）
- 单向分层：Controller → Service → Dao → Entity

范围之内:
- F-RES-001 新增村民档案（录入村民基础信息）
- F-RES-002 编辑村民档案（修改除身份证号外的信息）
- F-RES-003 删除村民档案（软删除）
- F-RES-004 销户登记（标记销户状态）
- F-RES-005 村民档案查询（多条件模糊查询、分页、排序）
- F-RES-006 人口统计（按人员类型、保障类型统计）
- F-RES-007 Excel报表导出

范围之外:
- 数据导入功能（本期不实现）
- 回收站模块（预留接口，本期不实现）
- 操作日志模块（预留接口，本期不实现）

## Open Questions

🔴 阻塞: 无
🟡 不阻塞: 无
🟢 已解决: Excel导出库选型 - 选择 EasyExcel（阿里开源，API 简洁，流式写入避免 OOM）

## Current Stage

阶段: deliver
产出: 代码实现完成，13 个 Task 完成
状态: ✅ 完成

### 产出文件列表
后端（13个）:
- pom.xml: 添加 EasyExcel 依赖
- src/main/resources/schema.sql: SQLite 建表语句（含 residents + resident_attachments）
- src/main/java/com/village/entity/Resident.java: 实体类
- src/main/java/com/village/dto/ResidentDTO.java: 入参 DTO
- src/main/java/com/village/dto/ResidentQueryDTO.java: 查询条件 DTO
- src/main/java/com/village/dto/ResidentStatisticsDTO.java: 统计结果 DTO
- src/main/java/com/village/dto/Result.java: 统一响应
- src/main/java/com/village/dao/ResidentDao.java: 数据访问层
- src/main/java/com/village/service/ResidentService.java: 服务接口
- src/main/java/com/village/service/impl/ResidentServiceImpl.java: 服务实现
- src/main/java/com/village/controller/ResidentController.java: 控制器（8个API）
- src/main/java/com/village/exception/BusinessException.java: 业务异常
- src/main/java/com/village/exception/GlobalExceptionHandler.java: 全局异常处理
- src/main/java/com/village/util/ExcelUtil.java: Excel导出工具

前端（5个）:
- src/main/webapp/src/request/resident.js: API封装
- src/main/webapp/src/views/resident/ResidentList.vue: 列表页面（含附件按钮）
- src/main/webapp/src/views/resident/ResidentStatistics.vue: 统计页面
- src/main/webapp/src/router/index.js: 路由配置
- src/main/webapp/src/components/layout/AppSidebar.vue: 侧边栏菜单

### 自检
- [x] Task 全部完成: 13/13
- [x] 约束遵守: 身份证号唯一性/软删除/销户状态/分层/DTO
- [x] API 实现: 8 个接口全部完成

### 评审
状态: ✅
轮次: 1
反馈: 实现完成，Review 通过。

## Bug 修复（Pipeline 外）

> 以下为 Pipeline 流程完成后发现的 bug 修复记录

### BUG-1: ExcelUtil.java 未使用的 imports
- 问题: 存在未使用的 imports，导致 `CellData cannot be resolved`
- 修复: 清理 7 个未使用的 imports
- 状态: ✅ 已修复

### BUG-2: application.yml deprecated 配置
- 问题: `spring.resources.static-locations` 已废弃
- 修复: 改为 `spring.web.resources.static-locations`
- 状态: ✅ 已修复

### BUG-3: 数据库路径配置
- 问题: 原始路径 `${user.dir}/data/village.db` 为相对路径
- 修复: 改为固定路径 `D:/village-data/village.db`
- 状态: ✅ 已修复

### BUG-4: 启动时数据库初始化
- 问题: SQLite 需要手动创建数据库文件
- 修复: 创建 `DatabaseInitializer.java`，启动时自动创建目录和表
- 状态: ✅ 已修复

### BUG-5: 目录创建时机
- 问题: JDBC 连接在目录创建前执行，导致路径不存在
- 修复: 调整代码顺序，先创建目录再连接数据库
- 状态: ✅ 已修复

### 修复后产出文件
- `src/main/java/com/village/config/DatabaseInitializer.java`: 数据库初始化器
- `src/main/java/com/village/VillageAffairsApplication.java`: 添加 @MapperScan

## Open Questions

🔴 阻塞: 无
🟡 不阻塞: 无
🟢 已解决: Excel导出库选型、数据库表设计、API接口风格

## References

> 后续阶段需要的文件列表

- docs/decisions/adr-residents-data-model.md: 数据表设计 ADR
- docs/decisions/adr-excel-library.md: Excel 导出库选型 ADR
- docs/decisions/adr-residents-backend-layers.md: 后端分层设计 ADR
- docs/decisions/adr-residents-api-design.md: API 接口设计 ADR
- docs/decisions/adr-residents-frontend-structure.md: 前端页面结构 ADR
- docs/plans/2026-04-22-residents-implementation-plan.md: 实现计划
- docs/issues/issue-1-村民档案管理.md: Issue 完整需求描述
- CLAUDE.md: 项目架构约束、分层规范、技术栈
- docs/knowledge/rulers/Code.md: 编码规范约束（implement 必读）
- docs/knowledge/rulers/SEC.md: 安全合规约束（implement 必读）
- pom.xml: Maven依赖配置（含 EasyExcel、MyBatis-Plus、Spring Boot 版本）
- src/main/resources/application.yml: 数据库配置模板

## 知识回流

| 阶段 | 回流类型 | 文件路径 | 状态 |
|------|---------|---------|------|
| understand | Issue 摘要 | docs/current/issues/issue-1-residents.md | ⏳ |
| design | ADR | docs/current/decisions/adr-residents.md | ⏳ |
| implement | Knowledge 更新 | docs/knowledge/resident.md | ⏳ |
| implement | Pipeline 文件修改 | stages/*.md | ⏳ |

## Deliver

PR: N/A（本地开发）
创建时间: 2026-04-22
完成时间: 2026-04-23
状态: ✅ 完成（含 Pipeline 外 5 个 bug 修复）

## 功能迭代（Pipeline 完成后）

> 以下为 Pipeline deliver 完成后新增的功能需求和实现

### 迭代 1: 村民附件管理
- **时间**: 2026-04-23
- **功能描述**: 村民档案附件上传、管理、预览功能
- **实现内容**:
  - 后端: ResidentAttachment 实体、DTO、Dao、Service、Controller
  - 附件存储: 本地固定文件夹 `D:/village-data/attachments/`
  - 前端: ResidentAttachment.vue 组件（支持身份证照片、个人照片、复印件等分类）
  - 初始化: 启动时自动创建附件目录

### 迭代 2: 详情抽屉
- **时间**: 2026-04-23
- **功能描述**: 点击村民姓名查看详情，右侧抽屉展示档案和附件
- **实现内容**:
  - 新增 ResidentDrawer.vue 组件
  - 基本信息展示（el-descriptions）
  - 附件信息展示（卡片式网格）

### 迭代 3: 附件管理优化
- **时间**: 2026-04-23
- **功能描述**: 附件按类型分类管理，支持图片/PDF预览
- **实现内容**:
  - 6种附件分类: 身份证照片、个人照片、身份证复印件、社保卡复印件、户口本复印件、其他
  - 图片预览（jpg/png/gif等）
  - PDF预览（内嵌iframe）
  - 其他格式提示"暂不支持"并提供下载
  - 全屏预览模式

### 迭代 4: 统计页面升级
- **时间**: 2026-04-23
- **功能描述**: 丰富统计指标，采用ECharts图表展示
- **实现内容**:
  - 新增统计卡片: 常住人口数、就读学生数、低保人数、五保户人数、非本地户籍人数
  - ECharts图表: 户籍状态分布(环形图)、人员类型分布(饼图)、保障类型分布(柱状图)、村组分布(柱状图)
  - 安装 echarts 依赖

### 迭代产出文件

后端新增:
- `src/main/java/com/village/entity/ResidentAttachment.java`: 附件实体
- `src/main/java/com/village/dto/ResidentAttachmentDTO.java`: 附件DTO
- `src/main/java/com/village/dao/ResidentAttachmentDao.java`: 附件DAO
- `src/main/java/com/village/service/ResidentAttachmentService.java`: 附件服务接口
- `src/main/java/com/village/service/impl/ResidentAttachmentServiceImpl.java`: 附件服务实现
- `src/main/java/com/village/controller/ResidentAttachmentController.java`: 附件控制器

前端新增:
- `src/main/webapp/src/views/resident/ResidentAttachment.vue`: 附件管理弹窗
- `src/main/webapp/src/views/resident/ResidentDrawer.vue`: 详情抽屉组件
- `src/main/webapp/package.json`: 添加 echarts 依赖

前端修改:
- `src/main/webapp/src/views/resident/ResidentList.vue`: 添加详情按钮、附件按钮、抽屉组件
- `src/main/webapp/src/views/resident/ResidentStatistics.vue`: 统计页面重构，ECharts图表
- `src/main/webapp/src/router/index.js`: 路由配置（已存在）
- `src/main/webapp/src/components/layout/AppSidebar.vue`: 添加 :router="true"
- `src/main/webapp/src/components/layout/AppMain.vue`: 内容区渲染
- `src/main/webapp/vite.config.js`: 添加 /attachments 代理

配置修改:
- `src/main/resources/application.yml`: 添加 app.attachment.path
- `src/main/java/com/village/config/DatabaseConfig.java`: 创建附件目录
- `src/main/java/com/village/config/WebConfig.java`: 附件静态资源映射
- `src/main/resources/schema.sql`: 添加 resident_attachments 表

### 统计页面字段映射

| 后端字段 | 前端显示 |
|---------|---------|
| totalCount | 总人口数 |
| localResidentCount | 常住人口数 |
| studentCount | 就读学生数 |
| allowanceCount | 低保人数 |
| fiveGuaranteeCount | 五保户人数 |
| nonLocalHouseholdCount | 非本地户籍人数 |
| householdStatusCount | 户籍状态分布（环形图） |
| personTypeCount | 人员类型分布（饼图） |
| securityTypeCount | 保障类型分布（柱状图） |
| villageCount | 村组分布（柱状图） |