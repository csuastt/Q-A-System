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
  |             |        |                         |
  |             |        |                         |

  

## API

- `/api/manager/login`: 管理员登陆

  - Method: `Post`

  - Parameters:

    | Name        | Type   | Description |
    | ----------- | ------ | ----------- |
    | managername | string |             |
    | password    | string |             |

    Response:

    - `200`：成功

    | Name  | Type   | Description |
    | ----- | ------ | ----------- |
    | token | string |             |

    - `400`：失败

- `/api/manager/logout`: 管理员登出

  - Method: `Post`

  - Parameters:

    | Name        | Type   | Description |
    | ----------- | ------ | ----------- |
    | managername | string |             |

  - Response:

    - `200`：成功登出

- `/api/manager/create`: 创建管理员

  - Method: `Post`

  - Parameters:

    | Name        | Type   | Description |
    | ----------- | ------ | ----------- |
    | managername | string |             |
    | permission  | string |             |

  - Response:

    - `200`：成功

    | Name        | Type   | Description |
    | ----------- | ------ | ----------- |
    | managername | string |             |
    | permission  | string |             |
    | password    | string | random init |

    - `400`：失败

- `/api/manager/delete`: 删除管理员

  - Method: `Delete`

  - Parameters:

    | Name        | Type   | Description |
    | ----------- | ------ | ----------- |
    | managername | string |             |

  - Response:

    - `200`：成功

    - `400`：失败

- `/api/manager/:managername`: 获取管理员详细信息

  - Method: `Get`

  - Parameters:

  - Response:

    - `200`：body中是`[manager]`

    - `400`：失败

- `api/manager/:managername/permission` : 获取管理员权限

  - Method: `Get`

  - Parameters:

  - Response:

    - `200`：成功：

      | Name       | Type   | Description            |
      | ---------- | ------ | ---------------------- |
      | permission | string | admin/auditor/observer |

    - `400`：失败

- `/api/manager/:managername/modify/info`: 修改管理员信息

  - Method: `Put`

  - RequestBody:

    | Name  | Type   | Description             |
    | ----- | ------ | ----------------------- |
    | email | string | should be a valid email |
    | phone | string | should be a valid phone |

    Response:

    - `200`: 修改成功
    - `400`: 修改失败

- `/api/manager/:managername/modify/password`: 修改管理员密码

  Method: `Put`

  Parameters:

  | Name     | Type   | Description |
  | -------- | ------ | ----------- |
  | origin   | string |             |
  | password | string |             |

  Response:

  - `200`: 修改成功
  - `400`: 修改失败（密码不符合标准或与原密码一致）

- `/api/manager/:managername/modify/permission`: 修改管理员权限

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

  - Response:

    - `200`:

      | Name        | Type   | Description         |
      | ----------- | ------ | ------------------- |
      | managerlist | string | list of `[manager]` |