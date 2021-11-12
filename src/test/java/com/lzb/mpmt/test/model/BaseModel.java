package com.lzb.mpmt.test.model;

import com.lzb.mpmt.service.multiwrapper.annotations.MultiTableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public abstract class BaseModel {
    @MultiTableId
    private Long id;
    private Date createTime;
    private Date updateTime;
}
