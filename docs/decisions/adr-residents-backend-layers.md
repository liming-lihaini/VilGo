# ADR: 村民档案后端分层设计

## 背景

项目采用 Controller → Service → Dao → Entity 单向分层架构（见 CLAUDE.md）。Issue #1 村民档案管理需遵循此架构规范进行后端代码组织。

## 备选方案

### 方案 A: 标准四层（Controller/Service/Dao/Entity）+ DTO

- 优势: 严格遵循 CLAUDE.md 分层约束；Controller 处理请求/响应，Service 处理业务逻辑，Dao 处理数据持久化，Entity 映射数据库表；DTO 用于前后端数据交互，解耦 Entity 与前端；结构清晰，便于维护和扩展
- 劣势: 文件数量相对较多，MVP 阶段略微增加工作量

### 方案 B: 三层（Controller/Service/Mapper）+ Entity

- 优势: 减少一层文件，结构更紧凑
- 劣势: 违反 CLAUDE.md 明确定义的「Controller → Service → Dao → Entity」分层规范；缺少 DTO 层会导致 Entity 直接暴露给前端，违反「禁止 Entity 直接作为前端出参」的约束

## 决策

选择方案 A（标准四层 + DTO），因为严格遵循 CLAUDE.md 架构约束；DTO 层实现数据脱敏和字段裁剪，符合「Entity 禁止直接作为前端出参」的规范；四层分离各司其职，符合「单向依赖、上层依赖下层」原则。

## 否决项

- 方案 B: 违反分层约束，Entity 直接暴露给前端存在安全风险（敏感字段如密码等可能泄露）

## 影响

- 新建 `src/main/java/com/village/controller/ResidentController.java`
- 新建 `src/main/java/com/village/service/ResidentService.java` 接口
- 新建 `src/main/java/com/village/service/impl/ResidentServiceImpl.java`
- 新建 `src/main/java/com/village/dao/ResidentDao.java`
- 新建 `src/main/java/com/village/entity/Resident.java`
- 新建 `src/main/java/com/village/dto/ResidentDTO.java`（入参）
- 新建 `src/main/java/com/village/dto/ResidentQueryDTO.java`（查询入参）
- 新建 `src/main/java/com/village/dto/ResidentStatisticsDTO.java`（统计出参）