/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lzb.mpmt.service.common;

import com.lzb.mpmt.service.util.MutilUtil;
import lombok.Data;
import lombok.SneakyThrows;

/**
 *
 */
@SuppressWarnings("unused")
@Data
public class SerializedLambdaData {
    private Class<?> clazz;
    private String propName;
    private Class<?> propReturnType;

    //驼峰
    private String clazzNameUnderline;
    private String propNameUnderline;

    @SneakyThrows
    public SerializedLambdaData(SerializedLambda serializedLambda) {
        clazz = serializedLambda.getImplClass();
        //去掉"get"
        propName = MutilUtil.firstToLowerCase(serializedLambda.getImplMethodName().substring(3));
        propReturnType = MutilUtil.toClassConfident(serializedLambda.getImplMethodSignature().substring(3, serializedLambda.getImplMethodSignature().indexOf(";")).replaceAll("/", "."));

        clazzNameUnderline = MutilUtil.camelToUnderline(MutilUtil.firstToLowerCase(clazz.getSimpleName()));
        propNameUnderline = MutilUtil.camelToUnderline(propName);
    }
}
