package com.lzb.mpmt.service;

import com.lzb.mpmt.enums.ClassRelationEnum;
import com.lzb.mpmt.enums.JoinTypeEnum;
import com.lzb.mpmt.service.common.MultiFunction;
import com.lzb.mpmt.service.common.WhereTreeNode;
import com.lzb.mpmt.service.intf.MultiWrapperSelect;
import com.lzb.mpmt.service.intf.MultiWrapperWhere;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSubAndRelation<SUB> {
   //聚合方式
   private JoinTypeEnum joinType;
   //关系
   private ClassRelationEnum relation;
   //内容
   private MultiWrapperSub<SUB> wrapperSub;
}
