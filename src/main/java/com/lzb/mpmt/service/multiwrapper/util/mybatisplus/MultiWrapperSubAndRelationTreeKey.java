//package com.lzb.mpmt.service.multiwrapper.util.mybatisplus;
//
//import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Objects;
//
///**
// * @author Administrator
// */
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class MultiWrapperSubAndRelationTreeKey implements TreeNode.IEqualsKey<MultiWrapperSubAndRelationTreeKey> {
//    private String tableName1;
//    private String tableName2;
//
//    @Override
//    public boolean keyEquals(MultiWrapperSubAndRelationTreeKey o) {
//        return super.equals(o) ||
//                Objects.equals(tableName1, o.tableName1)
//                || Objects.equals(tableName2, o.tableName2)
//                || Objects.equals(tableName1, o.tableName2)
//                || Objects.equals(tableName2, o.tableName1)
//                ;
//    }
//}
