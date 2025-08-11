package com.xcc.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CustomerDTO {

    private String nickName;                       //客户昵称（运营）
    private String name;                           //客户姓名（销售）
    private String clue;                        //联系方式
    private String phone;                          //手机号码
    private LocalDateTime data;               //预约日期
    private String platform;                       //对应平台
    private String source;                         //线索来源
    private List<String> screenshot;               //聊天截图
    private String remark;                         //备注
    private String reason;                         //无效原因
    private List<String> isUseful;                 //是否有效
    private String customerId;                     //客户id
    private String userName;                       //跟进人id
    private String userId;                         //跟进人id


}
