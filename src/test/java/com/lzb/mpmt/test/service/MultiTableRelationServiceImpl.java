package com.lzb.mpmt.test.service;

import com.lzb.mpmt.service.multiwrapper.IMultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.entity.MultiTableRelation;
import com.lzb.mpmt.service.multiwrapper.enums.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.test.model.User;
import com.lzb.mpmt.test.model.UserStaff;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MultiTableRelationServiceImpl implements IMultiTableRelationService {

    @Override
    public List<MultiTableRelation> loadRelation() {
        //可以查询数据库/枚举信息
        return Arrays.asList(MultiTableRelation.builder()
                        .code("user__user_staff")
                        .class1(User.class)
                        .tableName1("user")
                        .class1KeyProp("id")
                        .class1OneOrMany(ClassRelationOneOrManyEnum.ONE)
                        .class1Require(true)
                        .class2(UserStaff.class)
                        .tableName2("user_staff")
                        .class2KeyProp("admin_user_id")
                        .class2OneOrMany(ClassRelationOneOrManyEnum.MANY)
                        .class2Require(false)
                        .build(),
                MultiTableRelation.builder()
                        .code("user__user_address")
                        .class1(User.class)
                        .tableName1("user")
                        .class1KeyProp("id")
                        .class1OneOrMany(ClassRelationOneOrManyEnum.ONE)
                        .class1Require(true)
                        .class2(UserStaff.class)
                        .tableName2("user_address")
                        .class2KeyProp("user_id")
                        .class2OneOrMany(ClassRelationOneOrManyEnum.MANY)
                        .class2Require(false)
                        .build());
    }
}
