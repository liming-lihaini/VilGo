# ADR: 新闻分类管理方案

## 背景
Issue #8 要求实现新闻分类管理功能（F-NEW-006），设置新闻分类标签（预置：政策解读/活动报道/先进事迹/通知公告）。需要决定分类数据的存储和配置方式。

## 备选方案

### 方案 A: 数据库配置表 news_categories
- 优势: 支持动态增删改分类，灵活性强；便于后续扩展（如分类排序、分类图标）；符合数据库规范化设计；项目已有类似设计经验（positions 职位管理表）
- 劣势: 增加数据库表数量，MVP 阶段可能过度设计；需要额外的管理界面和权限控制

### 方案 B: Java 枚举预置
- 优势: 实现简单，无需数据库表；代码中类型安全，编译期检查；减少查询开销，性能最优；项目已有类似设计（HouseholdStatus、PersonType 等枚举）
- 劣势: 修改分类需要重新部署；不支持动态增删改

### 方案 C: 配置文件（application.yml）
- 优势: 无需数据库表，配置集中；修改无需重新编译（仅需重启）
- 劣势: 不支持运行时动态修改；与项目现有模式不一致（现有分类均使用枚举，如 Resident.personType、SpecialGroup.groupType）；配置文件修改需重启服务，不适合村级工作人员操作

## 决策
选择方案 B（Java 枚举预置），因为 MVP 阶段分类需求明确且固定（4种预置分类），无需动态管理；与项目现有设计模式一致（参考 Resident.personType、SpecialGroup.groupType 均使用枚举）；枚举类型安全，减少运行时错误；村级工作人员无需关心分类管理，降低操作复杂度。

## 否决项
- 方案 C: 配置文件修改需重启服务，不适合村级工作人员操作；与项目现有模式不一致（现有分类均使用枚举）
- 方案 A: MVP 阶段过度设计，增加开发和维护成本；预置分类已满足需求（Boundaries 范围之内）

## 影响
- 新建 `src/main/java/com/village/enums/NewsCategory.java` 枚举类（包含 POLICY_INTERPRETATION、ACTIVITY_REPORT、ADVANCED_DEEDS、NOTICE_ANNOTEMENT 四个枚举值）
- `News` 实体类 `category` 字段类型为 `String`（存储枚举名称）
- `NewsService` 校验分类有效性（调用 `NewsCategory.isValid()`）
- 前端 `el-select` 下拉选项硬编码四个预置分类（参考 PublicActivityList.vue 的 activityType 下拉）

## 枚举定义
```java
public enum NewsCategory {
    POLICY_INTERPRETATION("政策解读"),
    ACTIVITY_REPORT("活动报道"),
    ADVANCED_DEEDS("先进事迹"),
    NOTICE_ANNOTEMENT("通知公告");

    private final String displayName;

    NewsCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static boolean isValid(String categoryName) {
        for (NewsCategory category : values()) {
            if (category.name().equals(categoryName)) {
                return true;
            }
        }
        return false;
    }
}
```

## 后续扩展路径
如果未来需要支持动态分类管理，可迁移至方案 A（数据库配置表），届时需要：
1. 新建 `news_categories` 表
2. 提供数据迁移脚本（将枚举值写入表）
3. 新增分类管理界面（仅管理员可操作）
4. 修改 `NewsService` 校验逻辑（从数据库读取有效分类）
