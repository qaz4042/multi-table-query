package com.lzb.mpmt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableInfo("用户员工")
@Data
public class UserStaff extends BaseModel {
    private String staffName;
    private Integer sex;
}
