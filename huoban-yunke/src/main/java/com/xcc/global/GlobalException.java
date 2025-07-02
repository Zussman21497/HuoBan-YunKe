package com.xcc.global;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.xcc.commons.AjaxError;
import com.xcc.commons.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * 全局Controller异常拦截
 *
 * @ControllerAdvice 可指定包前缀，例如：(basePackages = "com.xcc.xxx.controller")
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {
    @Autowired
    HttpServletResponse response;

    /**
     * 全局异常拦截
     */
    @ExceptionHandler({Exception.class, Throwable.class})
    public AjaxJson handlerException(Exception e) {
//        log.error("全局Controller异常拦截,{}", e);
        // 记录日志信息
        AjaxJson aj = null;
        Throwable cause = e.getCause();
        // ------------- 判断异常类型，提供个性化提示信息
        // 如果是未登录异常
        if (e instanceof NotLoginException) {
            aj = AjaxJson.getNotLogin();
//            response.sendRedirect(AuthConstant.LOGIN_URL);
        }
        // 如果是权限异常
        else if (e instanceof NotRoleException) {
            NotRoleException ee = (NotRoleException) e;
            aj = AjaxJson.getNotJur("无此权限：" + ee.getRole());
        }
        // 如果是权限异常
        else if (e instanceof NotPermissionException) {
            NotPermissionException ee = (NotPermissionException) e;
            aj = AjaxJson.getNotJur("无此权限：" + ee.getPermission());
        }
        // 如果是AjaxError，则获取其具体code码
        else if (e instanceof AjaxError) {
            AjaxError ee = (AjaxError) e;
            aj = AjaxJson.get(ee.getCode(), ee.getMessage());
        }
        // 如果是SQLException，并且指定了hideSql，则只返回sql error , && SpCfgUtil.throwOutSql() == false
        else if ((e instanceof SQLException || (cause != null && cause instanceof SQLException))) {
            log.error("全局Controller异常拦截,{}", e);
            aj = AjaxJson.getError(e.getMessage());
//            aj = AjaxJson.getError("Sql Error");
        }
        // 普通异常输出：500 + 异常信息
        else {
            log.error("全局Controller异常拦截,{}", e);
            aj = AjaxJson.getError(e.getMessage());
        }
        return aj;
    }

}
