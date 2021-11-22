# 上传文件/图片 API

## API

### 上传文件

```
POST /api/orders/{id}/attachments
```

参数：

| 名称      | 类型           | 说明     |
| -------- | ------        | -------  |
| file     | MultipartFile |          |


### 上传图片

```
POST /api/orders/{id}/pictures
```
参数：

| 名称      | 类型           | 说明     |
| -------- | ------        | -------  |
| pic     | MultipartFile |          |

返回：
- 200 uuid
- 403没有权限


### 上传头像

```
POST /api/users/{id}/avatar
```

参数：

| 名称      | 类型           | 说明     |
| -------- | ------        | -------  |
| multiPartFile     | MultipartFile |          |

返回：

- 403 文件格式错误

### 获得头像

```
GET  /api/users/{id}/avatar
```