package com.lzb.mpmt.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseModel {
    private Long id;
    private Date createTime;
    private Date endTime;
}
