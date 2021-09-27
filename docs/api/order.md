# 订单 API

## Model

### Order

| Order.()         | 类型       | 说明                           |
| ---------------- | ---------- | ------------------------------ |
| id               | int        |                                |
| isDeleted        | boolean    | 删除标记                       |
| asker            | int        | 用户 ID                        |
| answerer         | int        | 用户 ID                        |
| state            | OrderState |                                |
| isActive         | boolean    | 订单结束后设为 false           |
| createTime       | int        | 创建时间                       |
| payTime          | int        | 支付/超时时间                  |
| reviewTime       | int        | 审核时间                       |
| respondTime      | int        | 接单/拒绝/超时时间             |
| answerTime       | int        | 首次回答/超时时间              |
| endTime          | int        | 聊天结束时间                   |
| fulfillTime      | int        | 结算时间                       |
| expireTime       | int        | 用来记录任何限时流程的截止时间 |
| chatMessageCount | int        | 聊天记录条数（备用）           |
| endReason        | EndReason  |                                |
| question         | string     | 问题                           |
| answerSummary    | string     | 回答（用于问答库）             |
| price            | int        |                                |

### OrderState (enum)

- CREATED - 已创建，等待支付
- PAYED - 已支付，等待审核
- PAY_TIMEOUT - 支付超时  **isActive=false**
- REVIEWED  - 审核通过，等待接单
- REJECTED_BY_REVIEWER - 审核不通过，已退款  **isActive=false**
- ACCEPTED - 已接单，等待首次回答
- REJECTED_BY_ANSWERER - 回答者拒绝接单，已退款  **isActive=false**
- RESPOND_TIMEOUT - 回答者接单超时，已退款  **isActive=false**
- ANSWERED - 回答者已做首次回答，聊天进行中
- ANSWER_TIMEOUT - 回答者首次回答超时，已退款  **isActive=false**
- CHAT_ENDED - 聊天已结束，等待结算（回答者结束服务或者聊天超过时限）  **isActive=false**
- FULFILLED - 已结单  **isActive=false**
- 可扩展：投诉……

### EndReason (enum)

- UNKNOWN - 未结束或者未知原因
- ASKER - 提问者结束服务
- ANSWERER - 回答者结束服务
- TIME_LIMIT - 服务时长超过时限
- MESSAGE_LIMIT - 备用：消息数量超过限制
- SYSTEM - 备用：系统/管理员原因

## API

### 创建订单

```
POST /api/order/create
```

参数：

| 名称     | 类型   | 说明    |
| -------- | ------ | ------- |
| answerer | int    | 用户 ID |
| question | string | 问题    |

返回值：

- `200` OK
  内容为 Order
  
- `400` 格式错误

- `401` 未登录

- `403` 错误

  | 名称    | 类型   | 说明                                                         |
  | ------- | ------ | ------------------------------------------------------------ |
  | message | string | `ORDER_CREATE_ANSWERER_INVALID`/`ORDER_CREATE_QUESTION_INVALID` |

### 删除订单

```
DELETE /api/order/:id
```

返回值：

- `200` OK
- `401` 未登录或权限不足（仅限管理员）
- `403` 错误（已删除）
- `404` 订单不存在

### 获取订单信息

```
GET /api/order/:id
```

返回值：

- `200` OK
  内容为 Order
- `401` 未登录或权限不足
- `404` 订单不存在

### 修改订单信息

```
PUT /api/order/:id
```

参数：Order 的任意属性（除了 id、isDeleted、isActive），可能会打破运行逻辑

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录或权限不足（仅限管理员）
- `404` 订单不存在

### 查询订单列表

```
GET /api/orders
```

公共参数：

| 名称     | 类型 | 说明                              |
| -------- | ---- | --------------------------------- |
| pageSize | int  | 单页最大订单数，可选，最大为 50   |
| page     | int  | 页数（从 1 开始），可选，默认为 1 |

参数：（用户）

| 名称       | 类型       | 说明                                     |
| ---------- | ---------- | ---------------------------------------- |
| asker      | int        | 必须指定 asker / answerer 之一为查询用户 |
| answerer   | int        | 必须指定 asker / answerer 之一为查询用户 |
| state      | OrderState | （可能用不着）                           |
| isActive   | boolean    |                                          |
| createTime | int 范围   |                                          |

参数：（问答库）

```
TODO
```

参数：（管理员）

| 名称             | 类型            | 说明 |
| ---------------- | --------------- | ---- |
| id               | int 范围        |      |
| isDeleted        | boolean         |      |
| asker            | int 范围        |      |
| answerer         | int 范围        |      |
| state            | OrderState 范围 |      |
| isActive         | boolean         |      |
| createTime       | int 范围        |      |
| payTime          | int 范围        |      |
| reviewTime       | int 范围        |      |
| respondTime      | int 范围        |      |
| answerTime       | int 范围        |      |
| endTime          | int 范围        |      |
| fulfillTime      | int 范围        |      |
| expireTime       | int 范围        |      |
| chatMessageCount | int 范围        |      |
| endReason        | EndReason 范围  |      |
| question         | string          |      |
| price            | int 范围        |      |

- int 范围：一个范围 `{ "from": 0, "to": 9999 }`（可省略某一边界，两边为闭区间）
- enum 范围：一个 enum 列表

返回值：

- `200` OK

  | 名称     | 类型    | 说明               |
  | -------- | ------- | ------------------ |
  | total    | int     | 总数               |
  | pageSize | int     | 单页最大订单数     |
  | page     | int     | 页数（从 1 开始）  |
  | orders   | Order[] | 超过最后一页时为空 |

- `400` 格式错误

- `401` 未登录或权限不足

### 支付订单

请从支付系统回调。

### 审核订单

```
POST /api/order/:id/review
```

参数：

| 名称   | 类型    | 说明                        |
| ------ | ------- | --------------------------- |
| accept | boolean | true 为通过，false 为不通过 |

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录或权限不足（仅限审核员）
- `403` 错误（订单不是待审核状态）
- `404` 订单不存在

### 回答者接单

```
POST /api/order/:id/respond
```

参数：

| 名称   | 类型    | 说明                      |
| ------ | ------- | ------------------------- |
| accept | boolean | true 为接单，false 为拒绝 |

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录或权限不足（仅限回答者）
- `403` 错误（订单不是待接单状态）
- `404` 订单不存在

### 回答者首次回答

由 IM 系统触发。

### 结束服务

```
POST /api/order/:id/end
```

返回值：

- `200` OK
- `401` 未登录或权限不足（仅限提问者、回答者）
- `403` 错误（订单不是聊天对话状态）
- `404` 订单不存在

