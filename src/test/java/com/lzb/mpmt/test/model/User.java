package com.lzb.mpmt.test.model;


import com.lzb.mpmt.service.multiwrapper.annotations.MultiTableField;
import com.lzb.mpmt.test.enums.TestConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class User extends BaseModel {
    private String username;
    private TestConst.SexEnum sex;
    @MultiTableField(exist = false)
    private Long parentId;

    private List<UserAddress> user_userAddress;

}
