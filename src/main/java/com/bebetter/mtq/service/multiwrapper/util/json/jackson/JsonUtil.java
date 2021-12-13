package com.bebetter.mtq.service.multiwrapper.util.json.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bebetter.mtq.service.multiwrapper.util.MultiUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 才有Multi框架,需要统一用jackson序列化,否则无法使用实体类自定义int值枚举功能
 * @author Administrator
 */
@Component
public class JsonUtil {

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        MultiEnumSerializeConfigJackson.addEnumAndNotNullConfigs(objectMapper);
        JsonUtil.objectMapper = objectMapper;
    }

    @SneakyThrows
    public static String toString(Object o) {
        if (objectMapper == null) {
            return o.toString();
        }
        return objectMapper.writeValueAsString(o);
    }

    @SneakyThrows
    public static <T> T fromString(String str, Class<T> clazz) {
        MultiUtil.assertNoNull(objectMapper,"请先引入jackson框架,确保spring注入了com.fasterxml.jackson.databind.ObjectMapper");
        return objectMapper.readValue(str, clazz);
    }

    @SneakyThrows
    public static <T> T fromString(String str, TypeReference<T> typeReference) {
        MultiUtil.assertNoNull(objectMapper,"请先引入jackson框架,确保spring注入了com.fasterxml.jackson.databind.ObjectMapper");
        return objectMapper.readValue(str, typeReference);
    }
}
