# Backend API Description

- 本目录下应包含后端API的使用文档
- 请按照模块名称组织文件
- 若无特殊说明，本项目的后端API调用均为HTTP请求，并使用`json`携带数据

## Example

下面为**可能**的`user.md`的**部分内容**：

- `/api/user/register`: 用户注册
  - Method: `POST`
  - Params:
    
    | Name     | Type   | Description             |
    | -------- | ------ | ----------------------- |
    | username | string |                         |
    | email    | string | should be a valid email |
    | password | string |                         |
  - Response:
    
    | Name    | Type   | Description  |
    | ------- | ------ | ------------ |
    | rt_code | int    |              |
    | err_msg | string | **Optional** |