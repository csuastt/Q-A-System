# 上传文件/图片 API

## API

### 上传文件

```
POST /api/orders/{id}/attachment
```

参数：

| 名称      | 类型           | 说明     |
| -------- | ------        | -------  |
| file     | MultipartFile |          |
| name     | String        | 文件名称  |


### 上传头像

```
POST /api/users/{id}/avatar
```

参数：

| 名称      | 类型           | 说明     |
| -------- | ------        | -------  |
| file     | MultipartFile |          |