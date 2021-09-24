# Roadmap

本文档表示了整个项目的任务分解、整体进度和待办事项。

如果要查看某轮迭代目标，请查看[ITERATIVEGOALS.md](ITERATIVEGOALS.md)。

## Instruction

请使用下面的符号标记当前任务的完成情况：

| 符号                    | 代码                      | 含义                                                   |
| ----------------------- | ------------------------- | ------------------------------------------------------ |
| :white_circle:          | `:white_circle:`          | 该部分还未开始，不在当前计划的范围内                   |
| :large_orange_diamond:  | `:large_orange_diamond:`  | 该部分的子任务正在进行或已经完成                       |
| :o:                     | `:o:`                     | 该部分还未开始，但在当前计划范围内，请组员尽快认领任务 |
| :construction:          | `:construction:`          | 该部分已经开始，正在施工                               |
| :eight_spoked_asterisk: | `:eight_spoked_asterisk:` | 该部分已经初步完成，已提交PR并等待合并                 |
| :white_check_mark:      | `:white_check_mark:`      | 该部分已经完成并合并入主分支                           |
| :question:              | `:question:`              | 该部分内容并未确定，有待后续讨论                       |

若认领任务，请在自己的分支中，对本文件相应部分进行修改，包括修改标记，注明认领人、issue和pr编号等操作。

若要对本文件进行更改，请提交Issue进行讨论。

## Tasks

- :large_orange_diamond:`Task Root`
  - `Frontend`
    - :large_orange_diamond: 用户相关
      - :construction: 登陆
      - :construction: 注册
      - :construction: 查看、编辑用户信息
      - :construction: 修改个人密码
    - :large_orange_diamond: 提问者特有
      - :construction: 查看、搜索回答者列表
      - :construction: 提问
      - :white_circle: 申请成为问答者
      - :construction: 购买的订单列表 (待接收/进行中/已完成/已退款)
      - :construction: 查看订单信息（已提问的问题）
    - :white_circle: 回答者特有
      - 回答者个人展示界面
      - 接受/拒绝订单
      - 首次回答订单问题
      - 回答的订单列表 (待回答/进行中/已回复)
      - 查看收到的订单信息
      - 月度收入统计
    - :white_circle: 问答相关
      - 及时通讯
      - 问答订单结算
    - :white_circle: 虚拟钱包/支付系统
      - 充值
      - 虚拟钱包展示
    - :white_circle: 管理员相关
      - 修改个人密码
      - 查看、搜索用户列表
      - 查看、更改用户详细信息与权限
      - 创建、删除用户
      - 查看、搜索管理员列表
      - 创建、删除管理员
      - 查看、搜索订单列表
      - 审核、删除订单
      - 参数配置
  - `Backend`
    - :large_orange_diamond: 用户相关
      - :construction: 登陆
      - :construction: 注册
      - :construction: 登出
      - :construction: 删除用户
      - :construction: 获取详细信息
      - :construction: 修改详细信息
      - :white_circle: 获取用户权限
      - :white_circle: 修改用户权限
      - :construction: 修改用户密码
      - :construction: 查询用户列表
      - :white_circle: 查询问答者列表
      - :white_circle: 申请成为问答者
      - :white_circle: 查询收入情况
    - :large_orange_diamond: 订单相关
      - :construction: 创建订单
      - :construction: 删除订单
      - :construction: 获取详细信息
      - :construction: 修改详细信息
      - :white_circle: 修改订单状态
      - :white_circle: 审核订单结果 (只有管理员才能审核)
      - :construction: 查询订单列表
    - :white_circle: 支付相关
      - 查询余额
      - 充值
      - 提问者扣款
      - 提问者退款
      - 问答者到账（需要等到订单完成一段时间后才到账）
    - :large_orange_diamond: 管理员相关
      - :construction: 登陆
      - :construction: 登出
      - :construction: 创建
      - :construction: 删除
      - :construction: 修改管理员密码
      - :construction: 获取管理员权限
      - :construction: 修改管理员权限
      - :white_circle: 修改系统参数
      - :construction: 查询管理员列表
    - :question: 即时通讯相关
  - `Misc`
    - CI/CD
