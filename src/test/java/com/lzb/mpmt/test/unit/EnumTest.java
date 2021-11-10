package com.lzb.mpmt.test.unit;

import cn.hutool.json.JSONUtil;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import com.lzb.mpmt.test.enums.DemoConst;
import org.junit.Test;

public class EnumTest {


    @Test
    public void testGetEnumByValue(){
        DemoConst.SexEnum enumByValue = MultiUtil.getEnumByValue(DemoConst.SexEnum.class, 1);
        int i = 0;
    }
    @Test
    public void testGetEnumByName(){
        DemoConst.SexEnum enumByValue = MultiUtil.getEnumByName(DemoConst.SexEnum.class, "man");
        int i = 0;
    }
}
