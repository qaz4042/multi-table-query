package com.lzb.mpmt.demo.model;

import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class SimpleTest {

    @Test
    public void simpleTest() {
        Class<User> userClass = User.class;
        Field[] declaredFields = userClass.getDeclaredFields();
        int i = 0;
    }
}
