# VilGo 村务管理系统

## 项目简介

VilGo 面向新农村村级管理场景，为村级工作人员（两委成员、网格员）和村民提供的单机版数字化管理工具，核心解决村级档案管理混乱、公益活动管理繁琐、信息传递不及时、数据可视化不足等痛点，实现"档案数字化、管理高效化、服务便民化、信息公开化、数据可视化"，操作简便、轻量化部署，适配中老年村级工作人员操作水平。

## 技术架构

本软件采用"前后端一体化（单工程）+ 本地数据库"架构：

- 后端：JDK 17 + Spring Boot 2.7 + MyBatis-Plus + SQLite 3
- 前端：Vue 3 + Vite 5 + Element Plus
- 构建：Maven

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+ (前端开发)

## 快速启动

### 后端启动

```bash
mvn spring-boot:run
```

服务启动后访问 http://localhost:8080

### 前端开发

```bash
cd src/main/webapp
npm install
npm run dev
```

## 目录结构

```
village-affairs-management/
├── src/main/java/com/village/     # 后端代码
│   ├── config/                    # 配置类
│   ├── controller/                # 控制器
│   ├── service/                   # 服务层
│   │   └── impl/                  # 服务实现
│   ├── dao/                       # 数据访问层
│   ├── entity/                    # 实体类
│   ├── dto/                       # 数据传输对象
│   ├── exception/                 # 异常处理
│   └── util/                      # 工具类
├── src/main/resources/            # 资源配置
│   ├── application.yml            # 全局配置
│   ├── static/                    # 前端静态资源
│   └── templates/                 # 模板目录
├── src/main/webapp/               # 前端代码
│   └── src/
│       ├── views/                 # 页面视图
│       │   ├── resident/          # 村民人事档案
│       │   ├── household/         # 户籍档案
│       │   ├── specialGroup/      # 特殊人群档案
│       │   ├── twoCommittee/      # 两委班子管理
│       │   ├── publicActivity/    # 公益活动管理
│       │   ├── partyWork/         # 党务管理
│       │   ├── notice/            # 村委公示栏
│       │   ├── news/              # 新闻动态
│       │   └── screen/            # 数字大屏
│       ├── components/            # 公共组件
│       ├── router/                # 路由配置
│       ├── store/                 # 状态管理
│       ├── request/               # 请求封装
│       └── utils/                 # 工具类
└── pom.xml                        # Maven配置
```

## 功能模块

- 村民人事档案
- 户籍档案
- 特殊人群档案
- 两委班子管理
- 公益活动管理
- 党务管理
- 村委公示栏
- 新闻动态
- 数字大屏

## 核心约束

- 单机版部署，无需外网依赖
- 本地 SQLite 数据库存储
- 无数据加密、备份组件
- 前后端一体化打包部署