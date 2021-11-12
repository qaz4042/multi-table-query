package com.lzb.mpmt.service.multiwrapper.util;

import com.lzb.mpmt.service.multiwrapper.IMultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.entity.MultiTableRelation;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Getter
public class MultiTableRelationFactory {

    private List<MultiTableRelation> relations;
    private Map<String, MultiTableRelation> relationCodeMap;
    private Map<String, Map<String, List<MultiTableRelation>>> relation2TableNameMap = new HashMap<>(256);

    public MultiTableRelationFactory(IMultiTableRelationService tableRelationService) {

        this.relations = Collections.unmodifiableList(tableRelationService.loadRelation());

        this.relationCodeMap = relations.stream().collect(Collectors.toMap(MultiTableRelation::getCode, o -> o));
        this.relationCodeMap = Collections.unmodifiableMap(relationCodeMap);

        Map<String, Map<String, List<MultiTableRelation>>> map1 = relations.stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName1)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName2))));
        Map<String, Map<String, List<MultiTableRelation>>> map2 = relations.stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName2)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors.groupingBy(MultiTableRelation::getTableName1))));
        this.relation2TableNameMap.putAll(map1);
        this.relation2TableNameMap.putAll(map2);
        this.relation2TableNameMap = Collections.unmodifiableMap(relation2TableNameMap);
    }
}
