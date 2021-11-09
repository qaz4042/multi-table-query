package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.util.TreeNode;

/**
 * 副表,和主副表对应关系信息
 *
 * @author Administrator
 */
public interface IMultiWrapperSubAndRelationTreeNode extends TreeNode.IEqualsKey<IMultiWrapperSubAndRelationTreeNode> {

    /**
     * 树节点关联信息
     */
    String getTableNameThis();
    String getTableNameOther();

    /**
     * 额外信息
     * 当前副表(或主表)对应类
     */
    Class<?> getTableClassThis();
    /**
     * 当前副表对应relationCode(或主表表名)
     */
    String getRelationCode();
    /**
     * select信息
     */
    MultiWrapperSelect getMultiWrapperSelectInfo();


    /**
     * 父.id
     *
     * @param child
     * @return
     */
    @Override
    default boolean parentKeyEqualsChildKey(IMultiWrapperSubAndRelationTreeNode child) {
        String tableNameOtherParent = getTableNameThis();
        return tableNameOtherParent.equals(child.getTableNameOther());
    }
}
