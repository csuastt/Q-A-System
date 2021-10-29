# 系统参数

## 默认值

```json
{
    "minPrice": 1,    // ≥ 0
    "maxPrice": 100,  // ≥ minPrice
    "respondExpirationMilliseconds": 259200000,  // 3 days 
    "answerExpirationMilliseconds": 86400000,    // 1 day
    "maxChatMessages": 9999,              // unlimited, ≥ 2
    "maxChatTimeMilliseconds": 604800000  // 7 days (after answering)
}
```

## API

```
GET /api/config
```

返回值：

- `200` OK
- `401` 未登录

```
PUT /api/config
```

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录
- `403` 无权限（仅限超级管理员）

