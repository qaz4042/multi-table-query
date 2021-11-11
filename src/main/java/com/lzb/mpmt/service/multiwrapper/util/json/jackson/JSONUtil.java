package com.lzb.mpmt.service.multiwrapper.util.json.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统一用jackson序列化
 */
@Component
public class JSONUtil {
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        MultiEnumSerializeConfigJackson.addConfigs(objectMapper);
        JSONUtil.objectMapper = objectMapper;
    }

    @SneakyThrows
    public static String toString(Object o) {
        return objectMapper.writeValueAsString(o);
    }

    @SneakyThrows
    public static <T> T fromString(String str, Class<T> clazz) {
        return objectMapper.readValue(str, clazz);
    }

    @SneakyThrows
    public static <T> T fromString(String str, TypeReference<T> typeReference) {
        return objectMapper.readValue(str, typeReference);
    }
}
