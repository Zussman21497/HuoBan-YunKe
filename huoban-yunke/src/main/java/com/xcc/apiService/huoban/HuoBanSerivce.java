package com.xcc.apiService.huoban;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Service
public class HuoBanSerivce {

    //private static final String API_KEY = "WYfpk1NWvy8LtPtU2USlbr7tRq9bo7vFjDN7sYEK";//「一行乡墅」工作区
    private static final String API_KEY = "M4vm9xD9CQdNThveASVbLTjMs4N2GCc0Ff41KoMy";//「运营平台」工作区


    private static final String URL1 = "https://api.huoban.com/openapi/v1";

//https://app.huoban.com/spaces/4000000007807513 「一行乡墅」工作区

    public static void main(String[] args) {
        //getTableListAndPrint("4000000003442925"); //获取所有表格及编号
        queryCustomerListFromHuoBan("2100000017297977");//获取单个表格中所有字段的编号
    }
    //表格编号：2100000067142049, 表格名称：客户表

    //获取表格列表（所有表格编号和表格名称）并打印在控制台
    public static String getTableListAndPrint(String spaceId){
        String url = URL1 + "/table/list";

        //按照API文档定义请求头
        HttpRequest post = HttpUtil.createPost(url);
        post.header("Open-Authorization", API_KEY);
        post.header("Content-Type", "application/json");

        //按照API文档定义请求体
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("space_id", spaceId);
        post.body(jsonObject.toString());

        //执行post请求，把返回的JSON信息打印在控制台
        String response = post.execute().body();
        System.out.println("表格列表JSON形式：" + response);
        System.out.println();

        //把JSON信息解析之后打印在控制台
        JSONObject json = JSONUtil.parseObj(response);
        int code = json.getInt("code");
        if(code == 0){
            JSONArray tables = json.getJSONObject("data").getJSONArray("tables");
            for(Object obj : tables){
                JSONObject table = (JSONObject) obj;
                String tableId = table.getStr("table_id");
                String name = table.getStr("name");
                System.out.printf("表格编号：%s, 表格名称：%s%n", tableId, name);
            }
        }else{
            System.out.println("调用接口失败，返回：" + response);
        }

        return response;

    }

    //获取tableId对应的表格中所有字段的编号和名称并打印在控制台
    public static String queryCustomerListFromHuoBan(String tableId) {
        String url = URL1 + "/table/" + tableId;

        // 设置请求头
        HttpRequest post = HttpUtil.createPost(url);
        post.header("Open-Authorization", API_KEY);
        post.header("Content-Type", "application/json");

        // 设置请求体
        JSONObject jsonObject = new JSONObject();
//        jsonObject.set("space_id", spaceId);
        jsonObject.set("table_id", tableId);
        post.body(jsonObject.toString());

        // 执行请求
        String response = post.execute().body();
        System.out.println("字段信息 JSON 形式：" + response);
        System.out.println();

        // 解析并打印字段信息
        JSONObject json = JSONUtil.parseObj(response);
        int code = json.getInt("code");
        if (code == 0) {
            JSONArray fields = json.getJSONObject("data").getJSONArray("fields");
            for (Object obj : fields) {
                JSONObject field = (JSONObject) obj;
                String fieldId = field.getStr("field_id");
                String fieldName = field.getStr("name");
                String fieldType = field.getStr("type");
                System.out.printf("字段ID：%s, 字段名称：%s, 字段类型：%s%n", fieldId, fieldName, fieldType);
            }
        } else {
            System.out.println("调用接口失败，返回：" + response);
        }

        return response;
    }



    //查询数据列表
    public static String getDataList(){
        String url = URL1 + "/table/list";

        HttpRequest post = HttpUtil.createPost(url);
        post.header("Open-Authorization", API_KEY);
        post.header("Content-Type", "application/json");

        return null;
    }


    public void updateHuoBan(JSONObject rquest) {
        // 获取 itemId
        String itemId = rquest.getStr("item_id");
        if (itemId == null || itemId.isEmpty()) {
            log.warn("item_id 为空，无法更新客户信息");
            return;
        }

        // 构建 fields 字段
        JSONObject fields = new JSONObject();

        // 是否有效字段（2200000164233839）
        String if_effective = rquest.getStr("if_effective");
        if ("有效线索".equals(if_effective)) {
            fields.put("2200000164233839", Collections.singletonList("1"));
        } else if ("无效线索".equals(if_effective)) {
            fields.put("2200000164233839", Collections.singletonList("3"));
        } else {
            fields.put("2200000164233839", Collections.singletonList("4")); // 默认有待确认
        }

        // 无效原因（2200000484011676）
        String reason = rquest.getStr("reason");
        if (reason != null && !reason.isEmpty()) {
            fields.put("2200000484011676", reason);
        }

        // 线索人员（2200000196458192）
        String partnerId = rquest.getStr("partnerId");
        if (partnerId != null && !partnerId.isEmpty()) {
            JSONArray partnerArray = new JSONArray();
            JSONObject partnerObj = new JSONObject();
            partnerObj.put("user_id", partnerId);
            partnerArray.add(partnerObj);
            fields.put("2200000196458192", partnerArray);

        }

        // 请求体封装
        JSONObject requestBody = new JSONObject();
        requestBody.put("fields", fields);

        // 请求地址
        String apiUrl = "https://api.huoban.com/openapi/v1/item/" + itemId;

        // 构建请求
        boolean useProxy = false;//true：本地; false：阿里云

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (useProxy) {
            builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("192.168.184.139", 1080)));
        }

        OkHttpClient client = builder.build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .put(RequestBody.create(
                        MediaType.parse("application/json"),
                        requestBody.toString()
                ))
                .addHeader("Content-Type", "application/json")
                .addHeader("Open-Authorization", "Bearer " + API_KEY)  // 注意 Bearer + 空格 + token
                .build();

        try (Response response = client.newCall(request).execute()) {
            String resp = response.body() != null ? response.body().string() : "无响应体";

            if (response.isSuccessful()) {
                log.info("客户更新成功，itemId={}, 返回内容={}", itemId, resp);
                // 如果需要解析JSON可以在这里解析resp字符串
            } else {
                log.error("客户更新失败，itemId={}, 响应码={}, 返回内容={}", itemId, response.code(), resp);
            }
        } catch (IOException e) {
            log.error("更新客户失败", e);
        }

    }




//旧版：获取表格列表的方法
//    public static void list() {
//        HttpRequest post = HttpUtil.createPost(URL+"/table/list");
////        post.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("124.220.90.55", 7482)));
//        post.header("Open-Authorization", "Bearer " + API_KEY);
//        post.header("Content-Type", "application/json;charset=utf-8");
////        post.body("{\"space_id\":\"\",\"type\":\"sync\"}");
//        post.body("{\n    \"space_id\": \"4000000007807513\"\n}");
//        String body = post.execute().body();
//        System.out.println(body);
//    }

//    {
//        "table_id": "2100000017297977",
//            "name": "线索表",
//            "alias": "",
//            "space_id": "4000000003442925",
//            "created_on": "2021-07-19 00:08:30"
//    },

    //旧版  更新线索状态的方法
//    public Boolean UpdateHuoBan(JSONObject rquest){
//        String item_id = rquest.getStr("item_id");
//        String if_effective = rquest.getStr("if_effective");
//        String reason = rquest.getStr("reason");
//        HttpRequest request = HttpUtil.createRequest(Method.PUT, URL + "/item/"+item_id);
//
////        request.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("124.220.90.55", 7482)));
//        request.header("Open-Authorization", "Bearer " + API_KEY);
//        request.header("Content-Type", "application/json; charset=utf-8");
////        JSONObject param = new JSONObject();
////        param.set("table_id", "2100000056521102");
////        param.set("update_type", "update");
//
////        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
////        jsonObject.set("item_id", huoban.getItemId());
//        JSONObject fields = new JSONObject();
//        String effective = "有效线索".equals(if_effective)? "1" : ("无效线索".equals(if_effective) ? "3" :"4");
//        fields.set("2200000164233839", Arrays.asList(effective));
//        fields.set("2200000484011676", reason);
//        fields.set("2200000483859365", Arrays.asList(rquest.getStr("partnerId")));
//        jsonObject.set("fields", fields);
////        jsonArray.add(jsonObject);
//
////        param.set("items", jsonArray);
//
////        log.info("更新线索有效状态请求参数：{}", jsonObject.toString());
//        request.body(jsonObject.toString());
//        String body = request.execute().body();
//        log.info("更新线索有效状态返回结果：{}", body);
//        JSONObject entries = JSONUtil.parseObj(body);
//        return entries.getInt("code") == 0;
//    }


//    public boolean dateExist() {
//
//    }

}
