package com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.util.MultiTreeNode;

/**
 * 副表,和主副表对应关系信息
 *
 * @author Administrator
 */
public interface IMultiWrapperSubAndRelationTreeNode extends MultiTreeNode.IEqualsKey<IMultiWrapperSubAndRelationTreeNode> {

    /**
     * 当前节点的当前表
     *
     * @return 当前节点的当前表
     */
    String getClassNameThis();

    /**
     * 当前节点的当其他表
     *
     * @return 当前节点的当其他表
     */
    String getClassNameOther();

    /**
     * 父子节点存在父子关系的判断
     *
     * @param   child 子节点
     * @return  当前节点(作为父节点)与子节点,是否存在父子关系
     */
    @Override
    default boolean parentKeyEqualsChildKey(IMultiWrapperSubAndRelationTreeNode child) {
        return getClassNameThis().equals(child.getClassNameOther());
    }


    // ----  以下是与树节点无关的额外信息 ----- start
    /**
     * This -> Other 的关系是one还是many
     *
     * @return This -> Other 的关系是one还是many
     */
    ClassRelationOneOrManyEnum getClassNameThisOneOrMany() ;
    /**
     * 关系中 是否是否,表1一定该有数据/表2一定该有数据
     *
     * @return 关系中 是否是否,表1一定该有数据/表2一定该有数据
     */
    Boolean getClassNameOtherRequire() ;

    /**
     * 当前副表(或主表)对应类
     *
     * @return 当前副表(或主表)对应类
     */
    Class<?> getTableClassThis();

    /**
     * 当前副表对应relationCode(或主表表名)
     *
     * @return 当前副表对应relationCode(或主表表名)
     */
    String getRelationCode();

    /**
     * select信息
     *
     * @return select信息
     */
    MultiWrapperSelect<?, ?> getMultiWrapperSelectInfo();

    // ----  以下是与树节点无关的额外信息 ----- end
}
