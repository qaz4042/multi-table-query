package com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.entity.MultiClassRelation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 副表,和主副表对应关系信息
 *
 * @param <SUB> 副表泛型
 * @author Administrator
 */
@Data
@NoArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class MultiWrapperSubAndRelation<SUB> implements IMultiWrapperSubAndRelationTreeNode {

//    public static MultiTableRelationFactory MULTI_TABLE_RELATION_FACTORY;

    /**
     * wrapperSub 为空表示主表临时对象
     */
    public MultiWrapperSubAndRelation(JoinTypeEnum joinType, String relationCode, MultiWrapperSub<SUB> wrapperSub) {
        this.joinType = joinType;
        this.relationCode = relationCode;
        this.wrapperSub = wrapperSub;
    }

    /**
     * 聚合方式
     */
    private JoinTypeEnum joinType;

    /**
     * 关系 ClassRelation.code {@link MultiClassRelation#getCode()}
     */
    private String relationCode;

    /**
     * 副表信息
     */
    private MultiWrapperSub<SUB> wrapperSub;

    /**
     * relationCode对应的关系表1
     */
    private String classNameThis;
    /**
     * relationCode对应的关系表2
     */
    private String classNameOther;
    /**
     * This -> Other 的关系是one还是many
     */
    private ClassRelationOneOrManyEnum classNameThisOneOrMany;
    /**
     * 关系中 是否是否,表1一定该有数据/表2一定该有数据
     */
    private Boolean classNameOtherRequire;

    @Override
    public Class<?> getTableClassThis() {
        return this.wrapperSub.getClazz();
    }

    @Override
    public MultiWrapperSelect<?, ?> getMultiWrapperSelectInfo() {
        return getWrapperSub();
    }
}
