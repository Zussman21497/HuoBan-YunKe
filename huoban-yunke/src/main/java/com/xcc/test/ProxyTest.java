package com.xcc.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class ProxyTest {
    public static void main(String[] args) {
        // 设置 SOCKS5 代理
        System.setProperty("socksProxyHost", "192.168.184.138"); // Linux代理服务器内网IP
        System.setProperty("socksProxyPort", "1080");

        try {
            HttpResponse response = HttpRequest.get("https://ifconfig.me")//125.83.82.95
                    .execute();

            String body = response.body();
            System.out.println("当前请求IP（应该是Linux公网IP）: " + body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

