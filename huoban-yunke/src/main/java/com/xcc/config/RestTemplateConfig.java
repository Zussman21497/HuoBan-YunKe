package com.xcc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Data
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class RestTemplateConfig {
    private String host = "121.36.213.195";
    private Integer port = 6700;

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30 * 1000);
        factory.setReadTimeout(10 * 60 * 1000);
        return new RestTemplate(factory);
    }

    @Bean(name = "proxyTemplate")
    public RestTemplate proxyTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30 * 1000);
        factory.setReadTimeout(10 * 60 * 1000);
        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
        return new RestTemplate(factory);
    }
}
