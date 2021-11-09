package com.lzb.mpmt.demo.model;

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
    private Integer sex;
    private Long adminUserId;

    private User user_userStaff;
}
