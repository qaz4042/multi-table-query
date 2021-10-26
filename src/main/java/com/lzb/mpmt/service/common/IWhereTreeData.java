package com.lzb.mpmt.service.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


public interface IWhereTreeData {
    default String toSql() {
        return "";
    }
}
