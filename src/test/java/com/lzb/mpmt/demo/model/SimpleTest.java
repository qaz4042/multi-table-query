package com.lzb.mpmt.demo.model;

import com.lzb.mpmt.service.multiwrapper.enums.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class SimpleTest {

    @Test
    public void simpleTest() {
        Class<? extends JoinTypeEnum> aClass = JoinTypeEnum.inner_join.getClass();
        int i = 0;
    }
}
