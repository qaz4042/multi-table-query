package com.lzb.mpmt.service.multiwrapper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mybatismulti")
public class MultiProperties {
    /**
     * 是否检查关系里的必要关系,并显式地抛异常
     */
    Boolean checkRelationRequire = false;
    /**
     * 是否检查关系里的一对多(多对多),一对一关系,并显式地抛异常
     */
    Boolean checkRelationOneOrMany = true;
}
