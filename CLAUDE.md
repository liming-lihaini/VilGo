# Issue Development Pipeline

## 项目概况

本软件采用“前后端一体化（单工程）\+ 本地数据库”架构，将前端、后端服务整合在一个工程中，适配单机部署模式，无外网依赖，所有数据存储于本地，简化部署流程、降低运行门槛，贴合村级工作人员操作场景。整体架构分为前端模块、后端模块、数据层三大模块，各模块在同一工程内封装、联动运行，无需单独部署前端服务。

核心技术栈：JDK 17、（后端开发）、SQLite 3（本地数据库）、Vue 3（前端）Vite、Maven（项目构建），无第三方加密、备份组件，聚焦核心业务功能实现，前后端一体化部署，无需额外配置前端服务

## 项目结构

工程名称：village\-affairs\-management，采用Maven构建，JDK版本指定为17，整合前后端代码，目录结构如下：

```plain text
village-affairs-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── village/
│   │   │           ├── VillageAffairsApplication.java  // 启动类（统一启动前后端服务）
│   │   │           ├── config/                        // 配置类目录（数据库、前端资源、接口配置等）
│   │   │           ├── controller/                   // 控制器目录（接收前端请求，返回响应）
│   │   │           ├── service/                      // 服务层目录（业务逻辑处理）
│   │   │           │   └── impl/                    // 服务实现类
│   │   │           ├── dao/                         // 数据访问层目录（操作数据库）
│   │   │           ├── entity/                      // 实体类目录（对应数据库表）
│   │   │           ├── dto/                         // 数据传输对象目录（前后端数据交互）
│   │   │           ├── exception/                   // 异常处理目录（自定义异常、全局异常）
│   │   │           └── util/                        // 工具类目录（通用工具方法）
│   │   ├── resources/
│   │   │   ├── application.yml                     // 全局配置文件（数据库连接、端口、前端资源等）
│   │   │   ├── static/                             // 前端打包后静态资源目录（自动托管）
│   │   │   └── templates/                          // 前端模板目录（可选，用于Vue打包后入口页面）
│   │   └── webapp/                                 // 前端源代码目录（开发阶段存放Vue代码）
│   │       ├── src/
│   │       │   ├── assets/                      // 前端静态资源（图片、样式、图标等）
│   │       │   │   ├── images/                  // 图片资源
│   │       │   │   └── css/                     // 样式文件
│   │       │   ├── components/                  // 前端公共组件（按钮、表单、弹窗等）
│   │       │   ├── views/                       // 前端页面视图（对应各功能模块）
│   │       │   │   ├── resident/                // 村民人事档案相关页面
│   │       │   │   ├── household/               // 户籍档案相关页面
│   │       │   │   ├── specialGroup/            // 特殊人群档案相关页面
│   │       │   │   ├── twoCommittee/            // 两委班子管理相关页面
│   │       │   │   ├── publicActivity/          // 公益活动管理相关页面
│   │       │   │   ├── partyWork/               // 党务管理相关页面
│   │       │   │   ├── notice/                  // 村委公示栏相关页面
│   │       │   │   ├── news/                    // 新闻动态相关页面
│   │       │   │   └── screen/                  // 数字大屏相关页面
│   │       │   ├── router/                      // 前端路由配置
│   │       │   ├── store/                       // 前端状态管理（本地会话数据）
│   │       │   ├── request/                     // 前端请求封装（调用后端接口）
│   │       │   ├── utils/                       // 前端工具类
│   │       │   ├── App.vue                      // 前端根组件
│   │       │   └── main.js                      // 前端入口文件
│   │       ├── public/                          // 前端公共静态资源（不被打包的资源）
│   │       ├── package.json                     // 前端依赖配置文件
│   │       └── vite.config.js                   // 前端Vite配置文件
│   └── test/                                       // 测试类目录（后端测试为主）
│       └── java/
│           └── com/
│               └── village/                        // 测试类对应后端包结构
├── pom.xml                                         // Maven依赖配置文件（整合前后端依赖）
└── README.md                                       // 工程说明文档（开发环境、启动步骤等）
    
```

## 架构约束

### 技术架构约束
| 层级 | 技术 | 约束 |
|------|------|------|
| 语言 | Java 17 | LTS版本 |
| 框架 | Spring Boot 2.7 | 禁止版本冲突 |
| ORM | MyBatis-Plus 3.5 | 禁止直接SQL |
| 前端 | Vue 3 + Element Plus | 组件内引入 |
| 编辑器 | Milkdown | 插件化 |
| 构建 | Vite 5 | 开发/构建 |
| DB | SQLite 3.40+ | 单文件 |
| 打包 | Launch4j | exe输出 |

### 分层约束

遵循**单向依赖、上层依赖下层、禁止反向调用**原则。

| 层级 | 包/目录路径 | 核心职责 | 访问依赖范围 |
| :--- | :--- | :--- | :--- |
| 启动层 | com.village | 项目启动、全局Bean扫描 | 仅框架加载 |
| 控制层 | controller | 接收请求、参数校验、接口响应 | 仅接收前端请求、调用Service |
| 传输层 | dto | 前后端数据封装、入参/出参定义 | 全层级可读 |
| 业务层 | service / service.impl | 业务逻辑、事务控制、数据组装 | 仅被Controller调用、依赖Dao |
| 数据层 | dao | 数据库CRUD、数据持久化 | 仅被Service调用、依赖Entity |
| 实体层 | entity | 数据库表映射实体 | 全层级可读 |
| 通用层 | config / exception / util | 配置、全局异常、通用工具 | 全层级公共引用 |
| 前端层 | webapp/src | 页面、组件、路由、接口请求 | 仅通过HTTP接口调用后端Controller |

**标准正向链路**
`Controller → Service → Dao → Entity`

**严格禁止**
- Dao 反向调用 Service / Controller
- Service 直接操作数据库、绕过Dao层
- Controller 直接调用 Dao，跳过业务层
- 工具类、配置类嵌入业务逻辑
- Entity 增加业务方法、耦合业务逻辑

#### Controller 控制层
1. 仅负责：请求接收、参数基础校验、调用业务接口、统一结果返回；
2. 禁止编写任何业务判断、数据处理、数据库操作逻辑；
3. 接口统一前缀：`/api/业务模块/操作标识`；
4. 入参优先使用DTO，禁止直接传递Entity实体；
5. 统一捕获异常、交由全局异常处理器处理。

####  Service 业务层
1. 业务接口定义在 `service` 目录，实现类统一放在 `service/impl`；
2. 所有事务管理、复杂业务逻辑、数据转换、跨表逻辑统一在此层；
3. 禁止直接编写SQL、原生JDBC操作；
4. 禁止强耦合前端视图，通过DTO完成数据交互；
5. 规避循环依赖，跨模块业务调用通过接口解耦。

#### Dao 数据访问层
1. 仅专注数据库增删改查操作；
2. 不处理业务异常、不做数据校验、不封装返回结果；
3. 入参、返回值优先使用Entity或简单查询条件。

#### Entity 实体层
1. 严格映射数据库表结构，字段与数据表一一对应；
2. 纯数据载体，无业务方法、无静态业务常量；
3. 禁止直接作为前端出参，需转换为DTO返回。

#### DTO 数据传输层
1. 专属前后端交互，用于入参校验、返回数据脱敏、字段裁剪；
2. 可集成JSR303校验注解，用于接口参数校验；
3. 禁止替代Entity进行数据库持久化操作。

#### 通用公共层（config/exception/util）
1. config：仅存放配置类、拦截器、跨域、权限、Web资源映射配置；
2. exception：统一存放自定义异常、全局异常统一处理类；
3. util：纯静态工具方法、无状态、无业务依赖，全局通用。



### 前端约束

| 约束项 | 规则 |
|---------|------|
| 目录结构 | views/页面、components/组件、api/接口、router/路由 |
| 组件通信 | props down, events up，禁止直接修改props |
| 状态管理 | 使用store保存全局状态，组件内状态用ref/reactive |
| API调用 | 统一通过api/目录封装，禁止直接fetch/axios |
| 样式隔离 | scoped styles + BEM命名 |
| 路由懒加载 | 路由使用() => import()实现代码分割 |
| 环境变量 | .env文件区分dev/prod |
| 表单验证 | 使用el-form + async-validator |

### 数据库约束

| 约束项 | 规则 |
|---------|------|
| 实体命名 | 驼峰命名，数据库下划线映射 |
| 软删除 | 使用deleted字段，Service层过滤 |
| 索引 | 根据查询需求在Repository定义 |
| 关联查询 | 优先用id关联，避免join |

### 命名约束

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Java类 | UpperCamelCase | DirectoryService |
| Java方法 | lowerCamelCase | getChildDirectories() |
| Java变量 | lowerCamelCase | currentParentId |
| 数据库表 | 复数+下划线 | directories, files |
| Vue组件 | PascalCase | DirectoryManager.vue |
| Vue方法 | lowerCamelCase | handleNodeClick |
| API路径 | RESTful风格 | /api/directory/create |

## 启动

1. `git pull origin main`
2. 创建 worktree：`git worktree add -b feature/issue-{N} .claude/worktrees/issue-{N} main`
3. 复制 stages/pipeline-state-template.md → .claude/pipeline-state-{issue编号}.md，  在文件中填入 Task + 技术栈 + 架构约束
4. 复制 stages/pipeline-log-template.md → .claude/pipeline-log-{issue编号}.md
5. 记录 Pipeline 版本到 state


## 阶段

```
understand → design → [review] → plan → [review] → implement → [review] → deliver
```

## 执行循环

对每个阶段：

1. 读 pipeline-state.md 确认当前阶段
2. **知识加载**：执行 `/pipeline-load {阶段名}` 加载阶段知识和规则知识
3. 准备子 agent prompt：
   - 如当前阶段不是 understand → 读 state References section，提取完整文件路径列表
   - understand 阶段不需要知识文件列表
4. 创建子 agent：
   ```
   [{阶段名} agent] {Issue 标题}

   运行 /pipeline-load {阶段名}。
   知识文件: {上一步提取的文件路径，逗号分隔}
   完成后严格遵循 stage 文件执行。
   ```
5. 等子 agent 完成
6. **Self-review（必须执行，不可跳过）**：
   按 stages/logging-guide.md 的双轨评估执行：
   - 轨道 A: SR-0 ~ SR-8（流程合规）
   - 轨道 B: QR-0 ~ QR-3（产出质量）
   每项写"合规"或"偏差: {具体描述}"。
   全部 SR + QR 结果写入日志"验证"段。
   **如果日志中缺少 SR 或 QR section，视为本阶段未完成。**
7. **Review 触发规则**：

   强制触发（必须执行）：
   - 任何 design/plan/implement 阶段完成时
   - 新增数据库表或修改 schema
   - 新增 API 接口（RESTful 端点）
   - 涉及安全相关功能（认证、授权、敏感数据）

   阈值触发（满足任一即触发）：
   - SR 偏差 ≥ 3 项
   - QR 偏差 ≥ 2 项
   - 复杂度评估为"高"（Issue 规模 > 5 个 Task）

   触发执行：
   ```
   [review agent] {被评审阶段} - {Issue 标题}

   运行 /pipeline-load review。
   完成后严格遵循 stage 文件执行
   ```
   **主 agent 职责**：review 完成后，主 agent 将评审结论写入 pipeline-state-{issue编号}.md Current Stage > 评审 section（状态、轮次、反馈）

8. review 不通过 → 反馈写入 state → 修复 → 最多重试 2 次 → 人工介入
9. 子 agent 失败 → 重试 1 次 → 仍失败 → 人工介入
10. Boundaries 冲突 → 停止
11. **写日志（按 stages/logging-guide.md）**
   - 所有阶段日志**统一写入一个文件**：`.claude/pipeline-log-{issue编号}.md`
   - 每个阶段日志用**两个空行**分隔
   - 日志必须包含所有 5 个阶段：understand → design → plan → implement → deliver
   - **验证日志完整性**：确保每个阶段都有日志，缺失则暂停并补写
   - design/plan/implement 阶段必须包含规则知识（rulers/*.md）检查结果
12. **SR/QR 偏差处理**：如果 SR 偏差 ≥ 3 项，或 QR 偏差 ≥ 2 项，暂停并升级人工介入。否则记录偏差，继续下一阶段
13. 更新 state → 下一阶段

## 断点续跑

session 中断后重新启动：
1. 读 pipeline-state-{issue编号}.md 的 Current Stage
2. **验证日志完整性**：检查所有 5 个阶段日志是否都存在于 `.claude/pipeline-log-{issue编号}.md`
   - 如有缺失，在缺失阶段标注"补写原因"，然后补写
3. **知识加载**：执行 `/pipeline-load {阶段名}` 加载阶段知识和规则知识
4. 从该阶段重新执行（已完成阶段产出保留）
5. 日志标注"恢复执行"

## 异常处理

| 异常 | 动作 | 恢复方式 |
|------|------|---------|
| 子 agent 失败 | 重试 1 次 | 仍失败 → 人工介入 |
| Review 不通过 | 反馈→修复→重审 | 最多 2 轮 → 人工介入 |
| Boundaries 冲突 | 停止 | 人工调整 Boundaries |
| State 未更新 | 主 agent 修正 | — |
| Session 中断 | 见断点续跑 | 从 Current Stage 恢复 |

## 完成后

按 stages/self-evolution.md 执行退化分析，追加 Iteration 记录。
