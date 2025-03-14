在作者（SplitfireUptown）原项目<a href=https://github.com/SplitfireUptown/datalinkx>DatalinkX</a>基础上进行了扩展

## 扩展点

### 后端：

### 1.添加用户管理模块`datalinkx-security`

基于RuoYi，通过引入SpringSecurity和JWT实现了用户管理模块，包括用户管理、角色管理、权限管理、菜单管理等功能

- 用户管理
- 角色管理
- 权限管理
- 菜单管理

### 2.添加DeepSeek大模型模块`datalinkx-deepseek`

接入DeepSeekAPI，通过SSE（Server-Send Events）实现了大模型流式输出，多伦对话等功能

- DeepSeek大模型模块

### 前端：

### 1.添加用户管理

![输入图片说明](datalinkx-server/src/main/resources/readme/security.png)

### 2.添加DeepSeek大模型（支持文件上传、模型切换`DeepSeek-R1`、`DeepSeek-V3`））

![输入图片说明](datalinkx-server/src/main/resources/readme/deepseek.png)

### 3.支持直接运行Python脚本、HTML代码

![输入图片说明](datalinkx-server/src/main/resources/readme/Python.png)

### 4.添加可视化BI,支持`上传数据`、`从数据库读取`、`自定义可视化`、`动态编辑`

![输入图片说明](datalinkx-server/src/main/resources/readme/BI.png)

![输入图片说明](datalinkx-server/src/main/resources/readme/BI2.png)

![输入图片说明](datalinkx-server/src/main/resources/readme/project_name.png)

<p align="center">
<a href="https://github.com/SplitfireUptown/datalinkx"><img src="https://img.shields.io/github/stars/SplitfireUptown/datalinkx.svg?style=flat&label=GithubStars"></a>
<a href="https://gitee.com/atuptown/datalinkx"><img src="https://gitee.com/atuptown/datalinkx/badge/star.svg?theme=dark" alt="Gitee Starts"></a>
  <a href="https://gitee.com/atuptown/datalinkx"><img src="https://gitee.com/atuptown/datalinkx/badge/fork.svg?theme=dark" alt="Gitee Starts"></a>
<a href="#"><img src="https://img.shields.io/badge/Author-在下uptown-orange.svg" alt="作者"></a>
<a href="#项目文档"><img src="https://img.shields.io/badge/JDK-8-red.svg" alt="jdk版本"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/SpringBoot-2.4.3-green.svg" alt="SpringBoot版本"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/MySQL-8.0-orange.svg" alt="MySQL版本"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/Redis-5.0-green.svg" alt="Redis版本"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/消息队列-Redis Stream-red.svg" alt="Redis版本"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/ORM-SpringData JPA-blue.svg" alt="ORM框架"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/分布式定时任务-xxljob-green.svg" alt="分布式定时任务"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/分布式计算引擎-Flink-red.svg" alt="计算引擎"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/分布式计算引擎-Seatunnel-blue.svg" alt="计算引擎"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/系统部署-Docker & DockerCompose-yellow.svg" alt="部署"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/前端-Vue2.x-green.svg" alt="部署"></a>
  <a href="#项目文档"><img src="https://img.shields.io/badge/前端UI-AntDesignUI-red.svg" alt="前端"></a>
<a href="#项目文档"><img src="https://img.shields.io/badge/RPC-Retrofit2-blue.svg" alt="RPC框架"></a>
<a href="#项目文档"><img src="https://img.shields.io/badge/同步框架-Chunjun(FlinkX)-green.svg" alt="同步框架"></a>
<a href="#项目文档"><img src="https://img.shields.io/badge/向量库-ElasticSearch 7.9.3-blue.svg" alt="向量库"></a>
<a href="#项目文档"><img src="https://img.shields.io/badge/大模型框架-ollama-orange.svg" alt="大模型框架"></a>
</p>

## 异构数据源同步服务DatalinkX介绍

**核心功能** ：在不同的异构数据源中进行数据同步，对同步任务进行管理和维护

**意义**：只要公司规模较大，部门与部门之间有数据协作都应该有类似DatalinkX的项目，比如爬虫组的同事爬下来数据要定时同步到数仓组负责的库下。同步服务会集中管理同步任务，收拢同步日志、提高内部工作效率。

![输入图片说明](datalinkx-server/src/main/resources/readme/image.png)

## 项目特性

- **简单易用**：通过Web页面快速创建数据源、同步任务，操作简单，一分钟上手
- **定时触发**：对接xxl-job定时，设置cron表达式触发同步任务
- **配置化任务对接**：将数据库信息、任务详情界面化配置
- **高性能同步**：使用高性能流式flink计算引擎
- **容器化部署**：支持docker部署
