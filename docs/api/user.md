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
  
- `api/user/logout` : 用户登出
  
  - Method: `POST`
  
  - Parameters:

  - Response:
    - `200`: 成功登出


- `api/users` : 删除用户
  
  - Method: `DELETE`
  
  - Parameters:

    | Name     | Type   | Description             |
    | -------- | ------ | ----------------------- |
    | id       | long   |                         |
  
  - Response:
    - `200`: 成功删除
    - `500`: 后台数据库出错
    - `400`: 没有该用户
    
    
- `api/users/:id` : 获取详细信息
    
    - Method: `GET`
  
    - Parameters:
  
    - Response:
        - `200`: body中是`[User]`
        - `400`: 没有该用户

- `api/users/:id/basic` : 获取基本信息

    - Method: `GET`

    - Parameters:

    - Response:
        - `200`: body中是`[Basic_User]`
        - `400`: 没有该用户

- `api/users/:id/permission` : 获取用户权限
  
  - Method: `GET`
  
  - Parameters:
  
  - Response:
    - `200`:

      | Name      | Type   | Description             |
      | --------  | ------ | ----------------------- |
      | permission| string | q/a                     |
    - `400`: 没有该用户
    

- `api/users/:id` : 修改详细信息
  
  - Method: `PUT`
  
  - RequestBody:

    | Name        | Type   | Description                       |
    | --------    | ------ | -----------------------           |
    | username    | string | not empty, unique       `optional`|
    | nickname    | string | not empty               `optional`|
    | email       | string | should be a valid email `optional`|
    | gender      | string | male/female/unknown     `optional`|
    | birthday    | string | yy/mm/dd                `optional`|
    | description | string |                         `optional`|
    | phone       | string |                         `optional`|


  - Response:
    - `200`:
    - `400`: 没有该用户
    - `500`: 没有修改权限


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
    
    