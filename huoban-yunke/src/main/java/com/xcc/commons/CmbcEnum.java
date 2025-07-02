package com.xcc.commons;

import org.springframework.util.ResourceUtils;

public enum CmbcEnum {
    INCUBATOR("01140311", "武汉商汇融合孵化器管理有限公司", "644536316", "0500075000000528", "04202403071550090157", "iw0fnzl28ocmyg7s"),
    PROPERTY("04710311", "武汉世纪商汇物业管理有限公司", "643012339", "0500702000001563", "04202403071550480158", "iw0fnzl28ocmyg7s"),
    SHANGHUI("91420311", "武汉新世纪商汇金属材料市场股份有限公司", "640055771", "0500702000001632", "04202403071549040156", "iw0fnzl28ocmyg7s"),
    TEST("cust0001", "武汉新世纪商汇金属材料市场股份有限公司", "683102144", "0500702000000159", "01202401231045540000", "123abc"),
    ;
    private String id;
    private String name;
    private String card;
    private String platformId;
    private String merchantNo;
    private String priKeyPwd;
    private static String merchantPrivateKey = "classpath:cmbc/%s.sm2";//先加签，后解密
    private static String encryptKey = "classpath:cmbc/%s.cer";//先加密，后验签

    CmbcEnum(String id, String name, String card, String platformId, String merchantNo, String priKeyPwd) {
        this.id = id;
        this.name = name;
        this.card = card;
        this.platformId = platformId;
        this.merchantNo = merchantNo;
        this.priKeyPwd = priKeyPwd;
    }

    public String getEncryptKey() throws Exception {
        String path = String.format(encryptKey, this.id);
        String file = ResourceUtils.getURL(path).getFile();
        return file;
    }

    public String getMerchantPrivateKey() throws Exception {
        String path = String.format(merchantPrivateKey, this.id);
        String file = ResourceUtils.getURL(path).getFile();
        return file;
    }

    public static CmbcEnum getById(String id) {
        for (CmbcEnum enu : CmbcEnum.values()) {
            if (enu.getId().equals(id)) {
                return enu;
            }
        }
        return null;
    }

    public static CmbcEnum getByPlatformId(String platformId) {
        for (CmbcEnum enu : CmbcEnum.values()) {
            if (enu.getPlatformId().equals(platformId)) {
                return enu;
            }
        }
        return null;
    }

    public static CmbcEnum getByMerchantNo(String merchantNo) {
        for (CmbcEnum enu : CmbcEnum.values()) {
            if (enu.getMerchantNo().equals(merchantNo)) {
                return enu;
            }
        }
        return null;
    }

    public static CmbcEnum getByCard(String card) {
        for (CmbcEnum enu : CmbcEnum.values()) {
            if (enu.getCard().equals(card)) {
                return enu;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCard() {
        return card;
    }

    public String getPlatformId() {
        return platformId;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public String getPriKeyPwd() {
        return priKeyPwd;
    }
}
