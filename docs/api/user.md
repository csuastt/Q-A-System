# Backend User API


## Data


 User 用户:
  - `username` unique  

  | Name        | Type   | Description             |
  | --------    | ------ | ----------------------- |
  | id          | Long   | unique, auto generated  | 
  | username    | string | not empty, unique       |
  | email       | string | should be a valid email |
  | phone       | string | should be a valid phone |
  | password    | string | 6 to 12 in length       |
  | gender      | string | male/female             |
  | birthday    | string | yy/mm/dd                |
  | permission  | string | q/a                     |
  | money       | int    | `initial` 100           |
  | description | string |                         |
 

## Authentication


- 在所有和用户相关的 HTTP 请求中的 Header 里设置 `Authorization: Bearer <token>`
  - ref [JWT Token](https://jwt.io/introduction)


## API


- `/api/users`: 用户注册
  
  - Method: `POST`
  
  - RequestBody:

    | Name     | Type   | Description             | Necessity |
    | -------- | ------ | ----------------------- | --------- |
    | username | string | not empty, unique       | essential |
    | password | string | 6 to 12 in length       | essential |
    | email    | string | should be a valid email | optional  |
    | gender   | string | male/female             | optional  |
    | birthday | string | yy/mm/dd                | optional  |
    | phone    | string | should be a valid phone | optional  |
  
  - Response:
    - `200`:
 
    | Name    | Type   | Description  |
    | ------- | ------ | ------------ |
    | token | string    |             |
    | user  | string    | [User]      |
        
    - `403 Forbidden`: 用户名已注册


- `api/user/login` : 用户登录
  
  - Method: `Post`

  - Params:

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
    - `400`:
     ```
    密码错误
    ```
  
- `api/user/logout` : 用户登出
  
  - Method: `Post`
  
  - Parameters:

  - Response:
    - `200`: 成功登出


- `api/users` : 删除用户
  
  - Method: `Delete`
  
  - Parameters:

    | Name     | Type   | Description             |
    | -------- | ------ | ----------------------- |
    | id       | long   |                         |
  
  - Response:
    - `200`: 成功删除
    - `500`: 后台数据库出错
    - `400`: 没有该用户
    
    
- `api/users/:id` : 获取详细信息
    
    - Method: `Get`
  
    - Parameters:
  
    - Response:
        - `200`: body中是`[User]`
        - `400`: 没有该用户


- `api/users/:id/permission` : 获取用户权限
  
  - Method: `Get`
  
  - Parameters:
  
  - Response:
    - `200`:

      | Name      | Type   | Description             |
      | --------  | ------ | ----------------------- |
      | permission| string | q/a                     |
    - `400`: 没有该用户
    

- `api/users/:id` : 修改详细信息
  
  - Method: `Put`
  
  - RequestBody:

    | Name     | Type   | Description                       |
    | -------- | ------ | -----------------------           |
    | username | string | not empty, unique       `optional`|
    | nickname | string | not empty               `optional`|
    | email    | string | should be a valid email `optional`|
    | gender   | string | male/female             `optional`|
    | birthday | string | yy/mm/dd                `optional`|
  
  - Response:
    - `200`:
    - `400`: 没有该用户


- `api/users/:id/password` : 修改密码
  
  - Method: `Put`
  
  - Parameters:

    | Name      | Type   | Description             |
    | --------  | ------ | ----------------------- |
    | origin    | string |                         |
    | password  | string |                         |
  
  - Response:
    - `200`: 修改成功
    - `400`: 没有该用户
    - `403`: 原密码不正确


- `api/users/:id/permission` : 修改用户权限
    ```
  TODO
  ```


- `api/users/:id/apply` : 申请成为回答者

    ```
  TODO
  ```


- `api/users/:id/income` : 查询收入

    ```
  TODO
  ```


- `api/users` : 用户列表
  
  - Method: `Get`
  
  - Parameters:

    | Name     | Type      | Description      |
    | -------  | ------    | ------------     |
    | answerer | boolean   | true/false       |
    | page     | int       |                  |
    | maxitem  | int       |                  |
  

  - Response:
    - `200`:
      
      | Name     | Type      | Description      |
      | -------  | ------    | ------------     |
      | userlist | string    | list of `[User]` |
    
    