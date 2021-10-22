package com.lzb.mpmt.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserStaff extends BaseModel {
    private String staffName;
    private Integer sex;
}
