package com.lzb.mpmt.service;

import com.lzb.mpmt.enums.ClassRelationEnum;
import com.lzb.mpmt.enums.JoinTypeEnum;
import com.lzb.mpmt.service.common.*;
import com.lzb.mpmt.service.main.MainTableData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperMain<MAIN> {

    private MainTableData mainTableData = new MainTableData();
    private Boolean hasOr = false;

    public static <MAIN> MultiWrapperMain<MAIN> lambda(Class<MAIN> clazz) {
        return new MultiWrapperMain<>();
    }

    public <VAL> MultiWrapperMain<MAIN> and(Consumer<MultiWrapperMain<MAIN>> andContent) {
        MultiWrapperMain<MAIN> whereContent = new MultiWrapperMain<>();
        andContent.accept(whereContent);
        this.mainTableData.getWhereTree().getSubNodes().add(whereContent.getMainTableData().getWhereTree());
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> or() {
        mainTableData.getWhereTree().setAndOr(WhereAndOrEnum.or);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> eq(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        SerializedLambdaData resolve = SerializedLambda.resolveCache(prop);
        if (null == mainTableData.getTableName()) {
            mainTableData.setTableName(resolve.getClazzNameUnderline());
        }
        String propNameUnderline = resolve.getPropNameUnderline();
        mainTableData.getWhereTree().getWhereDatas().add(new WhereTreeNodeData(propNameUnderline, WhereOptEnum.eq, value));
        return this;
    }

}
