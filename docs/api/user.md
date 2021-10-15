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

### 删除

（仅限管理员）

```
DELETE /api/users/{id}
```

返回值：

- `200` OK
- `401` 未登录或权限不足
- `403` 错误（已删除）
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
- `401` 未登录或查询非本人信息
- `404` 管理员查询用户不存在（已删除也会返回 200）

### 修改

```
PUT /api/users/{id}
```

本用户允许参数：`{ nickname, description, phone, gender }`

管理员允许参数：以上 + `{ email, role, balance, price }`

返回值：

- `200` OK

- `401` 未登录或权限不足

- `403` 错误（仅限用户本人修改，管理员修改不做验证）

  | message 属性          | 说明                           |
  | --------------------- | ------------------------------ |
  | `NICKNAME_INVALID`    | 昵称格式错误                   |
  | `DESCRIPTION_INVALID` | 个人说明长度错误（仅限回答者） |

- `404` 管理员修改用户不存在或已删除


- `api/users/:id/password` : 修改密码
  
  - Method: `PUT`
  
  - RequestBody:

    | Name      | Type   | Description             |
    | --------  | ------ | ----------------------- |
    | origin    | string |                         |
    | password  | string |                         |
  
  - Response:
    - `200`: 修改成功
    - `400`: 没有该用户
    - `403`: 原密码不正确


- `api/users/:id/price` : 获得用户定价
  
  - Method : `GET`

  - Parameters:

  - Response:
      - `200`:

        | Name      | Type   | Description             |
        | --------  | ------ | ----------------------- |
        | price     | int    |                         |
      - `400`: 没有该用户
      - `403`: 没有权限


- `api/users/:id/price` : 修改用户定价

    - Method: `PUT`

    - RequestBody:

      | Name      | Type   | Description             |
      | --------  | ------ | ----------------------- |
      | price     | int    | min 20   max  100       |

    - Response:
        - `200`: 修改成功
        - `400`: 没有该用户
        - `403`: 没有定价权限
        - `500`: 定价不在允许的范围内

    
- `api/users/:id/permission` : 修改用户权限

  - Method: `PUT`

  - RequestBody:

    | Name        | Type   | Description             |
    | --------    | ------ | ----------------------- |
    | permission  | string | q/a                     |

  - Response:
      - `200`: 修改成功
      - `400`: 没有该用户
      - `403`: 没有修改权限
      - `500`: 修改错误(从q修改至q)


- `api/users/:id/apply` : 申请成为回答者

    ```
  TODO
  ```


- `api/users/:id/income` : 查询收入

    ```
  TODO
  ```


- `api/users` : 用户列表
  
  - Method: `GET`
  
  - Parameters:

    | Name     | Type      | Description                |
    | -------  | ------    | ------------               |
    | answerer | boolean   | true/false  default `false`|
    | page     | int       |      default `1`           |
    | limit    | int       |      default `20`          |
  

  - Response:
    - `200`:
      
      | Name     | Type      | Description      |
      | -------  | ------    | ------------     |
      | user_list | string    | list of `[Basic_User]` |
    
    