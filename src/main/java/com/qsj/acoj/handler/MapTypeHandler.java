package com.qsj.acoj.handler;

/**
 * @Description
 * @Author qsj
 * @Date 2024/09/05
 */
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class MapTypeHandler extends AbstractJsonTypeHandler<Map<String, String>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected Map<String, String> parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String toJson(Map<String, String> obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
