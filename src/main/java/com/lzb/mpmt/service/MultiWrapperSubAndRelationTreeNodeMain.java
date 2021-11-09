//package com.lzb.mpmt.service;
//
//import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 主表要作为关系的父节点
// *
// * @author Administrator
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Slf4j
//@SuppressWarnings("unused")
//public class MultiWrapperSubAndRelationTreeNodeMain implements IMultiWrapperSubAndRelationTreeNode {
//
//
//    private String mainTableName;
//    private Class<?> mainTableClass;
//
//    @Override
//    public String getTableNameThis() {
//        return mainTableName;
//    }
//
//    @Override
//    public Class<?> getTableClassThis() {
//        return mainTableClass;
//    }
//
//    @Override
//    public String getRelationCode() {
//        return mainTableName;
//    }
//
//    @Override
//    public String getTableNameOther() {
//        return "";
//    }
//}
