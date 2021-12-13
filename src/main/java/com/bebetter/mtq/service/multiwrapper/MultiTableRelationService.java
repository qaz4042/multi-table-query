package com.bebetter.mtq.service.multiwrapper;

import com.bebetter.mtq.service.multiwrapper.entity.MultiClassRelation;

import java.util.List;

/**
 *
 * @author Administrator
 */
public interface MultiTableRelationService {
    /**
     * 加载表和表的关系
     *
     * @return 加载表和表的关系
     */
    List<MultiClassRelation> loadRelation();
}
