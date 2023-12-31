# Iterative Goals

本文件描述了该项目的每轮迭代目标。

## First Round

**技术选型：**

- 前端
  - 语言：[TypeScript](https://www.typescriptlang.org/)
  - 主框架：[React](https://reactjs.org/)
  - 组件库：[Material-UI](https://next.material-ui.com/)
  - App(TBD)：[Flutter](https://flutter.dev/)
  - 推荐IDE：[WebStorm](https://www.jetbrains.com/webstorm/)
- 后端
  - 语言：[Kotlin](https://kotlinlang.org/)
  - 主框架：[Spring Framework](https://spring.io/projects/spring-framework)
    - Spring Boot：[Spring Boot](https://spring.io/projects/spring-boot)
    - Web服务器：[Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)
    - 持久化：[Spring Data JPA](https://spring.io/projects/spring-data-jpa)
    - 用户会话管理：[Spring Session](https://spring.io/projects/spring-session)
    - 安全与鉴权：[Spring Security](https://spring.io/projects/spring-security)
  - 数据库：[PostgreSQL](https://www.postgresql.org/)
  - 推荐IDE：[Intellij IDEA Ultimate](https://www.jetbrains.com/idea/)

**迭代主要目标：**

- 熟悉框架的使用 (前端：React；后端：Spring)。
- 实现可运行的demo。
- 实现基本项目功能。
- 完成在secoder上的部署。

**迭代细节目标：**

- 前端
  - 完成用户相关的页面逻辑（刘松铭）
  - 完成提问者特有的部分页面逻辑（徐晨曦）
    - 主页
    - 查看、搜索回答者列表
    - 提问
    - 购买的订单列表 (待接收/进行中/已完成/已退款)
    - 查看订单信息（已提问的问题）
- 后端
  - 用户相关的部分接口（肖震宇）
    - 登陆
    - 注册
    - 登出
    - 删除用户
    - 获取详细信息
    - 修改详细信息
    - 修改用户密码
    - 查询用户列表
  - 订单相关的部分接口（孙天乐）
    - 创建订单
    - 删除订单
    - 获取详细信息
    - 修改详细信息
    - 查询订单列表
  - 管理员相关的部分接口（刘程华）
    - 登陆
    - 登出
    - 创建
    - 删除
    - 修改管理员密码
    - 获取管理员权限
    - 修改管理员权限
    - 查询管理员列表
  - 完成在secoder上的部署（孙天乐）
- 制定下一轮迭代的目标和本轮展示ppt（刘松铭）

**第一轮迭代预期成果：**

完成一个基本demo，该demo用户可以正常注册和登录。用户能做的事情只有创建订单和查看自己创建的订单。不能接受订单，不能申请成为回答者。