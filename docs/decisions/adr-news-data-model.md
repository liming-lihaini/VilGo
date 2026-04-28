# ADR: 新闻数据模型设计

## 背景
Issue #8 要求实现新闻动态管理功能，包括新闻发布、编辑、删除、归档和查询（F-NEW-001~F-NEW-006）。需要设计新闻数据存储模型，支持标题、内容（支持图片）、分类标签、发布时间、归档状态等核心属性。

## 备选方案

### 方案 A: 新建独立 news 表
- 优势: 符合单一职责原则，新闻作为独立业务实体，不与现有模块耦合；字段设计灵活，可根据需求定制（如支持富文本内容、图片路径存储）；便于后续扩展（如新闻评论、点赞等功能）；项目已有成熟的 Entity/Service/Controller 分层架构（参考 PublicActivity），可直接复用设计模式
- 劣势: 需要新建表结构，增加数据库维护成本；与 notices 公示栏表功能相似，可能存在数据重复

### 方案 B: 复用 notices 公示栏表
- 优势: 无需新建表，减少数据库对象；新闻与公示栏本质都是信息发布，业务相似度高
- 劣势: notices 表结构是针对公示栏设计的（参考 schema.sql），字段可能不完全适配新闻需求（如新闻需要支持更长的富文本内容、分类体系）；与 Boundaries 冲突：Issue #7 已定义 notices 为"村委公示栏"，Issue #8 定义 news 为"新闻动态"，两者业务边界清晰，强制复用会导致职责不清；违反架构约束中的单一职责原则，增加后续维护复杂度

### 方案 C: 新闻与公示栏共享基表，通过 type 字段区分
- 优势: 统一信息发布管理，减少代码重复
- 劣势: MVP 阶段过早优化，增加查询复杂度（需额外过滤 type）；违反数据库设计第三范式，导致字段语义模糊（如 content 字段在新闻和公示栏中格式要求不同）；与项目现有架构不一致（PublicActivity、TwoCommittee 等均为独立表）

## 决策
选择方案 A（新建独立 news 表），因为新闻与公示栏是不同业务域（Boundaries 明确定义），独立表设计符合项目分层架构约束（Controller → Service → Dao → Entity），便于后续扩展和字段定制；项目已有成熟的软删除、时间戳自动填充机制（参考 Resident.java @TableLogic、@TableField），可直接复用。

## 否决项
- 方案 C: MVP 阶段过早优化，增加调试复杂度（ADR-003 设计原则：Reuse First，但不是 Over-Abstract）；与项目现有表设计模式不一致（Residents、PublicActivities 等均为独立表）
- 方案 B: 与 Boundaries 冲突，Issue #7 和 Issue #8 明确定义为不同功能模块；notices 表结构未公开，假设其字段适配新闻需求存在风险

## 影响
- 新建 `src/main/resources/schema.sql` 添加 news 表建表语句（包含 id、title、content、category、publish_time、status、creator、create_time、update_time、deleted 字段）
- 新建 `src/main/java/com/village/entity/News.java` 实体类（参考 PublicActivity.java 结构）
- 新建 `src/main/java/com/village/dto/NewsDTO.java`、`NewsQueryDTO.java` 数据传输对象（参考 PublicActivityDTO.java）
- 新建 `src/main/java/com/village/dao/NewsDao.java` 数据访问接口（继承 BaseMapper）
- 新建 `src/main/java/com/village/service/NewsService.java` 及 `impl/NewsServiceImpl.java` 业务逻辑（参考 PublicActivityService）
- 新建 `src/main/java/com/village/controller/NewsController.java` 控制器（RESTful 风格：/api/news/create、/api/news/update、/api/news/delete/{id}、/api/news/list）
- 前端新建 `src/main/webapp/src/request/news.js` API 封装，`src/main/webapp/src/views/news/NewsList.vue` 管理页面

## 数据库表结构设计
```sql
CREATE TABLE IF NOT EXISTS news (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL UNIQUE,
    content TEXT NOT NULL,
    category VARCHAR(50),
    publish_time VARCHAR(50),
    status VARCHAR(20) DEFAULT 'published',
    creator VARCHAR(50),
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_news_title ON news(title);
CREATE INDEX IF NOT EXISTS idx_news_category ON news(category);
CREATE INDEX IF NOT EXISTS idx_news_publish_time ON news(publish_time);
CREATE INDEX IF NOT EXISTS idx_news_status ON news(status);
CREATE INDEX IF NOT EXISTS idx_news_deleted ON news(deleted);
```

**字段说明**:
- `title`: 新闻标题，全局唯一（满足 Boundaries "标题全局唯一性校验"）
- `content`: 新闻内容，支持富文本（图片以 Base64 或相对路径存储）
- `category`: 新闻分类（预置：政策解读/活动报道/先进事迹/通知公告）
- `publish_time`: 发布时间
- `status`: 新闻状态（published-已发布、archived-已归档）
- `deleted`: 软删除标记（满足 Boundaries "软删除保留历史"）
