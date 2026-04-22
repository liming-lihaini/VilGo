# ADR-7: MD模板管理技术方案

## 背景

Issue #7 需要实现Markdown模板的定义、管理、应用功能。用户可以自定义MD模板，在创建MD文件时快速引用模板内容生成新文件。

核心需求：
- F-TEMPLATE-001: MD模板的增删改查（模板名称、标识、内容、创建时间、状态）
- F-TEMPLATE-002: 模板选择与自动填充（新建MD文件时选择模板并填充内容）
- F-TEMPLATE-003: 模板高级功能（分类管理、模糊搜索、导入导出、默认模板）

## 方案

### 1. 数据模型设计

#### 方案 A: 新建 md_templates 表

**优点**:
- 独立存储，职责清晰，易于维护
- 可灵活扩展字段（分类、描述、优先级等）
- 与现有业务表（files, directories）隔离，查询效率高
- 便于后续添加模板版本管理

**缺点**:
- 需要新增数据库表和对应的Java Model
- 需要在 DatabaseInitializer 中添加建表语句

#### 方案 B: 扩展 files 表，添加模板标识字段

**优点**:
- 无需新建表，简化数据库设计
- 复用现有的文件存储逻辑

**缺点**:
- 业务逻辑混杂，files表职责不单一
- 模板的特定字段（category, is_default等）不适合放在files表
- 难以实现模板的独立管理（导入导出等）

**选择**: 方案A

**原因**:
- 项目已有清晰的Model分层，模板作为独立业务实体应独立建模
- 参考现有设计（directories, files表），模板表设计符合项目规范
- 便于后续扩展（模板分类、版本管理）

---

### 2. 数据库表结构设计

```sql
-- MD模板表
CREATE TABLE IF NOT EXISTS md_templates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,                    -- 模板名称（展示用）
    template_key TEXT NOT NULL UNIQUE,     -- 模板标识（程序用，英文小写+下划线）
    content TEXT NOT NULL,                  -- 模板内容（Markdown）
    category TEXT DEFAULT '通用',           -- 分类（会议、周报、需求等）
    description TEXT,                      -- 模板描述
    is_default INTEGER DEFAULT 0,           -- 是否系统默认模板（0-否，1-是）
    user_id INTEGER NOT NULL,               -- 创建用户ID（支持多用户）
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,               -- 软删除标记
    delete_at TIMESTAMP                     -- 删除时间
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_md_templates_user_id ON md_templates(user_id);
CREATE INDEX IF NOT EXISTS idx_md_templates_category ON md_templates(category);
CREATE INDEX IF NOT EXISTS idx_md_templates_deleted ON md_templates(deleted);
CREATE UNIQUE INDEX IF NOT EXISTS idx_md_templates_key ON md_templates(template_key);
```

**字段说明**:
- `template_key`: 唯一标识，使用英文小写+下划线格式（如 `meeting_minutes`, `weekly_report`）
- `is_default`: 支持系统默认模板，普通用户只能查看不能修改
- `category`: 预定义分类（通用、会议、周报、需求）+ 用户自定义
- `deleted`: 软删除标记，遵循项目规范

---

### 3. API 设计

#### 方案 A: 独立 /api/template 路径

**优点**:
- 路径语义清晰，与业务模块解耦
- 便于后续扩展模板相关功能（模板市场、模板推荐等）

**缺点**:
- 需新增路由配置

#### 方案 B: 复用 /api/file 路径，添加 type=template 参数

**优点**:
- 减少路由配置

**缺点**:
- 路径语义混乱，与文件操作混淆
- 不符合 RESTful 规范

**选择**: 方案A

**API 路径设计**:

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/template/list | 获取模板列表（分页+筛选） |
| GET | /api/template/{id} | 获取模板详情 |
| POST | /api/template/create | 创建模板 |
| PUT | /api/template/update | 更新模板 |
| DELETE | /api/template/{id} | 删除模板 |
| POST | /api/template/{id}/apply | 应用模板（返回模板内容） |
| POST | /api/template/import | 导入模板（JSON） |
| GET | /api/template/export/{id} | 导出模板（JSON） |
| PUT | /api/template/{id}/set-default | 设置默认模板 |

**响应格式**:

```json
// 成功
{
  "code": 0,
  "message": "success",
  "data": {}
}

// 错误
{
  "code": 1,
  "message": "错误描述"
}
```

---

### 4. 架构分层设计

| 层级 | 类名 | 职责 |
|------|------|------|
| Controller | TemplateController | 参数校验、响应封装、路由映射 |
| Service | TemplateService | 业务逻辑、事务管理、模板应用逻辑 |
| Repository | TemplateRepository | 数据访问、软删除过滤 |
| Model | MdTemplate | 模板实体 |

**分层约束遵循**:
- Controller 只做参数校验和响应封装，禁止执行业务逻辑
- Service 处理所有业务逻辑，使用 @Transactional
- Repository 继承 BaseMapper，使用 QueryWrapper 过滤 deleted=0
- Model 仅定义字段，无业务逻辑

---

### 5. 前端设计

#### 方案 A: 独立模板管理页面 + 编辑器集成

**优点**:
- 模板管理功能完整（增删改查、分类、导入导出）
- 用户体验好

**缺点**:
- 需要新增页面路由和组件

#### 方案 B: 仅在编辑器中提供模板选择

**优点**:
- 实现简单

**缺点**:
- 无法管理模板（增删改查、分类等）
- 不满足 F-TEMPLATE-001 和 F-TEMPLATE-003

**选择**: 方案A

**前端文件**:

| 文件 | 说明 |
|------|------|
| src/views/TemplateManager.vue | 模板管理页面 |
| src/api/template.js | 模板API封装 |
| src/router/index.js | 添加模板路由 |

**路由设计**:
```javascript
{
    path: 'templates',
    name: 'Templates',
    component: () => import('../views/TemplateManager.vue')
}
```

---

### 6. 安全设计

| 安全项 | 实现 |
|--------|------|
| SQL注入防护 | 使用 MyBatis-Plus #{} 占位符，禁止 ${} |
| XSS防护 | 模板内容存储为纯文本，前端渲染时使用 v-html 需谨慎 |
| 输入校验 | Controller 层使用 @Valid 注解 |
| 敏感信息 | 无敏感信息，无需脱敏 |
| 默认模板保护 | is_default=1 的模板，普通用户只能读取 |

**输入校验规则**:
- 模板名称: 非空，最大50字符
- 模板标识: 非空，英文小写+下划线，最大30字符，唯一性校验
- 模板内容: 非空，最大100KB
- 分类: 最大20字符

---

## 选择

**选择**: 方案A（全部）

**原因**:
1. 数据模型：独立表设计符合项目分层规范，便于扩展
2. API设计：独立 /api/template 路径，语义清晰，符合 RESTful
3. 架构分层：严格遵循 Controller → Service → Repository → Model
4. 前端设计：独立页面满足完整功能需求
5. 安全设计：遵循 JAVA-MUST-0001（SQL注入防护）、JAVA-MUST-0003（事务注解）

## 影响

### 对后续实现的影响

1. **DatabaseInitializer**: 需添加 md_templates 建表语句
2. **Model层**: 新增 MdTemplate.java
3. **Repository层**: 新增 TemplateRepository.java
4. **Service层**: 新增 TemplateService.java
5. **Controller层**: 新增 TemplateController.java
6. **前端**: 新增 TemplateManager.vue、template.js，更新 router/index.js
7. **侧边栏**: 在主界面左侧导航添加「MD模板管理」菜单项

### Open Questions 解决

| 问题 | 解决方案 |
|------|----------|
| 模板分类是否可配置 | 使用预定义分类（通用、会议、周报、需求）+ 用户自定义输入 |
| 模板标识规范 | 英文小写+下划线格式，最大30字符，唯一性约束 |

---

## 验证

本ADR通过以下规则验证：

| 规则ID | 验证结果 |
|--------|----------|
| JAVA-MUST-0001 | 合规 - 使用 #{} 占位符 |
| JAVA-MUST-0003 | 合规 - Service层使用 @Transactional |
| 分层约束 | 合规 - Controller→Service→Repository→Model |
| RESTful API | 合规 - 标准HTTP方法+资源路径 |
| 响应格式 | 合规 - {code, message, data} |
| 软删除 | 合规 - 使用 deleted 字段 |

---

**ADR创建时间**: 2026/04/21
**状态**: 已完成
