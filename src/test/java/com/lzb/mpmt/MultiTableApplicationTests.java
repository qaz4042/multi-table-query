package com.lzb.mpmt;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.lzb.mpmt.service.multiwrapper.util.json.fastjson.config.MultiEnumSerializeConfigFastJson;
import com.lzb.mpmt.test.model.BaseModel;
import com.lzb.mpmt.test.model.User;
import com.lzb.mpmt.test.model.UserAddress;
import com.lzb.mpmt.test.model.UserStaff;
import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMain;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMainSubWhere;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSub;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSubAndRelation;
import com.lzb.mpmt.service.multiwrapper.executor.MultiJdbcExecutor;
import com.lzb.mpmt.service.multiwrapper.util.MultiTableRelationFactory;
import com.lzb.mpmt.test.service.MultiTableRelationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MultiTableApplicationTests {

    @BeforeEach
    public void doBefore() {
        System.out.println("BeforeEach");

        // 1.实现 MultiTableRelationServiceImpl.loadRelation()
        MultiWrapperSubAndRelation.MULTI_TABLE_RELATION_FACTORY = new MultiTableRelationFactory(new MultiTableRelationServiceImpl());
    }

    @Test
    void testQuerySimple() {
        System.out.println("testSimple");

        //2.简单查询
        List<UserStaff> userStaffsSimple = MultiJdbcExecutor.query(
                new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class)
        );

        MultiEnumSerializeConfigFastJson.addConfigs();
        System.out.println("" + JSON.toJSONString(userStaffsSimple));
//        System.out.println(JSONUtil.toJsonStr(userStaffsSimple));
    }

    @Test
    void testQueryComplex() {

        //3.复杂查询
        List<UserStaff> userStaffsComplex = MultiJdbcExecutor.query(MultiWrapper
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
