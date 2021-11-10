package com.lzb.mpmt.test.unit;

import cn.hutool.json.JSONUtil;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import com.lzb.mpmt.test.enums.DemoConst;
import org.junit.jupiter.api.Test;

public class EnumTest {
    @Test
    public void testEnum() {
        Object enumByValue = MultiUtil.getEnumByValue(DemoConst.SexEnum.class, 1);
        System.out.println(JSONUtil.toJsonStr(enumByValue));
    }
}
