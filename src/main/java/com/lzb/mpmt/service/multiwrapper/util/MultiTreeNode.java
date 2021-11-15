package com.lzb.mpmt.service.multiwrapper.util;

import com.lzb.mpmt.service.multiwrapper.util.json.jackson.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树节点
 *
 * @author Administrator
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class MultiTreeNode<T> {
    private T parent;
    private T curr;
    private List<MultiTreeNode<T>> children;

    public interface IEqualsKey<T extends IEqualsKey<T>> {
        /**
         * 父子节点存在父子关系的判断
         *
         * @param   child 子节点
         * @return  当前节点(作为父节点)与子节点,是否存在父子关系
         */
        boolean parentKeyEqualsChildKey(T child);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key implements MultiTreeNode.IEqualsKey<Key> {
        Long id;

        @Override
        public boolean parentKeyEqualsChildKey(Key o) {
            return id.equals(o.getId());
        }
    }

    public static <T, KEY extends IEqualsKey<KEY>> MultiTreeNode<T> buildTreeLong(List<T> list, Function<T, Long> keyPropFun, Function<T, Long> parentKeyPropFun) {
        return buildTree(list, u -> null == keyPropFun.apply(u) ? null : new Key(keyPropFun.apply(u)), u -> null == parentKeyPropFun.apply(u) ? null : new Key(parentKeyPropFun.apply(u)));
    }

    public static <T, KEY extends IEqualsKey<KEY>> MultiTreeNode<T> buildTree(List<T> list, Function<T, KEY> keyPropFun, Function<T, KEY> parentKeyPropFun) {
        return buildTree(list, keyPropFun, parentKeyPropFun, o -> null == parentKeyPropFun.apply(o));
    }

    public static <T, KEY extends IEqualsKey<KEY>> MultiTreeNode<T> buildTree(List<T> list, Function<T, KEY> keyPropFun, Function<T, KEY> parentKeyPropFun, Function<T, Boolean> isParentFun) {
        List<T> listNew = new ArrayList<>(list);
        List<T> parents = listNew.stream().filter(isParentFun::apply).collect(Collectors.toList());
        if (parents.size() != 1) {
            throw new MultiException("树根节点(parentProp为空的节点)理论上只且只能能有一个,结果有" + parents.size() + "个");
        }
        listNew.removeAll(parents);
        return buildTreeUnderTopRecursion(null, parents.get(0), listNew, keyPropFun, parentKeyPropFun);
    }

    public static <T, KEY extends IEqualsKey<KEY>> MultiTreeNode<T> buildTreeUnderTopRecursion(T parent, T curr, List<T> list, Function<T, KEY> keyPropFun, Function<T, KEY> parentKeyPropFun) {
        KEY currKey = keyPropFun.apply(curr);
        if (currKey == null) {
            throw new MultiException("所有树节点的key不允许为空" + parent);
        }
        List<T> childrenT = list.stream().filter(o -> currKey.parentKeyEqualsChildKey(parentKeyPropFun.apply(o))).collect(Collectors.toList());
        List<MultiTreeNode<T>> children = childrenT.stream().map(childT -> buildTreeUnderTopRecursion(curr, childT, list, keyPropFun, parentKeyPropFun)).collect(Collectors.toList());
        list.removeAll(childrenT);
        return new MultiTreeNode<T>(parent, curr, children);
    }

    public void consumerTopToBottom(Consumer<T> consumer) {
        if (parent != null) {
            log.warn("不是从树根节点开始执行topToBottom的:" + JSONUtil.toString(parent));
        }
        this.consumerTopToBottomRecursion(consumer);
    }

    private void consumerTopToBottomRecursion(Consumer<T> consumer) {
        consumer.accept(curr);
        if (children != null) {
            children.forEach(child -> child.consumerTopToBottomRecursion(consumer));
        }
    }
}
