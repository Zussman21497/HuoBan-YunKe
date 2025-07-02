package com.xcc.apiService.huoban;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;

@Slf4j
@Service
public class HuoBanSerivce {

    private static final String API_KEY = "WYfpk1NWvy8LtPtU2USlbr7tRq9bo7vFjDN7sYEK";

    private static final String URL = "https://api.huoban.com/openapi/v1";

    //第一次测试


    public static void main(String[] args) {
        getTableListAndPrint("4000000007807513");
    }

    //获取表格列表（所有表格编号和表格名称）并打印在控制台
    public static String getTableListAndPrint(String spaceId){
        String url = URL + "/table/list";

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

    //查询数据列表
    public static String getDataList(){
        String url = URL + "/table/list";

        HttpRequest post = HttpUtil.createPost(url);
        post.header("Open-Authorization", API_KEY);
        post.header("Content-Type", "application/json");

        return null;
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

    //更新线索状态的方法
    public Boolean UpdateHuoBan(JSONObject rquest){
        String item_id = rquest.getStr("item_id");
        String if_effective = rquest.getStr("if_effective");
        String reason = rquest.getStr("reason");
        HttpRequest request = HttpUtil.createRequest(Method.PUT, URL + "/item/"+item_id);

//        request.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("124.220.90.55", 7482)));
        request.header("Open-Authorization", "Bearer " + API_KEY);
        request.header("Content-Type", "application/json; charset=utf-8");
//        JSONObject param = new JSONObject();
//        param.set("table_id", "2100000056521102");
//        param.set("update_type", "update");

//        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
//        jsonObject.set("item_id", huoban.getItemId());
        JSONObject fields = new JSONObject();
        String effective = "有效线索".equals(if_effective)? "1" : ("无效线索".equals(if_effective) ? "3" :"4");
        fields.set("2200000164233839", Arrays.asList(effective));
        fields.set("2200000484011676", reason);
        fields.set("2200000483859365", Arrays.asList(rquest.getStr("partnerId")));
        jsonObject.set("fields", fields);
//        jsonArray.add(jsonObject);

//        param.set("items", jsonArray);

//        log.info("更新线索有效状态请求参数：{}", jsonObject.toString());
        request.body(jsonObject.toString());
        String body = request.execute().body();
        log.info("更新线索有效状态返回结果：{}", body);
        JSONObject entries = JSONUtil.parseObj(body);
        return entries.getInt("code") == 0;
    }


//    public boolean dateExist() {
//
//    }

}
