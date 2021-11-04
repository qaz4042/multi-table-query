package com.lzb.mpmt.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class User extends BaseModel {
    private String username;
    private Integer sex;
    private Long parentId;

    private static final String staticFinal = "staticFinal";
    private static String statics = "statics";
    private final String finals = "finals";

    static final String staticFinal1 = "staticFinal1";
    static String statics1 = "statics1";
    final String finals1 = "finals1";
}
