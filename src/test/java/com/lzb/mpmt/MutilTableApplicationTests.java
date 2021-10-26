package com.lzb.mpmt;
import cn.hutool.json.JSONUtil;
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
        MultiWrapperMain<UserStaff> eq = MultiWrapperMain.lambda(UserStaff.class)
                .and(w ->
                        w.eq(true, UserStaff::getStaffName, "StaffName1")
                                .eq(true, UserStaff::getStaffName, "StaffName2")

                )
                .and(w ->
                        w.eq(true, UserStaff::getStaffName, "StaffName3")
                                .or()
                                .eq(true, UserStaff::getStaffName, "StaffName4")
                )
                .eq(true, UserStaff::getSex, 1);
        System.out.println(JSONUtil.toJsonPrettyStr(eq));
    }

}
