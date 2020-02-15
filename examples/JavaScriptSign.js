const CryptoJS = require("crypto-js");
const Base64 = require("crypto-js/enc-base64")

const data = {
    'accessKeyId': 'testid',
    'uidKey': '0802',
    'signatureNonce': '53c593e7-766d-4646-8b58-0b795ded0ed6',
    'timestamp': '2019-10-10T08:26:01Z'
}
const accessKeySecret = 'testsecret'

// 计算排序函数
function objKeySort(data) {
    //先用Object内置类的keys方法获取要排序对象的属性名数组，再利用Array的sort方法进行排序
    const newkey = Object.keys(data).sort();
    let newObj = ''; //创建一个新的对象，用于存放排好序的键值对
    for (let i = 0; i < newkey.length; i++) {
        //遍历newkey数组
        newObj += [newkey[i]] + '=' + data[newkey[i]] + '&';
    }
    return newObj.substring(0, newObj.length - 1);
}

const sortStr = objKeySort(data)
const signature = CryptoJS.HmacSHA1(sortStr, accessKeySecret + '&')
const hmacDigest = Base64.stringify(signature);

console.log("sortStr", sortStr)
console.log("hmacDigest", hmacDigest)