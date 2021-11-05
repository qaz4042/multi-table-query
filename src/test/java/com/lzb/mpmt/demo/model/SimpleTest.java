package com.lzb.mpmt.demo.model;

import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class SimpleTest {

    @Test
    public void simpleTest() {
        Field[] declaredFields = User.class.getDeclaredFields();
        System.out.println(declaredFields);
    }
}
