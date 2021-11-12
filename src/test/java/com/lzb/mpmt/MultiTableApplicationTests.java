package com.lzb.mpmt;

import com.lzb.mpmt.service.multiwrapper.executor.MultiExecutor;
import com.lzb.mpmt.service.multiwrapper.util.json.jackson.JSONUtil;
import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMain;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMainSubWhere;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSub;
import com.lzb.mpmt.test.model.BaseModel;
import com.lzb.mpmt.test.model.User;
import com.lzb.mpmt.test.model.UserAddress;
import com.lzb.mpmt.test.model.UserStaff;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = MultiTableApplication.class)
class MultiTableApplicationTests {

    @Test
    @SneakyThrows
    void testQuerySimple() {
        System.out.println("testSimple");

        long time1 = new Date().getTime();
        for (int i = 0; i < 200; i++) {
            //2.简单查询
            List<UserStaff> userStaffsSimple = MultiExecutor.list(
                    new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class)
            );
        }
        long time2 = new Date().getTime();

        System.out.println("耗时:" + (time2 - time1));
    }

    @Test
    void testQueryComplex() {

        //3.复杂查询
        List<UserStaff> userStaffsComplex = MultiExecutor.list(MultiWrapper
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
        System.out.println(JSONUtil.toString(userStaffsComplex));
    }

    //1. 编写通用 sum功能  page功能  前端自定义参数功能
    //2. 抽出jdbc层,可以自定义实现
}
