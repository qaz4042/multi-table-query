package com.lzb.mpmt.service.multiwrapper.util;

import com.lzb.mpmt.service.multiwrapper.MultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.entity.MultiClassRelation;
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
public class MultiClassRelationFactory {
    public static MultiClassRelationFactory INSTANCE;
    private List<MultiClassRelation> relations;
    private Map<String, MultiClassRelation> relationCodeMap;
    private Map<String, Map<String, List<MultiClassRelation>>> relation2ClassNameMap = new HashMap<>(256);

    public MultiClassRelationFactory(MultiTableRelationService tableRelationService) {

        this.relations = Collections.unmodifiableList(tableRelationService.loadRelation());

        this.relationCodeMap = relations.stream().collect(Collectors.toMap(MultiClassRelation::getCode, o -> o));
        this.relationCodeMap = Collections.unmodifiableMap(relationCodeMap);

        Map<String, Map<String, List<MultiClassRelation>>> map1 = relations.stream().collect(Collectors.groupingBy(MultiClassRelation::getClassName1)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors.groupingBy(MultiClassRelation::getClassName2))));
        Map<String, Map<String, List<MultiClassRelation>>> map2 = relations.stream().collect(Collectors.groupingBy(MultiClassRelation::getClassName2)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors.groupingBy(MultiClassRelation::getClassName1))));
        this.relation2ClassNameMap.putAll(map1);
        this.relation2ClassNameMap.putAll(map2);
        this.relation2ClassNameMap = Collections.unmodifiableMap(relation2ClassNameMap);

        INSTANCE = this;
    }
}
