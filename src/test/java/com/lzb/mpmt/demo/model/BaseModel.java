package com.lzb.mpmt.demo.model;

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
public class BaseModel {
    @MultiTableId
    private Long id;
    private Date createTime;
    private Date updateTime;
}
