package com.lzb.mpmt.demo.model;

import com.lzb.mpmt.service.MultiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class BaseModel implements MultiModel {
    Long id;
    Date createTime;
    Date updateTime;
}
