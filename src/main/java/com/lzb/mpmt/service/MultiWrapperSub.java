package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;

import static com.lzb.mpmt.service.MultiWrapperMain.ID_FIELD_NAME;
import static com.lzb.mpmt.service.MultiWrapperMain.ID_FIELD_NAME_DEFAULT;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSub<SUB extends MultiModel> implements
        MultiWrapperWhere<SUB, MultiWrapperSub<SUB>>,
        MultiWrapperSelect<SUB, MultiWrapperSub<SUB>> {


    /**
     * 下划线表名
     */
    private String tableName;

    /**
     * where条件
     */
    private WhereDataTree whereTree = new WhereDataTree();

    /**
     * select属性列表
     */
    private List<String> selectFields;

    /**
     * id字段名
     */
    private Field idField;
    /**
     * id字段名
     */
    private String idFieldName;

    /**
     * 类为了生成List<SUB>
     */
    private Class<SUB> clazz;


    // todo 可再把 class能确定的信息 比如 idField 放全局缓存
    public static <SUB extends MultiModel> MultiWrapperSub<SUB> lambda(Class<SUB> clazz) {
        String tableName = MultiUtil.camelToUnderline(clazz.getSimpleName());
        MultiWrapperSub<SUB> wrapperSub = new MultiWrapperSub<>();
        wrapperSub.setTableName(tableName);
        wrapperSub.setClazz(clazz);
        //默认是id 用它来去重 setIdFieldName(null) 则不去重  setIdFieldName("code")则用code去去重
//        clazz.getDeclaredField("id").get(null)
        Field idField = MultiUtil.getField(clazz, MultiUtil.getFieldValue(clazz, ID_FIELD_NAME, ID_FIELD_NAME_DEFAULT));
        wrapperSub.setIdField(idField); // IdFieldName 需要relationCode
        return wrapperSub;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperSub<SUB> select(MultiFunction<SUB, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }
}
