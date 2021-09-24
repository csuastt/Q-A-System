# Backend User API

## Data

- User 用户:
  - `username` unique  

  | Name      | Type   | Description             |
  | --------  | ------ | ----------------------- |
  | username  | string | not empty, unique       |
  | email     | string | should be a valid email |
  | phone     | string | should be a valid phone |
  | password  | string | 6 to 12 in length       |
  | gender    | string | male/female             |
  | birthday  | string | yy/mm/dd                |
  | permission| string | q/a                     |
  | money     | string | `initial` 100           |

## Authentication

- 在所有和用户相关的 HTTP 请求中的 Header 里设置 `Authorization: Bearer <token>`
  - ref [JWT Token](https://jwt.io/introduction)

## API

- `/api/user/register`: 用户注册
  - Method: `POST`
  - RequestBody:

    | Name     | Type   | Description             |
    | -------- | ------ | ----------------------- |
    | username | string | not empty, unique       |
    | email    | string | should be a valid email |
    | password | string | 6 to 12 in length       |
    | gender   | string | male/female             |
    | birthday | string | yy/mm/dd                |
    | phone    | string | should be a valid phone |
  - Response:

    - `200`:
    
    | Name    | Type   | Description  |
    | ------- | ------ | ------------ |
    | token | string    |             |
    | user  | string    | [User]      |
        
    - `403 Forbidden`：已注册


- `api/user/login` : 用户登录
  - Method: `Post`
  - Parameters:

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

- `api/user/logout` : 用户登出
  - Method: `Post`
  - Parameters:

  | Name     | Type   | Description             |
  | -------- | ------ | ----------------------- |
  | username | string |                         | 

  - Response:

    - `200`: 成功登出


- `api/user/delete` : 删除用户
  - Method: `Delete`
  - Parameters:

    | Name     | Type   | Description             |
    | -------- | ------ | ----------------------- |
    | username | string |                         |
  
  - Response:

    - `200`: 成功删除
    
    
- `api/user/:username` : 获取详细信息
    - Method: `Get`
    - Parameters:
    - Response:
        - `200`: body中是`[User]`


- `api/user/:username/permission` : 获取用户权限
  - Method: `Get`
  - Parameters:
  - Response:
    - `200`:

      | Name      | Type   | Description             |
      | --------  | ------ | ----------------------- |
      | permission| string | q/a                     |
    

- `api/user/:username/modify/info` : 修改详细信息
  - Method: `Put`
  - RequestBody:

    | Name     | Type   | Description                       |
    | -------- | ------ | -----------------------           |
    | username | string | not empty, unique       `optional`|
    | email    | string | should be a valid email `optional`|
    | gender   | string | male/female             `optional`|
    | birthday | string | yy/mm/dd                `optional`|
  - Response:
    - `200`:

      | Name    | Type   | Description  |
      | ------- | ------ | ------------ |
      | user  | string    | [User]      |

- `api/user/:username/modify/password` : 修改密码
  - Method: `Put`
  - Parameters:

    | Name      | Type   | Description             |
    | --------  | ------ | ----------------------- |
    | origin    | string |                         |
    | password  | string |                         |
  - Response:
    - `200`: 修改成功

- `api/user/:username/modify/permission` : 修改用户权限
    ```
  TODO
  ```

- `api/user/:username/apply` : 申请成为回答者

    ```
  TODO
  ```

- `api/user/:username/income` : 查询收入

    ```
  TODO
  ```

- `api/users` : 用户列表
  - Method: `Get`
  - Parameters:
  - Response:

    | Name     | Type      | Description      |
    | -------  | ------    | ------------     |
    | userlist | string    | list of `[User]` |


- `api/users/answers` : 回答者列表
  - Method: `Get`
  - Parameters:
  - Response:

    | Name       | Type      | Description        |
    | -------    | ------    | ------------       |
    | answerlist | string    | list of `[Answer]` |