package com.xcc.global;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.xcc.commons.AjaxJson;
import com.xcc.commons.Constant;
import com.xcc.util.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * 全局过滤器
 * <p>
 * 拦截器和过滤器执行顺序：
 * 1、Filter.init();
 * 2、Filter.doFilter(); before doFilter
 * 3、HandlerInterceptor.preHandle();
 * 4、Controller方法执行
 * 5、HandlerInterceptor.postHandle();
 * 6、DispatcherServlet视图渲染
 * 7、HandlerInterceptor.afterCompletion();
 * 8、Filter.doFilter(); after doFilter
 * 9、Filter.destroy();
 * Filter是基于函数回调（doFilter()方法）的，而Interceptor则是基于Java反射的（AOP思想）。
 * Filter依赖于Servlet容器，而Interceptor不依赖于Servlet容器。
 * Filter对几乎所有的请求起作用，而Interceptor只能对action请求起作用。
 * Interceptor可以访问Action的上下文，值栈里的对象，而Filter不能。
 * 在action的生命周期里，Interceptor可以被多次调用，而Filter只能在容器初始化时调用一次。
 * https://upload-images.jianshu.io/upload_images/23386680-b9b2ef4b7bdfa18b.png?imageMogr2/auto-orient/strip|imageView2/2/w/1142/format/webp
 * <p>
 */
@Slf4j
@Component
public class GlobalFilter implements Filter {
    private static final String OPTIONS = "OPTIONS";

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding(Constant.UTF8);
        response.setCharacterEncoding(Constant.UTF8);
        // 获得客户端domain
        String origin = request.getHeader("Origin");
        if (origin == null) origin = request.getHeader("Referer");
        // 允许指定域访问跨域资源
        response.setHeader("Access-Control-Allow-Origin", origin);
        // 允许客户端携带跨域cookie，此时origin值不能为“*”，只能为指定单一域名
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 允许所有请求方式
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        // 有效时间
        response.setHeader("Access-Control-Max-Age", "3600");
        // 允许的header参数
//        response.setHeader("Access-Control-Allow-Headers", "*");
        // 允许的header参数
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type,content-disposition,token");
        response.setHeader("Access-Control-Expose-Headers", "x-requested-with,content-type,content-disposition,token");

        // 如果是预检请求，直接返回
        if (OPTIONS.equals(request.getMethod())) {
//            log.info("=======================浏览器发来了OPTIONS预检请求==========");
            response.getWriter().print("");
            return;
        }

        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            //如果没有配置全局异常(@ControllerAdvice @ExceptionHandler)处理,则异常会传递到此处
            log.info("全局过滤器", e);// 打印堆栈，以供调试
            Throwable cause = e.getCause();
            AjaxJson aj = null;
            if (e instanceof NotLoginException || (cause != null && cause instanceof NotLoginException)) {
                aj = AjaxJson.getNotLogin();
//                response.sendRedirect(AuthConstant.LOGIN_URL);
            } else if (e instanceof NotRoleException || (cause != null && cause instanceof NotRoleException)) {
                NotRoleException ee = (NotRoleException) e;
                aj = AjaxJson.getNotJur("无此权限：" + ee.getRole());
            } else if (e instanceof NotPermissionException || (cause != null && cause instanceof NotPermissionException)) {
                NotPermissionException ee = (NotPermissionException) e;
                aj = AjaxJson.getNotJur("无此权限：" + ee.getPermission());
            } else if (e instanceof SQLException || (cause != null && cause instanceof SQLException)) {
                aj = AjaxJson.getError(e.getMessage());
//                aj = AjaxJson.getError("Sql Error");
            } else {
                aj = AjaxJson.getError(e.getMessage());
            }
            response.setContentType(Constant.CONTENT_TYPE);
            PrintWriter printWriter = response.getWriter();
            printWriter.write(Jackson.toJsonString(aj));
            printWriter.flush();
            printWriter.close();
        }
    }

    @Override
    public void destroy() {
    }

}
