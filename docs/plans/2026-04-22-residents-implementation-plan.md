# 村民档案管理实现计划

> Issue #1 实现计划 | 日期: 2026-04-22 | 阶段: plan

## 概述

本文档定义 Issue #1 村民档案管理功能的具体实现计划，包含 7 个功能点的 Task 拆分、执行顺序和依赖关系。

## 功能范围

| 功能码 | 功能名称 | 复杂度 |
|--------|----------|--------|
| F-RES-001 | 新增村民档案 | 中 |
| F-RES-002 | 编辑村民档案 | 低 |
| F-RES-003 | 删除村民档案（软删除） | 低 |
| F-RES-004 | 销户登记 | 中 |
| F-RES-005 | 村民档案查询 | 高 |
| F-RES-006 | 人口统计 | 中 |
| F-RES-007 | Excel导出 | 中 |

## Task 拆分

### Task 1: 环境准备 - 添加 EasyExcel 依赖
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-1 |
| 功能码 | - |
| 描述 | 在 pom.xml 中添加 EasyExcel 依赖 |
| 步骤 | 1. 在 pom.xml dependencies 中添加 com.alibaba:easyexcel 3.3.4 |
| 复杂度 | 低 |
| 依赖 | 无 |
| 产出 | pom.xml |

### Task 2: 数据库配置 - SQLite 表创建
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-2 |
| 功能码 | - |
| 描述 | 配置 SQLite 数据库连接，创建 residents 表 |
| 步骤 | 1. 在 application.yml 中添加 SQLite 数据源配置<br>2. 在 data 目录创建 schema.sql 建表语句<br>3. 配置 MyBatis-Plus 扫描路径 |
| 复杂度 | 中 |
| 依赖 | TASK-1 |
| 产出 | application.yml, schema.sql |

### Task 3: 后端 - Entity 实体层
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-3 |
| 功能码 | F-RES-001~007 |
| 描述 | 创建 Resident 实体类，映射数据库表 |
| 步骤 | 1. 创建 src/main/java/com/village/entity/Resident.java<br>>2. 使用 MyBatis-Plus @TableName 注解映射<br>>3. 配置主键策略、软删除注解 |
| 复杂度 | 低 |
| 依赖 | TASK-2 |
| 产出 | Resident.java |

### Task 4: 后端 - DTO 数据传输层
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-4 |
| 功能码 | F-RES-001~007 |
| 描述 | 创建入参和出参 DTO 类 |
| 步骤 | 1. 创建 ResidentDTO.java (新增/编辑入参)<br>>2. 创建 ResidentQueryDTO.java (查询入参)<br>>3. 创建 ResidentStatisticsDTO.java (统计出参)<br>>4. 创建 Result.java (统一响应) |
| 复杂度 | 低 |
| 依赖 | TASK-3 |
| 产出 | ResidentDTO.java, ResidentQueryDTO.java, ResidentStatisticsDTO.java, Result.java |

### Task 5: 后端 - Dao 数据访问层
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-5 |
| 功能码 | F-RES-001~007 |
| 描述 | 创建 ResidentDao 数据访问接口 |
| 步骤 | 1. 创建 src/main/java/com/village/dao/ResidentDao.java<br>>2. 继承 BaseMapper<Resident><br>>3. 定义自定义查询方法（如按身份证号查询） |
| 复杂度 | 低 |
| 依赖 | TASK-3 |
| 产出 | ResidentDao.java |

### Task 6: 后端 - Service 业务层
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-6 |
| 功能码 | F-RES-001~007 |
| 描述 | 创建 Service 接口和实现类 |
| 步骤 | 1. 创建 ResidentService.java 接口<br>>2. 创建 ResidentServiceImpl.java 实现类<br>>3. 实现所有业务方法（新增/编辑/删除/查询/销户/统计）<br>>4. 添加事务注解 |
| 复杂度 | 高 |
| 依赖 | TASK-5 |
| 产出 | ResidentService.java, ResidentServiceImpl.java |

### Task 7: 后端 - Controller 控制层
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-7 |
| 功能码 | F-RES-001~007 |
| 描述 | 创建 Controller 接收前端请求 |
| 步骤 | 1. 创建 ResidentController.java<br>>2. 实现 8 个 API 接口<br>>3. 参数校验和异常处理 |
| 复杂度 | 中 |
| 依赖 | TASK-6 |
| 产出 | ResidentController.java |

### Task 8: 后端 - Excel 导出工具
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-8 |
| 功能码 | F-RES-007 |
| 描述 | 创建 Excel 导出工具类 |
| 步骤 | 1. 创建 ExcelUtil.java<br>>2. 实现流式写入导出方法<br>>3. 配置表头映射 |
| 复杂度 | 中 |
| 依赖 | TASK-1 |
| 产出 | ExcelUtil.java |

### Task 9: 前端 - 村民列表页面
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-9 |
| 功能码 | F-RES-005 |
| 描述 | 创建村民档案列表页面 |
| 步骤 | 1. 创建 src/main/webapp/src/views/resident/ResidentList.vue<br>>2. 实现表格展示<br>>3. 实现分页组件<br>>4. 实现查询条件栏 |
| 复杂度 | 高 |
| 依赖 | TASK-7 |
| 产出 | ResidentList.vue |

### Task 10: 前端 - 新增/编辑表单
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-10 |
| 功能码 | F-RES-001, F-RES-002 |
| 描述 | 创建村民档案表单组件 |
| 步骤 | 1. 创建 src/main/webapp/src/views/resident/ResidentForm.vue<br>>2. 实现表单字段<br>>3. 实现校验规则<br>>4. 实现提交逻辑 |
| 复杂度 | 中 |
| 依赖 | TASK-9 |
| 产出 | ResidentForm.vue |

### Task 11: 前端 - 人口统计页面
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-11 |
| 功能码 | F-RES-006 |
| 描述 | 创建人口统计页面 |
| 步骤 | 1. 创建 src/main/webapp/src/views/resident/ResidentStatistics.vue<br>>2. 实现统计卡片<br>>3. 实现统计图表（可选） |
| 复杂度 | 中 |
| 依赖 | TASK-9 |
| 产出 | ResidentStatistics.vue |

### Task 12: 前端 - API 接口封装
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-12 |
| 功能码 | F-RES-001~007 |
| 描述 | 封装前端 API 调用 |
| 步骤 | 1. 创建 src/main/webapp/src/request/resident.js<br>>2. 封装 8 个 API 方法 |
| 复杂度 | 低 |
| 依赖 | TASK-7 |
| 产出 | resident.js |

### Task 13: 前端 - 路由配置
| 项目 | 内容 |
|------|------|
| 任务ID | TASK-13 |
| 功能码 | F-RES-001~007 |
| 描述 | 配置前端路由 |
| 步骤 | 1. 在 router/index.js 中添加路由<br>>2. 配置懒加载 |
| 复杂度 | 低 |
| 依赖 | TASK-9, TASK-11 |
| 产出 | router/index.js |

## 执行顺序

```
TASK-1 ──► TASK-2 ──► TASK-3 ──► TASK-4 ──► TASK-5 ──► TASK-6 ──► TASK-7 ◄── TASK-8
                                         │                                      │
                                         │                                      ▼
                              (后端完成)                          TASK-12 ◄───────
                                         │                          │
                                         ▼                          ▼
                                      TASK-9 ──► TASK-10 ──► TASK-13
                                             │             │
                                             └──────►─────┘
                                             (TASK-11 并行)
```

## 关键依赖说明

| Task | 依赖说明 |
|------|----------|
| TASK-1 | 无依赖 |
| TASK-2 | 依赖 TASK-1 添加依赖后才能连接数据库 |
| TASK-3 | 依赖 TASK-2 数据库配置后才能创建实体 |
| TASK-4 | 依赖 TASK-3 创建实体后才能定义 DTO |
| TASK-5 | 依赖 TASK-3 实体创建后才能定义 DAO |
| TASK-6 | 依赖 TASK-5 DAO 创建后才能实现业务逻辑 |
| TASK-7 | 依赖 TASK-6 Service 创建后才能接收请求 |
| TASK-8 | 依赖 TASK-1 EasyExcel 依赖后才能实现导出 |
| TASK-9 | 依赖 TASK-7 后端 API 创建后才能实现前端 |
| TASK-10 | 依赖 TASK-9 列表页面创建后才能实现表单 |
| TASK-11 | 依赖 TASK-9 与 TASK-10 并行开发 |
| TASK-12 | 依赖 TASK-7 后端 API 创建后才能封装接口 |
| TASK-13 | 依赖 TASK-9, TASK-11, TASK-12 所有页面完成后配置路由 |

## 文件清单

### 后端文件
| 文件路径 | Task |
|----------|------|
| pom.xml | TASK-1 |
| src/main/resources/application.yml | TASK-2 |
| src/main/resources/schema.sql | TASK-2 |
| src/main/java/com/village/entity/Resident.java | TASK-3 |
| src/main/java/com/village/dto/ResidentDTO.java | TASK-4 |
| src/main/java/com/village/dto/ResidentQueryDTO.java | TASK-4 |
| src/main/java/com/village/dto/ResidentStatisticsDTO.java | TASK-4 |
| src/main/java/com/village/dto/Result.java | TASK-4 |
| src/main/java/com/village/dao/ResidentDao.java | TASK-5 |
| src/main/java/com/village/service/ResidentService.java | TASK-6 |
| src/main/java/com/village/service/impl/ResidentServiceImpl.java | TASK-6 |
| src/main/java/com/village/controller/ResidentController.java | TASK-7 |
| src/main/java/com/village/util/ExcelUtil.java | TASK-8 |

### 前端文件
| 文件路径 | Task |
|----------|------|
| src/main/webapp/src/views/resident/ResidentList.vue | TASK-9 |
| src/main/webapp/src/views/resident/ResidentForm.vue | TASK-10 |
| src/main/webapp/src/views/resident/ResidentStatistics.vue | TASK-11 |
| src/main/webapp/src/request/resident.js | TASK-12 |
| src/main/webapp/src/router/index.js | TASK-13 |

## 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 数据库连接配置 | 中 | SQLite JDBC 配置较简单，提前准备 |
| 前后端联调 | 高 | 定义清晰 API 接口，先 mock 测试 |
| Excel 样式导出 | 低 | 使用 EasyExcel 默认样式，后续优化 |

## 验收条件

- [ ] 后端 8 个 API 接口可正常调用
- [ ] 前端 3 个页面可正常展示
- [ ] 分页查询功能正常
- [ ] Excel 可正常导出下载
- [ ] 人口统计结果正确
- [ ] 软删除和销户状态独立

## 后续阶段

本计划为 implement 阶段的执行指南。按 Task 顺序执行，完成后更新 pipeline-state-1.md 的 Task 状态。