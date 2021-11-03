package com.lzb.mpmt.service.multiwrapper.util;

import com.lzb.mpmt.service.multiwrapper.IMultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.joindata.MultiTableRelation;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Getter
public class MultiTableRelationFactory {

    private final List<MultiTableRelation> relations;
    private final Map<String, MultiTableRelation> relationCodeMap;
    private final Map<String, Map<String, List<MultiTableRelation>>> relation2TableNameMap = new HashMap<>(256);

    public MultiTableRelationFactory(IMultiTableRelationService tableRelationService) {
        this.relations = tableRelationService.loadRelation();

        this.relationCodeMap = relations.stream().collect(Collectors.toMap(MultiTableRelation::getCode, o -> o));
        Map<String, Map<String, List<MultiTableRelation>>> map1 = relations.stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName1)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName2))));
        Map<String, Map<String, List<MultiTableRelation>>> map2 = relations.stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName2)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName1))));
        relation2TableNameMap.putAll(map1);
        relation2TableNameMap.putAll(map2);
    }
}
