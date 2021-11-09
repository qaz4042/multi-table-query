package com.lzb.mpmt.demo.model;

import com.lzb.mpmt.service.multiwrapper.annotations.MutilTableId;
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
    @MutilTableId
    private Long id;
    private Date createTime;
    private Date updateTime;
}
