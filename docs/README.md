# 机器人服务生开放接口文档

## 调用方式

### 请求结构

#### 接入地址

开放接口服务的接入地址为：`https://open-api.yunjiai.cn`
测试环境接口服务的接入地址为：`https://dev-open-api.yunjiai.cn`

#### 通信协议

为了保证通信的安全性，开放接口服务仅支持HTTPS安全通道发送请求。

#### HTTP请求方法

接口支持HTTP POST,GET方法发送请求,其中GET请求URL时，须对URL进行urlencode。

#### 请求参数

每个请求都需要指定如下信息：
- 操作接口所特有的请求参数。

### 公共参数

#### 公共请求参数
| 名称           | 类型   | 是否必需 | 描述                                                                                     |
| -------------- | ------ | -------- | ---------------------------------------------------------------------------------------- |
| signature      | String | 是       | 消息签名                                                                                 |
| signatureNonce | String | 是       | 唯一随机数                                                                               |
| accessKeyId    | String | 是       | 访问秘钥                                                                                 |
| timestamp      | String | 是       | 请求时间戳，日期格式按照ISO8601标准表示，并需要使用UTC时间。格式为：YYYY-MM-DDThh:mm:ssZ |
#### 公共返回参数
| 名称      | 类型   | 描述                                                                              |
| --------- | ------ | --------------------------------------------------------------------------------- |
| requestId | String | 请求ID.无论成功与否，系统都会返回一个唯一的识别码给用户。此参数用于识别每一次请求 |

#### 请求示例

#### 返回示例
```json
｛
    "requestId":"0139d33c-5204-4a6a-8830-9947c6bee3c0"
 ｝
```
### 计算签名
开放平台会对每一个访问的请求进行身份认证，每一个请求都需要在请求中包含签名信息。通过机器人服务生系统颁发的`accessKeyId`和`accessKeySecret`进行对称加密的方法来验证请求的发送者身份，其中accessKeyId用于标识访问者的身份，accessKeySecret用于加密签名字符串和服务器端验证签名字符串的秘钥，请开发者妥善保管，以防泄露。
#### 操作步骤
- 使用请求参数构造规范化的请求字符串，特别注意以下规则：
  - 对参数按照key=value的格式，并按照参数名ASCII字典序，排序参数包括公共参数和接口自定义参数。不包括公共请求参数中的**signature**参数；
  - 如果参数的值为空不参与签名；
  - 参数名区分大小写；
- 计算签名HMAC值。注意计算签名时使用的Key就是用户持有的`accessKeySecret`并加上一个“&”字符,使用的哈希算法是SHA1。
- 按照Base64编码规则将HMAC值编码成字符串，得到签名值。
- 将签名值作为`signature`添加到请求参数中。
#### 执行示例
以创建用户uid为例，假设传送的参数如下：
```json
{
    "uidKey":"0802",
    "signatureNonce": "53c593e7-766d-4646-8b58-0b795ded0ed6",
    "accessKeyId": "testid",
    "timestamp": "2019-10-10T08:26:01Z"
}
```
- 对参数按照key=value的格式，并按照参数名ASCII字典序排序如下：
    `accessKeyId=testid&signatureNonce=53c593e7-766d-4646-8b58-0b795ded0ed6&timestamp=2019-10-10T08:26:01Z&uidKey=0802`

假设accessKeyId为`testId`，accessKeySecret为`testsecret`,则用于计算的HMAC的key为：`testsecret&`。
计算得到的签名值为：`VQAdXELyv7rl/6E4bj1VdV4X6vI=`

最终得到的发送数据为：
```json
{
    "uidKey":"0802",
    "signature": "VQAdXELyv7rl/6E4bj1VdV4X6vI=",
    "signatureNonce": "53c593e7-766d-4646-8b58-0b795ded0ed6",
    "accessKeyId": "testId",
    "timestamp": "2019-10-10T08:26:01Z",
}
```
相关语言生成验签示例：

| 实现语言   | 文件                                                                                             |
| ---------- | ------------------------------------------------------------------------------------------------ |
| Java       | [JavaSign.java](https://github.com/yunji-ai/open-api/blob/master/examples/JavaSign.java)         |
| PHP        | [PHPSign.php](https://github.com/yunji-ai/open-api/examples/PHPSIgn.php)                         |
| .Net       | [.NetSign.cs](https://github.com/yunji-ai/open-api/examples/.NetSign.cs)                         |
| JavaScript | [JavaScriptSign.js](https://github.com/yunji-ai/open-api/blob/master/examples/JavaScriptSign.js) |
| Python     | [pythonSign.py](https://github.com/yunji-ai/open-api/blob/master/examples/pythonSign.py)         |
| C          | [CSign.c](https://github.com/yunji-ai/open-api/examples/CSign.c)                                 |
| C++        | [C++Sign.cc](https://github.com/yunji-ai/open-api/examples/C++Sign.cc)                           |
| Go         | [goSign.go](https://github.com/yunji-ai/open-api/examples/goSign.go)                           |

### 返回结果
#### 成功结果
调用开放平台API后，如果返回的HTTP状态码为:`2xx`,代表调用成功。
- 返回示例
```json
{
    "requestId":"0139d33c-5204-4a6a-8830-9947c6bee3c0"
}
```
#### 错误结果
如果返回的HTTP状态码为: `4xx`或`500`,代表调用失败，系统将不会返回结果数据。此时，返回的消息体中包含:具体的错误代码、错误信息、全局唯一的请求id.

- 返回示例

```json
  {
      "requestId":"0139d33c-5204-4a6a-8830-9947c6bee3c0",
      "code":10086,
      "message":"action is not valid"
  }
```
## 订单
### 创建商品订单
根据所选商品id创建订单，返回订单唯一标识符
> POST /v1/order/createPoiOrder

#### 请求参数
| 名称          | 类型                          | 是否必选 | 示例值                   | 描述                                 |
| ------------- | ----------------------------- | -------- | ------------------------ | ------------------------------------ |
| userId        | String                        | 是       | 5a38d03a60b6286d9c544f58 | 用户唯一标识，可通过创建用户接口创建 |
| storeId       | String                        | 是       | 5a38d03a60b6286d9c544f58 | 酒店唯一标识，账户分配               |
| orderFormInfo | List&lt;OrderFormInfoType&gt; | 是       |                          | 订单信息                             |
| thirdPayType  | Int                           | 是       | 3                        | 3：预付                              |
| amount        | Decimal                       | 是       | 12.0                     | 订单实际支付总金额                   |
| contactInfo   | ContactInfoType               | 是       |                          | 订单联系人信息                       |

#### OrderFormInfoType参数说明
| 名称      | 类型   | 是否必选 | 示例值                   | 描述         |
| --------- | ------ | -------- | ------------------------ | ------------ |
| productId | String | 是       | 5a38d03a60b6286d9c544f58 | 商品Id       |
| quantity  | Int    | 是       | 1                        | 商品购买数量 |

#### ContactInfoType参数说明
| 名称             | 类型   | 是否必选 | 示例值      | 描述                                                             |
| ---------------- | ------ | -------- | ----------- | ---------------------------------------------------------------- |
| recipientAddress | String | 是       | 0701        | 酒店唯一房间编号，应与机器人点位一一对应，如对应不上，将返回报错 |
| recipientPhone   | String | 否       | 16612561256 | 预订人手机号                                                     |
| recipientName    | String | 否       | 张三        | 预订人姓名                                                       |

#### 返回数据
| 名称      | 类型          | 示例值                               | 描述     |
| --------- | ------------- | ------------------------------------ | -------- |
| requestId | String        | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id   |
| orderInfo | OrderInfoType |                                      | 订单信息 |

#### OrderInfoType参数说明
| 名称           | 类型                          | 示例值               | 描述         |
| -------------- | ----------------------------- | -------------------- | ------------ |
| orderSN        | Long                          | 223445452233         | 订单号       |
| createDate     | String                        | 2019-10-01T12:00:18Z | 创建时间     |
| orderTaskInfos | List&lt;OrderTaskInfoType&gt; |                      | 订单任务信息 |

#### OrderTaskInfoType参数说明
| 名称        | 类型   | 示例值                   | 描述         |
| ----------- | ------ | ------------------------ | ------------ |
| orderTaskId | String | 5a38d03a60b6286d9c544f58 | 订单子任务Id |
| createDate  | String | 2019-10-01T12:00:18Z     | 任务创建时间 |


#### 示例
请求示例
```json
{
    "userId": "5a38d03a60b6286d9c544f58",
    "storeId": "5a38d03a60b6286d9c544f58",
    "amount": 12.0,
    "orderFormInfo": [
        {
            "productId": "5a38d03a60b6286d9c544f58",
            "quantity": 1
        }
    ],
    "thirdPayType": 3,
    "contactInfo": {
        "recipientAddress": "0821",
    }
    /* 公共请求参数 */
}
```

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "orderInfo": {
        "orderSN": 223445452233,
        "createDate": "2019-10-01T12:00:18Z",
         "orderTaskInfos": [
            {
                "orderTaskId": "5a38d03a60b6286d9c544f58",
                "createDate": "2019-10-01T12:00:18Z",
            }
        ],
    }
}
```

### 根据订单号查询订单
> GET /v1/order/queryByOrderSN

#### 请求参数
| 名称    | 类型 | 是否必选 | 示例值     | 描述   |
| ------- | ---- | -------- | ---------- | ------ |
| orderSN | Long | 是       | 1274692748 | 订单号 |

#### 返回参数
| 名称      | 类型          | 示例值                               | 描述   |
| --------- | ------------- | ------------------------------------ | ------ |
| requestId | String        | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id |
| orderInfo | OrderInfoType |                                      | 订单ID |
#### OrderInfoType参数说明
| 名称           | 类型                          | 示例值               | 描述                             |
| -------------- | ----------------------------- | -------------------- | -------------------------------- |
| orderSN        | Long                          | 1467803671           | 订单唯一标识                     |
| orderName      | String                        | 矿泉水 * 1           | 订单名称                         |
| createDate     | String                        | 2019-10-01T12:00:18Z | 订单创建时间                     |
| orderStatus    | Int                           | 0                    | 订单若无退订，订单最终状态为成交 |
| orderItemInfos | List&lt;OrderItemInfoType&gt; |                      | 订单项信息                       |
| orderTaskInfos | List&lt;OrderTaskInfoType&gt; |                      | 订单任务信息                     |
| orderBasicInfo | OrderBasicInfoType            |                      | 订单基本信息                     |
| extendProperty | ExtendPropertyType            |                      | 订单扩展信息                     |


#### OrderItemInfoType参数说明
| 名称        | 类型    | 示例值                                              | 描述                                |
| ----------- | ------- | --------------------------------------------------- | ----------------------------------- |
| imageUrl    | String  | http://images.sp.yunjichina.com.cn/goods/757a19.png | 图片地址                            |
| productId   | String  | 5a38d03a60b6286d9c544f58                            | 商品id                              |
| productName | String  | 矿泉水                                              | 商品名称                            |
| productType | Int     | 1                                                   | 商品类型:1-普通商品,2-赠品,3-客需品 |
| amount      | Decimal | 3.00                                                | 商品总卖价                          |
| quantity    | Int     | 2                                                   | 数量                                |
| unitPrice   | Decimal | 150                                                 | 单价，单位为分                      |

#### OrderTaskInfoType参数说明
| 名称                 | 类型                   | 示例值                   | 描述                                                                   |
| -------------------- | ---------------------- | ------------------------ | ---------------------------------------------------------------------- |
| orderTaskId          | String                 | 5a38d03a60b6286d9c544f58 | 订单子任务Id                                                           |
| detail               | List&lt;DetailType&gt; |                          | 备注                                                                   |
| processStatus        | Int                    | 1                        | 任务状态                                                               |
| taskType             | Int                    | 1                        | 任务类型,0-发货员协同派送 1-机器人自动派送 2-货柜机器人派送 3-无需派送 |
| admissionCertificate | String                 | 1923                     | 数字凭证或取物码，当商户在后台配置商品需通过取物码取货时，该字段将返回 |
| createDate           | String                 | 2019-10-01T12:00:18Z     | 任务创建时间                                                           |

#### DetailType参数说明
| 名称        | 类型   | 示例值                                              | 描述         |
| ----------- | ------ | --------------------------------------------------- | ------------ |
| productId   | String | 5a38d03a60b6286d9c544f58                            | 商品唯一标识 |
| productName | String | 矿泉水                                              | 商品名称     |
| imageUrl    | String | http://images.sp.yunjichina.com.cn/goods/757a19.png | 商品缩略图   |
| quantity    | Int    | 1                                                   | 商品数量     |

#### OrderBasicInfoType参数说明
| 名称   | 类型    | 示例值 | 描述               |
| ------ | ------- | ------ | ------------------ |
| amount | Decimal | 1.50   | 原始下单总价(不变) |

#### ExtendPropertyType参数说明
| 名称             | 类型   | 示例值      | 描述                               |
| ---------------- | ------ | ----------- | ---------------------------------- |
| recipientAddress | String | 0801        | 房间号                             |
| recipientPhone   | String | 12312341234 | 收货人电话                         |
| recipientName    | String | 张三        | 若用户没有填写姓名，此字段默认为空 |

#### 订单状态对应关系
| 值  | 描述   |
| --- | ------ |
| 0   | 未付款 |
| 1   | 待发货 |
| 2   | 待收货 |
| 3   | 已收货 |
| 5   | 已取消 |
| 6   | 退款中 |
| 7   | 已退款 |
| 8   | 已关闭 |


#### 任务状态对应关系
| 值  | 描述       |
| --- | ---------- |
| 0   | 排队中     |
| 1   | 送货中     |
| 3   | 任务取消   |
| 4   | 用户未取物 |
| 5   | 取货中     |
| 6   | 已取货     |
| 7   | 任务异常   |
| 8   | 任务失败   |

#### 示例
请求参数

https://open-api.yunjiai.cn/v1/order/queryByOrderSN?<br/>
orderSN=32938472<br/>
&signatureNonce=349sjf2j334j<br/>
&timestamp=1243324234<br/>
&sign=39bcfd48c3dd6fbcc19eead125917971e9bf2d61<br/>
&accessKeyId=c0a55b403ac0f7ac9e63c93ced<br/>

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "orderInfo": {
        "orderSN": 223445452233,
        "orderName": "矿泉水 * 1",
        "createDate": "2019-10-01T12:00:18Z",
        "orderStatus": 2,
        "orderItemInfos": [
            {
                "imageUrl": "http://images.sp.yunjichina.com.cn/goods/sd23.png",
                "productId": "5a38d03a60b6286d9c544f58",
                "productName": "矿泉水",
                "amount": 5.00,
                "quantity": 2,
                "unitPrice": 250
            }
        ],
        "orderTaskInfos": [
            {
                "orderTaskId": "5a38d03a60b6286d9c544f58",
                "processStatus": 1,
                "taskType": 1,
                "admissionCertificate": 1212,
                "detail": [
                    {1
                      "productId": "5a38d03a60b6286d9c544f58",
                      "productName": "矿泉水",
                      "imageUrl": "http://images.sp.yunjichina.com.cn/goods/s.png",
                      "quantity": 1
                    }
                ],
                "createDate": "2019-10-01T12:00:18Z",
            }
        ],
        "orderBasicInfos": {
            "amount": 5.00,
        },
        "extendProperty": {
            "recipientAddress": "0801",
        }
    }
}
```

### 根据用户id查询订单详情
> GET /v1/order/queryByUserId

#### 请求参数
| 名称   | 类型   | 是否必选 | 示例值                             | 描述         |
| ------ | ------ | -------- | ---------------------------------- | ------------ |
| userId | String | 是       | 12740139d33520a68947c6bee3c0692748 | 用户唯一标识 |

#### 返回参数
| 名称          | 类型                      | 示例值                               | 描述             |
| ------------- | ------------------------- | ------------------------------------ | ---------------- |
| requestId     | String                    | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id           |
| orderInfoList | List&lt;OrderInfoType&gt; |                                      | 查询订单详情集合 |
#### OrderInfoType参数说明
| 名称           | 类型                          | 示例值               | 描述                             |
| -------------- | ----------------------------- | -------------------- | -------------------------------- |
| orderSN        | Long                          | 1467803671           | 订单唯一标识                     |
| orderName      | String                        | 矿泉水 * 1           | 订单名称                         |
| createDate     | String                        | 2019-10-01T12:00:18Z | 订单创建时间                     |
| orderStatus    | Int                           | 0                    | 订单若无退订，订单最终状态为成交 |
| orderItemInfos | List&lt;OrderItemInfoType&gt; |                      | 订单项信息                       |
| orderTaskInfos | List&lt;OrderTaskInfoType&gt; |                      | 订单任务信息                     |
| orderBasicInfo | OrderBasicInfoType            |                      | 订单基本信息                     |
| extendProperty | ExtendPropertyType            |                      | 订单扩展信息                     |


#### OrderItemInfoType参数说明
| 名称        | 类型    | 示例值                                              | 描述                                |
| ----------- | ------- | --------------------------------------------------- | ----------------------------------- |
| imageUrl    | String  | http://images.sp.yunjichina.com.cn/goods/757a19.png | 图片地址                            |
| productId   | String  | 5a38d03a60b6286d9c544f58                            | 商品id                              |
| productName | String  | 矿泉水                                              | 商品名称                            |
| productType | Int     | 1                                                   | 商品类型:1-普通商品,2-赠品,3-客需品 |
| amount      | Decimal | 3.00                                                | 商品总卖价                          |
| quantity    | Int     | 2                                                   | 数量                                |
| unitPrice   | Decimal | 150                                                 | 单价,单位为分                       |

#### OrderTaskInfoType参数说明
| 名称                 | 类型                   | 示例值                   | 描述                                                                   |
| -------------------- | ---------------------- | ------------------------ | ---------------------------------------------------------------------- |
| orderTaskId          | String                 | 5a38d03a60b6286d9c544f58 | 订单子任务Id                                                           |
| detail               | List&lt;DetailType&gt; |                          | 备注                                                                   |
| processStatus        | Int                    | 1                        | 任务状态                                                               |
| taskType             | Int                    | 1                        | 任务类型,0-发货员协同派送 1-机器人自动派送 2-货柜机器人派送 3-无需派送 |
| admissionCertificate | String                 | 1923                     | 数字凭证或取物码，当商户在后台配置商品需通过取物码取货时，该字段将返回 |

#### DetailType参数说明
| 名称        | 类型   | 示例值                                              | 描述         |
| ----------- | ------ | --------------------------------------------------- | ------------ |
| productId   | String | 5a38d03a60b6286d9c544f58                            | 商品唯一标识 |
| productName | String | 矿泉水                                              | 商品名称     |
| imageUrl    | String | http://images.sp.yunjichina.com.cn/goods/757a19.png | 商品缩略图   |
| quantity    | Int    | 1                                                   | 商品数量     |

#### OrderBasicInfoType参数说明
| 名称   | 类型    | 示例值 | 描述               |
| ------ | ------- | ------ | ------------------ |
| amount | Decimal | 1.50   | 原始下单总价(不变) |

#### ExtendPropertyType参数说明
| 名称             | 类型   | 示例值      | 描述                               |
| ---------------- | ------ | ----------- | ---------------------------------- |
| recipientAddress | String | 0801        | 房间号                             |
| recipientPhone   | String | 12312341234 | 收货人电话                         |
| recipientName    | String | 张三        | 若用户没有填写姓名，此字段默认为空 |

#### 订单状态对应关系
| 值  | 描述   |
| --- | ------ |
| 0   | 未付款 |
| 1   | 待发货 |
| 2   | 待收货 |
| 3   | 已收货 |
| 5   | 已取消 |
| 6   | 退款中 |
| 7   | 已退款 |
| 8   | 已关闭 |


#### 任务状态对应关系
| 值  | 描述       |
| --- | ---------- |
| 0   | 排队中     |
| 1   | 送货中     |
| 3   | 任务取消   |
| 4   | 用户未取物 |
| 5   | 取货中     |
| 6   | 已取货     |
| 7   | 任务异常   |
| 8   | 任务失败   |

#### 示例
请求参数

https://open-api.yunjiai.cn/v1/order/queryByOrderSN?<br/>
orderSN=32938472<br/>
&signatureNonce=349sjf2j334j<br/>
&timestamp=1243324234<br/>
&sign=39bcfd48c3dd6fbcc19eead125917971e9bf2d61<br/>
&accessKeyId=c0a55b403ac0f7ac9e63c93ced<br/>

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "orderInfoList": [{
        "orderSN": 223445452233,
        "orderName": "矿泉水 * 1",
        "createDate": "2019-10-01T12:00:18Z",
        "orderStatus": 2,
        "orderItemInfos": [
            {
                "imageUrl": "http://images.sp.yunjichina.com.cn/goods/sd23.png",
                "productId": "5a38d03a60b6286d9c544f58",
                "productName": "矿泉水",
                "amount": 5.00,
                "quantity": 2,
                "unitPrice": 250
            }
        ],
        "orderTaskInfos": [
            {
                "orderTaskId": "5a38d03a60b6286d9c544f58",
                "processStatus": 1,
                "taskType": 1,
                "admissionCertificate": 1212,
                "detail": {
                    "productId": "5a38d03a60b6286d9c544f58",
                    "productName": "矿泉水",
                    "imageUrl": "http://images.sp.yunjichina.com.cn/goods/s.png",
                    "quantity": 1
                }
            }
        ],
        "orderBasicInfos": {
            "amount": 5.00,
        },
        "extendProperty": {
            "recipientAddress": "0801",
        }
    }]
}
```

### 取消订单
如果订单任务处于异常或用户需要进行退款操作，可调用此接口将订单标记为已取消状态。该订单下的所有未配送任务将标记为取消，不再进行配送
> POST /v1/order/cancel

#### 请求参数
| 名称    | 类型   | 是否必选 | 示例值         | 描述         |
| ------- | ------ | -------- | -------------- | ------------ |
| orderSN | Long   | 是       | 223445452233   | 订单唯一标识 |
| reason  | String | 是       | 机器人送货失败 | 取消原因     |

#### 返回数据
| 名称      | 类型   | 示例值                               | 描述                         |
| --------- | ------ | ------------------------------------ | ---------------------------- |
| requestId | String | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id                       |
| code      | Int    | 200                                  | 操作处理结果，成功则返回200  |
| message   | String | SUCCESS                              | 操作信息，错误讲返回错误信息 |

#### 示例
请求示例
```json
{
    "orderSN": 223445452233,
    "reason": "5a38d03a60b6286d9c544f58",
    /* 公共请求参数 */
}
```

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "code": 0,
    "message": "SUCCESS",
}
```

## 商品

### 获取全部商品列表
获取产品列表，注意：支持分页，每页最多返回100条,默认值为20,页码从1开始，默认为第一页
> GET /v1/goods/queryByStore

#### 请求参数
| 名称     | 类型   | 是否必选 | 示例值                   | 描述                   |
| -------- | ------ | -------- | ------------------------ | ---------------------- |
| storeId  | String | 是       | 5a38d03a60b6286d9c544f58 | 酒店唯一标识，账户分配 |
| pageSize | Int    | 否       | 20                       | 条数                   |
| current  | Int    | 否       | 1                        | 当前页数               |

#### 返回参数
| 名称        | 类型                        | 示例值                               | 描述         |
| ----------- | --------------------------- | ------------------------------------ | ------------ |
| requestId   | String                      | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id       |
| productList | List&lt;ProductItemType&gt; |                                      | 产品列表详情 |
| pagination  | PaginationType              |                                      | 分页信息     |

#### ProductItemType参数说明
| 名称        | 类型    | 示例值                                         | 描述               |
| ----------- | ------- | ---------------------------------------------- | ------------------ |
| productName | String  | 矿泉水                                         | 商品名称           |
| productId   | String  | 5a38d03a60b6286d9c544f58                       | 商品唯一标识       |
| imageUrl    | String  | http://images.sp.yunjichina.com.cn/goods/s.png | 产品图片           |
| unitPrice   | Decimal | 1120                                           | 商品单价，单位为分 |
| actualPrice | Decimal | 1120                                           | 实际售价，单位为分 |
| storage     | Int     | 999                                            | 商品库存           |

#### PaginationType参数说明
| 名称     | 类型 | 示例值 | 描述     |
| -------- | ---- | ------ | -------- |
| current  | Int  | 1      | 当前页数 |
| pageSize | Int  | 20     | 条数     |
| total    | Int  | 48     | 总数     |

#### 示例
请求参数

https://open-api.yunjiai.cn/v1/goods/queryByStore?<br />current=1<br />
&pageSize=20<br />
&signatureNonce=349sjf2j334j<br />
&timestamp=1243324234<br />
&sign=39bcfd48c3dd6fbcc19eead125917971e9bf2d61<br />
&accessKeyId=c0a55b403ac0f7ac9e63c93ced<br />
&storeId=5a38d03a60b6286d9c544f58

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "productList": [
        {
            "productName": "矿泉水",
            "productId": "5a38d03a60b6286d9c544f58",
            "imageUrl"::"http://images.sp.yunjichina.com.cn/goods/s.png",
            "unitPrice": 150,
            "actualPrice": 150,
            "storage": 23
        }
    ],
    "pagination": {
        "current": 1,
        "pageSize": 20,
        "total": 99
    }
}
```

### 获取全部分组商品
获取门店所有分组下的分组详情和分组下所包含的商品详情列表
> GET /v1/goods/group/list

#### 请求参数
| 名称    | 类型   | 是否必选 | 示例值                   | 描述                   |
| ------- | ------ | -------- | ------------------------ | ---------------------- |
| storeId | String | 是       | 5a38d03a60b6286d9c544f58 | 门店唯一标识，账户分配 |

#### 返回参数
| 名称             | 类型                             | 示例值                               | 描述         |
| ---------------- | -------------------------------- | ------------------------------------ | ------------ |
| requestId        | String                           | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id       |
| groupProductList | List&lt;GroupProductItemType&gt; |                                      | 产品列表详情 |

#### GroupProductItemType参数说明
| 名称               | 类型                        | 示例值                                         | 描述                                                         |
| ------------------ | --------------------------- | ---------------------------------------------- | ------------------------------------------------------------ |
| groupName          | String                      | 矿泉水                                         | 商品分组名称                                                 |
| groupId            | String                      | 5a38d03a60b6286d9c544f58                       | 商品分组id                                                   |
| groupImageUrl      | String                      | http://images.sp.yunjichina.com.cn/goods/s.png | 如果商品分组下拥有分组图标，则返回该图标url,未设置则返回为空 |
| lowestSellingPrice | Int                         | 0                                              | 该分组下最低销售价格，默认为0，单位为分                      |
| sort               | Int                         | 1                                              | 商品分组排序权重值                                           |
| productList        | List&lt;ProductItemType&gt; |                                                | 分组下商品详情                                               |

#### ProductItemType参数说明
| 名称        | 类型    | 示例值                                         | 描述               |
| ----------- | ------- | ---------------------------------------------- | ------------------ |
| productName | String  | 矿泉水                                         | 商品名称           |
| productId   | String  | 5a38d03a60b6286d9c544f58                       | 商品唯一标识       |
| imageUrl    | String  | http://images.sp.yunjichina.com.cn/goods/s.png | 产品图片           |
| unitPrice   | Decimal | 1120                                           | 商品单价，单位为分 |
| actualPrice | Decimal | 1120                                           | 实际售价，单位为分 |
| storage     | Int     | 999                                            | 商品库存           |


#### 示例
请求参数

https://open-api.yunjiai.cn/v1/goods/queryByStore?
&signatureNonce=349sjf2j334j<br />
&timestamp=1243324234<br />
&sign=39bcfd48c3dd6fbcc19eead125917971e9bf2d61<br />
&accessKeyId=c0a55b403ac0f7ac9e63c93ced<br />
&storeId=5a38d03a60b6286d9c544f58

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "groupProductList": [
        {
            "groupName": "饮品",
            "groupId": "5a38d03a60b6286d9c544f58",
            "groupImageUrl": "http://images.sp.yunjichina.com.cn/goods/s.png",
            "lowestSellingPrice": 0,
            "sort": 1,
            "productList": [
                {
                    "productName": "矿泉水",
                    "productId": "5a38d03a60b6286d9c544f58",
                    "imageUrl"::"http://images.sp.yunjichina.com.cn/goods/s.png",
                    "unitPrice": 150,
                    "actualPrice": 150,
                    "storage": 23
                }
            ],
        }
    ],
}
```

### 获取客用品代码列表
获取客用品分类代码列表。

>GET /v1/goods/guestSuppliesList

#### 返回参数
| 名称              | 类型                              | 示例值                               | 描述               |
| ----------------- | --------------------------------- | ------------------------------------ | ------------------ |
| requestId         | String                            | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id             |
| version           | Int                               | 20190828                             | 当前分类版本号     |
| guestSuppliesList | List&lt;GuestSuppliesListType&gt; |                                      | 客用品分类代码列表 |

#### GuestSuppliesListType参数说明
| 名称       | 类型   | 示例值 | 描述                               |
| ---------- | ------ | ------ | ---------------------------------- |
| code       | Int    | 10001  | 客需品对应类别id                   |
| categoryId | Int    | 1      | 客需品类别id,1-洗护用品;2-生活用品 |
| comment    | String | 沐浴露 | 客需品对应分类名称                 |

#### 示例
请求参数

https://open-api.yunjiai.cn/v1/goods/guestSuppliesList?<br/>
signatureNonce=349sjf2j334j<br/>
&timestamp=1243324234<br/>
&sign=39bcfd48c3dd6fbcc19eead125917971e9bf2d61<br/>
&accessKeyId=c0a55b403ac0f7ac9e63c93ced

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "version": 20190828,
    "guestSuppliesList": [
        {
            "code":10001,
            "categoryId":1,
            "comment":"沐浴露"
        },
        {
            "code":10002,
            "categoryId":1,
            "comment":"洗发水"
        },
        {
            "code":20001,
            "categoryId":2,
            "comment":"免费水"
        }
    ],
}
```

### 根据客用品代码获取对应分类商品列表

根据客用品代码获取对应门店下分类商品列表，注意：支持分页，每页最多返回100条,默认值为20,页码从1开始，默认为第一页。

>GET /v1/goods/queryByGuestSuppliesCode

#### 请求参数
| 名称              | 类型   | 是否必选 | 示例值                   | 描述                   |
| ----------------- | ------ | -------- | ------------------------ | ---------------------- |
| storeId           | String | 是       | 5a38d03a60b6286d9c544f58 | 酒店唯一标识，账户分配 |
| guestSuppliesCode | Int    | 是       | 10001                    | 客用品分类代码         |
| pageSize          | Int    | 否       | 20                       | 条数，默认值20         |
| current           | Int    | 否       | 1                        | 当前页数，默认值1      |

#### 返回参数
| 名称        | 类型                        | 示例值                               | 描述         |
| ----------- | --------------------------- | ------------------------------------ | ------------ |
| requestId   | String                      | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id       |
| productList | List&lt;ProductItemType&gt; |                                      | 产品列表详情 |
| pagination  | PaginationType              |                                      | 分页信息     |

#### ProductItemType参数说明
| 名称        | 类型    | 示例值                                         | 描述               |
| ----------- | ------- | ---------------------------------------------- | ------------------ |
| productName | String  | 矿泉水                                         | 商品名称           |
| productId   | String  | 5a38d03a60b6286d9c544f58                       | 商品唯一标识       |
| imageUrl    | String  | http://images.sp.yunjichina.com.cn/goods/s.png | 产品图片           |
| unitPrice   | Decimal | 1120                                           | 商品单价,单位为分  |
| actualPrice | Decimal | 1120                                           | 实际售价，单位为分 |
| storage     | Int     | 999                                            | 商品库存           |

#### PaginationType参数说明
| 名称     | 类型 | 示例值 | 描述     |
| -------- | ---- | ------ | -------- |
| current  | Int  | 1      | 当前页数 |
| pageSize | Int  | 20     | 条数     |
| total    | Int  | 48     | 总数     |

#### 示例
请求参数

https://open-api.yunjiai.cn/v1/goods/queryByGuestSuppliesCode?<br/>
guestSuppliesCode=10001<br/>
&current=1<br/>
&pageSize=20<br/>
&signatureNonce=349sjf2j334j<br/>
&timestamp=1243324234<br/>
&sign=39bcfd48c3dd6fbcc19eead125917971e9bf2d61<br/>
&accessKeyId=c0a55b403ac0f7ac9e63c93ced<br/>
&storeId=5a38d03a60b6286d9c544f58<br/>

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "productList": [
        {
            "productName": "矿泉水",
            "productId": "5a38d03a60b6286d9c544f58",
            "imageUrl"::"http://images.sp.yunjichina.com.cn/goods/s.png",
            "unitPrice": 150,
            "actualPrice": 150,
            "storage": 23
        }
    ],
    "pagination": {
        "current": 1,
        "pageSize": 20,
        "total": 99
    }
}
```

## 用户

### 创建用户标识

客户可根据自己所拥有的唯一标识(房间号，设备号)生成在平台生成唯一用户标识，如有填写手机号或用户名等需求，可对应输入相应字段进行注册

> POST /v1/user/createUser

#### 请求参数
| 名称        | 类型   | 是否必选 | 示例值                   | 描述                                                                 |
| ----------- | ------ | -------- | ------------------------ | -------------------------------------------------------------------- |
| uidKey      | String | 是       | 0802                     | 用户唯一标志，可以是手机号也可以是房间号，系统将根据改字段生成用户id |
| mobilePhone | String | 否       | 86-178398290238          | 用户手机号                                                           |
| userName    | String | 否       | 张三                     | 指定用户名                                                           |
| storeId     | String | 是       | 5a38d03a60b6286d9c544f58 | 酒店唯一标识，账户分配                                               |

#### 返回参数
| 名称      | 类型   | 示例值                               | 描述         |
| --------- | ------ | ------------------------------------ | ------------ |
| requestId | String | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id       |
| userId    | String | 5a38d03a60b6286d9c544f58             | 用户唯一标识 |

#### 示例
请求参数
```json
{
    "storeId":"5a38d03a60b6286d9c544f58",
    "uidKey": "0802",
     /* 公共请求参数 */
}
```
正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "userId": "5a38d03a60b6286d9c544f58",
}
```

## 地点
## 根据送货类型返回门店下所有点位
> GET /v1/position/query

#### 请求参数
| 名称 | 类型 | 是否必选 | 示例值 | 描述            |
| ---- | ---- | -------- | ------ | --------------- |
| type | Int  | 是       | 1      | 任务类型 1-送物 |

#### 返回参数
| 名称      | 类型                         | 示例值                               | 描述         |
| --------- | ---------------------------- | ------------------------------------ | ------------ |
| requestId | String                       | 0139d33c-5204-4a6a-8830-9947c6bee3c0 | 请求id       |
| positions | List&lt;PositionItemType&gt; |                                      | 点位列表详情 |


#### PositionItemType参数说明
| 名称 | 类型   | 示例值                   | 描述                               |
| ---- | ------ | ------------------------ | ---------------------------------- |
| name | String | 8291                     | 点位名称，唯一标识                 |
| type | String | 5a38d03a60b6286d9c544f58 | 点位类型，0-正常，1-充电桩，2-电梯 |

#### 示例
请求参数

https://open-api.yunjiai.cn/v1/position/query?type=1

正常返回示例
```json
{
    "requestId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "positions": [
        {
            "name": "8291",
            "type": "0",
        }
    ]
}
```

## 消息推送
回调接口统一使用http post或https post方式，但我们十分建议您同时将其配置为https地址，未来会择期要求全部开发者升级到https方式，contentType 为“application/x-www-form-urlencoded”，端口号仅支持80或8080

### 任务异常处理
当服务生系统执行任务时发生了失败或异常时，如在后台管理系统配置了该消息的回调接口，将推送该消息。

#### 推送参数
| 名称       | 类型   | 示例值                   | 描述                                                                 |
| ---------- | ------ | ------------------------ | -------------------------------------------------------------------- |
| storeId    | String | 5a38d03a60b6286d9c544f58 | 用户唯一标志，可以是手机号也可以是房间号，系统将根据改字段生成用户id |
| orderSN    | Long   | 223445452233             | 订单唯一标识                                                         |
| taskId     | String | 5a38d03a60b6286d9c544f58 | 失败任务id                                                           |
| errorCode  | Int    | 1290                     | 失败错误码                                                           |
| errorMsg   | String | 出货失败                 | 失败错误信息                                                         |
| createTime | Long   | 1575629914274            | 推送时间戳，格式为UTC                                                |

#### 推送示例
```json
{
    "storeId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "orderSN": 223445452233,
    "taskId": "5a38d03a60b6286d9c544f58",
    "errorCode": 10086,
    "errorMsg": "出货失败",
    "createTime":1575629914274
}
```

### 到达目的地回调通知
当任务为机器人派送时，如果在后台管理系统配置了该消息的回调接口，将推送该消息

#### 推送参数
| 名称       | 类型   | 示例值                   | 描述                                                                 |
| ---------- | ------ | ------------------------ | -------------------------------------------------------------------- |
| storeId    | String | 5a38d03a60b6286d9c544f58 | 用户唯一标志，可以是手机号也可以是房间号，系统将根据改字段生成用户id |
| orderSN    | Long   | 223445452233             | 订单唯一标识                                                         |
| taskId     | String | 5a38d03a60b6286d9c544f58 | 失败任务id                                                           |
| createTime | Long   | 1575629914274            | 推送时间戳，格式为UTC                                                |

#### 推送示例
```json
{
    "storeId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "orderSN": 223445452233,
    "taskId": "5a38d03a60b6286d9c544f58",
    "createTime":1575629914274
}
```
### 订单下货通知
当任务为货柜任务并开始执行货柜下货任务时，如果在后台管理系统配置了该消息的回调接口，将推送该消息

#### 推送参数
| 名称       | 类型   | 示例值                   | 描述                                                                 |
| ---------- | ------ | ------------------------ | -------------------------------------------------------------------- |
| storeId    | String | 5a38d03a60b6286d9c544f58 | 用户唯一标志，可以是手机号也可以是房间号，系统将根据改字段生成用户id |
| orderSN    | Long   | 223445452233             | 订单唯一标识                                                         |
| taskId     | String | 5a38d03a60b6286d9c544f58 | 失败任务id                                                           |
| createTime | Long   | 1575629914274            | 推送时间戳，格式为UTC                                                |

#### 推送示例
```json
{
    "storeId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "orderSN": 223445452233,
    "taskId": "5a38d03a60b6286d9c544f58",
    "createTime":1575629914274
}
```
### 订单任务完成通知
当任务完成时，如果在后台管理系统配置了该消息的回调接口，将推送该消息

#### 推送参数
| 名称       | 类型   | 示例值                   | 描述                                                                 |
| ---------- | ------ | ------------------------ | -------------------------------------------------------------------- |
| storeId    | String | 5a38d03a60b6286d9c544f58 | 用户唯一标志，可以是手机号也可以是房间号，系统将根据改字段生成用户id |
| orderSN    | Long   | 223445452233             | 订单唯一标识                                                         |
| taskId     | String | 5a38d03a60b6286d9c544f58 | 失败任务id                                                           |
| createTime | Long   | 1575629914274            | 推送时间戳，格式为UTC                                                |

#### 推送示例
```json
{
    "storeId": "0139d33c-5204-4a6a-8830-9947c6bee3c0",
    "orderSN": 223445452233,
    "taskId": "5a38d03a60b6286d9c544f58",
    "createTime":1575629914274
}
```


## 错误码
| 状态码 | 错误码                   | 错误说明                     |
| ------ | ------------------------ | ---------------------------- |
| 10400  | SystemParametersIndeed   | 系统参数缺失                 |
| 10501  | RemoteError              | 调用接口失败                 |
| 10502  | TimestampExpire          | 请求时间戳已经过期           |
| 10503  | CalculatedSignatureError | 计算签名错误                 |
| 11001  | GoodsOverdue             | 商品已下架                   |
| 11002  | OffSalePeriod            | 商品不在销售时间段售卖       |
| 11003  | InsufficientInventory    | 商品库存不足                 |
| 11004  | ExceededPurchaseLimit    | 超出商品限购次数             |
| 11005  | ProductDoesNotExist      | 商品不存在                   |
| 12001  | MissingOrderParameters   | 订单房间号缺失               |
| 12002  | MissingOrderParameters   | 订单联系人信息缺失           |
| 12003  | OrderParameterError      | 订单金额小于门店起送金额     |
| 12004  | OrderParameterError      | 订单金额小于商品分类起送金额 |