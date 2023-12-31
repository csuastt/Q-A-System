# Backend User API


## Model

### User

#### 公开属性

| 属性        | 类型   | JSON | 说明                                 |
| ----------- | ------ | ---- | ------------------------------------ |
| id          | long   |      |                                      |
| username    | string |      | Unique，不可修改                     |
| nickname    | string |      | 默认设成与 username 相同             |
| description | string |      |                                      |
| price       | int    |      | 回答问题定价（仅回答者）             |
| ratingCount | int    |      | 评分次数（有可能为 0，代表没有评分） |
| ratingTotal | int    |      | 评分总数（除以评分次数得到评分）     |
| rating      | double |      | 评分（评分总数/评分次数）            |

#### 私有属性 (仅限本用户)

| 属性        | 类型    | JSON   | 说明                                              |
| ----------- | ------- | ------ | ------------------------------------------------- |
| password    | string  | 不返回 | 只写不读                                          |
| email       | string  |        | 有数据验证                                        |
| phone       | string  |        | 可随意修改                                        |
| gender      | enum    | string | { UNKNOWN, MALE, FEMALE }                         |
| role        | enum    | string | { USER, ANSWERER }                                |
| applying    | boolean |        | USER + 设为 true 代表正在申请成为回答者           |
| balance     | int     |        | 余额                                              |
| askCount    | int     |        | 提问单数（仅计算有效单数，回答者第一次回答时 +1） |
| answerCount | int     |        | 回答单数（仅计算有效单数，每单第一次回答时 +1）   |

#### 私有属性 (仅限管理员)

| 属性       | 类型          | JSON              | 说明 |
| ---------- | ------------- | ----------------- | ---- |
| createTime | ZonedDateTime | ISO string in UTC |      |

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

- `200` OK `{ "token": "my.jwt.token" }`
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

| 属性          | 类型    | 说明                                               |
| ------------- | ------- | -------------------------------------------------- |
| role          | enum    | 用户只能填 ANSWERER，管理员任意（可重复多个）      |
| applying      | boolean | 设为 {1,true,yes} 列出待审核用户，忽略 role 和排序 |
| pageSize      | int     | 单页用户数，默认为 20                              |
| page          | int     | 页数，默认为 1                                     |
| sortDirection | enum    | { ASC, DESC } 默认 ASC                             |
| sortProperty  | String  | 默认 id                                            |

返回值：

- `200` OK `{ pageSize: 20, page: 1, totalPages: 2, totalCount: 999, data: [...] }`
- `400` 格式错误
- `403` 权限不足

### 查询

```
GET /api/users/{id}
```

返回值：

- `200` OK（返回值是单个 User，详细程度见 Model）
- `404` 用户不存在

### 修改

```
PUT /api/users/{id}
```

本用户允许参数：`{ nickname, description, phone, gender, price }`

管理员允许参数：以上 + `{ email, role, balance }`

返回值：

- `200` OK

- `400` 格式错误

- `401` 未登录

- `403` 错误

  | message 属性          | 说明                   |
  | --------------------- | ---------------------- |
  | `NO_PERMISSION`       | 非本用户或管理员       |
  | `NICKNAME_INVALID`    | 昵称格式错误           |
  | `DESCRIPTION_INVALID` | 个人说明长度错误       |
  | `PRICE_INVALID`       | 价格错误（仅限回答者） |

- `404` 管理员修改用户不存在

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
- `404` 管理员修改用户不存在

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

### 收入

（仅限本人或管理员）

```
GET /api/users/{id}/earnings
```

返回值：

- `200` OK
- `401` 未登录
- `403` 错误（没权限或者不是回答者）

```json
{
    "total": 100,
    "monthly": [
        {
            "month": "2021-02",
            "earnings": 20
        },
        {
            "month": "2021-07",
            "earnings": 80
        }
    ]
}
```

### 统计数据

（仅限本人或管理员）

```
GET /api/users/{id}/stats
```

返回值：

- `200` OK
- `401` 未登录
- `403` 错误（没权限或者不是回答者）

```json
{
    "askCount": 0,
    "answerCount": 0
}
```

### 审核回答者申请

```
POST /api/users/{id}/review
```

参数：

| 属性   | 类型    | 说明                      |
| ------ | ------- | ------------------------- |
| accept | boolean | true 为通过，false 为拒绝 |

返回值：

- `200` OK

- `401` 未登录

- `403` 错误（只允许审核员或超级管理员）

