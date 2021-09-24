# 2021秋软件工程项目：付费问答系统

本文档是付费问答系统项目的介绍文档。

## Git操作相关

1. 分支：
   1. 除非特殊操作，请**不要直接操作master分支**。
   2. 绝大部分通常操作（新功能、bug修复等）都应在其他分支中进行并在审核通过后并入`master`分支。
   3. 请各位为自己的分支取有意义的名称。请在分支名最后加入关联的issue编号，如`backend-user-1`。
   4. 请**不要执行`git push --force`等类似的危险操作**。
   
2. 请检查自己的git所设置的用户名和邮箱是否正确。SECoder平台为大家设置的用户名`user.name`为中文姓名，邮箱`user.email`为`<学号>@secoder.net`。若非在SECoder上明确更改，请保持这项设置，以免无法统计commit数量等情况出现。若不想改变全局git设置，在项目文件夹中执行`git config`可以只为本项目进行设置。

   例如：

   ```bash
   # 在项目目录prj2a-repo/下
   # 如下设置仅仅会改变这个目录下的姓名和邮箱，不会影响全局
   git config --local user.name "丘勇"
   git config --local user.email "2020011790@secoder.net"
   # 验证一下是否修改成功
   git config --list
   ```

3. `commit`信息应有意义。下面给出项目 **（提议的）** `commit message`规范：

   1. Template：`(type) (optional scope): [description]`
   2. `type`：此`commit`所做的事情的类型，如：
      1. `fix`：修复bug
      2. `feat`：添加新特性、新功能
      3. `refactor`：代码重构
      4. `build` `chore` `ci` `docs` `style` `test` 等。
   3. `scope`：此`commit`所影响的范围，如：
      
      1. `frontend` `backend`
      2. `user` `payment` `question`
      
      在scope后需要加上#和**关联的issue编号**。
   4. `description`：简要描述
      
      1. 现在时，全小写，末尾应无标点符号
      2. 最重要的，不要说废话
   5. Demo:
      1. `(feat) (frontend #1): add user information page`
      2. `(fix) (backend, question #2 #3): check inputs in creating question`

   **注意**：根据助教的要求，所有的commit都**需要关联issue**，一个例子如下。

   ```bash
   # 关联一个issue
   git commit -m "(fix) (frontend #2): fix user info overflow bug"
   # 关联多个issue
   git commit -m "(feat) (backend #2 #3): check audit info"
   ```

4. 关于git issue的使用

   本项目的所有任务的分配都通过issue的形式来完成。

   issue一般情况下由组长统一进行分配，当某位组员发现bug或者发现某些需要改进的地方的时候，可以自行创建issue分配给自己。若要分配给其他组员，请与组员进行沟通。

   issue尽可能要打上标签，已有的标签如下

   - `bug`：说明这个issue与一个bug有关。
   - `critical`：说明这个issue非常重要，需要首先完成。
   - `development`：说明这是一个开发进程的issue。
   - `docs`：说明这个issue的内容是要修改或新增某个文档，例如API文档。
   - `enhancement`：说明这个issue是在已有的基础上进行改进，例如im系统需要支持传图片和视频的功能。

   issue必须要在审核完成和合并到master分支后并且确认无问题才能关闭。

5. *(Optional)* 可以设置个人`GPG Key`来保证署名的提交确实为本人操作。具体操作参见[文档](https://gitlab.secoder.net/help/user/project/repository/gpg_signed_commits/index.md)

## 技术选型

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

## 工作流程

1. [docs/ROADMAP.md](docs/ROADMAP.md)中包含了项目的整体计划与待办事项。
2. 当前应当进行和正在进行的工作应当在[Issues](https://gitlab.secoder.net/HardEng/prj2a-repo/-/issues)中进行进度跟踪和管理。请对应工作的负责人将`issue`关联(`assign`)给自己。
3. 一切正在进行中的工作都应在单独的分支中进行。对于某项工作的疑问、建议等都应在对应`issue`中进行讨论。工作完成后应当提起`pull request`，等待各位组员都审核通过、CI测试顺利后，合并入`master`分支。鉴于项目规模，目前不采用额外的`dev`分支进行合并操作。
4. 某项工作完成后，应当关闭对应Issue（应当由GitLab自动关闭），在ROADMAP.md中进行标记。

## 后端API通讯协议

**(TBD)**

在组员沟通后，请后端同学在[docs/api](docs/api/)文件夹中写明后端API的使用方法。具体格式参见目录下的[README.md](docs/api/README.md)。

## CI/CD与部署

**(TODO)**