# 管理员 API


## Model

### Admin

| 属性       | 类型          | JSON              | 说明                             |
| ---------- | ------------- | ----------------- | -------------------------------- |
| id         | long          |                   |                                  |
| username   | string        |                   | Unique，不可修改                 |
| password   | string        | 不返回            | 只写不读                         |
| role       | enum          | string            | { ADMIN, REVIEWER, SUPER_ADMIN } |
| createTime | ZonedDateTime | ISO string in UTC |                                  |

## Authentication


- 在 HTTP 请求中的 Header 里设置 `Authorization: Bearer <token>`
  - ref [JWT Token](https://jwt.io/introduction)

## API

### 添加管理员

（仅限超级管理员）

```
POST /api/admins
```

参数：

| 属性     | 类型   | 说明                                 |
| -------- | ------ | ------------------------------------ |
| username | string |                                      |
| role     | string | 可选，默认 ADMIN，不能为 SUPER_ADMIN |

返回值：

- `200` OK `{ "password": "password" }`
- `400` 格式错误
- `401` 未登录
- `403` 错误（`NO_PERMISSION` 无权限或者不能创建超级管理员，`USERNAME_INVALID` 用户名已被占用）

### 登录

```
POST /api/admin/login
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
POST /api/admin/logout
```

返回值：

- `200` OK

### 管理员列表

（仅限超级管理员）

```
GET /api/admins
```

参数：

| 属性     | 类型 | 说明                    |
| -------- | ---- | ----------------------- |
| role     | enum | 可选（可以重复多个）    |
| pageSize | int  | 单页管理员数，默认为 20 |
| page     | int  | 页数，默认为 1          |

```
GET /api/admins  => 默认获取所有非超级管理员
GET /api/admins?role=SUPER_ADMIN&role=REVIEWER&role=ADMIN  => 获取多个权限的管理员
```

返回值：

- `200` OK `{ pageSize: 20, page: 1, totalPages: 2, totalCount: 999, data: [...] }`
- `400` 格式错误
- `401` 未登录
- `403` 权限不足

### 查询

```
GET /api/admins/{id}
```

返回值：

- `200` OK（返回值是单个 Admin）
- `401` 未登录
- `403` 错误（普通管理员查询非自己的管理员）
- `404` 管理员不存在

### 修改

（仅限超级管理员）

```
PUT /api/admins/{id}
```

参数：`{ password, role }`（传 null 即不修改）

返回值：

- `200` OK

- `400` 格式错误

- `401` 未登录

- `403` 错误

  | message 属性    | 说明                                                         |
  | --------------- | ------------------------------------------------------------ |
  | `NO_PERMISSION` | 非超级管理员，或者超级管理员修改自己，或者尝试修改为 SUPER_ADMIN |
  
- `404` 管理员不存在

### 修改密码

```
PUT /api/admins/{id}/password
```

参数：

| 属性     | 类型   | 说明                                                 |
| -------- | ------ | ---------------------------------------------------- |
| original | string | 超级管理员修改普通管理员不用此参数，也可以用修改接口 |
| password | string |                                                      |

返回值：

- `200` OK
- `401` 未登录
- `403` 错误
  
  | message 属性     | 说明                 |
  | ---------------- | -------------------- |
  | `NO_PERMISSION`  | 非本用户或超级管理员 |
  | `WRONG_PASSWORD` | 原密码错误           |
- `404` 超级管理员修改管理员不存在
