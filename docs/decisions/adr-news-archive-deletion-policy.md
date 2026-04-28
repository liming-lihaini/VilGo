# ADR: 新闻归档后删除策略

## 背景
Issue #8 要求实现新闻归档功能（F-NEW-004）和删除功能（F-NEW-003），Boundaries 明确"归档后不可编辑"约束。需要决策归档后的新闻是否允许删除，以及删除的业务规则。

## 备选方案

### 方案 A: 归档后允许软删除
- 优势: 保留灵活性，允许误操作归档后删除；软删除机制保留历史数据，可恢复；与项目现有软删除模式一致（参考 Resident、PublicActivity 的 deleted 字段）；满足 Boundaries "软删除保留历史"要求
- 劣势: 可能导致误删重要归档新闻；需要额外的权限控制或二次确认

### 方案 B: 归档后禁止删除
- 优势: 保护归档新闻完整性，避免误删；符合档案管理最佳实践（归档即封存）
- 劣势: 过于严格，无法处理违规新闻等特殊场景；与 Boundaries "软删除保留历史"冲突（禁止删除导致无法清理垃圾数据）

### 方案 C: 归档后仅管理员可删除
- 优势: 平衡灵活性和安全性；引入权限控制，符合实际操作场景
- 劣势: MVP 阶段项目未实现权限管理模块（参考 CLAUDE.md，当前项目无认证授权设计），增加实现复杂度；与 Boundaries 范围之外冲突（权限管理不在当前 Issue 范围）

## 决策
选择方案 A（归档后允许软删除），因为软删除保留历史数据，可恢复，满足 Boundaries "软删除保留历史"要求；与项目现有模式一致（所有实体均支持软删除，如 Resident、PublicActivity）；MVP 阶段无需引入权限管理，降低复杂度；删除操作需二次确认（前端 ElMessageBox.confirm），减少误删风险。

## 否决项
- 方案 C: 权限管理模块不在 Boundaries 范围之内，MVP 阶段不实现；增加实现复杂度，违反 MVP 原则（Minimum Viable Product）
- 方案 B: 与 Boundaries "软删除保留历史"冲突，无法清理垃圾数据；过于严格，无法处理特殊场景（如违规新闻）

## 影响
- `NewsService.delete(Long id)` 方法校验新闻状态（允许删除已归档新闻）
- `NewsService.update(NewsDTO dto)` 方法校验新闻状态（禁止编辑已归档新闻，抛出 BusinessException）
- 前端 `NewsList.vue` 删除按钮对归档新闻显示警告提示（"确认删除该归档新闻吗？删除后不可恢复"）
- 前端 `NewsList.vue` 编辑按钮对归档新闻置灰或隐藏（参考 PublicActivityList.vue 的状态按钮控制逻辑）

## 业务规则
1. **归档后编辑**: 禁止编辑已归档新闻（Boundaries 约束），`NewsService.update()` 校验 `status === 'archived'` 时抛出异常
2. **归档后删除**: 允许软删除已归档新闻，前端二次确认提示风险
3. **软删除机制**: 删除操作仅设置 `deleted = 1`，不物理删除数据（满足 Boundaries "软删除保留历史"）
4. **查询过滤**: `NewsService.list()` 默认过滤 `deleted = 1` 的记录（MyBatis-Plus @TableLogic 自动处理）

## 异常处理
```java
// NewsServiceImpl.java
public void update(NewsDTO dto) {
    News news = getById(dto.getId());
    if (news == null) {
        throw new BusinessException("新闻不存在");
    }
    if ("archived".equals(news.getStatus())) {
        throw new BusinessException("已归档新闻不可编辑");
    }
    // ... 更新逻辑
}

public void delete(Long id) {
    News news = getById(id);
    if (news == null) {
        throw new BusinessException("新闻不存在");
    }
    // 软删除，允许删除已归档新闻
    baseMapper.deleteById(id);
}
```
