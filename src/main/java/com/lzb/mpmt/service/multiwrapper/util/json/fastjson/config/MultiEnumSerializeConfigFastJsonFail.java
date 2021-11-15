//package com.lzb.mpmt.service.multiwrapper.util.json.fastjson.config;
//
//import com.alibaba.fastjson.serializer.JSONSerializer;
//import com.alibaba.fastjson.serializer.ObjectSerializer;
//import com.alibaba.fastjson.serializer.SerializeConfig;
//import com.lzb.mpmt.service.multiwrapper.enums.MultiConst.IMultiEnum;
//
//import java.io.Serializable;
//import java.lang.reflect.Type;
//
///**
// * @author Administrator
// * @deprecated 枚举泛型处理,定制失败 无效定制
// */
//public class MultiEnumSerializeConfigFastJsonFail {
//
//    public static void addConfigs() {
//        SerializeConfig globalInstance = SerializeConfig.getGlobalInstance();
//        // todo 应用失败 是通过具体类去匹配解析器的,不是通过 class.isAssignableFrom()去判断,所有取不到我们的解析器
//        globalInstance.put(Enum.class, EnumSerializerConfig.instance);
//        globalInstance.put(IMultiEnum.class, MultiEnumSerializerConfig.instance);
//    }
//
//    public static class EnumSerializerConfig implements ObjectSerializer {
//        public static EnumSerializerConfig instance = new EnumSerializerConfig();
//
//        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
//            serializer.out.write(((Enum<?>) object).name());
//        }
//    }
//
//    public static class MultiEnumSerializerConfig implements ObjectSerializer {
//        public static MultiEnumSerializerConfig instance = new MultiEnumSerializerConfig();
//
//        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
//            Serializable value = ((IMultiEnum<?>) object).getValue();
//            if (value instanceof Integer) {
//                serializer.out.write((Integer) value);
//            } else if (value instanceof String) {
//                serializer.out.write((String) value);
//            }
//        }
//    }
//}