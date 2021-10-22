# 订单 API

## Model

### Order

| 属性          | 类型           | JSON                           | 说明                                    |
| ------------- | -------------- | ------------------------------ | --------------------------------------- |
| id            | long           |                                |                                         |
| deleted       | boolean        |                                | 删除标记                                |
| asker         | User           | Get: User, Set: number         |                                         |
| answerer      | User           | Get: User, Set: number         |                                         |
| state         | OrderState     | string                         |                                         |
| finished      | boolean        |                                | 订单结束后设为 true                     |
| createTime    | ZonedDateTime  | ISO string (UTC, 即末尾带 `Z`) | 创建时间                                |
| endReason     | OrderEndReason | string                         |                                         |
| question      | string         |                                | 问题（最长 100 字符，后续交给系统设置） |
| answerSummary | string         |                                | 请求详细信息才会返回                    |
| price         | int            |                                |                                         |

### OrderState (enum)

- CREATED - 已创建，等待支付
- PAYED - 已支付，等待审核
- PAY_TIMEOUT - 支付超时  **finished**
- REVIEWED  - 审核通过，等待接单
- REJECTED_BY_REVIEWER - 审核不通过，已退款  **finished**
- ACCEPTED - 已接单，等待首次回答
- REJECTED_BY_ANSWERER - 回答者拒绝接单，已退款  **finished**
- RESPOND_TIMEOUT - 回答者接单超时，已退款  **finished**
- ANSWERED - 回答者已做首次回答，聊天进行中
- ANSWER_TIMEOUT - 回答者首次回答超时，已退款  **finished**
- CHAT_ENDED - 聊天已结束，等待结算（回答者结束服务或者聊天超过时限）  **finished**
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

| 名称     | 类型   | 说明                  |
| -------- | ------ | --------------------- |
| asker    | number | 用户 ID（仅限管理员） |
| answerer | number | 用户 ID               |
| question | string | 问题                  |

参数：（管理员）

任意 Order 属性，必须有 asker、answerer、question，无视 id、deleted、finished、createTime，设置 price > 0（后续添加上限检查）即为覆盖默认。

返回值：

- `200` OK，内容为 Order
  
- `400` 格式错误

- `401` 未登录（后续实现）

- `403` 错误

  | message 属性       | 说明                                           |
  | ------------------ | ---------------------------------------------- |
  | `ASKER_INVALID`    | 未找到提问者（后续改为仅管理员创建时提示）     |
  | `ANSWERER_INVALID` | 回答者与提问者相同/未找到回答者/回答者没有权限 |
  | `QUESTION_INVALID` | 问题字数不符合系统要求（暂设 5≤length≤100）    |

### 删除订单

（仅限管理员）

```
DELETE /api/orders/{id}
```

返回值：

- `200` OK
- `401` 未登录（后续实现）
- `403` 错误（没权限或已删除）
- `404` 订单不存在

### 获取订单信息

（管理员可以查询已删除订单，普通用户会报 404）

```
GET /api/orders/{id}
```

返回值：

- `200` OK，内容为 Order
- `401` 未登录（后续实现）
- `403` 错误（没权限）
- `404` 订单不存在或已删除

### 修改订单信息

（仅限管理员，已删除的订单不能修改）

```
PUT /api/orders/{id}
```

参数：任意 Order 属性，无视 id、deleted、finished、createTime，设置 price > 0 可以修改（后续添加上限检查）。已被删除的用户必须修改。

返回值：

- `200` OK

- `400` 格式错误

- `401` 未登录或权限不足（仅限管理员，后续实现）

- `403` 错误

  | message 属性       | 说明                                                         |
  | ------------------ | ------------------------------------------------------------ |
  | `ASKER_INVALID`    | 未找到新提问者/旧提问者已被删除却没有修改                    |
  | `ANSWERER_INVALID` | 任何会导致回答者与提问者相同的情况/未找到新回答者/旧回答者已被删除却没有修改/回答者没有权限 |
  | `QUESTION_INVALID` | 新问题字数不符合系统要求（暂设 5≤length≤100）                |

- `404` 订单不存在或已删除

### 查询订单列表

```
GET /api/orders
```

公共参数：

| 名称     | 类型 | 说明                                       |
| -------- | ---- | ------------------------------------------ |
| pageSize | int  | 单页最大订单数，可选，默认为 20，最大为 50 |
| page     | int  | 页数（从 1 开始），可选，默认为 1          |

参数：（用户）

| 名称     | 类型 | 说明                                                     |
| -------- | ---- | -------------------------------------------------------- |
| asker    | long | 只能指定 asker / answerer 之一为查询用户                 |
| answerer | long | 只能指定 asker / answerer 之一为查询用户（未过审不显示） |

参数：（管理员）

| 名称  | 类型       | 说明                                        |
| ----- | ---------- | ------------------------------------------- |
| state | OrderState | 指定 state=PAYED 获取待审核订单（时间升序） |

返回值：

- `200` OK `{ "pageSize": 20, "page": 1, "totalPages": 2, "orders": [...] }` （时间降序）
- `400` 格式错误
- `401` 未登录
- `403` 错误

### 支付订单

```
POST /api/orders/{id}/pay
```

返回值：

- `200` OK

- `401` 未登录

- `403` 错误（余额不足）

  | message 属性         | 说明     |
  | -------------------- | -------- |
  | `BALANCE_NOT_ENOUGH` | 余额不足 |

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
- `403` 错误（订单无法取消 或者没权限）
- `404` 订单不存在或已删除

