package com.lzb.mpmt.service.multiwrapper.util.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.lzb.mpmt.service.multiwrapper.enums.IMultiEnum;
import com.lzb.mpmt.service.multiwrapper.util.MultiException;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Administrator
 */
@SuppressWarnings("ALL")
public class MultiEnumSerializeConfigJackson {

    public static void addConfigs(ObjectMapper objectMapper) {
        SimpleModule simpleModule = new SimpleModule();
        //json值序列化
        simpleModule.addSerializer(IMultiEnum.class, MultiEnumSerializer.INSTANCE);
        simpleModule.addDeserializer(IMultiEnum.class, MultiEnumDeserializer.INSTANCE);
        //todo 待测试
        simpleModule.addSerializer(Enum.class, EnumSerializer.INSTANCE);
        simpleModule.addDeserializer(Enum.class, EnumDeserializer.INSTANCE);

        objectMapper.registerModule(simpleModule);
    }


    public static class EnumSerializer<T extends Enum<T>> extends JsonSerializer<T> {
        public static final EnumSerializer INSTANCE = new EnumSerializer();

        @Override
        public void serialize(T val, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(val.name());
        }
    }

    public static class EnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> implements ContextualDeserializer {
        public static final EnumDeserializer<?> INSTANCE = new EnumDeserializer<>();
        private Class<T> clazz;

        public EnumDeserializer() {
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
            JavaType type = ctxt.getContextualType() != null
                    ? ctxt.getContextualType()
                    : property.getMember().getType();
            //noinspection unchecked
            return new MultiEnumDeserializer(type.getRawClass());
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return MultiUtil.getEnumByName(clazz, p.getValueAsString());
        }
    }


    public static class MultiEnumSerializer<T extends IMultiEnum> extends JsonSerializer<T> {
        public static final MultiEnumSerializer INSTANCE = new MultiEnumSerializer();

        @Override
        public void serialize(T val, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            Serializable value = val.getValue();
            if (value instanceof String) {
                jsonGenerator.writeString((String) value);
            } else if (value instanceof Integer) {
                jsonGenerator.writeNumber((Integer) value);
            }
        }
    }

    public static class MultiEnumDeserializer<T extends IMultiEnum> extends JsonDeserializer<T> implements ContextualDeserializer {
        public static final MultiEnumDeserializer INSTANCE = new MultiEnumDeserializer();
        private Class<T> clazz;

        public MultiEnumDeserializer() {
        }

        public MultiEnumDeserializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Class<?> valueType = MultiUtil.getGenericFirst(clazz);
            if (Integer.class.isAssignableFrom(valueType)) {
                //noinspection unchecked
                return (T) MultiUtil.getEnumByValue(clazz, p.getValueAsInt());
            } else if (String.class.isAssignableFrom(valueType)) {
                //noinspection unchecked
                return (T) MultiUtil.getEnumByValue(clazz, p.getValueAsString());
            }
            throw new MultiException("暂不支持的枚举value的类型" + valueType);
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
            JavaType type = ctxt.getContextualType() != null
                    ? ctxt.getContextualType()
                    : property.getMember().getType();
            return new MultiEnumDeserializer(type.getRawClass());
        }
    }

}
