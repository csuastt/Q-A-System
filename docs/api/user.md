# Backend User API


## Data


 User 用户:
  - `username` unique  

  | Name         | Type           | Description             |
  | --------     | ------         | ----------------------- |
  | id           | Long           | unique, auto generated  | 
  | username     | string         | not empty, unique       |
  | nickname     | string         | empty, or max_len < 10  |
  | ava_url      | string         |                         |
  | createTime   | ZonedDateTime  | the time signing up     |
  | email        | string         | should be a valid email |
  | phone        | string         | should be a valid phone |
  | gender       | string         | male/female/unknown     |
  | birthday     | string         | yy/mm/dd                |
  | permission   | string         | q/a                     |
  | money        | int            | `initial` 100           |
  | description  | string         |                         |

 Basic_User :

  | Name              | Type   | Description             |
  | --------          | ------ | ----------------------- |
  | id                | Long   | unique, auto generated  | 
  | username          | string | not empty, unique       |
  | nickname          | string | empty, or max_len < 10  |
  | ava_url           | string |                         |
  | description       | string |                         |
 

## Authentication


- 在所有和用户相关的 HTTP 请求中的 Header 里设置 `Authorization: Bearer <token>`
  - ref [JWT Token](https://jwt.io/introduction)

## Agreement

- `Parameters` : 用`param`传参
- `RequestBody` : 用`body`传参

## API


- `/api/users`: 用户注册
  
  - Method: `POST`
  
  - RequestBody:

    | Name     | Type   | Description             | Necessity |
    | -------- | ------ | ----------------------- | --------- |
    | username | string | not empty, unique       | essential |
    | password | string | 6 to 12 in length       | essential |
    | email    | string | should be a valid email | optional  |
    | gender   | string | male/female/unknown     | optional  |
    | phone    | string | should be a valid phone | optional  |
  
  - Response:
    - `200`: 注册成功
        
    - `403 Forbidden`: 用户名已注册

    - `400` : 数据格式错误


- `api/user/login` : 用户登录
  
  - Method: `POST`

  - RequestBody:

    | Name     | Type   | Description             |
    | -------- | ------ | ----------------------- |
    | username | string |                         |
    | password | string |                         |
  
  - Response:
    - `200`:

    | Name    | Type   | Description  |
    | ------- | ------ | ------------ |
    | token   | string |              |
    | user    | string | [User]       |
    - `401`:
     ```
    密码/用户名错误
    ```
  
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
    
    