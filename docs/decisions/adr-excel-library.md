# ADR: Excel导出库选型

## 背景

Issue #1 村民档案管理要求支持 Excel 报表导出（F-RES-007）。项目采用 JDK 17 + Spring Boot 2.7 + SQLite 技术栈，当前 pom.xml 无 Excel 库依赖。需要选择合适的 Excel 操作库。

## 备选方案

### 方案 A: Apache POI

- 优势: 历史悠久，社区成熟，文档丰富；功能全面，支持 .xls/.xlsx 格式；与项目 Spring Boot 生态兼容良好
- 劣势: API 相对繁琐，代码编写量大；内存占用较高，大数据量导出可能 OOM；更新不活跃（最新版本 5.2.5，2023年）

### 方案 B: EasyExcel（阿里开源）

- 优势: API 简洁，开发效率高；针对大数据量做了优化（流式写入，避免 OOM）；国内社区活跃，文档中文；与 POI 相比写入性能提升显著
- 劣势: 依赖 POI 本身，引入传递依赖；功能上更偏重导出，导入功能更强大但部分功能对导出场景冗余

### 方案 C: Java Excel (JXL)

- 优势: 轻量级，依赖少
-劣势: 仅支持 .xls 格式（已被淘汰）；已停止维护多年

## 决策

选择方案 B（EasyExcel），因为阿里开源项目在国内社区活跃，中文文档丰富；API 简洁，MVP 阶段开发效率优先；流式写入避免大数据量 OOM，更贴合村级档案数据导出场景（虽然数据量不大，但技术前瞻性更好）。

## 否决项

- 方案 A: API 繁琐，增加开发时间；内存占用高
- 方案 C: 仅支持 .xls 格式，已被淘汰

## 影响

- 在 `pom.xml` 中添加 EasyExcel 依赖（com.alibaba:easyexcel）
- 新建 `src/main/java/com/village/util/ExcelUtil.java` 导出工具类
- API 响应需支持文件下载（MediaType.APPLICATION_OCTET_STREAM）