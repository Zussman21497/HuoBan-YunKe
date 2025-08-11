package com.xcc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Data
@Component
//自定义字段配置类
public class CustomFieldConfig {

    private Map<String, FieldMeta> fieldMap = new LinkedHashMap<>();

    public CustomFieldConfig(){
        fieldMap.put("联系方式", new FieldMeta("17452", "0" , null));
        fieldMap.put("录入日期", new FieldMeta("17427", "4" , null));
        fieldMap.put("对应平台", new FieldMeta("13540", "2" , null));
        fieldMap.put("聊天截图", new FieldMeta("13469", "9" , null));
        fieldMap.put("其他备注", new FieldMeta("18742", "0" , null));
        fieldMap.put("是否有效", new FieldMeta("13539", "2" , null));
        fieldMap.put("无效原因", new FieldMeta("17182", "0" , null));
        fieldMap.put("跟进人工号", new FieldMeta("17482", "0" , null));
        fieldMap.put("客户昵称", new FieldMeta("13801", "0", null));
        fieldMap.put("来源账号", new FieldMeta("17453", "0", null));
    }
    @Data
    @AllArgsConstructor
    public static class FieldMeta{
        private String id;
        private String type;
        private String text;
    }
}
