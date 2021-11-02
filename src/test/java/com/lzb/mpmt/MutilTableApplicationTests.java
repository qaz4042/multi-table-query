package com.lzb.mpmt;

import com.lzb.mpmt.demo.model.User;
import com.lzb.mpmt.demo.model.UserStaff;
import com.lzb.mpmt.service.MultiWrapper;
import com.lzb.mpmt.service.MultiWrapperMain;
import com.lzb.mpmt.service.MultiWrapperMainSubWhere;
import com.lzb.mpmt.service.MultiWrapperSub;
import org.junit.jupiter.api.Test;

//@SpringBootTest
class MutilTableApplicationTests {

    @Test
    void testSimple() {
        MultiWrapper<UserStaff> wrapper = MultiWrapper
                .main(MultiWrapperMain.lambda(UserStaff.class))
                .leftJoin(MultiWrapperSub.lambda(User.class));
        System.out.println(wrapper.computeSql());
    }

    @Test
    void testComplex() {
        MultiWrapper<UserStaff> wrapper = MultiWrapper
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
                                .notIn(true, UserStaff::getStaffName, "111", "11122", "1112"),
                        MultiWrapperMainSubWhere.lambda(User.class)
                                .eq(User::getSex, 1)
                )
                .leftJoin(MultiWrapperSub.lambda(User.class)
                        .select(User::getUsername)
                        .likeDefault(User::getUsername, "1")
                );

        System.out.println(wrapper.computeSql());
    }

}
