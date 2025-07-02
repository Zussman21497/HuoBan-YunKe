package com.xcc.global;

import com.xcc.commons.AjaxJson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 全局Controller切面, 拦截所有Controller请求
 */
@Aspect
@Component
public class GlobalAspect {
    /**
     * 环绕通知,环绕增强，相当于MethodInterceptor
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
//    @Around(value = "execution(* com.xcc..*Controller*.*(..))")
    public Object surround(ProceedingJoinPoint pjp) throws Throwable {
        // 1、开始时  移入
        try {
            // 2、执行时
            Object obj = pjp.proceed();
            // 如果是 AjaxJson 
            if (obj instanceof AjaxJson) {
            }
            // 如果是 String  
            else if (obj instanceof String) {
                AjaxJson.get(901, String.valueOf(obj));
            }
            // 如果都不是 
            else {
                AjaxJson.get(902, String.valueOf(obj));
            }
            return obj;
        } catch (Throwable e) {
            throw e;
        }
    }


}