package com.lzb.mpmt.service.multiwrapper;

import com.lzb.mpmt.service.multiwrapper.entity.MultiTableRelation;

import java.util.List;

/**
 *
 * @author Administrator
 */
public interface IMultiTableRelationService {
    List<MultiTableRelation> loadRelation();
}
