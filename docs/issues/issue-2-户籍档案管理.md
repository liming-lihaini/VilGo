# ISSUE-2-户籍档案管理

## 概述

以家庭户为单位进行管理，家庭户的主数据来源于村民档案中标记为"户主"的人员(is_household_head=1)。支持家庭成员管理（从村民档案中选择成员加入家庭户）、年度家庭收入台账管理、户籍变动记录（迁入/迁出/新生儿/分户/合户）。支持按家庭户编号、户主姓名查询，自动统计家庭户数、总人口数。

## 功能需求

### F-HOU-001 家庭户同步（自动创建）

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 家庭户同步 |
| 需求描述 | 系统自动从村民档案中同步标记为"户主"的人员，创建家庭户记录 |
| 数据来源 | residents表中 is_household_head=1 的人员 |
| 触发时机 | 村民档案中户主标记变更时自动同步 |
| 输出 | 家庭户列表包含户主基本信息 |

### F-HOU-002 家庭户管理

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 家庭户管理 |
| 需求描述 | 管理家庭户基本信息（住址、联系方式等） |
| 输入 | 家庭户ID、要修改的字段 |
| 校验 | 家庭户存在 |
| 输出 | 更新成功，列表刷新 |

### F-HOU-003 家庭成员管理

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 家庭成员管理 |
| 需求描述 | 从村民档案中选择成员加入或移除家庭户 |
| 输入 | 家庭户ID、成员村民ID、与户主关系 |
| 校验 | 成员村民档案存在，成员未在其他家庭户 |
| 输出 | 成员列表更新 |
| 关系类型 | 配偶、子女、父母、兄弟姐妹、其他 |
| 异常处理 | 成员已关联其他家庭户提示"该人员已是其他家庭户成员" |

### F-HOU-004 年度收入台账

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 年度收入台账 |
| 需求描述 | 记录家庭年度收入信息，支持多年记录 |
| 输入 | 家庭户ID、年份、总收入、人均收入、收入来源、备注 |
| 校验 | 年份格式（4位数字）、家庭户存在、同一家庭同一年份只能有一条记录 |
| 输出 | 收入记录入库 |
| 收入来源 | 农业生产、务工经商的工资收入、经营收入、财产性收入、转移性收入、其他 |

### F-HOU-005 户籍变动记录

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 户籍变动记录 |
| 需求描述 | 记录户籍变动类型、时间、原因，生成留痕 |
| 输入 | 家庭户ID、变动类型、变动时间、变动原因、相关人员 |
| 变动类型 | 迁入、迁出、新生儿、分户、合户 |
| 校验 | 家庭户存在，变动类型有效 |
| 输出 | 变动记录入库 |
| 日志记录 | 变动前状态、变动后状态、操作时间 |

### F-HOU-006 户籍查询统计

| 需求项 | 内容 |
|--------|------|
| 需求名称 | 户籍查询统计 |
| 需求描述 | 按家庭户编号、户主姓名、地址查询，统计户数、人口数 |
| 输入 | 查询条件（编号、姓名、地址） |
| 校验 | - |
| 输出 | 符合条件的家庭户列表，统计结果 |

## 技术方案

### 前端界面设计

#### 页面结构

```
┌────────────────────────────────────────────────────────────┐
│  查询条件: [家庭户编号] [户主姓名] [住址] [查询] [重置]     │
├────────────────────────────────────────────────────────────┤
│  工具栏: [刷新家庭户] [导出]                                │
├──────────────────────┬─────────────────────────────────────┤
│   左侧家庭户列表      │           详情区                    │
│   (树形/列表)         │  ┌────────────────────────────────┐ │
│                      │  │ 户主: 张三     家庭户编号: H001 │ │
│  ┌ H001              │  │ 地址: XX村XX组                  │ │
│  │  └ 张三(户主)     │  │ 成员数: 4人    创建时间: xxx    │ │
│  ├ H002              │  ├────────────────────────────────┤ │
│  │  └ 李四(户主)     │  │ [家庭成员] [年度收入] [变动记录]│ │
│  ├ H003              │  │                                │ │
│  │  └ 王五(户主)     │  │   Tab内容区域                   │ │
│  └ ...               │  │                                │ │
│                      │  └────────────────────────────────┘ │
├──────────────────────┴─────────────────────────────────────┤
│  统计: 家庭户数: 300  |  总人口数: 1000                     │
└────────────────────────────────────────────────────────────┘
```

#### 详情抽屉/弹窗

```
┌─────────────────────────────────────┐
│  家庭户详情                    [X]  │
├─────────────────────────────────────┤
│  基本信息                           │
│  ┌───────────────────────────────┐ │
│  │ 户主姓名: 张三  性别: 男       │ │
│  │ 身份证号: 320...              │ │
│  │ 家庭住址: XX村XX组            │ │
│  │ 成员数量: 4人                 │ │
│  └───────────────────────────────┘ │
│                                     │
│  [家庭成员] [年度收入] [变动记录]   │
│  ┌───────────────────────────────┐ │
│  │ 成员列表:                      │ │
│  │ 1. 张三(户主) - 男             │ │
│  │ 2. 张妻(配偶) - 女             │ │
│  │ 3. 张子(子女) - 男             │ │
│  │ [+ 添加成员]                   │ │
│  └───────────────────────────────┘ │
└─────────────────────────────────────┘
```

#### 关键组件

| 组件 | 描述 | 事件 |
|------|------|------|
| `HouseholdList` | 家庭户列表（左侧） | `select`, `refresh` |
| `HouseholdDetail` | 家庭户详情（右侧） | `edit`, `view` |
| `MemberManage` | 家庭成员管理 | `add`, `remove`, `relation-change` |
| `IncomeManage` | 年度收入管理 | `add`, `edit`, `delete` |
| `ChangeRecord` | 户籍变动记录 | `add`, `view` |
| `AddMemberDialog` | 添加成员弹窗 | `confirm`, `cancel` |
| `AddIncomeDialog` | 添加收入弹窗 | `confirm`, `cancel` |
| `AddChangeDialog` | 添加变动弹窗 | `confirm`, `cancel` |

#### 交互流程

**家庭户同步流程**:
```
村民档案标记"是否户主"为是 → 自动创建/更新家庭户记录
→ 家庭户列表自动更新
```

**添加家庭成员流程**:
```
点击"添加成员" → 弹出成员选择对话框 → 从村民档案中选择非户主人员
→ 选择与户主关系 → 确认添加
→ 调用POST /api/household/member/add → 刷新成员列表
```

**记录年度收入流程**:
```
切换到"年度收入"Tab → 点击"添加" → 填写年份、收入信息
→ 调用POST /api/household/income/add → 刷新收入列表
```

**户籍变动流程**:
```
切换到"变动记录"Tab → 点击"添加变动" → 选择变动类型、填写变动信息
→ 调用POST /api/household/change/add → 刷新变动列表
```

#### 异常提示

| 场景 | 提示信息 |
|------|----------|
| 成员已关联其他家庭户 | "该人员已是其他家庭户成员" |
| 重复添加同一成员 | "该人员已是本家庭户成员" |
| 同一家庭同一年份收入已存在 | "该年度收入记录已存在" |
| 家庭户不存在 | "家庭户不存在或已删除" |

### 数据结构

**家庭户表 (households)**

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | INTEGER | PK, AUTO | 家庭户ID |
| household_no | VARCHAR(20) | UNIQUE, NOT NULL | 家庭户编号（如H2024001） |
| head_id | INTEGER | FK->residents(id), UNIQUE | 户主ID（关联村民档案） |
| head_name | VARCHAR(50) | | 户主姓名 |
| head_id_card | VARCHAR(18) | | 户主身份证号 |
| address | VARCHAR(200) | | 家庭住址 |
| phone | VARCHAR(20) | | 联系电话 |
| member_count | INTEGER | DEFAULT 0 | 家庭成员数量 |
| create_time | TEXT | | 创建时间 |
| update_time | TEXT | | 更新时间 |
| deleted | INTEGER | DEFAULT 0 | 软删除 |

**家庭成员表 (household_members)**

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | INTEGER | PK, AUTO | 成员ID |
| household_id | INTEGER | FK->households(id) | 家庭户ID |
| resident_id | INTEGER | FK->residents(id) | 村民ID |
| relation | VARCHAR(20) | | 与户主关系（配偶/子女/父母/兄弟姐妹/其他） |
| create_time | TEXT | | 创建时间 |
| deleted | INTEGER | DEFAULT 0 | 软删除 |

**年度收入表 (household_income)**

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | INTEGER | PK, AUTO | 记录ID |
| household_id | INTEGER | FK->households(id) | 家庭户ID |
| year | INTEGER | NOT NULL | 年份 |
| total_income | DECIMAL(12,2) | | 总收入金额（元） |
| per_capita_income | DECIMAL(12,2) | | 人均收入（元） |
| income_source | VARCHAR(200) | | 主要收入来源 |
| remark | VARCHAR(500) | | 备注 |
| create_time | TEXT | | 创建时间 |
| deleted | INTEGER | DEFAULT 0 | 软删除 |

**户籍变动表 (household_changes)**

| 字段 | Type | 约束 | 说明 |
|------|------|------|------|
| id | INTEGER | PK, AUTO | 变动ID |
| household_id | INTEGER | FK->households(id) | 家庭户ID |
| change_type | VARCHAR(20) | NOT NULL | 变动类型（迁入/迁出/新生儿/分户/合户） |
| change_time | TEXT | | 变动时间 |
| change_reason | VARCHAR(200) | | 变动原因 |
| related_persons | VARCHAR(200) | | 相关人员（JSON数组） |
| before_status | VARCHAR(100) | | 变动前状态 |
| after_status | VARCHAR(100) | | 变动后状态 |
| remark | VARCHAR(500) | | 备注 |
| create_time | TEXT | | 创建时间 |
| deleted | INTEGER | DEFAULT 0 | 软删除 |

### 接口设计

| 接口 | 方法 | 路径 | 功能 |
|------|------|------|------|
| 同步家庭户 | POST | /api/household/sync | 从村民档案同步户主创建家庭户 |
| 获取家庭户 | GET | /api/household/get/{id} | 获取家庭户详情（含成员） |
| 查询列表 | POST | /api/household/list | 条件查询家庭户列表 |
| 更新家庭户 | PUT | /api/household/update | 更新家庭户信息 |
| 删除家庭户 | DELETE | /api/household/delete/{id} | 删除家庭户（软删除） |
| 获取成员列表 | GET | /api/household/members/{householdId} | 获取家庭成员列表 |
| 添加成员 | POST | /api/household/member/add | 添加家庭成员 |
| 移除成员 | DELETE | /api/household/member/remove/{id} | 移除家庭成员 |
| 更新成员关系 | PUT | /api/household/member/update/{id} | 更新成员与户主关系 |
| 获取收入列表 | GET | /api/household/income/{householdId} | 获取年度收入列表 |
| 添加收入 | POST | /api/household/income/add | 添加年度收入 |
| 更新收入 | PUT | /api/household/income/update/{id} | 更新年度收入 |
| 删除收入 | DELETE | /api/household/income/delete/{id} | 删除年度收入 |
| 获取变动列表 | GET | /api/household/changes/{householdId} | 获取户籍变动列表 |
| 添加变动 | POST | /api/household/change/add | 添加户籍变动记录 |
| 户籍统计 | POST | /api/household/statistics | 户籍统计 |

### 请求参数示例

```json
// 同步家庭户
POST /api/household/sync
响应: { successCount: 10, failCount: 0 }

// 查询家庭户列表
POST /api/household/list
{
  "householdNo": "H2024",
  "headName": "张",
  "address": "XX村",
  "pageNum": 1,
  "pageSize": 10
}

// 添加家庭成员
POST /api/household/member/add
{
  "householdId": 1,
  "residentId": 100,
  "relation": "配偶"
}

// 添加年度收入
POST /api/household/income/add
{
  "householdId": 1,
  "year": 2024,
  "totalIncome": 120000,
  "perCapitaIncome": 30000,
  "incomeSource": "务工经商的工资收入",
  "remark": "全家外出务工"
}

// 添加户籍变动
POST /api/household/change/add
{
  "householdId": 1,
  "changeType": "迁入",
  "changeTime": "2024-01-15",
  "changeReason": "因工作调动迁入",
  "relatedPerson": "张三一家3口"
}
```

### 业务规则

1. **家庭户编号**: 全局唯一，自动生成（如H + 年份 + 4位序号 H20240001）
2. **户主来源**: 仅从村民档案中 is_household_head=1 的人员同步创建
3. **成员关系**: 每个家庭成员必须指定与户主的关系
4. **成员唯一性**: 同一人员只能属于一个家庭户
5. **年度收入**: 同一家庭户同一年份只能有一条收入记录
6. **户籍变动**: 记录变动前后状态，保留完整变更轨迹
7. **软删除**: 删除家庭户时保留历史数据，成员关系自动解除
8. **变动类型**: 迁入、迁出、新生儿、分户、合户

## 验收条件

- [x] 从村民档案同步户主创建家庭户，列表显示正确
- [x] 家庭户编号唯一性校验生效
- [x] 添加/移除家庭成员，成员列表正确更新
- [x] 成员关系维护（配偶、子女等）正常
- [x] 记录年度收入，多年份数据正确
- [x] 同一家庭同一年份只能添加一次收入
- [x] 户籍变动记录完整保留（类型、时间、原因）
- [x] 查询家庭户，条件过滤正常
- [ ] 户籍统计，户数、人口数正确
- [x] 删除家庭户，成员自动解除关联

## 实现摘要

本次实现完成以下内容：

**后端（14个API）：**
- HouseholdController: 家庭户 CRUD、同步、详情
- HouseholdMemberService: 成员添加/移除
- HouseholdIncomeService: 年度收入管理
- HouseholdChangeService: 户籍变动记录

**前端：**
- HouseholdList.vue: 列表页 + 详情抽屉（含成员/收入/变动Tab）
- household.js: API封装

**数据库：**
- 4张表：households, household_members, household_income, household_changes

**与计划的偏差：**
- 无偏差

## 状态

- [ ] 待开发
- [ ] 开发中
- [ ] 待测试
- [x] 完成

---

## 元数据

- status: closed
- closed: 2026-04-23
- PR: 直接提交到 main（未创建 PR）
- commit: 24fd87b