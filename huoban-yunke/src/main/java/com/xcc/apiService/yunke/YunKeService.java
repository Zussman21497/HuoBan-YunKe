package com.xcc.apiService.yunke;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONBeanParser;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xcc.config.CustomFieldConfig;
import com.xcc.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YunKeService {
    private static final String PARTNER_ID = "pBA7970178D034E89A27343EAC71490AC";

    private static final String COMPANY = "l92yhy";
    private static final String API_KEY = "49AD762D961F49919883F1";

    @Autowired
    private CustomFieldConfig customFieldConfig;


    public static void main(String[] args) {
        customFields();
//        addCustomer();
//        getUserByPhone();
    }

    public static void customFields() {
        HttpRequest post = HttpUtil.createPost("https://phone.yunkecn.com/open/customer/customFields");
        setHeader(post);
        String body = post.execute().body();
        log.info("customFields:{}", body);
    }

    //增强版addOrUpdate(新增或修改用户)
    public void addOrUpdate(JSONObject userParam) {

        try {

            boolean useProxy = false;//true: 本地; false: 阿里云

            //使用OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (useProxy) {
                builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("192.168.184.139", 1080)));
            }

            OkHttpClient client = builder.build();

            //构建请求JSON数组体
            JSONArray list = new JSONArray();
            userParam.set("type", "sales");
            list.add(userParam);
            String jsonBody = list.toString();

            //构建请求体
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    jsonBody
            );

            //时间戳
            String timestamp = String.valueOf(System.currentTimeMillis());

            Request request = new Request.Builder()
                    .url("https://phone.yunkecn.com/open/user/addOrUpdate")
                    .addHeader("company", COMPANY)
                    .addHeader("partnerId", PARTNER_ID)
                    .addHeader("timestamp", timestamp)
                    .addHeader("sign", getSign(timestamp))
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .post(body)
                    .build();

            //发起请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("调用云客接口失败，Http：{}", response.code());
                } else {
                    String responseBody = response.body() != null ? response.body().string() : "无响应内容";
                    log.info("云客返回结果：{}", responseBody);
                }
            }


        } catch (Exception e) {
            log.error("调用接口 addOrUpdate 异常", e);
        }
    }

    //旧版addOrUpdate（新增用户）
//    public void addOrUpdate(JSONObject params) {
//        HttpRequest post = HttpUtil.createPost("https://phone.yunkecn.com/open/user/addOrUpdate");
//        setHeader(post);
//        params.set("type", "sales");
//        JSONArray list = new JSONArray();
//        list.add(params);
//        post.body(list.toString());
//        String body = post.execute().body();
//        log.info("getUserByPhone:{}", body);
//    }


    //旧版updateCustomer（更新客户）
//    public void updateCustomer(JSONObject params) {
//        HttpRequest post = HttpUtil.createPost("https://phone.yunkecn.com/open/customer/update");
//        setHeader(post);
//        JSONArray list = new JSONArray();
//        JSONObject paramsMap = new JSONObject();
//        paramsMap.put("customerId", params.getStr("customerId"));//客户id
//        paramsMap.put("userId", params.getStr("userId"));//客户id
//        List<String> collaborator = new ArrayList<>();
//        paramsMap.put("collaborators", collaborator);
//        List<Map<String, Object>> customFieldValuesList = new ArrayList<>();
//        Map<String, Object> name = new HashMap<>();
//        name.put("id", "13539");//协作人的用户Id数组
//        name.put("values", Arrays.asList("有待确认"));//协作人的用户Id数组
//        name.put("fieldType", "2");
//        customFieldValuesList.add(name);
//        paramsMap.put("customFieldValues", customFieldValuesList);
//        list.add(paramsMap);
//        post.body(list.toString());
//        log.info("paramsMap:{}", post.toString());
//        String body = post.execute().body();
//        log.info("dataUpdata:{}", body);
//    }

    //更新客户信息
    public void updateCustomer(CustomerDTO dto) {
        try {
            //构建请求参数
            JSONObject param = new JSONObject();

            //客户电话号码
            param.put("phone", dto.getPhone());
            //跟进人id
            param.put("userId", dto.getUserId());
            //客户id
            param.put("customerId", dto.getCustomerId());
            //客户姓名（销售）
            param.put("name", dto.getName());

            //自定义字段构造（是否有效）
            List<Map<String, Object>> customFieldValues = new ArrayList<>();

            if (dto.getIsUseful() != null && !dto.getIsUseful().isEmpty()) {
                String isUsefulId = switch (dto.getIsUseful().get(0)) {
                    case "1" -> "有效线索"; // 有效线索
                    case "2" -> "无效线索"; // 无效线索
                    case "3" -> "有待确认"; // 有待确认
                    default -> null;
                };
                if (isUsefulId != null) {
                    Map<String, Object> isUsefulMap = new HashMap<>();
                    isUsefulMap.put("id", "13539");
                    isUsefulMap.put("values", Collections.singletonList(isUsefulId));
                    isUsefulMap.put("fieldType", 2);
                    customFieldValues.add(isUsefulMap);
                }
            }
            param.put("customFieldValues", customFieldValues);

            //构造对象数组
            JSONArray payload = new JSONArray();
            payload.add(param);

            log.info("即将发送给云客的客户信息 payload: {}", payload.toString());


            sendUpdatedCustomerToYunke(payload);


        } catch (Exception e) {
            log.error("调用updateCustomer接口异常：{}", e);
        }

    }

    //把更新后的客户信息发送到云客
    private void sendUpdatedCustomerToYunke(JSONArray payload) {

        try {


            boolean useProxy = false;//true: 本地; false: 阿里云

            //使用OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (useProxy) {
                builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("192.168.184.139", 1080)));
            }

            OkHttpClient client = builder.build();

            //构造请求体
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    payload.toString()
            );

            String timestamp = String.valueOf(System.currentTimeMillis());

            Request request = new Request.Builder()
                    .url("https://phone.yunkecn.com/open/customer/update")
                    .addHeader("company", COMPANY)
                    .addHeader("partnerId", PARTNER_ID)
                    .addHeader("timestamp", timestamp)
                    .addHeader("sign", getSign(timestamp))
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .post(body)
                    .build();

            //发起请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("调用云客接口失败, HTTP: {}", response.code());
                } else {
                    String responseBody = response.body() != null ? response.body().string() : "无响应内容";
                    log.info("云客返回内容：{}", responseBody);
                }
            }


        } catch (Exception e) {
            log.error("调用updateCustomer异常：{}", e);
        }
    }


//----------------------------------------------------------------------------------------------------------------

    //新增客户
    public void addCustomer(CustomerDTO dto) {
        JSONObject param = new JSONObject();

        // 基础字段
        param.put("name", dto.getName());
        param.put("phone", dto.getPhone());
        param.put("customerId", dto.getCustomerId());
        param.put("userId", dto.getUserId());

        // 自定义字段统一构造
        List<Map<String, Object>> customFieldValues = buildCustomFieldValues(dto);
        param.put("customFieldValues", customFieldValues);

        //封装进对象数组
        JSONArray payload = new JSONArray();
        payload.add(param);

        // 调用发送方法
        sendNewCustomerToYunke(payload);
    }


    //构造自定义字段格式
    private List<Map<String, Object>> buildCustomFieldValues(CustomerDTO dto) {
        List<Map<String, Object>> customFieldValues = new ArrayList<>();

        for (Map.Entry<String, CustomFieldConfig.FieldMeta> entry : customFieldConfig.getFieldMap().entrySet()) {
            String fieldName = entry.getKey();
            CustomFieldConfig.FieldMeta meta = entry.getValue();

            Object value = getFieldValueByName(dto, fieldName);

            if (value == null) continue;

            String text = buildCustomFieldText(meta.getType(), value);
            if (text == null) continue;

            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("id", meta.getId());

            JSONArray values = new JSONArray();
            values.add(text);

            fieldMap.put("values", values);
            customFieldValues.add(fieldMap);

        }

        return customFieldValues;

    }


    // 根据字段名从dto反射或手动映射取值
    private Object getFieldValueByName(CustomerDTO dto, String fieldName) {
        return switch (fieldName) {
            case "跟进人工号" -> dto.getUserName();
            case "联系方式" -> dto.getClue();
            case "录入日期" ->
                    dto.getData() != null ? dto.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
            case "对应平台" -> dto.getPlatform();
            case "客户昵称" -> dto.getNickName();
            case "来源账号" -> dto.getSource();
            case "聊天截图" -> dto.getScreenshot();
            case "其他备注" -> dto.getRemark();
            case "是否有效" -> {
                // 假设dto里是List<String>，转成文字
                if (dto.getIsUseful() != null && !dto.getIsUseful().isEmpty()) {
                    yield switch (dto.getIsUseful().get(0)) {
                        case "1" -> "有效线索";
                        case "2" -> "无效线索";
                        case "3" -> "有待确认";
                        default -> null;
                    };
                } else {
                    yield null;
                }
            }
            case "无效原因" -> dto.getReason();
            default -> null;
        };
    }

    private String buildCustomFieldText(String type, Object value) {
        if (value == null) return "";

        try {
            return switch (type) {
                case "0", "2" -> value.toString();    // 文本/单选
                case "4" -> {
                    if (value instanceof String str) {
                        try {
                            yield LocalDate.parse(str).toString();
                        } catch (DateTimeParseException e) {
                            yield str;
                        }
                    } else {
                        yield value.toString();
                    }
                }
                case "9" -> "附件";                    // 附件
                default -> value.toString();
            };
        } catch (Exception e) {
            return value.toString();
        }
    }


    //把新增的客户信息发送到云客
    private void sendNewCustomerToYunke(JSONArray payload) {

        try {

            log.info("上传到云客的payload：{}", payload.toString());


            boolean useProxy = false;//true：本地; false：阿里云

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (useProxy) {
                builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("192.168.184.139", 1080)));
            }

            OkHttpClient client = builder.build();

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=UTF-8"),
                    payload.toString()
            );

            String timestamp = String.valueOf(System.currentTimeMillis());

            Request request = new Request.Builder()
                    .url("https://phone.yunkecn.com/open/customer/add")
                    .addHeader("company", COMPANY)
                    .addHeader("partnerId", PARTNER_ID)
                    .addHeader("timestamp", timestamp)
                    .addHeader("sign", getSign(timestamp))
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .post(body)
                    .build();

            //发起请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("云客接口调用失败：HTTP：{}", response.code());
                } else {
                    String requestBody = response.body() != null ? response.body().string() : "无响应内容";
                    log.info("云客返回结果：{}", requestBody);
                }
            }

        } catch (Exception e) {
            log.error("调用addCustomer接口出现异常", e);
        }

    }


    //改进版setHeader方法
    public static void setHeader(HttpRequest request) {
        request.header("company", COMPANY);
        request.header("partnerId", PARTNER_ID);

        String timestamp = String.valueOf(System.currentTimeMillis());
        request.header("timestamp", timestamp);
        request.header("sign", getSign(timestamp));
        request.header("Content-Type", "application/json; charset=UTF-8");

    }


    //旧setHeader方法
//    private static void setHeader(HttpRequest request) {
//        request.header("Content-Type", "application/json; charset=utf-8");
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        request.header("partnerId", PARTNER_ID);
//        request.header("company", COMPANY);
//        request.header("key", API_KEY);
//        request.header("timestamp", timestamp);
//        request.header("sign", getSign(timestamp));
//
//    }

//客户昵称(文本字段)
//联系方式(文本字段)
//跟进人员(成员字段)
//聊天截图(图片字段)  9  13469
//线索平台(选项字段)  2  13540
// 来源账号(关联字段) 0  17453
// 录入日期(日期字段) 4  17427
// 线索人员(关联字段) 0  17452

    //旧addCustomer
//        public void addCustomer(JSONObject customer) {
//            HttpRequest post = HttpUtil.createPost("https://phone.yunkecn.com/open/customer/add");
//    //        post.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("124.220.90.55", 7482)));
//            setHeader(post);
//            post.body(customer(customer.getStr("name"), customer.getStr("phone"),
//                    customer.getStr("userName"), customer.getStr("customerId"), customer.getStr("platform"),
//                    customer.getStr("source"), customer.getStr("data"), customer.getStr("clue"), customer.getStr("userName")).toString());
//            String body = post.execute().body();
//            log.info("body:{}", body);
//        }


//    public JSONArray customer(String... args) {
//
//        JSONArray list = new JSONArray();
//        JSONObject paramsMap = new JSONObject();
//        paramsMap.put("company", "");//公司名称
//        paramsMap.put("customerId", args[3]);//客户id
//        paramsMap.put("name", args[0]);//客户姓名.
//
//        boolean mobileNumber = isMobileNumber(args[1]);
//        paramsMap.put("phone",  args[1] );//客户手机号
//        paramsMap.put("position", "");//职位
//        paramsMap.put("userId", args[2]);//跟进人的用户Id
//        paramsMap.put("gender", "");//性别 男/女
//        paramsMap.put("birthday", "");//性别 男/女
//        paramsMap.put("telephone", "");//性别 男/女
//        paramsMap.put("qq", "");//qq
//        paramsMap.put("wechat", mobileNumber? "" :args[1]);//微信号
//        paramsMap.put("address", "");//微信号
//        paramsMap.put("email", "");//微信号
//        paramsMap.put("from", "0");//客户分配：0分配到客户列表（缺省），1分配到公海
//        paramsMap.put("seaId", "");//公海id
//        paramsMap.put("remark", "");//公海id
//        paramsMap.put("dataSource", "致墅设计-抖音【欧式图文】周颖");//客户来源（可自定义）
//
//        //协作人的用户Id数组
//        List<String> collaborators = new ArrayList<>();
//        paramsMap.put("collaborators", collaborators);
//        //自定义字段
//        List<Map<String, Object>> customFieldValuesList = new ArrayList<>();
//        Map<String, Object> name = new HashMap<>();
//        name.put("id", "13801");//协作人的用户Id数组
//        name.put("values", Arrays.asList(args[0]));//协作人的用户Id数组
//        name.put("fieldType", "0");
//        customFieldValuesList.add(name);
//
////        name.put("id", "17482");//协作人的用户Id数组
////        name.put("values", Arrays.asList(args[2]));//协作人的用户Id数组
////        name.put("fieldType", "0");
////        customFieldValuesList.add(name);
//
//        for (int i = 4; i < args.length; i++){
//            Map<String, Object> customFieldValuesMap = new HashMap<>();
//            customFieldValuesMap.put("id", getId(i));//协作人的用户Id数组
//            customFieldValuesMap.put("values", Arrays.asList(args[i]));//协作人的用户Id数组
//            customFieldValuesMap.put("text", "");//协作人的用户Id数组
//            customFieldValuesMap.put("fieldType", getType(i));//自定义字段类型 ，取自 查询自定义字段设置（/open/customer/customFields） 接口的type字段。特殊类型，创建或修改客户信息时，需要回传类型值
//            //其他联系人
//            //当fieldType=6 既其他联系方式类型时，传此参数
////        List<Map<String, Object>> multiContactsList = new ArrayList<>();
////        Map<String, Object> multiContactsMap = new HashMap<>();
////        multiContactsMap.put("optionId", "");//取自 查询自定义字段设置 接口（/open/customer/customFields）的options选项数据的id
////        multiContactsMap.put("optionId", "");//取自 查询自定义字段设置 接口（/open/customer/customFields）的options选项数据的id
////        multiContactsList.add(multiContactsMap);
////        customFieldValuesMap.put("multiContacts", multiContactsList);
//            customFieldValuesList.add(customFieldValuesMap);
//        }
//
//        paramsMap.put("customFieldValues", customFieldValuesList);
//        list.add(paramsMap);
//        return list;
//    }
//
//    private String getId(int i){
//        switch (i){
////            case 3:
////                return "13469";
//            case 4:
//                return "13540";
//            case 5:
//                return "17453";
//            case 6:
//                return "17427";
//            case 8:
//                return "17482";
//            default:
//                return "17452";
//        }
//    }
//
//    private String getType(int i){
//        switch (i){
////            case 3:
////                return "9";
//            case 4:
//                return "2";//单选
//            case 6:
//                return "4";//时间
//            default:
//                return "0";//文字
//        }
//    }

    private static String getSign(String timestamp) {
        String sign = SecureUtil.md5(API_KEY + COMPANY + PARTNER_ID + timestamp).toUpperCase();
        return sign;
    }


//    public static String getSign(String timestamp) {
//        String md = API_KEY+COMPANY+PARTNER_ID+timestamp;
//        return DigestUtil.md5Hex(md).toUpperCase();
//    }

    public boolean isMobileNumber(String number) {
        String regex = "^1[3-9]\\d{9}$";
        return number.matches(regex);
    }
}
