package com.lzb.mpmt;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lzb.mpmt.model.UserStaff;
import com.lzb.mpmt.service.MultiWrapperMain;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MutilTableApplicationTests {

    WrapperServiceSub<UserStaff> wrapperServiceSub = new WrapperServiceSub<>();

//    @Test
//    void testSql() {
//        Page<UserStaff> page = new Page<>();
//        String sql = MultiWrapper
//                .main(
//                        Wrappers.lambdaQuery(UserStaff.class)
//                                .select(UserStaff::getStaffName, UserStaff::getSex)
//                                .eq(UserStaff::getStaffName, "staffName")
//                                .eq(UserStaff::getSex, "1")
//                                .orderByDesc(UserStaff::getId)
//                        ,
//                        Wrappers.lambdaQuery(User.class)
//                                .eq(User::getUsername, "username")
//                                .eq(User::getSex, "0")
//                )
//                .leftJoin(Wrappers.lambdaQuery(User.class)
//                        .select(User::getUsername)
//                        .eq(User::getUsername, "usernameSub")
//                )
//                .limit(0, 10)
//                .limit(page)
//                .computeSql();
//        System.out.println("testSql=" + sql);
//    }

    @Test
    void testSql2() {
//        Wrappers.lambdaQuery().select()
        MultiWrapperMain<UserStaff> wrapperMain = MultiWrapperMain.lambda(UserStaff.class)
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
                .eq(true, UserStaff::getSex, 1);
        int it = 0;
        System.out.println(wrapperMain.getSelectSql());
        System.out.println(wrapperMain.getWhereSql());
    }

}
