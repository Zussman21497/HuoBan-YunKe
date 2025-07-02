package com.xcc.global;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component// 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {
//    @Autowired
//    IUserService userService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        //        list.add("101");
        //        list.add("user-add");
        //        list.add("user-delete");
        //        list.add("user-update");
        //        list.add("user-get");
        //        list.add("article-get");
        //         User user = userService.getById(String.valueOf(loginId));
        //         String role = user.getRole();
        //         String dept = user.getDept();
        //         list.add(dept);
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
//        User user = userService.getById(String.valueOf(loginId));
//        String role = user.getRole();
//        for (String r : role.split(";|；|:|：")) {
//            list.add(r);
//        }
        return list;
    }

}
