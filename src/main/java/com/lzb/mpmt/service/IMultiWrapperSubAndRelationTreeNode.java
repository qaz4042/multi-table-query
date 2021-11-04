package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.util.TreeNode;

/**
 * 副表,和主副表对应关系信息
 *
 * @author Administrator
 */
public interface IMultiWrapperSubAndRelationTreeNode extends TreeNode.IEqualsKey<IMultiWrapperSubAndRelationTreeNode> {

    String getTableNameThis();

    String getTableNameOther();

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
