package com.xcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class HuoBanApplication {
    public static void main(String[] args) {

        // 设置 SOCKS5 代理（确保所有后续网络请求都走代理）
//        System.setProperty("socksProxyHost", "192.168.184.138");
//        System.setProperty("socksProxyPort", "1080");

        SpringApplication.run(HuoBanApplication.class, args);
    }

}
