package com.lzb.mpmt.service.multiwrapper.wrapper;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.entity.MultiClassRelation;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.MultiWrapperInner;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.MultiWrapperSubInner;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 多表联查器
 * 举例说明 主表:user_staff 副表:user_staff_address
 *
 * @param <MAIN>
 * @author Administrator
 */
@NoArgsConstructor
@Slf4j
@Data
@SuppressWarnings("unused")
public class MultiWrapper<MAIN> {
    MultiWrapper(MultiWrapperInner<MAIN> wrapperInner) {
        this.wrapperInner = wrapperInner;
    }

    /**
     * 主表信息
     */
    private MultiWrapperInner<MAIN> wrapperInner;

    public MultiWrapper(MultiWrapperMain<MAIN> wrapperMain) {
        wrapperInner = new MultiWrapperInner<>(wrapperMain.getWrapperMainInner());
    }

    public MultiWrapper(MultiWrapperMain<MAIN> wrapperMain, MultiWrapperSub<?>... subTableWrappers) {
        wrapperInner = new MultiWrapperInner<>(wrapperMain.getWrapperMainInner(), Arrays.stream(subTableWrappers).map(MultiWrapperSub::getWrapperSubInner).toArray(MultiWrapperSubInner<?>[]::new));
    }

    public MultiWrapper(MultiWrapperMain<MAIN> wrapperMain, Class<?>... subTableClasses) {
        wrapperInner = new MultiWrapperInner<>(wrapperMain.getWrapperMainInner());
        Arrays.stream(subTableClasses).forEach(subTableClass -> wrapperInner.leftJoin(MultiWrapperSubInner.lambda(subTableClass)));
    }

    /**
     * 主表信息
     * 例如 select * from user_staff
     *
     * @return MultiWrapper
     */
    public static <MAIN> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain) {
        MultiWrapper<MAIN> wrapper = new MultiWrapper<>(new MultiWrapperInner<>());
        wrapper.wrapperInner.wrapperMain = wrapperMain.getWrapperMainInner();
        return wrapper;
    }

    /***
     * join 副表信息
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN> leftJoin(MultiWrapperSub<?> subTableWrapper) {
        return leftJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN> innerJoin(MultiWrapperSub<?> subTableWrapper) {
        return innerJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param relationCode      {@link MultiClassRelation#getCode()}
     * @param subTableWrapper 副表的select和 on内条件信息
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN> leftJoin(String relationCode, MultiWrapperSub<?> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.left_join;
        wrapperInner.getMainMultiWrapper(joinType, relationCode, subTableWrapper.getWrapperSubInner());
        return this;
    }

    public MultiWrapper<MAIN> innerJoin(String relationCode, MultiWrapperSub<?> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.left_join;
        wrapperInner.getMainMultiWrapper(joinType, relationCode, subTableWrapper.getWrapperSubInner());
        return this;
    }


    /**
     * @param extendParams 参数Map例如:
     *                     {
     *                     "userAndUserStaff_balance":"100#%#", //其中userAndUserStaff是relationCode,  like '张三%' 才能走索引
     *                     "userStaff_sex":"1"
     *                     }
     */
    public MultiWrapper<MAIN> extendParams(Map<String, ?> extendParams) {
        wrapperInner.extendParams(extendParams);
        return this;
    }
}
