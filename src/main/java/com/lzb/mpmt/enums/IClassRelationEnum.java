package com.lzb.mpmt.enums;

public interface IClassRelationEnum {
    Class<?> getClass1() ;
    Class<?> getClass2() ;
    ClassRelationOneOrManyEnum getClass1One() ;
    ClassRelationOneOrManyEnum getClass2One();
    boolean isClass1Require() ;
    boolean isClass2Require() ;
}
