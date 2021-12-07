package com.lzb.mpmt;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.dto.IMultiPage;
import com.lzb.mpmt.service.multiwrapper.dto.MultiAggregateResult;
import com.lzb.mpmt.service.multiwrapper.dto.MultiPage;
import com.lzb.mpmt.service.multiwrapper.executor.MultiExecutor;
import com.lzb.mpmt.service.multiwrapper.util.json.jackson.JSONUtil;
import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMain;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMainSubWhere;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSub;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSubMainWhere;
import com.lzb.mpmt.test.model.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@SpringBootTest(classes = MultiTableApplication.class)
class MultiTableApplicationTests {

    /**
     * 简单查询test
     */
    @Test
    @SneakyThrows
    void testQuerySimple() {
        List<UserStaff> userStaffsSimple = MultiExecutor.list(new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class, Address.class));
        System.out.println("testQuerySimple=" + JSONUtil.toString(userStaffsSimple));
    }

    /**
     * 分页查询
     */
    @Test
    @SneakyThrows
    void testQueryPage() {
        IMultiPage<UserStaff> page = MultiExecutor.page(new MultiPage<>(1, 10), new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class, Address.class));
        System.out.println("testQueryPage=" + JSONUtil.toString(page));
    }

    /**
     * 基本聚合查询
     */
    @Test
    @SneakyThrows
    void testQueryAggregate() {
        MultiAggregateResult aggregateSumAll = MultiExecutor.aggregate(new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class).aggregateAll(MultiConstant.MultiAggregateTypeEnum.SUM),
                MultiWrapperSub.lambda(User.class)
        ));
        System.out.println("aggregateSumAll=" + aggregateSumAll.getSum());

        MultiAggregateResult aggregateSpecific = MultiExecutor.aggregate(new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class),
                MultiWrapperSub.lambda(User.class).sum(User::getBalance, "tempBalance")
        ));
        System.out.println("aggregateSpecific=" + aggregateSpecific.getSum().get("tempBalance"));
    }

    /**
     * 复杂查询test
     */
    @Test
    void testQueryComplex() {
        //1.复杂查询
        List<UserStaff> userStaffsComplex = MultiExecutor.list(MultiWrapper
                .main(
                        MultiWrapperMain.lambda(UserStaff.class)
                                .select(UserStaff::getSex, UserStaff::getStaffName)
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

                )
                .leftJoin(MultiWrapperSub.lambda(User.class)
                        .select(User::getUsername)
                        .gt(BaseModel::getCreateTime, new Date())
                        .gt(BaseModel::getUpdateTime, LocalDateTime.now())
                        .in(BaseModel::getId, "1", "1", "3")
                        .likeDefault(User::getUsername, "1")
                        .mainWhere(mainWhere ->
                                mainWhere.eq(User::getSex, 1)
                        )
                )
                .leftJoin(MultiWrapperSub.lambda(UserAddress.class)
                        .select(UserAddress::getProvince)
                        .gt(UserAddress::getId, "1")
                ));
        System.out.println("testQueryComplex=" + JSONUtil.toString(userStaffsComplex));
    }

    //1. todo 编写通用 sum功能  page功能 .paramMap(paramMap) 前端自定义参数功能
}
