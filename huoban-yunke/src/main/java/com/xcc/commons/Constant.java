package com.xcc.commons;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class Constant {
    public static final String UTF8 = "UTF-8";
    public static final String CONTENT_TYPE = "application/json";
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final BigDecimal FEN = new BigDecimal(100);
}
