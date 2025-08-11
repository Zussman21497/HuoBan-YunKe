package com.xcc.controller.api;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xcc.apiService.huoban.HuoBanSerivce;
import com.xcc.apiService.yunke.YunKeService;
import com.xcc.commons.AjaxJson;
import com.xcc.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wan
 * @since 2024-07-18 17:13:57
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private YunKeService yunKeService;
    @Autowired
    private HuoBanSerivce huoBanSerivce;

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

    //增加客户信息
    @PostMapping("/push")
    public AjaxJson poll(@RequestBody CustomerDTO dto) {
        log.info("伙伴云客户信息新增接口入参: {}", dto);
        yunKeService.addCustomer(dto);
        return AjaxJson.getSuccess();
    }

    @PostMapping("/data/update")
    public AjaxJson dataUpdate(@RequestBody CustomerDTO dto) {
        log.info("伙伴云客户信息更新接口入参: {}", dto);
        yunKeService.updateCustomer(dto);
        return AjaxJson.getSuccess();
    }

    @PostMapping("/user/update")
    public AjaxJson update(@RequestBody JSONObject obj) {
        log.info("伙伴云用户信息新增/更新接口入参: {}", obj);
        yunKeService.addOrUpdate(obj);
        return AjaxJson.getSuccess();
    }


    @RequestMapping(value = "/v2/pull", method = {RequestMethod.POST, RequestMethod.GET})
    public AjaxJson pollV2(@RequestBody Object obj) {
        JSONArray objects = JSONUtil.parseArray(obj);
        log.info("云客订阅接口入参: {}", objects);

        objects.forEach(o -> {
            JSONObject entries = JSONUtil.parseObj(o);
            System.out.println("entries: "+ entries.toString());

            if (entries.containsKey("providerId")){
                JSONObject rquest = new JSONObject();
                rquest.put("item_id", entries.getStr("providerId"));
                rquest.put("partnerId",entries.getStr("partnerId"));
                JSONArray jsonArray = entries.getJSONArray("customFieldValueForms");
                jsonArray.forEach(j -> {
                    JSONObject object = JSONUtil.parseObj(j);
                    if (13539 == object.getInt("id") && object.containsKey("values")){
                        rquest.put("if_effective", object.getJSONArray("values").getStr(0));
                    }
                    if (17182 == object.getInt("id") && object.containsKey("values")){
                        rquest.put("reason", object.getJSONArray("values").getStr(0));
                    }
                });

                huoBanSerivce.updateHuoBan(rquest);
            }

        });
        return AjaxJson.getSuccess();
    }

//    19112781169
    @RequestMapping(value = "/v2/callback", method = {RequestMethod.POST, RequestMethod.GET})
    public AjaxJson callback(@RequestParam(required = false) String code) {
        log.info("云客回调接口入参: {}", code);
        return AjaxJson.getSuccess();
    }



}
