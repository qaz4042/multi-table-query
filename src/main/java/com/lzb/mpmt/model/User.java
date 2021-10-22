package com.lzb.mpmt.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends BaseModel {
    private String username;
    private Integer sex;
}
