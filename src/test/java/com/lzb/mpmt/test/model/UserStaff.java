package com.lzb.mpmt.test.model;

import com.lzb.mpmt.test.enums.TestConst;
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
    private TestConst.SexEnum sex;
    private Long adminUserId;

    private User userAndUserStaff;
}
