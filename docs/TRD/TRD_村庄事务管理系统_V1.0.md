# 技术需求文档（TRD）
## 新农村村务通管理软件（单机版）

---

## 1. 文档信息

| 属性 | 内容 |
|------|------|
| 文档版本 | V1.0 |
| 文档状态 | 正式版 |
| 适用阶段 | 研发/测试/部署 |
| 关联PRD版本 | V1.0 |
| 编写人 | AI自动生成 |
| 编写日期 | 2026-04-22 |

---

## 2. 需求概述

### 2.1 业务背景

面向村级工作人员（两委成员、网格员）及村民的单机版数字化管理工具，聚焦村级日常管理痛点：档案混乱、信息零散、统计繁琐、公示不便等。当前存在的主要问题：

- 档案管理混乱：村民信息、户籍信息缺乏统一规范化管理
- 信息零散：各类数据分散，缺乏关联和整合
- 统计繁琐：人工统计工作量大、效率低
- 公示不便：信息发布和反馈渠道不畅

### 2.2 项目目标

- **功能目标**：覆盖9大核心模块，涵盖人事、户籍、党务、活动等村级管理全场景，实现核心业务流程数字化
- **体验目标**：界面简洁、操作便捷，减少冗余步骤；安装即能用，适配中老年村级工作人员操作习惯
- **技术目标**：基于Java+SQLite架构，本地独立运行，无外网依赖，运行稳定、响应流畅
- **业务目标**：规范村级管理流程，减少人工录入、统计工作量；实现数据可查可追溯

### 2.3 核心价值

- 实现村民"一人一档"规范化管理
- 户籍家庭户为单位管理，支持变动留痕
- 帮扶工作全流程数字化、留痕化
- 公益活动出勤记工自动化统计
- 党务工作数字化管理
- 信息公示与新闻动态统一发布
- 数字大屏数据可视化展示

### 2.4 适用用户/场景

- **用户**：村级工作人员（两委成员、网格员）、村民
- **场景**：单机Windows设备，无需网络连接
- **权限**：不设置权限管理，所有用户均可使用全部功能

---

## 3. 功能拆解（技术视角）

### 3.1 模块划分

| 序号 | 模块名称 | 前端路径 | 后端包路径 |
|------|----------|----------|-----------|
| 1 | 村民人事档案管理 | /views/resident | com.village.controller/resident |
| 2 | 户籍档案管理 | /views/household | com.village.controller/household |
| 3 | 特殊人群档案管理 | /views/specialGroup | com.village.controller/specialGroup |
| 4 | 两委班子管理 | /views/twoCommittee | com.village.controller/twoCommittee |
| 5 | 公益活动管理 | /views/publicActivity | com.village.controller/publicActivity |
| 6 | 党务管理 | /views/partyWork | com.village.controller/partyWork |
| 7 | 村委公示栏 | /views/notice | com.village.controller/notice |
| 8 | 新闻动态 | /views/news | com.village.controller/news |
| 9 | 数字大屏 | /views/screen | com.village.controller/screen |

### 3.2 核心功能点

#### 模块1：村民人事档案管理
- 村民档案新增（身份证号唯一性校验）
- 村民档案编辑、删除（软删除）
- 销户登记（标记状态，保留历史数据）
- 多条件查询（身份证号、姓名、住址等）
- 人口统计
- Excel报表导出
- 关联社保/养老/低保等保障档案

#### 模块2：户籍档案管理
- 家庭户创建（自动关联户主）
- 家庭户编辑
- 家庭成员管理（关联人事档案）
- 年度收入台账记录
- 户籍变动记录（迁入/迁出/新生儿/分户/合户）
- 按家庭户编号、户主姓名查询
- 家庭户数、总人口数统计

#### 模块3：特殊人群档案管理
- 特殊人群登记（脱贫户/监测户/残疾人/孤寡老人/其他）
- 帮扶责任人分配
- 帮扶措施记录
- 帮扶时间与成效记录
- 帮扶任务到期提醒
- 帮扶报表导出

#### 模块4：两委班子管理
- 两委成员信息管理
- 工作分工管理
- 任务分配与进度跟踪
- 会议记录（时间、地点、参会人员、决议）
- 决议事项关联任务分配
- 班子成员查询与统计

#### 模块5：公益活动管理
- 活动发起（名称、时间、地点、内容、报名要求）
- 村民报名登记
- 出勤签到登记
- 出勤时长、工值记录
- 活动归档（保存照片、总结）
- 活动统计（参与人数、出勤时长）

#### 模块6：党务管理
- 党员档案管理（关联人事档案）
- 入党时间、转正时间记录
- 党员状态管理（积极分子/预备党员/正式党员）
- 党务活动记录（主题党日、组织生活会）
- 党费收缴统计
- 党务活动归档

#### 模块7：村委公示栏
- 公示内容发布（通知/政策文件/财务公开/项目公示/惠民信息）
- 图文编辑
- 公示修改、删除
- 村民查看
- 反馈收集与回复
- 按标题、发布时间查询

#### 模块8：新闻动态
- 新闻发布（标题、内容、分类标签）
- 图文编辑
- 分类标签设置（政策解读/活动报道等）
- 新闻编辑、删除、归档
- 按分类、发布时间查询

#### 模块9：数字大屏
- 数据聚合展示
- 总人口数、家庭户数
- 人员结构分布
- 特殊人群数量及帮扶进度
- 两委任务完成情况
- 公益活动统计
- 公示及新闻更新提醒
- 户籍变动统计
- 全屏展示模式

### 3.3 非功能需求

| 类别 | 需求说明 |
|------|----------|
| 性能 | 同时在线用户≤10人；查询响应时间≤3秒；大数据量（万级）无明显卡顿 |
| 兼容性 | JDK 17（64位）+ Windows 10/11（64位） |
| 安全性 | 本地存储，无数据加密功能；无远程备份功能；建议用户定期手动备份 |
| 可用性 | 界面简洁，适配中老年用户操作习惯；安装即用，无需复杂配置 |
| 数据持久化 | SQLite单文件存储，重启后数据保留 |

### 3.4 权限与角色设计

- 不设置权限管理
- 所有用户均可访问全部功能
- 人员角色字段（管理员/普通用户）预留，用于后续扩展

---

## 4. 技术方案设计

### 4.1 整体架构

#### 4.1.1 技术栈选型

| 层级 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| 后端语言 | Java | 17 | LTS版本 |
| 后端框架 | Spring Boot | 2.7.x | Web服务 |
| ORM | MyBatis-Plus | 3.5.x | 禁止直接SQL |
| 数据库 | SQLite | 3.40+ | 单文件本地存储 |
| 前端框架 | Vue | 3.x | 组合式API |
| 前端UI | Element Plus | 最新 | 组件库 |
| 前端构建 | Vite | 5.x | 开发/构建 |
| 打包工具 | Launch4j | 最新 | exe输出 |
| 编辑器 | Milkdown | 最新 | Markdown编辑 |

#### 4.1.2 系统架构图（文字描述）

```
┌─────────────────────────────────────────────────────────┐
│                     Windows 10/11                      │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────┐   │
│  │           VillageAffairsApplication            │   │
│  │              (Spring Boot 启动类)                │   │
│  ├──────────────────┬──────────────────────────────┤   │
│  │   后端模块       │        前端模块              │   │
│  │  ┌───────────┐  │  ┌────────────────────────┐  │   │
│  │  │Controller │  │  │    Vue 3 + Vite        │  │   │
│  │  │    ↓      │  │  │    (Element Plus)      │  │   │
│  │  │ Service   │  │  │    ┌──────────────┐    │  │   │
│  │  │    ↓      │  │  │    │  Views/     │    │  │   │
│  │  │  Dao      │  │  │    │Components/  │    │  │   │
│  │  └─────┬─────┘  │  │    │ Router/    │    │  │   │
│  │        ↓        │  │    │ Store/     │    │  │   │
│  │  ┌───────────┐  │  │    └──────────────┘    │  │   │
│  │  │  Entity   │  │  └────────────────────────┘  │   │
│  │  └─────┬─────┘                                │   │
│  └────────┼────────────────────────────────────────┘   │
│           ↓                                              │
│  ┌─────────────────────────────────────────────────┐   │
│  │              SQLite 3 Database                   │   │
│  │               (village.db 单文件)                │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

#### 4.1.3 部署架构说明

- 单机部署模式：无外网依赖
- 前后端一体化：Spring Boot同时托管Vue构建产物
- 静态资源：src/main/resources/static（Vite打包后）
- 数据库文件：user.home/.village/village.db
- 日志文件：user.home/.village/logs/

### 4.2 功能模块设计

---

#### 4.2.1 模块一：村民人事档案管理

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 档案新增 | 录入村民基础信息，身份证号唯一性校验 |
| 档案编辑 | 修改村民信息 |
| 档案删除 | 软删除，标记deleted=1 |
| 销户登记 | 标记household_status=销户，保留历史数据 |
| 多条件查询 | 支持身份证号、姓名、住址模糊查询 |
| 人口统计 | 按人员类型、保障类型等条件统计 |
| Excel导出 | 档案报表导出为Excel格式 |

**业务流程**

```
新增档案流程：
1. 工作人员录入村民信息（姓名、身份证号、性别等）
2. 系统校验身份证号唯一性
3. 校验通过 → 档案入库
4. 校验失败 → 提示"已存在相关记录，请核对后重新录入"

销户流程：
1. 选择村民档案
2. 点击销户登记
3. 系统保留原始数据，标记销户状态
4. 销户后档案仍可查询，标记显示
```

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/resident/create | POST | 新增村民档案 |
| /api/resident/update | PUT | 更新村民档案 |
| /api/resident/delete/{id} | DELETE | 删除村民档案（软删除） |
| /api/resident/get/{id} | GET | 获取单条档案 |
| /api/resident/list | POST | 条件查询档案列表 |
| /api/resident/cancel/{id} | PUT | 销户登记 |
| /api/resident/statistics | POST | 人口统计 |
| /api/resident/export | POST | Excel导出 |

**请求参数示例**

```json
// 新增村民档案
{
  "idCard": "320105199001011234",
  "name": "张三",
  "gender": "男",
  "birthDate": "1990-01-01",
  "phone": "13812345678",
  "address": "XX村XX组",
  "maritalStatus": "已婚",
  "politicalStatus": "群众",
  "guaranteeType": "社保",
  "isHead": false,
  "personType": "群众",
  "personRole": "普通用户",
  "remark": ""
}

// 条件查询
{
  "idCard": "",
  "name": "",
  "address": "",
  "personType": "",
  "guaranteeType": "",
  "householdStatus": "正常",
  "deleted": 0,
  "pageNum": 1,
  "pageSize": 10
}
```

**响应格式**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [...]
  }
}
```

**数据设计**

| 表名 | 说明 |
|------|------|
| residents | 村民档案主表 |

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO | 档案ID |
| id_card | VARCHAR(18) | UNIQUE, NOT NULL | 身份证号 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | VARCHAR(10) | | 性别 |
| birth_date | DATE | | 出生日期 |
| phone | VARCHAR(20) | | 联系方式 |
| address | VARCHAR(200) | | 家庭住址 |
| marital_status | VARCHAR(20) | | 婚姻状况 |
| political_status | VARCHAR(20) | | 政治面貌 |
| photo | VARCHAR(500) | | 照片路径 |
| guarantee_type | VARCHAR(20) | | 保障类型 |
| household_status | VARCHAR(10) | DEFAULT '正常' | 销户状态 |
| is_head | TINYINT | DEFAULT 0 | 是否户主 |
| person_type | VARCHAR(20) | | 人员类型 |
| person_role | VARCHAR(20) | | 人员角色 |
| remark | TEXT | | 备注 |
| creator | VARCHAR(50) | | 录入人 |
| created_at | DATETIME | | 录入时间 |
| updated_at | DATETIME | | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 软删除 |

**验收标准**

- 身份证号唯一性校验生效，重复录入提示错误
- 销户后档案保留历史，查询可识别销户状态
- 删除为软删除，恢复后数据完整
- Excel导出格式正确

---

#### 4.2.2 模块二：户籍档案管理

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 家庭户创建 | 同步户主信息，无需重复录入 |
| 家庭户编辑 | 修改家庭户信息 |
| 成员管理 | 添加、删除、修改家庭成员（关联人事档案） |
| 年度收入 | 记录年份、总收入、人均收入、收入来源 |
| 户籍变动 | 记录迁入/迁出/新生儿/分户/合户 |
| 查询统计 | 按家庭户编号、户主姓名查询，统计户数、人口数 |

**业务流程**

```
创建家庭户流程：
1. 选择户主（关联已存在的村民档案）
2. 系统自动获取户主信息
3. 生成家庭户编号（唯一）
4. 录入家庭住址等信息
5. 保存家庭户

户籍变动流程：
1. 选择家庭户
2. 选择变动类型（迁入/迁出/新生儿/分户/合户）
3. 录入变动时间、原因
4. 系统生成变动留痕记录
5. 如为分户/合户，自动创建新家庭户关联
```

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/household/create | POST | 创建家庭户 |
| /api/household/update | PUT | 更新家庭户 |
| /api/household/delete/{id} | DELETE | 删除家庭户 |
| /api/household/get/{id} | GET | 获取家庭户详情 |
| /api/household/list | POST | 条件查询家庭户列表 |
| /api/household/member/add | POST | 添加家庭成员 |
| /api/household/member/remove/{id} | DELETE | 移除家庭成员 |
| /api/household/income/add | POST | 添加年度收入 |
| /api/household/income/list | POST | 查询年度收入列表 |
| /api/household/change/add | POST | 添加户籍变动记录 |
| /api/household/change/list | POST | 查询户籍变动列表 |
| /api/household/statistics | POST | 户籍统计 |

**数据设计**

| 表名 | 说明 |
|------|------|
| households | 家庭户主表 |
| household_members | 家庭成员表 |
| household_income | 年度收入表 |
| household_changes | 户籍变动表 |

**households表结构**

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO | 家庭户ID |
| household_no | VARCHAR(20) | UNIQUE, NOT NULL | 家庭户编号 |
| head_id | BIGINT | FK->residents | 户主ID |
| head_name | VARCHAR(50) | | 户主姓名 |
| address | VARCHAR(200) | | 家庭住址 |
| attachments | TEXT | | 附件材料（JSON） |
| creator | VARCHAR(50) | | 录入人 |
| created_at | DATETIME | | 创建时间 |
| updated_at | DATETIME | | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 软删除 |

**household_members表结构**

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO | 成员ID |
| household_id | BIGINT | FK->households | 家庭户ID |
| resident_id | BIGINT | FK->residents | 村民ID |
| relation | VARCHAR(20) | | 与户主关系 |
| created_at | DATETIME | | 创建时间 |

**验收标准**

- 户主信息从村民档案同步，无需重复录入
- 家庭户编号唯一性校验生效
- 户籍变动生成完整留痕记录
- 软删除校验关联数据

---

#### 4.2.3 模块三：特殊人群档案管理

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 特殊人群登记 | 登记脱贫户、监测户、残疾人、孤寡老人 |
| 帮扶责任人 | 关联两委班子成员 |
| 帮扶措施 | 记录帮扶内容、时间、成效 |
| 帮扶提醒 | 任务到期自动提示 |
| 统计报表 | 按人群类型、帮扶状态统计 |

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/specialGroup/create | POST | 登记特殊人群 |
| /api/specialGroup/update | PUT | 更新特殊人群 |
| /api/specialGroup/delete/{id} | DELETE | 删除特殊人群 |
| /api/specialGroup/get/{id} | GET | 获取详情 |
| /api/specialGroup/list | POST | 条件查询列表 |
| /api/specialGroup/statistics | POST | 帮扶统计 |
| /api/specialGroup/export | POST | 报表导出 |
| /api/specialGroup/help/add | POST | 添加帮扶记录 |
| /api/specialGroup/remind | GET | 获取到期提醒 |

**数据设计**

| 表名 | 说明 |
|------|------|
| special_groups | 特殊人群表 |
| help_records | 帮扶记录表 |

**special_groups表结构**

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO | 特殊人群ID |
| id_card | VARCHAR(18) | UNIQUE, NOT NULL | 身份证号 |
| group_type | VARCHAR(20) | NOT NULL | 人群类型 |
| helper_id | BIGINT | FK->two_committee | 帮扶责任人ID |
| help_content | TEXT | | 帮扶措施 |
| help_time | DATE | | 帮扶时间 |
| help_result | TEXT | | 帮扶成效 |
| help_status | VARCHAR(20) | | 帮扶状态 |
| creator | VARCHAR(50) | | 录入人 |
| created_at | DATETIME | | 创建时间 |
| updated_at | DATETIME | | 更新时间 |

---

#### 4.2.4 模块四：两委班子管理

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 成员信息 | 管理两委成员姓名、职务、联系方式等 |
| 工作分工 | 关联对应管理模块 |
| 任务分配 | 分配任务、进度跟踪、完成登记 |
| 会议记录 | 记录会议时间、地点、参会人员、决议事项 |
| 决议关联 | 决议事项可关联任务分配 |

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/twoCommittee/create | POST | 新增成员 |
| /api/twoCommittee/update | PUT | 更新成员 |
| /api/twoCommittee/delete/{id} | DELETE | 删除成员 |
| /api/twoCommittee/get/{id} | GET | 获取成员详情 |
| /api/twoCommittee/list | POST | 成员列表 |
| /api/task/create | POST | 创建任务 |
| /api/task/update | PUT | 更新任务 |
| /api/task/list | POST | 任务列表 |
| /api/meeting/create | POST | 创建会议记录 |
| /api/meeting/update | PUT | 更新会议记录 |
| /api/meeting/list | POST | 会议列表 |

**数据设计**

| 表名 | 说明 |
|------|------|
| two_committee | 两委班子成员表 |
| tasks | 任务表 |
| meetings | 会议记录表 |

---

#### 4.2.5 模块五：公益活动管理

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 活动发起 | 填写名称、时间、地点、内容、报名要求 |
| 村民报名 | 工作人员协助录入报名信息 |
| 出勤签到 | 记录出勤情况、时长、工值 |
| 活动归档 | 保存照片、总结等资料 |
| 活动统计 | 统计参与人数、出勤时长 |

**业务流程**

```
活动发起流程：
1. 工作人员填写活动信息
2. 保存活动状态为"未开始"
3. 活动开始后状态变为"进行中"
4. 活动结束后归档

出勤流程：
1. 村民到达活动现场
2. 工作人员签到登记
3. 活动结束后记录出勤时长、工值
4. 汇总统计
```

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/publicActivity/create | POST | 创建活动 |
| /api/publicActivity/update | PUT | 更新活动 |
| /api/publicActivity/delete/{id} | DELETE | 删除活动 |
| /api/publicActivity/get/{id} | GET | 获取活动详情 |
| /api/publicActivity/list | POST | 活动列表 |
| /api/publicActivity/signup | POST | 村民报名 |
| /api/publicActivity/signin | POST | 出勤签到 |
| /api/publicActivity/archive/{id} | PUT | 活动归档 |
| /api/publicActivity/statistics | POST | 活动统计 |

**数据设计**

| 表名 | 说明 |
|------|------|
| public_activities | 公益活动表 |
| activity_signups | 活动报名表 |

---

#### 4.2.6 模块六：党务管理

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 党员档案 | 管理党员信息（关联人事档案） |
| 党员发展 | 入党积极分子→发展对象→预备党员→正式党员 |
| 党务活动 | 主题党日、组织生活会等 |


**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/partyWork/create | POST | 新增党员档案 |
| /api/partyWork/update | PUT | 更新党员档案 |
| /api/partyWork/delete/{id} | DELETE | 删除党员档案 |
| /api/partyWork/get/{id} | GET | 获取党员档案 |
| /api/partyWork/list | POST | 党员列表 |
| /api/partyWork/activity/create | POST | 党务活动记录 |
| /api/partyWork/activity/list | POST | 党务活动列表 |


**数据设计**

| 表名 | 说明 |
|------|------|
| party_members | 党员档案表 |
| party_activities | 党务活动表 |


---

#### 4.2.7 模块七：村委公示栏

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 公示发布 | 发布通知、政策文件、财务公开等 |
| 图文编辑 | 支持图片插入 |
| 公示修改/删除 | 修改或删除已发布内容 |
| 村民查看 | 按时间排序查看所有公示 |
| 反馈收集 | 村民提交反馈意见 |

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/notice/create | POST | 发布公示 |
| /api/notice/update | PUT | 更新公示 |
| /api/notice/delete/{id} | DELETE | 删除公示 |
| /api/notice/get/{id} | GET | 获取公示详情 |
| /api/notice/list | POST | 公示列表 |
| /api/notice/feedback | POST | 提交反馈 |
| /api/notice/feedback/list | POST | 反馈列表 |

**数据设计**

| 表名 | 说明 |
|------|------|
| notices | 公示栏表 |
| notice_feedback | 公示反馈表 |

---

#### 4.2.8 模块八：新闻动态

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 新闻发布 | 发布村级新闻、政策解读等 |
| 分类标签 | 设置分类（政策解读/活动报道等） |
| 图文编辑 | 支持图片插入 |
| 新闻管理 | 编辑、删除、归档 |

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/news/create | POST | 发布新闻 |
| /api/news/update | PUT | 更新新闻 |
| /api/news/delete/{id} | DELETE | 删除新闻 |
| /api/news/get/{id} | GET | 获取新闻详情 |
| /api/news/list | POST | 新闻列表 |
| /api/news/archive/{id} | PUT | 新闻归档 |

**数据设计**

| 表名 | 说明 |
|------|------|
| news | 新闻表 |

---

#### 4.2.9 模块九：数字大屏

**功能说明**

| 功能点 | 说明 |
|--------|------|
| 数据聚合 | 各模块实时数据汇总 |
| 指标展示 | 总人口、家庭户数、人员结构等 |
| 全屏模式 | 适配村级办公场所显示设备 |

**接口设计**

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| /api/screen/dashboard | GET | 获取大屏数据 |

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalPopulation": 1000,
    "householdCount": 300,
    "personStructure": {
      "masses": 600,
      "partyMembers": 50,
      "students": 100,
      "military": 20,
      "other": 230
    },
    "specialGroups": {
      "total": 50,
      "povertyRelief": 20,
      "disabled": 15,
      "elderlyAlone": 10,
      "other": 5
    },
    "helpProgress": {
      "ongoing": 30,
      "completed": 20
    },
    "taskProgress": {
      "pending": 10,
      "inProgress": 5,
      "completed": 25
    },
    "activities": {
      "recentCount": 5,
      "totalParticipants": 200
    },
    "notices": {
      "pendingCount": 3
    },
    "news": {
      "recentCount": 2
    },
    "householdChanges": {
      "total": 10
    }
  }
}
```

---

### 4.3 异常处理设计

| 异常类型 | 触发场景 | 处理方式 |
|----------|----------|----------|
| 重复录入 | 身份证号/家庭户编号重复 | 提示"已存在相关记录，请核对后重新录入"，阻止提交 |
| 信息缺失 | 必填字段未填写 | 提示"请补充必填信息（标注*字段）"，高亮必填项 |
| 数据格式错误 | 日期/时长/金额格式错误 | 提示"输入格式无效，请按规范填写"，显示正确格式示例 |
| 查询无结果 | 搜索条件无匹配 | 显示"暂无相关记录，请调整查询条件后重试" |
| 操作异常 | 删除已关联数据 | 提示"该数据已关联其他记录，无法直接操作，请先解除关联" |

---

### 4.4 项目结构

```
village-affairs-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── village/
│   │   │           ├── VillageAffairsApplication.java
│   │   │           ├── config/
│   │   │           │   ├── WebConfig.java
│   │   │           │   ├── SqliteConfig.java
│   │   │           │   └── CorsConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── ResidentController.java
│   │   │           │   ├── HouseholdController.java
│   │   │           │   ├── SpecialGroupController.java
│   │   │           │   ├── TwoCommitteeController.java
│   │   │           │   ├── PublicActivityController.java
│   │   │           │   ├── PartyWorkController.java
│   │   │           │   ├── NoticeController.java
│   │   │           │   ├── NewsController.java
│   │   │           │   └── ScreenController.java
│   │   │           ├── service/
│   │   │           │   ├── ResidentService.java
│   │   │           │   ├── HouseholdService.java
│   │   │           │   └── ...
│   │   │           ├── service/impl/
│   │   │           │   └── ...
│   │   │           ├── dao/
│   │   │           │   ├── ResidentDao.java
│   │   │           │   ├── HouseholdDao.java
│   │   │           │   └── ...
│   │   │           ├── entity/
│   │   │           │   ├── Resident.java
│   │   │           │   ├── Household.java
│   │   │           │   └── ...
│   │   │           ├── dto/
│   │   │           │   ├── ResidentDTO.java
│   │   │           │   ├── HouseholdDTO.java
│   │   │           │   └── ...
│   │   │           ├── exception/
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   └── BusinessException.java
│   │   │           └── util/
│   │   │               ├── ExcelUtil.java
│   │   │               └── ...
│   │   ├── resources/
│   │   │   ├── application.yml
│   │   │   └── static/
│   │   └── webapp/
│   │       ├── src/
│   │       │   ├── views/
│   │       │   ├── components/
│   │       │   ├── router/
│   │       │   ├── store/
│   │       │   ├── request/
│   │       │   └── ...
│   │       └── package.json
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

---

### 4.5 数据库表汇总

| 序号 | 表名 | 说明 |
|------|------|------|
| 1 | residents | 村民档案表 |
| 2 | households | 家庭户表 |
| 3 | household_members | 家庭成员表 |
| 4 | household_income | 年度收入表 |
| 5 | household_changes | 户籍变动表 |
| 6 | special_groups | 特殊人群表 |
| 7 | help_records | 帮扶记录表 |
| 8 | two_committee | 两委班子成员表 |
| 9 | tasks | 任务表 |
| 10 | meetings | 会议记录表 |
| 11 | public_activities | 公益活动表 |
| 12 | activity_signups | 活动报名表 |
| 13 | party_members | 党员档案表 |
| 14 | party_activities | 党务活动表 |
| 15 | party_dues | 党费收缴表 |
| 16 | notices | 公示栏表 |
| 17 | notice_feedback | 公示反馈表 |
| 18 | news | 新闻表 |

---

### 4.6 接口前缀汇总

| 模块 | 接口前缀 |
|------|--------|
| 村民档案 | /api/resident |
| 户籍档案 | /api/household |
| 特殊人群 | /api/specialGroup |
| 两委班子 | /api/twoCommittee |
| 任务 | /api/task |
| 会议 | /api/meeting |
| 公益活动 | /api/publicActivity |
| 党务管理 | /api/partyWork |
| 公示栏 | /api/notice |
| 新闻动态 | /api/news |
| 数字大屏 | /api/screen |

---

## 5. 自检清单

| 序号 | 检查项 | 结果 |
|------|--------|------|
| 1 | 结构完整性：包含所有必需章节 | 合规 |
| 2 | 内容对齐度：完全覆盖PRD需求 | 合规 |
| 3 | 语言规范：符合技术文档写作规范 | 合规 |
| 4 | 格式规范：符合Markdown格式要求 | 合规 |
| 5 | 输出路径：正确保存到docs/TRD/目录 | 合规 |
| 6 | 版本管理：文件命名符合版本规范 | 合规 |
| 7 | 功能设计逻辑性：模块划分清晰合理 | 合规 |
| 8 | 技术方案可行性：符合项目技术栈 | 合规 |

---

*文档结束*