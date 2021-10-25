# IM API

## Model

### Message

| 属性        | 类型   | JSON | 说明                 |
| ----------- | ------ | ------ | -----------------|
| senderId    | long   |        |  发送者的id       |
| receiverId  | long   |        |  接收者的id       |
| content     | String |        |  图片或文字消息    |
| type        | enum   | String |  {IMG, TXT}     |

#### 图片格式

- 图片缩略图，格式为 `JPG`，大小建议为 5k，最大不超过 80k，注意在 Base64 进行 Encode 后需要将所有 `\r\n` 和 `\r` 和 `\n` 替换成空。
    - ref [缩略图生成逻辑](https://support.rongcloud.cn/ks/MTA3MQ==)


## API

### 发送

```
POST  /api/im
```

请求体：

| 属性        | 类型   | JSON | 说明                 |
| ----------- | ------ | ------ | -----------------|
| senderId    | long   |        |  发送者的id       |
| receiverId  | long   |        |  接收者的id       |
| roomId      | long   |        |  聊天室id         |  
| content     | String |        |  图片或文字消息    |
| type        | enum   | String |  {IMG, TXT}     |


返回值：

- `200` OK
- `400` 格式错误
- `403` 错误


### 历史记录

```
GET   /api/im
```

请求体：

 | 属性         | 类型    | JSON   | 说明             |
 | ----------- | ------ | ------ | -----------------|
 | roomId      | long   |        |  会话id          |


返回值：

- `200` OK   `{ msgs: [...] }`
- `400` 格式错误
- `403` 错误