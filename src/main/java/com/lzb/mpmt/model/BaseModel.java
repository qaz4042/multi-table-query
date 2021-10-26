package com.lzb.mpmt.model;

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
    private Long id;
    private Date createTime;
    private Date endTime;
}
