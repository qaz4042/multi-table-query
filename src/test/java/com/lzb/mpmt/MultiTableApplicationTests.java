package com.lzb.mpmt;

import cn.hutool.json.JSONUtil;
import com.lzb.mpmt.demo.model.BaseModel;
import com.lzb.mpmt.demo.model.User;
import com.lzb.mpmt.demo.model.UserAddress;
import com.lzb.mpmt.demo.model.UserStaff;
import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMain;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMainSubWhere;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSub;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSubAndRelation;
import com.lzb.mpmt.service.multiwrapper.enums.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.executor.MultiSqlExecutor;
import com.lzb.mpmt.service.multiwrapper.entity.MultiTableRelation;
import com.lzb.mpmt.service.multiwrapper.util.MultiTableRelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MultiTableApplicationTests {

    @BeforeEach
    public void doBefore() {
        System.out.println("BeforeEach");

        // todo 理论上应该从数据库查询
        MultiWrapperSubAndRelation.MULTI_TABLE_RELATION_FACTORY = new MultiTableRelationFactory(() -> Arrays.asList(
                MultiTableRelation.builder()
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
                        .build()
        ));
    }

    @Test
    void testQuerySimple() {
        System.out.println("testSimple");
        List<UserStaff> userStaffsSimple = MultiSqlExecutor.query(
                new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class)
        );
        System.out.println(JSONUtil.toJsonStr(userStaffsSimple));
    }

    @Test
    void testQueryComplex() {
        List<UserStaff> userStaffsComplex = MultiSqlExecutor.query(MultiWrapper
                .main(
                        MultiWrapperMain.lambda(UserStaff.class)
                                .select(UserStaff::getSex, UserStaff::getStaffName)
                                .and(w ->
                                        w.eq(true, UserStaff::getStaffName, "StaffName1")
                                                .eq(true, UserStaff::getStaffName, "StaffName2")

                                )
                                .and(w ->
                                        w.eq(true, UserStaff::getStaffName, "StaffName3")
                                                .or()
                                                .and(w2 -> w2.eq(true, UserStaff::getStaffName, "StaffName4")
                                                        .eq(true, UserStaff::getStaffName, "StaffName4"))

                                )
                                .eq(true, UserStaff::getSex, 1)
                                .likeDefault(true, UserStaff::getStaffName, "111")
                                .notIn(true, UserStaff::getStaffName, "111", "11122", "1112")
                                .limit(0, 20)
                        ,
                        MultiWrapperMainSubWhere.lambda(User.class)
                                .eq(User::getSex, 1)
                )
                .leftJoin(MultiWrapperSub.lambda(User.class)
                        .select(User::getUsername)
                        .gt(BaseModel::getCreateTime, new Date())
                        .gt(BaseModel::getUpdateTime, LocalDateTime.now())
                        .gt(BaseModel::getId, "1")
                        .in(BaseModel::getId, "1", "1", "3")
                        .likeDefault(User::getUsername, "1")
                )
                .leftJoin(MultiWrapperSub.lambda(UserAddress.class)
                        .select(UserAddress::getProvince)
                        .gt(UserAddress::getId, "1")
                ));
        System.out.println(JSONUtil.toJsonStr(userStaffsComplex));
    }

}
