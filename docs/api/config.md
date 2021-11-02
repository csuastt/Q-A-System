# 系统参数

## 默认值

```json
{
    "minPrice": 1,    // ≥ 0
    "maxPrice": 100,  // ≥ minPrice
    "respondExpirationSeconds": 259200,  // 3 days 
    "answerExpirationSeconds": 86400,    // 1 day
    "fulfillExpirationSeconds": 259200,  // 3 days
    "maxChatMessages": 9999,       // unlimited, ≥ 2
    "maxChatTimeSeconds": 604800,  // 7 days (after answering)
    "feeRate": 30  // 0 < feeRate < 100
}
```

## API

### 获取系统设置

（无需鉴权）

```
GET /api/config
```

返回值：

- `200` OK

### 修改系统设置

（仅限超级管理员）

```
PUT /api/config
```

返回值：

- `200` OK
- `400` 格式错误
- `401` 未登录
- `403` 无权限（仅限超级管理员）

### 查询平台收入

（仅限超级管理员）

```
GET /api/config/earnings
```

返回值：

- `200` OK `{ earnings: 0 }`
- `400` 格式错误
- `401` 未登录
- `403` 无权限（仅限超级管理员）
