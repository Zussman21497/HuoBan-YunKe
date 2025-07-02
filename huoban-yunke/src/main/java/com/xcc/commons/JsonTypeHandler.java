package com.xcc.commons;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonTypeHandler extends AbstractJsonTypeHandler<Object> {
    private final Class<?> type;

    public JsonTypeHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("JsonTypeHandler(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    @Override
    protected Object parse(String json) {
        if (JSONUtil.isTypeJSONArray(json)) {
            return JSONUtil.parseArray(json);
        } else if (JSONUtil.isTypeJSONObject(json)) {
            return JSONUtil.toBean(json, type);
        } else {
            return null;
        }
    }

    @Override
    protected String toJson(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }
}
