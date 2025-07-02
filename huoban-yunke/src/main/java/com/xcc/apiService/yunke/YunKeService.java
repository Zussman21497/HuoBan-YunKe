package com.xcc.apiService.yunke;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

@Slf4j
@Service
public class YunKeService {
    private static final String PARTNER_ID = "pBA7970178D034E89A27343EAC71490AC";
    private static final String COMPANY = "l92yhy";
    private static final String API_KEY = "49AD762D961F49919883F1";

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

    public void addOrUpdate(JSONObject params) {
        HttpRequest post = HttpUtil.createPost("https://phone.yunkecn.com/open/user/addOrUpdate");
        setHeader(post);
        params.set("type", "sales");
        JSONArray list = new JSONArray();
        list.add(params);
        post.body(list.toString());
        String body = post.execute().body();
        log.info("getUserByPhone:{}", body);
    }

    public void dataUpdate(JSONObject params) {
        HttpRequest post = HttpUtil.createPost("https://phone.yunkecn.com/open/customer/update");
        setHeader(post);
        JSONArray list = new JSONArray();
        JSONObject paramsMap = new JSONObject();
        paramsMap.put("customerId", params.getStr("customerId"));//客户id
        paramsMap.put("userId", params.getStr("userId"));//客户id
        List<String> collaborator = new ArrayList<>();
        paramsMap.put("collaborators", collaborator);
        List<Map<String, Object>> customFieldValuesList = new ArrayList<>();
        Map<String, Object> name = new HashMap<>();
        name.put("id", "13539");//协作人的用户Id数组
        name.put("values", Arrays.asList("有待确认"));//协作人的用户Id数组
        name.put("fieldType", "2");
        customFieldValuesList.add(name);
        paramsMap.put("customFieldValues", customFieldValuesList);
        list.add(paramsMap);
        post.body(list.toString());
        log.info("paramsMap:{}", post.toString());
        String body = post.execute().body();
        log.info("dataUpdata:{}", body);
    }


    public void addCustomer(JSONObject customer) {
        HttpRequest post = HttpUtil.createPost("https://phone.yunkecn.com/open/customer/add");
//        post.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("124.220.90.55", 7482)));
        setHeader(post);
        post.body(customer(customer.getStr("name"), customer.getStr("phone"),
                customer.getStr("userName"), customer.getStr("customerId"), customer.getStr("platform"),
                customer.getStr("source"), customer.getStr("data"), customer.getStr("clue"), customer.getStr("userName")).toString());
        String body = post.execute().body();
        log.info("body:{}", body);
    }

    private static void setHeader(HttpRequest request) {
        request.header("Content-Type", "application/json; charset=utf-8");
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.header("partnerId", PARTNER_ID);
        request.header("company", COMPANY);
        request.header("key", API_KEY);
        request.header("timestamp", timestamp);
        request.header("sign", getSign(timestamp));

    }

//客户昵称(文本字段)
//联系方式(文本字段)
//跟进人员(成员字段)
//聊天截图(图片字段)  9  13469
//线索平台(选项字段)  2  13540
// 来源账号(关联字段) 0  17453
// 录入日期(日期字段) 4  17427
// 线索人员(关联字段) 0  17452


    public JSONArray customer(String... args) {

        JSONArray list = new JSONArray();
        JSONObject paramsMap = new JSONObject();
        paramsMap.put("company", "");//公司名称
        paramsMap.put("customerId", args[3]);//客户id
        paramsMap.put("name", args[0]);//客户姓名.

        boolean mobileNumber = isMobileNumber(args[1]);
        paramsMap.put("phone",  args[1] );//客户手机号
        paramsMap.put("position", "");//职位
        paramsMap.put("userId", args[2]);//跟进人的用户Id
        paramsMap.put("gender", "");//性别 男/女
        paramsMap.put("birthday", "");//性别 男/女
        paramsMap.put("telephone", "");//性别 男/女
        paramsMap.put("qq", "");//qq
        paramsMap.put("wechat", mobileNumber? "" :args[1]);//微信号
        paramsMap.put("address", "");//微信号
        paramsMap.put("email", "");//微信号
        paramsMap.put("from", "0");//客户分配：0分配到客户列表（缺省），1分配到公海
        paramsMap.put("seaId", "");//公海id
        paramsMap.put("remark", "");//公海id
        paramsMap.put("dataSource", "致墅设计-抖音【欧式图文】周颖");//客户来源（可自定义）

        //协作人的用户Id数组
        List<String> collaborators = new ArrayList<>();
        paramsMap.put("collaborators", collaborators);
        //自定义字段
        List<Map<String, Object>> customFieldValuesList = new ArrayList<>();
        Map<String, Object> name = new HashMap<>();
        name.put("id", "13801");//协作人的用户Id数组
        name.put("values", Arrays.asList(args[0]));//协作人的用户Id数组
        name.put("fieldType", "0");
        customFieldValuesList.add(name);

//        name.put("id", "17482");//协作人的用户Id数组
//        name.put("values", Arrays.asList(args[2]));//协作人的用户Id数组
//        name.put("fieldType", "0");
//        customFieldValuesList.add(name);

        for (int i = 4; i < args.length; i++){
            Map<String, Object> customFieldValuesMap = new HashMap<>();
            customFieldValuesMap.put("id", getId(i));//协作人的用户Id数组
            customFieldValuesMap.put("values", Arrays.asList(args[i]));//协作人的用户Id数组
            customFieldValuesMap.put("text", "");//协作人的用户Id数组
            customFieldValuesMap.put("fieldType", getType(i));//自定义字段类型 ，取自 查询自定义字段设置（/open/customer/customFields） 接口的type字段。特殊类型，创建或修改客户信息时，需要回传类型值
            //其他联系人
            //当fieldType=6 既其他联系方式类型时，传此参数
//        List<Map<String, Object>> multiContactsList = new ArrayList<>();
//        Map<String, Object> multiContactsMap = new HashMap<>();
//        multiContactsMap.put("optionId", "");//取自 查询自定义字段设置 接口（/open/customer/customFields）的options选项数据的id
//        multiContactsMap.put("optionId", "");//取自 查询自定义字段设置 接口（/open/customer/customFields）的options选项数据的id
//        multiContactsList.add(multiContactsMap);
//        customFieldValuesMap.put("multiContacts", multiContactsList);
            customFieldValuesList.add(customFieldValuesMap);
        }

        paramsMap.put("customFieldValues", customFieldValuesList);
        list.add(paramsMap);
        return list;
    }

    private String getId(int i){
        switch (i){
//            case 3:
//                return "13469";
            case 4:
                return "13540";
            case 5:
                return "17453";
            case 6:
                return "17427";
            case 8:
                return "17482";
            default:
                return "17452";
        }
    }

    private String getType(int i){
        switch (i){
//            case 3:
//                return "9";
            case 4:
                return "2";
            case 6:
                return "4";
            default:
                return "0";
        }
    }

    public static String getSign(String timestamp) {
        String md = API_KEY+COMPANY+PARTNER_ID+timestamp;
        return DigestUtil.md5Hex(md).toUpperCase();
    }

    public boolean isMobileNumber(String number) {
        String regex = "^1[3-9]\\d{9}$";
        return number.matches(regex);
    }
}
