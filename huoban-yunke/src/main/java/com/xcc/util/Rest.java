package com.xcc.util;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class Rest {
    public static final RestTemplate template = new RestTemplate();

    static {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30 * 1000);
        factory.setReadTimeout(10 * 60 * 1000);
        template.setRequestFactory(factory);
    }
}
