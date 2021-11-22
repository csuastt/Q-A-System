# 订单 API

## Model

### Order

| 属性                | 类型           | JSON                                       | 说明                 |
| ------------------- | -------------- | ------------------------------------------ | -------------------- |
| id                  | long           |                                            |                      |
| deleted             | boolean        |                                            | 删除标记（仅管理员） |
| asker               | User           | Get: User, Set: number                     |                      |
| answerer            | User           | Get: User, Set: number                     |                      |
| state               | OrderState     | string                                     |                      |
| finished            | boolean        |                                            | 订单结束后设为 true  |
| createTime          | ZonedDateTime  | ISO string (UTC, 即末尾带 `Z`)             | 创建时间             |
| expireTime          | ZonedDateTime  | ISO string (UTC, 即末尾带 `Z`)             | 超时时间             |
| endReason           | OrderEndReason | string                                     |                      |
| questionTitle       | string         | Get: questionTitle, Set: title             | 问题标题             |
| questionDescription | string         | Get: questionDescription, Set: description | 请求详细信息才会返回 |
| answer              | string         |                                            | 请求详细信息才会返回 |
| price               | int            |                                            |                      |
| showPublic          | boolean        |                                            | 公开问题             |
| messageCount        | int            |                                            | 聊天消息条数         |
| rating              | int            | Get: > 0 已评分，= 0 未评分                | 评分                 |
| ratingText          | String         |                                            | 仅限详细信息         |

### OrderState (enum)

- CREATED - 已创建并支付，等待审核
- ~~PAYED - 已支付，等待审核~~
- ~~PAY_TIMEOUT - 支付超时  **finished**~~
- REVIEWED  - 审核通过，等待接单【expireTime=接单时间】
- REJECTED_BY_REVIEWER - 审核不通过，已退款  **finished**
- ACCEPTED - 已接单，等待首次回答【expireTime=回答时间】
- REJECTED_BY_ANSWERER - 回答者拒绝接单，已退款  **finished**
- RESPOND_TIMEOUT - 回答者接单超时，已退款  **finished**
- ANSWERED - 回答者已做首次回答，聊天进行中【expireTime=聊天自动结束时间】
- ANSWER_TIMEOUT - 回答者首次回答超时，已退款  **finished**
- CHAT_ENDED - 聊天已结束，等待结算（回答者结束服务或者聊天超过时限）  **finished** 【expireTime=结算时间】
- FULFILLED - 已结单  **finished**
- CANCELLED - 回答者接单前被提问者取消  **finished**

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
POST /api/orders
```

参数：（用户）

| 名称       | 类型    | 说明     |
| ---------- | ------- | -------- |
| answerer   | number  | 用户 ID  |
| question   | string  | 问题     |
| showPublic | boolean | 是否公开 |

参数：（超级管理员）

必须 `{ asker, answerer, question }` 

可选 `{ state, endReason, answerSummary, price }`

返回值：

- `200` OK，内容为 Order
  
- `400` 格式错误

- `401` 未登录

- `403` 错误

  | message 属性         | 说明                                           |
  | -------------------- | ---------------------------------------------- |
  | `ASKER_INVALID`      | 未找到提问者（仅管理员）                       |
  | `ANSWERER_INVALID`   | 回答者与提问者相同/未找到回答者/回答者没有权限 |
  | `QUESTION_INVALID`   | 问题字数不符合系统要求                         |
  | `BALANCE_NOT_ENOUGH` | 余额不足                                       |

### 删除订单

（仅限超级管理员）

```
DELETE /api/orders/{id}
```

返回值：

- `200` OK
- `401` 未登录
- `403` 错误
  
  | message 属性      | 说明       |
  | ----------------- | ---------- |
  | `NO_PERMISSION`   | 不是管理员 |
  | `ALREADY_DELETED` | 已经删除   |
- `404` 订单不存在

### 获取订单信息

（管理员可以查询已删除订单，普通用户会报 404）

```
GET /api/orders/{id}
```

返回值：

- `200` OK，内容为 Order
- `401` 未登录
- `403` 错误（没权限）
- `404` 订单不存在或已删除

### 修改订单信息

（仅限管理员，已删除的订单不能修改）

```
PUT /api/orders/{id}
```

参数：

可选 `{ state, endReason, question, answerSummary, price }`

问答方不可修改。

返回值：

- `200` OK

- `400` 格式错误

- `401` 未登录

- `403` 错误（仅限管理员，无权限）

- `404` 订单不存在或已删除

### 查询订单列表

```
GET /api/orders
```

公共参数：

| 名称          | 类型   | 说明                                       |
| ------------- | ------ | ------------------------------------------ |
| pageSize      | int    | 单页最大订单数，可选，默认为 20，最大为 50 |
| page          | int    | 页数（从 1 开始），可选，默认为 1          |
| sortDirection | enum   | { ASC, DESC } 默认用户 DESC，管理员 ASC    |
| sortProperty  | string | 默认 createTime                            |

参数：（用户）

```
?asker={自己的id}
```

获取自己的订单列表。

```
?answerer={自己的id}
```

获取自己的回答列表。

```
?{asker;answerer}={id}&finished={true,yes,1;false,no,0}
```

获取已完成/进行中的订单。

```
?showPublic={true,yes,1}
?showPublic={true,yes,1}&keyword={关键词}
```

获取问答库/搜索问答库。（无需登录）

参数：（管理员）

```
(无参数)
?state=CREATED  => 获取待审核订单
?state=CREATED&state=CANCELLED  => 筛选多个状态
?reviewed={true,yes,1}&sortDirection=DESC  => 获取已审核订单（reviewed 不可设为 false）
```

返回值：

- `200` OK `{ "pageSize": 20, "page": 1, "totalPages": 2, "totalCount": 999, "data": [...], "timeMillis": 20 }` 
- `400` 格式错误
- `401` 未登录
- `403` 错误

### 审核订单

（仅限审核员）

```
POST /api/orders/{id}/review
```

参数：

| 名称   | 类型    | 说明                        |
| ------ | ------- | --------------------------- |
| accept | boolean | true 为通过，false 为不通过 |

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录
- `403` 错误（`{message: "NOT_TO_BE_REVIEWED"}` 订单不是待审核状态 或者没权限）
- `404` 订单不存在或已删除

### 回答者接单

```
POST /api/orders/{id}/respond
```

参数：

| 名称   | 类型    | 说明                      |
| ------ | ------- | ------------------------- |
| accept | boolean | true 为接单，false 为拒绝 |

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录
- `403` 错误（`{message: "NOT_TO_BE_RESPONDED"}` 订单不是待接单状态 或者没权限）
- `404` 订单不存在或已删除

### 回答者首次回答

由 IM 系统触发。

临时接口（回答完直接结束服务）：

```
POST /api/orders/{id}/answer
```

参数：

| 名称   | 类型   | 说明 |
| ------ | ------ | ---- |
| answer | string |      |

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录
- `403` 错误（订单不是待回答状态  或者没权限）
- `404` 订单不存在或已删除

### 结束服务

```
POST /api/orders/{id}/end
```

返回值：

- `200` OK
- `401` 未登录
- `403` 错误（`{message: "NOT_TO_BE_ENDED"}`订单不是聊天对话状态  或者没权限）
- `404` 订单不存在或已删除

### 取消订单

```
POST /api/orders/{id}/cancel
```

返回值：

- `200` OK
- `401` 未登录
- `403` 错误
  | message 属性    | 说明           |
  | --------------- | -------------- |
  | `NO_PERMISSION` | 不是提问者     |
  | `CANNOT_CANCEL` | 已接单无法取消 |
- `404` 订单不存在或已删除

### 评价订单

```
POST /api/orders/{id}/rate
```

参数：

```json
{ "value": 5, "text": "Very good!" }
```

（text 限 200 字，如果传 null 则获取时也会返回 null）

返回值：

- `200` OK

- `401` 未登录

- `403` 错误

  | message 属性     | 说明                           |
  | ---------------- | ------------------------------ |
  | `NO_PERMISSION`  | 不是提问者                     |
  | `CANNOT_RATE`    | 不能评分（聊天未结束或已评分） |
  | `RATING_INVALID` | 不是 1~5                       |
  | `TEXT_INVALID`   | 超过 200 字                    |

- `404` 订单不存在或已删除

