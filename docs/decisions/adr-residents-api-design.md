# ADR: 村民档案API接口设计

## 背景

Issue #1 村民档案管理需要设计 8 个 RESTful API 接口（新增/编辑/删除/查询/销户/统计/导出），遵循 CLAUDE.md 约束「接口统一前缀：/api/业务模块/操作标识」。

## 备选方案

### 方案 A: RESTful 风格（资源导向）

- 优势: 符合 RESTful 设计规范，语义清晰；资源命名统一（resident 作为资源名）；支持 HTTP 方法语义（POST 新增、PUT 更新、DELETE 删除、GET 查询）
- 劣势: 部分复杂操作（如统计、导出）难以完全映射到标准 RESTful 动词

### 方案 B: 混合风格（RESTful + 操作导向）

- 优势: 灵活处理统计、导出等非 CRUD 操作；前端调用直观
- 劣势: 不符合纯 RESTful 风格，统一性稍差

## 决策

选择方案 A（RESTful 风格为主），因为遵循 CLAUDE.md 接口规范前缀；资源导向设计更符合 RESTful 最佳实践；统计/导出使用 POST 方法（因为涉及请求体），保持一致性。

## 否决项

- 方案 B: 混合风格导致接口风格不统一，增加维护成本

## 影响

API 路由设计：
- `POST /api/resident/create` - 新增村民档案
- `PUT /api/resident/update` - 更新村民档案
- `DELETE /api/resident/delete/{id}` - 删除档案（软删除）
- `GET /api/resident/get/{id}` - 获取单条档案
- `POST /api/resident/list` - 条件查询列表（分页）
- `PUT /api/resident/cancel/{id}` - 销户登记
- `POST /api/resident/statistics` - 人口统计
- `POST /api/resident/export` - Excel导出（文件下载）

所有接口统一前缀 `/api/resident/`，符合 CLAUDE.md 规范。