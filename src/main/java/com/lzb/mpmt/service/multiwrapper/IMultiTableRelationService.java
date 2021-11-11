package com.lzb.mpmt.service.multiwrapper;

import com.lzb.mpmt.service.multiwrapper.entity.MultiTableRelation;

import java.util.List;

/**
 *
 * @author Administrator
 */
public interface IMultiTableRelationService {
    /**
     * 加载表和表的关系
     *
     * @return 加载表和表的关系
     */
    List<MultiTableRelation> loadRelation();
}
