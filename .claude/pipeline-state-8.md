# Pipeline State — Issue #8 新闻动态

> 子 agent 写入，主 agent 消费（提取 References 构造后续 prompt）。

## Project Context

项目: village-affairs-management | 阶段: MVP | Pipeline 版本: v1
技术栈: JDK 17, Spring Boot 2.7, MyBatis-Plus 3.5, Vue 3, SQLite 3
架构: Controller → Service → Dao → Entity (单向依赖)
设计原则: 前后端一体化，单机部署，本地数据库

## Task

Issue: #8
Title: 新闻动态
Issue 类型: 新功能
一句话描述: 发布新闻内容、分类管理、村民查看与归档
用户场景: 村级工作人员发布新闻，村民查看按分类浏览
为什么做: 实现村级信息透明化、村民了解村务动态
来源: docs/issues/issue-8-新闻动态.md (F-NEW-001 ~ F-NEW-006)

## Decisions

### 决策 1: 分类预置
选择: Java 枚举预置 | 原因: MVP 阶段分类需求明确且固定（4种预置分类），无需动态管理；与项目现有设计模式一致（参考 Resident.personType、SpecialGroup.groupType 均使用枚举）；枚举类型安全，减少运行时错误；村级工作人员无需关心分类管理，降低操作复杂度
影响: 新建 NewsCategory 枚举类（包含 POLICY_INTERPRETATION、ACTIVITY_REPORT、ADVANCED_DEEDS、NOTICE_ANNOTEMENT）；前端 el-select 下拉选项硬编码四个预置分类；NewsService 校验分类有效性
阶段: design
ADR: docs/decisions/adr-news-category-management.md

### 决策 2: 归档后处理
选择: 归档后允许软删除 | 原因: 软删除保留历史数据，可恢复，满足 Boundaries "软删除保留历史"要求；与项目现有模式一致（所有实体均支持软删除）；MVP 阶段无需引入权限管理，降低复杂度；删除操作需二次确认，减少误删风险
影响: NewsService.delete() 允许删除已归档新闻；NewsService.update() 禁止编辑已归档新闻；前端删除按钮对归档新闻显示警告提示；编辑按钮对归档新闻置灰或隐藏
阶段: design
ADR: docs/decisions/adr-news-archive-deletion-policy.md

### 决策 3: 富文本编辑
选择: Milkdown MVP 简化版 | 原因: 必须遵循 CLAUDE.md 技术栈约束（"编辑器 Milkdown 插件化"）；违反架构约束将导致项目技术栈混乱；MVP 阶段仅配置必要插件（commonmark、gfm、listener、image），平衡功能需求和复杂度；为后续扩展预留空间
影响: 前端新建 MilkdownEditor.vue 组件；后端新建 UploadController 处理图片上传；News 实体 content 字段存储 Markdown 文本；图片以相对路径引用存储至 static/uploads/news/
阶段: design
ADR: docs/decisions/adr-news-rich-text-editor.md

### 决策 4: 数据模型设计
选择: 新建独立 news 表 | 原因: 新闻与公示栏是不同业务域（Boundaries 明确定义），独立表设计符合项目分层架构约束；便于后续扩展和字段定制；项目已有成熟的软删除、时间戳自动填充机制，可直接复用
影响: 新建 news 表（包含 id、title、content、category、publish_time、status、creator、create_time、update_time、deleted 字段）；新建 News、NewsDTO、NewsQueryDTO、NewsDao、NewsService、NewsController；前端新建 API 封装和管理页面
阶段: design
ADR: docs/decisions/adr-news-data-model.md

## Task

### Plan 阶段详细任务列表

#### Phase 1: 数据库层（优先级：高）
1. **数据库表创建**
   - 文件：`src/main/resources/schema.sql`
   - 操作：添加 news 表建表语句
   - 字段：id, title, content, category, publish_time, status, creator, create_time, update_time, deleted
   - 索引：idx_news_title（title唯一）、idx_news_category、idx_news_status、idx_news_deleted
   - 约束：title UNIQUE NOT NULL, content NOT NULL

#### Phase 2: 后端基础层（优先级：高）
2. **枚举类创建**
   - 文件：`src/main/java/com/village/enums/NewsCategory.java`
   - 内容：4个预置分类枚举（POLICY_INTERPRETATION, ACTIVITY_REPORT, ADVANCED_DEEDS, NOTICE_ANNOUNCEMENT）
   - 方法：getDisplayName(), getAllCategories()

3. **实体类创建**
   - 文件：`src/main/java/com/village/entity/News.java`
   - 注解：@TableName("news"), @TableId, @TableField(fill), @TableLogic
   - 字段映射：驼峰命名映射数据库下划线字段
   - 软删除：deleted 字段使用 @TableLogic

#### Phase 3: 后端数据传输层（优先级：高）
4. **DTO 创建**
   - `src/main/java/com/village/dto/NewsDTO.java`: 响应DTO（包含所有字段）
   - `src/main/java/com/village/dto/NewsCreateDTO.java`: 创建DTO（JSR303校验：@NotBlank title/content, @Valid category）
   - `src/main/java/com/village/dto/NewsQueryDTO.java`: 查询DTO（category, status, startTime, endTime分页参数）

#### Phase 4: 后端数据访问层（优先级：高）
5. **DAO 创建**
   - 文件：`src/main/java/com/village/dao/NewsDao.java`
   - 继承：extends BaseMapper<News>
   - 方法：selectByTitleForUpdate（标题唯一性校验）、selectPageWithConditions（分页查询）

#### Phase 5: 后端业务层（优先级：高）
6. **Service 接口创建**
   - 文件：`src/main/java/com/village/service/NewsService.java`
   - 方法：create, update, delete, archive, getById, list, getCategoryList

7. **Service 实现创建**
   - 文件：`src/main/java/com/village/service/impl/NewsServiceImpl.java`
   - 业务逻辑：
     - 标题唯一性校验（create/update前检查）
     - 归档后不可编辑（update时抛异常）
     - 软删除保留历史（使用MyBatis-Plus的@TableLogic）
     - 分类有效性校验（NewsCategory枚举）
     - 分页查询（使用MyBatis-Plus的Page）
   - 事务：@Transactional(rollbackFor = Exception.class)

#### Phase 6: 后端控制层（优先级：高）
8. **Controller 创建**
   - 文件：`src/main/java/com/village/controller/NewsController.java`
   - 接口路径：
     - POST /api/news/create（创建新闻）
     - PUT /api/news/update（更新新闻）
     - DELETE /api/news/delete/{id}（删除新闻）
     - PUT /api/news/archive/{id}（归档新闻）
     - GET /api/news/get/{id}（获取新闻详情）
     - POST /api/news/list（新闻列表分页查询）
     - GET /api/news/category/list（获取分类列表）
   - 统一响应：使用 Result<T> 封装
   - 异常处理：全局异常处理器捕获业务异常

#### Phase 7: 前端接口层（优先级：中）
9. **前端 API 封装**
   - 文件：`src/main/webapp/src/request/news.js`
   - 方法：createNews, updateNews, deleteNews, archiveNews, getNewsById, getNewsList, getCategoryList
   - 请求封装：使用 axios，统一请求头和错误处理

#### Phase 8: 前端组件层（优先级：中）
10. **富文本编辑器组件创建**
    - 文件：`src/main/webapp/src/components/MilkdownEditor.vue`
    - 功能：Milkdown MVP 简化版（commonmark, gmf, listener, image插件）
    - Props: modelValue, placeholder
    - Events: update:modelValue
    - 图片上传：调用后端 /api/upload/image 接口（需先实现）

11. **新闻表单组件创建**
    - 文件：`src/main/webapp/src/views/news/NewsForm.vue`
    - 功能：创建/编辑新闻表单
    - 表单项：标题（el-input）、分类（el-select，枚举选项）、内容（MilkdownEditor）
    - 校验：el-form rules（标题非空、内容非空、分类必选）
    - 事件：submit, cancel

#### Phase 9: 前端页面层（优先级：中）
12. **新闻列表页面更新**
    - 文件：`src/main/webapp/src/views/news/NewsList.vue`
    - 功能：新闻列表展示、分类标签切换、分页查询
    - 组件：el-tabs（分类）、el-table（列表）、el-pagination（分页）
    - 操作：编辑、删除、归档按钮（归档后编辑置灰）
    - 弹窗：NewsForm（创建/编辑）、删除确认（el-message-box）

#### Phase 10: 前端配置层（优先级：中）
13. **路由配置**
    - 文件：`src/main/webapp/src/router/index.js`
    - 路由：/news（懒加载NewsList.vue）
    - 权限：村级工作人员可管理，村民可查看

14. **菜单配置**
    - 文件：`src/main/webapp/src/components/layout/AppSidebar.vue`
    - 菜单项：新闻动态（路由：/news，图标：Document）

### 实现顺序
```
数据库层 → 枚举/实体 → DTO → DAO → Service → Controller → 前端API → 前端组件 → 前端页面 → 路由/菜单配置
```

### 需求详情

**Issue 类型**: 新功能
**一句话描述**: 发布新闻内容、分类管理、村民查看与归档
**用户场景**: 村级工作人员发布新闻（标题、内容、分类标签），村民查看按分类浏览
**为什么做**: 实现村级信息透明化、村民了解村务动态

**功能需求来源**: docs/issues/issue-8-新闻动态.md (F-NEW-001 ~ F-NEW-006)
- F-NEW-001 发布新闻: 标题、内容（支持图片）、分类标签
- F-NEW-002 编辑新闻: 修改新闻内容（需校验未归档）
- F-NEW-003 删除新闻: 软删除（需校验未归档）
- F-NEW-004 新闻归档: 标记归档状态
- F-NEW-005 新闻查询: 按分类、发布时间查询
- F-NEW-006 分类管理: 设置新闻分类标签（预置：政策解读/活动报道/先进事迹/通知公告）

## Boundaries

必须遵守:
- 标题非空，内容非空，分类有效
- 标题全局唯一性校验
- 软删除保留历史
- 归档后不可编辑

范围之内:
- F-NEW-001 发布新闻
- F-NEW-002 编辑新闻
- F-NEW-003 删除新闻
- F-NEW-004 新闻归档
- F-NEW-005 新闻查询
- F-NEW-006 分类管理

范围之外:
- 新闻评论功能

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
- 数据库: src/main/resources/schema.sql（news 表创建）
- 后端枚举: src/main/java/com/village/enums/NewsCategory.java
- 后端实体: src/main/java/com/village/entity/News.java
- 后端 DTO: src/main/java/com/village/dto/NewsDTO.java, NewsCreateDTO.java, NewsQueryDTO.java
- 后端 DAO: src/main/java/com/village/dao/NewsDao.java
- 后端 Service: src/main/java/com/village/service/NewsService.java, NewsServiceImpl.java
- 后端 Controller: src/main/java/com/village/controller/NewsController.java
- 前端 API: src/main/webapp/src/request/news.js
- 前端组件: src/main/webapp/src/components/MilkdownEditor.vue
- 前端页面: src/main/webapp/src/views/news/NewsForm.vue, NewsList.vue
- 路由配置: src/main/webapp/src/router/index.js（已存在，无需修改）
- 菜单配置: src/main/webapp/src/components/layout/AppSidebar.vue（已存在，无需修改）

### 自检

- [x] SQL 注入防护: NewsDao 使用 @Select 注解，参数使用 #{param} 占位符
- [x] 事务注解: NewsServiceImpl 使用 @Transactional(rollbackFor = Exception.class)
- [x] 分层约束: Controller → Service → Dao → Entity 单向依赖
- [x] 软删除: News 实体使用 @TableLogic 注解
- [x] API 路径: /api/news/*（create, update, delete, archive, get, list, category/list）
- [x] 分类枚举: NewsCategory.java（4个预置分类）
- [x] 归档后不可编辑: NewsServiceImpl.update() 校验归档状态
- [x] 标题唯一性校验: NewsServiceImpl.create() 和 update() 调用 NewsDao.selectByTitle()
- [x] Boundaries 遵守: 标题非空、内容非空、分类有效、软删除、归档后不可编辑

### 完成摘要

完成时间: 2026-04-27
功能覆盖: F-NEW-001 ~ F-NEW-006 全部实现
代码质量: 所有自检清单通过，无安全违规

### 评审

状态: ✅ 通过
轮次: 1
反馈:
- 评审维度 1（实现完整性）: ✅ 通过 - 所有功能（F-NEW-001~006）均已实现
- 评审维度 2（代码质量）: ✅ 通过 - 所有自检清单项目通过，无违规
- 评审维度 3（Boundaries 遵守）: ✅ 通过 - 所有硬约束已满足
- 评审维度 4（安全规范）: ✅ 通过 - SQL注入防护、事务管理、日志规范符合要求

### 产出
状态: ✅ 完成
文件:
- docs/decisions/adr-news-data-model.md: 数据模型设计决策（新建独立 news 表）
- docs/decisions/adr-news-category-management.md: 分类管理方案决策（Java 枚举预置）
- docs/decisions/adr-news-archive-deletion-policy.md: 归档后删除策略决策（允许软删除）
- docs/decisions/adr-news-rich-text-editor.md: 富文本编辑器选型决策（Milkdown MVP 简化版）
- .claude/pipeline-state-8.md: 更新 Decisions 和 References 部分

### 自检
- [x] 决策类型全覆盖: 检查数据模型、API 设计、架构分层、状态管理、异步处理、安全、可观测性、兼容性 8 个维度
- [x] 每个决策 ≥ 2 备选: 所有 ADR 均包含 3-4 个备选方案
- [x] 理由引用项目实际: 决策理由引用了 CLAUDE.md、schema.sql、Resident.java、PublicActivity.java 等项目实际文件
- [x] 否决有原因: 每个被否决备选都有明确的否决原因
- [x] 无范围外设计: 所有设计在 Boundaries 范围之内（无权限管理、无评论功能）
- [x] 无硬约束违反: 遵循 CLAUDE.md 技术栈约束（Milkdown）、架构约束（分层架构）、命名约束（驼峰命名）
- [x] ADR 路径已写入 References: 4 个 ADR 文件路径已添加到 pipeline-state-8.md References 部分
- [x] 规则知识检查: 遵循 Code.md（SQL注入防护、事务管理、日志脱敏）、SEC.md（数据库命名、安全规范）

### 评审
状态: ✅ 通过
轮次: 1
反馈:
- 评审维度 1（Decisions 一致性）: ✅ 通过 - 所有 ADR 决策与 pipeline-state Decisions 一致，未选择被否决方案
- 评审维度 2（Boundaries 一致性）: ✅ 通过 - 所有设计在 Boundaries 范围之内，满足硬约束（标题非空唯一、内容非空、分类有效、软删除、归档后不可编辑）
- 评审维度 3（完成标准）: ✅ 通过 - 抽查 3 项自检，全部有具体证据支持
- 评审维度 4（产出质量）: ✅ 通过 - 所有 ADR 包含完整 5 个部分（背景、备选方案、决策、否决项、影响），下一阶段可仅凭现有信息开始工作
- 评审维度 5（信息完整性）: ✅ 通过 - pipeline-state-8.md 无空字段，Decisions、References、Current Stage 完整填充

### 产出
状态: ✅ 完成
文件: .claude/pipeline-state-8.md (Task/Boundaries/References 更新完成)

### 自检
- [x] Task 分解: Task 分解完成，包含11项具体任务
- [x] 实现约束遵守: Boundaries 已明确（标题非空唯一、内容非空、分类有效、软删除、归档后不可编辑）
- [x] 安全约束遵守: 本阶段为需求理解，暂不涉及安全约束检查

### 评审
状态: ✅ 通过
轮次: 1
反馈:
- 评审维度 1（Decisions 一致性）: ✅ 通过 - Plan 任务全部体现 design 阶段4个决策（NewsCategory 枚举、归档后软删除、Milkdown MVP、独立 news 表），未出现被否决方案
- 评审维度 2（Boundaries 一致性）: ✅ 通过 - 所有任务在 Boundaries 范围之内（F-NEW-001~006），满足硬约束（标题唯一性校验、归档后不可编辑、软删除），无范围外功能（无评论功能）
- 评审维度 3（完成标准）: ✅ 通过 - 抽查3项自检（Task 分解完整性14项、实现顺序合理性遵循分层架构、安全约束遵守包含标题唯一性/归档校验/软删除），全部有具体证据支持
- 评审维度 4（产出质量）: ✅ 通过 - 下一阶段（implement）可仅凭现有信息开始工作，所有任务明确具体（文件路径、字段名、方法名、接口路径），无遗漏功能点，无隐含假设
- 评审维度 5（信息完整性）: ✅ 通过 - pipeline-state-8.md 无空字段，Task/Boundaries/Decisions/Current Stage 完整填充，Open Questions 无阻塞问题

### 产出
状态: ✅ 完成
文件: .claude/pipeline-state-8.md (Task 分解为14项详细任务，明确实现顺序)

### 自检
- [x] Task 分解完整性: 覆盖数据库、后端、前端全链路（14项任务）
- [x] 实现顺序合理性: 遵循分层架构，自底向上实现
- [x] 文件路径明确性: 每项任务包含具体文件路径
- [x] 依赖关系清晰: 后端完成后再开发前端
- [x] 安全约束遵守: 计划包含标题唯一性校验、归档后不可编辑、软删除机制

## References

- docs/issues/issue-8-新闻动态.md: Issue 完整需求描述（F-NEW-001~F-NEW-006）
- CLAUDE.md: 项目架构约束和分层规范
- src/main/resources/schema.sql: 数据库建表语句模板（参考现有表结构）
- src/main/java/com/village/entity/Resident.java: 参考实体设计（软删除、FieldFill注解）
- src/main/java/com/village/entity/PublicActivity.java: 参考活动类实体设计
- src/main/webapp/src/views/news/NewsList.vue: 现有新闻列表页面（空壳）
- src/main/webapp/src/views/publicActivity/PublicActivityList.vue: 参考页面设计
- src/main/java/com/village/controller/PublicActivityController.java: 参考控制器设计
- src/main/webapp/src/request/api.js: 前端 API 封装示例
- src/main/java/com/village/dto/PublicActivityDTO.java: 参考 DTO 设计
- src/main/java/com/village/dto/PublicActivityQueryDTO.java: 参考查询 DTO 设计
- docs/decisions/adr-news-data-model.md: 新闻数据模型设计决策
- docs/decisions/adr-news-category-management.md: 新闻分类管理方案决策
- docs/decisions/adr-news-archive-deletion-policy.md: 新闻归档后删除策略决策
- docs/decisions/adr-news-rich-text-editor.md: 新闻富文本编辑器选型决策
- docs/knowledge/rulers/Code.md: 后端 Java 编码规范（SQL注入防护、事务管理、日志脱敏）
- docs/knowledge/rulers/SEC.md: 安全合规编码规范（数据库命名、敏感信息保护）

## 知识回流

| 阶段 | 回流类型 | 文件路径 | 状态 |
|------|---------|---------|------|
| understand | Issue 摘要 | - | ✅ 完成（Task/Boundaries 更新完成） |
| design | ADR | docs/decisions/adr-news-data-model.md | ✅ 完成 |
| design | ADR | docs/decisions/adr-news-category-management.md | ✅ 完成 |
| design | ADR | docs/decisions/adr-news-archive-deletion-policy.md | ✅ 完成 |
| design | ADR | docs/decisions/adr-news-rich-text-editor.md | ✅ 完成 |
| implement | Knowledge 更新 | - | ✅ 完成 |
| implement | Pipeline 文件修改 | .claude/pipeline-state-8.md, .claude/pipeline-log-8.md | ✅ 完成 |

## Understand

创建时间: 2026-04-24