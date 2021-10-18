# Backend User API


## Model

### User

#### 公开属性

| 属性        | 类型   | JSON | 说明                     |
| ----------- | ------ | ---- | ------------------------ |
| id          | long   |      |                          |
| username    | string |      | Unique，不可修改         |
| nickname    | string |      | 默认设成与 username 相同 |
| avatar      | string |      | 未上传则为空             |
| description | string |      |                          |
| price       | int    |      | 回答问题定价（仅回答者） |

#### 私有属性 (仅限本用户)

| 属性     | 类型   | JSON   | 说明                      |
| -------- | ------ | ------ | ------------------------- |
| password | string | 不返回 | 只写不读                  |
| email    | string |        | 有数据验证                |
| phone    | string |        | 可随意修改                |
| gender   | enum   | string | { UNKNOWN, MALE, FEMALE } |
| role     | enum   | string | { USER, ANSWERER }        |
| balance  | int    |        | 余额                      |

#### 私有属性 (仅限管理员)

| 属性       | 类型          | JSON              | 说明     |
| ---------- | ------------- | ----------------- | -------- |
| deleted    | boolean       |                   | 删除标记 |
| createTime | ZonedDateTime | ISO string in UTC |          |

## Authentication


- 在所有和用户相关的 HTTP 请求中的 Header 里设置 `Authorization: Bearer <token>`
  - ref [JWT Token](https://jwt.io/introduction)

## API

### 注册

```
POST /api/users
```

参数：

| 属性     | 类型   | 说明       |
| -------- | ------ | ---------- |
| username | string |            |
| password | string |            |
| email    | string | 有数据验证 |

返回值：

- `200` OK
- `400` 格式错误
- `403` 错误

  | message 属性       | 说明                          |
  | ------------------ | ----------------------------- |
  | `USERNAME_INVALID` | 用户名已被使用/用户名格式错误 |
  | `PASSWORD_INVALID` | 密码格式错误                  |
  | `EMAIL_INVALID`    | 邮箱格式错误                  |

### 登录

```
POST /api/user/login
```

参数：

| 属性     | 类型   | 说明 |
| -------- | ------ | ---- |
| username | string |      |
| password | string |      |

返回值：

- `200` OK
- `400` 格式错误
- `403` 错误（用户名/密码错误）

### 退出

```
POST /api/user/logout
```

返回值：

- `200` OK

### 用户列表

```
GET /api/users
```

参数：

| 属性     | 类型 | 说明                            |
| -------- | ---- | ------------------------------- |
| role     | enum | 用户只能填 ANSWERER，管理员任意 |
| pageSize | int  | 单页用户数，默认为 20           |
| page     | int  | 页数，默认为 1                  |

返回值：

- `200` OK `{ users: [...] }`
- `400` 格式错误
- `403` 权限不足

### 删除

（仅限管理员）

```
DELETE /api/users/{id}
```

返回值：

- `200` OK
- `401` 未登录

- `403` 错误
  
  | message 属性      | 说明       |
  | ----------------- | ---------- |
  | `NO_PERMISSION`   | 不是管理员 |
  | `ALREADY_DELETED` | 已经删除   |
  
- `404` 用户不存在

### 查询

```
GET /api/users/{id}
```

返回值：

- `200` OK（返回值是单个 User，详细程度见 Model）
- `401` 未登录
- `404` 用户不存在（或用户已删除且查询者不是管理员）

【预期结果】

（仅限本用户或者管理员）

- `200` OK（返回值是单个 User，详细程度见 Model）
- `401` 未登录
- `403` 查询非本人信息
- `404` 管理员查询用户不存在（不含已删除）

### 修改

```
PUT /api/users/{id}
```

本用户允许参数：`{ nickname, description, phone, gender, price }`

管理员允许参数：以上 + `{ email, role, balance }`

返回值：

- `200` OK

- `400` 格式错误

- `401` 未登录或权限不足

- `403` 错误（仅限用户本人修改，管理员修改不做验证）

  | message 属性          | 说明                                 |
  | --------------------- | ------------------------------------ |
  | `NO_PERMISSION`       | 非本用户或管理员                     |
  | `NICKNAME_INVALID`    | 昵称格式错误                         |
  | `DESCRIPTION_INVALID` | 个人说明长度错误（仅限回答者，后续） |
  | `PRICE_INVALID`       | 价格错误（仅限回答者）               |

- `404` 用户已删除或管理员修改用户不存在

### 修改密码

```
PUT /api/users/{id}/password
```

参数：

| 属性     | 类型   | 说明             |
| -------- | ------ | ---------------- |
| original | string | 管理员不用此参数 |
| password | string |                  |

返回值：

- `200` OK
- `401` 未登录
- `403` 错误
  
  | message 属性       | 说明             |
  | ------------------ | ---------------- |
  | `NO_PERMISSION`    | 非本用户或管理员 |
  | `WRONG_PASSWORD`   | 原密码错误       |
  | `PASSWORD_INVALID` | 新密码格式错误   |
- `404` 管理员修改用户不存在或已删除

### 申请成为回答者

（仅限用户本人）

```
POST /api/users/{id}/apply
```

参数：

| 属性        | 类型   | 说明           |
| ----------- | ------ | -------------- |
| description | string | 即修改个人说明 |
| price       | int    | 定价           |

返回值：

- `200` OK

- `401` 未登录

- `403` 错误

  | message 属性          | 说明             |
  | --------------------- | ---------------- |
  | `NO_PERMISSION`       | 非本用户         |
  | `ALREADY_ANSWERER`    | 已经是回答者     |
  | `DESCRIPTION_INVALID` | 个人说明长度错误 |
  | `PRICE_INVALID`       | 价格超过范围     |

### 充值

```
POST /api/users/{id}/recharge
```

参数：

| 属性  | 类型 | 说明       |
| ----- | ---- | ---------- |
| value | int  | 只能为正数 |

返回值：

- `200` OK

- `401` 未登录

- `403` 错误

  | message 属性       | 说明                   |
  | ------------------ | ---------------------- |
  | `NO_PERMISSION`    | 非本用户               |
  | `RECHARGE_INVALID` | 充值金额超过范围       |
  | `BALANCE_INVALID`  | （充值后）余额超过范围 |
