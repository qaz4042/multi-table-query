package com.lzb.mpmt.test.enums;

import com.lzb.mpmt.service.multiwrapper.enums.IMultiEnum;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;

public class TestConst {
//implements IMultiEnum<Integer>
    @Getter
    @AllArgsConstructor
    public enum SexEnum  {
        man(0, "男"),
        woman(1, "女"),
        ;
        Integer value;
        String label;
    }

}