package com.lzb.mpmt.service.multiwrapper;

import com.lzb.mpmt.service.multiwrapper.sqlsegment.joindata.MultiTableRelation;

import java.util.List;

public interface IMultiTableRelationService {
    List<MultiTableRelation> loadRelation();
}
