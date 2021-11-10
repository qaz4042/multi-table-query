package com.lzb.mpmt.demo.model;

import com.lzb.mpmt.test.enums.DemoConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class UserStaff extends BaseModel {
    private String staffName;
    private DemoConst.SexEnum sex;
    private Long adminUserId;

    private User user_userStaff;
}
