# Backend Manager API

## Data

- manager 管理员：

  | Name        | Type   | Description             |
  | ----------- | ------ | ----------------------- |
  | managername | string | not empty, unique       |
  | password    | string | 6 to 12 in length       |
  | permission  | string | admin/auditor/observer  |
  | email       | string | should be a valid email |
  | phone       | string | should be a valid phone |
  | id          | Long   | unique, auto generated  |
  


## API

- `/api/manager/login`: 管理员登录

  - Method: `Post`

  - Parameters:

    | Name        | Type   | Description |
    | ----------- | ------ | ----------- |
    | managername | string |             |
    | password    | string |             |

    Response:

    - `200`：成功

    | Name    | Type   | Description |
    | ------- | ------ | ----------- |
    | token   | string |             |
    | manager | string | [manager]   |
    
    - `400`：失败：密码错误

- `/api/manager/logout`: 管理员登出

  - Method: `Post`

  - Parameters:

  - Response:
  
    - `200`：成功登出

- `/api/managers`: 创建管理员

  - Method: `Post`

  - RequestBody:

    | Name        | Type   | Description             | Necessity |
    | ----------- | ------ | ----------------------- | --------- |
    | managername | string | not empty, unique       | essential |
    | permission  | string | auditor/observer        | essential |
    | email       | string | should be a valid email | optional  |
    | phone       | string | should be a valid phone | optional  |

  - Response:

    - `200`：成功

    | Name    | Type   | Description |
    | ------- | ------ | ----------- |
    | token   | string |             |
    | manager | string | [manager]   |
    
  - `403 Forbidden   `：管理员名已注册

- `/api/managers`: 删除管理员

  - Method: `Delete`

  - Parameters:

    | Name | Type | Description |
    | ---- | ---- | ----------- |
    | id   | long |             |

  - Response:

    - `200`：成功
  - `500`：后台数据库出错
    - `400`：没有该管理员

- `/api/managers/:id`: 获取管理员详细信息

  - Method: `Get`

  - Parameters:

  - Response:

    - `200`：body中是`[manager]`

    - `400`：失败

- `api/managers/:id/permission` : 获取管理员权限

  - Method: `Get`

  - Parameters:

  - Response:

    - `200`：成功：

      | Name       | Type   | Description            |
      | ---------- | ------ | ---------------------- |
      | permission | string | admin/auditor/observer |

    - `400`：没有该用户

- `/api/managers/:id`: 修改管理员信息

  - Method: `Put`

  - RequestBody:

    | Name        | Type   | Description             |
    | ----------- | ------ | ----------------------- |
    | email       | string | should be a valid email |
    | phone       | string | should be a valid phone |
    | managername | string | not empty, unique       |
    |             |        |                         |

    Response:
    
    - `200`: 修改成功
    - `400`: 没有该用户

- `/api/managers/:id/password`: 修改管理员密码

  Method: `Put`

  Parameters:

  | Name     | Type   | Description |
  | -------- | ------ | ----------- |
  | origin   | string |             |
  | password | string |             |

  Response:

  - `200`: 修改成功
  - `400`: 没有该用户
  - `403`：原密码不正确 

- `/api/managers/:id/permission`: 修改管理员权限

  - Method: `Put`

  - Parameters:·

    | Name        | Type   | Description                   |
    | ----------- | ------ | ----------------------------- |
    | managername | string |                               |
    | permission  | string | permission you want change to |

  - Response:

    - `200`：成功
    - `400`：失败

- `/api/managers`: 查询管理员列表

  - Method: `Get`

  - Parameters:

    | Name    | Type    | Description |
    | ------- | ------- | ----------- |
    | auditor | boolean | true/false  |
    | page    | int     |             |
    | maxitem | int     |             |
  
    
  
  - Response:
  
    - `200`:
  
      | Name        | Type   | Description         |
      | ----------- | ------ | ------------------- |
      | managerlist | string | list of `[manager]` |