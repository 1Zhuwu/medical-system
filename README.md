# 医疗系统

基于 **Spring Boot 2.7 + MyBatis-Plus + MySQL 8** 的医院门诊管理系统。

## 技术栈

- JDK 17（源码级别兼容 JDK 8）
- Spring Boot 2.7.15
- MyBatis-Plus 3.5.3.1（分页插件、逻辑删除、自动填充）
- MySQL 8.0 + HikariCP
- Lombok
- 前端一个 `static/index.html`，纯 HTML，无样式无脚本

## 目录结构

```
spr_demo1/
├── sql/medical_db.sql              # 建库建表脚本（含演示数据）
├── src/main/java/org/example/spr_demo1/
│   ├── common/                     # Result 统一响应、业务异常、全局异常处理
│   ├── config/                     # 分页插件、防全表更新、自动填充、Jackson
│   ├── entity/                     # 7 个实体
│   ├── mapper/                     # MyBatis-Plus Mapper
│   ├── service/                    # 接口 + impl 实现
│   ├── controller/                 # 科室 / 医生 / 患者 REST 接口
│   └── generator/CodeGenerator.java # 代码生成器
└── src/main/resources/
    ├── application.properties
    ├── application-local.properties  # 本机密码，不入库
    └── static/index.html             # 首页，列出接口地址
docs/实验一实验二实施指南.md          # 实施与验收对照文档
```

## 快速开始

### 1. 建库

```bash
mysql -uroot -p < spr_demo1/sql/medical_db.sql
```

会创建 `medical_db` 库、7 张表，并写入演示数据（5 科室 / 4 医生 / 2 患者 / 3 药品）。

### 2. 配置数据库密码

新建 `spr_demo1/src/main/resources/application-local.properties`：

```properties
spring.datasource.password=你的密码
```

该文件已在 `.gitignore` 中，不会被提交。也可以不改文件，直接设置环境变量 `DB_PASSWORD`。

### 3. 启动

```bash
cd spr_demo1
mvn spring-boot:run
```

默认端口 `8080`。浏览器打开 <http://localhost:8080/> 是一个接口索引页，能直接点开各接口看返回；<http://localhost:8080/hello> 返回：

```
医疗系统 Hello World！实验一接口访问成功
```

## 页面

`src/main/resources/static/index.html` 一个文件，纯 HTML，没有样式表和脚本。里面列了 `GET` 类接口的链接，点一下即可在浏览器里看到 JSON 返回。

页面上没有增删改的操作入口，这几个接口用 Postman 或 curl 调用，方法与参数见下面的接口清单。

## 接口清单

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/hello` | HelloWorld 测试接口 |
| GET | `/api/departments` | 科室分页查询（`pageNum` `pageSize` `keyword` `status`） |
| GET | `/api/departments/{id}` | 科室详情 |
| POST | `/api/departments` | 新增科室 |
| PUT | `/api/departments/{id}` | 修改科室 |
| DELETE | `/api/departments/{id}` | 删除科室（逻辑删除，有医生时拒绝） |
| GET | `/api/doctors` | 医生分页查询（`keyword` `deptId` `title`），返回带 `deptName` |
| POST/PUT/DELETE | `/api/doctors`、`/api/doctors/{id}` | 医生增改删 |
| GET | `/api/patients` | 患者分页查询，手机号/身份证脱敏 |
| GET | `/api/patients/{id}` | 患者详情（返回完整值，供编辑回填） |
| POST/PUT/DELETE | `/api/patients`、`/api/patients/{id}` | 患者增改删 |

统一响应格式：

```json
{ "code": 200, "message": "操作成功", "data": { } }
```

## 数据库设计

| 表 | 说明 |
|---|---|
| `department` | 科室 |
| `doctor` | 医生（`dept_id` → department） |
| `patient` | 患者 |
| `registration` | 挂号记录（→ patient / doctor） |
| `prescription` | 电子处方（→ registration / doctor） |
| `drug` | 药品 |
| `prescription_item` | 处方明细（→ prescription / drug） |

设计满足 3NF，含主外键约束、唯一键、非空约束、查询索引与中文注释；`drug` 与 `prescription_item` 为后续库存扣减与处方明细功能预留。

## 说明

- 数据库密码等敏感信息通过 `application-local.properties`（已忽略）或环境变量注入，仓库内不含真实凭据。
- `target/`、`.idea/`、`*.iml` 已忽略，克隆后需用 IDEA 重新导入 Maven 项目。
