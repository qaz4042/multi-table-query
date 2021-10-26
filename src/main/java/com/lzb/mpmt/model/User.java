package com.lzb.mpmt.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableInfo("用户")
@Data
public class User extends BaseModel {
    private String username;
    private Integer sex;
}
