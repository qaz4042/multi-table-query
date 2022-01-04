package com.bebetter.mtq.service.multiwrapper.wrapper;

import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
import com.bebetter.mtq.service.multiwrapper.entity.MultiClassRelation;
import com.bebetter.mtq.service.multiwrapper.wrapper.inner.MultiWrapperSubInner;
import com.bebetter.mtq.service.multiwrapper.wrapper.inner.MultiWrapperInner;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 多表联查器
 * 举例说明 主表:user_staff 副表:user_staff_address
 *
 * @param <MAIN, DTO>
 * @author Administrator
 */
@NoArgsConstructor
@Slf4j
@Data
@SuppressWarnings("unused")
public class MultiWrapper<MAIN, DTO> {
    MultiWrapper(MultiWrapperInner<MAIN, DTO> wrapperInner) {
        this.wrapperInner = wrapperInner;
    }

    /**
     * 主表信息
     */
    private MultiWrapperInner<MAIN, DTO> wrapperInner;

    public MultiWrapper(MultiWrapperMain<MAIN, DTO> wrapperMain) {
        wrapperInner = new MultiWrapperInner<>(wrapperMain.getWrapperMainInner());
    }

    public MultiWrapper(MultiWrapperMain<MAIN, DTO> wrapperMain, MultiWrapperSub<?>... subTableWrappers) {
        wrapperInner = new MultiWrapperInner<>(wrapperMain.getWrapperMainInner(), Arrays.stream(subTableWrappers).map(MultiWrapperSub::getWrapperSubInner).toArray(MultiWrapperSubInner<?>[]::new));
    }

    public MultiWrapper(MultiWrapperMain<MAIN, DTO> wrapperMain, Class<?>... subTableClasses) {
        wrapperInner = new MultiWrapperInner<>(wrapperMain.getWrapperMainInner());
        Arrays.stream(subTableClasses).forEach(subTableClass -> wrapperInner.leftJoin(MultiWrapperSubInner.lambda(subTableClass)));
    }

    /**
     * 主表信息
     * 例如 select * from user_staff
     *
     * @return MultiWrapper
     */
    public static <MAIN, DTO> MultiWrapper<MAIN, DTO> main(MultiWrapperMain<MAIN, DTO> wrapperMain) {
        MultiWrapper<MAIN, DTO> wrapper = new MultiWrapper<>(new MultiWrapperInner<>());
        wrapper.wrapperInner.wrapperMain = wrapperMain.getWrapperMainInner();
        return wrapper;
    }

    /***
     * join 副表信息
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN, DTO> leftJoin(MultiWrapperSub<?> subTableWrapper) {
        return leftJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN, DTO> innerJoin(MultiWrapperSub<?> subTableWrapper) {
        return innerJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param relationCode      {@link MultiClassRelation#getCode()}
     * @param subTableWrapper 副表的select和 on内条件信息
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN, DTO> leftJoin(String relationCode, MultiWrapperSub<?> subTableWrapper) {
        MultiConstant.JoinTypeEnum joinType = MultiConstant.JoinTypeEnum.left_join;
        wrapperInner.getMainMultiWrapper(joinType, relationCode, subTableWrapper.getWrapperSubInner());
        return this;
    }

    public MultiWrapper<MAIN, DTO> innerJoin(String relationCode, MultiWrapperSub<?> subTableWrapper) {
        MultiConstant.JoinTypeEnum joinType = MultiConstant.JoinTypeEnum.left_join;
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
    public MultiWrapper<MAIN, DTO> extendParams(Map<String, ?> extendParams) {
        wrapperInner.extendParams(extendParams);
        return this;
    }
}
