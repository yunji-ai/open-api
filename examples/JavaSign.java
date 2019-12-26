package examples;

public class JavaSign {
    public static void main(String[] args) throws Exception {
        // 业务参数
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("uidKey", "0802");
        // 转换为List
        java.util.List<String> paraAllList = new java.util.ArrayList<>();
        map.forEach((k, v) -> {
            if (null != v && v.length() > 0 && !"null".equals(v)) {
                paraAllList.add(k + "=" + v);
            }
        });
        // 公共参数
        paraAllList.add("signatureNonce=53c593e7-766d-4646-8b58-0b795ded0ed6");
        paraAllList.add("accessKeyId=testid");
        paraAllList.add("timestamp=2019-10-10T08:26:01Z");
        // 排序
        Object[] params = paraAllList.toArray();
        java.util.Arrays.sort(params);
        // 签名
        StringBuffer paramBuffer = new StringBuffer();
        for (Object param : params) {
            if (paramBuffer.length() > 0) {
                paramBuffer.append("&");
            }
            paramBuffer.append(String.valueOf(param));
        }
        String accessKeySecret = "testsecret";
        String MAC_NAME = "HmacSHA1";
        String ENCODING = "UTF-8";
        byte[] data = (accessKeySecret + "&").getBytes(ENCODING);
        javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(data, MAC_NAME);
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance(MAC_NAME);
        mac.init(secretKey);
        byte[] text = paramBuffer.toString().getBytes(ENCODING);
        byte[] bytes = mac.doFinal(text);
        String signatureStr = java.util.Base64.getEncoder().encodeToString(bytes);
        System.out.println(signatureStr);
    }
}