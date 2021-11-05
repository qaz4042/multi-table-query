package com.lzb.mpmt.service.multiwrapper.util;

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
public class TreeNode<T> {
    private T parent;
    private T curr;
    private List<TreeNode<T>> children;

    public interface IEqualsKey<T extends IEqualsKey<T>> {
        boolean parentKeyEqualsChildKey(T o);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key implements TreeNode.IEqualsKey<Key> {
        Long id;

        @Override
        public boolean parentKeyEqualsChildKey(Key o) {
            return id.equals(o.getId());
        }
    }

    public static <T, KEY extends IEqualsKey<KEY>> TreeNode<T> buildTreeLong(List<T> list, Function<T, Long> keyPropFun, Function<T, Long> parentKeyPropFun) {
        return buildTree(list, u -> null == keyPropFun.apply(u) ? null : new Key(keyPropFun.apply(u)), u -> null == parentKeyPropFun.apply(u) ? null : new Key(parentKeyPropFun.apply(u)));
    }

    public static <T, KEY extends IEqualsKey<KEY>> TreeNode<T> buildTree(List<T> list, Function<T, KEY> keyPropFun, Function<T, KEY> parentKeyPropFun) {
        return buildTree(list, keyPropFun, parentKeyPropFun, o -> null == parentKeyPropFun.apply(o));
    }

    public static <T, KEY extends IEqualsKey<KEY>> TreeNode<T> buildTree(List<T> list, Function<T, KEY> keyPropFun, Function<T, KEY> parentKeyPropFun, Function<T, Boolean> isParentFun) {
        List<T> listNew = new ArrayList<>(list);
        List<T> parents = listNew.stream().filter(isParentFun::apply).collect(Collectors.toList());
        if (parents.size() != 1) {
//            log.warn("树根节点(parentProp为空的节点)理论上只能有一个" + parents.size());
            throw new MultiException("树根节点(parentProp为空的节点)理论上只且只能能有一个,结果有" + parents.size() + "个");
        }
        listNew.removeAll(parents);
        return buildTreeUnderTopRecursion(null, parents.get(0), listNew, keyPropFun, parentKeyPropFun);
    }

    public static <T, KEY extends IEqualsKey<KEY>> TreeNode<T> buildTreeUnderTopRecursion(T parent, T curr, List<T> list, Function<T, KEY> keyPropFun, Function<T, KEY> parentKeyPropFun) {
        KEY currKey = keyPropFun.apply(curr);
        if (currKey == null) {
            throw new MultiException("所有树节点的key不允许为空" + parent);
        }
        List<T> childrenT = list.stream().filter(o -> currKey.parentKeyEqualsChildKey(parentKeyPropFun.apply(o))).collect(Collectors.toList());
        List<TreeNode<T>> children = childrenT.stream().map(childT -> buildTreeUnderTopRecursion(curr, childT, list, keyPropFun, parentKeyPropFun)).collect(Collectors.toList());
        list.removeAll(childrenT);
        return new TreeNode<T>(parent, curr, children);
    }

    public void consumerTopToBottom(Consumer<T> consumer) {
        if (parent != null) {
            //todo parent toJson
            log.warn("不是从树根节点开始执行topToBottom的:" + parent);
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
