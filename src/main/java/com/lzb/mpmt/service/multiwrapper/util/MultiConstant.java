package com.lzb.mpmt.service.multiwrapper.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * @author Administrator
 */
public class MultiConstant {
    /***
     * 公共常量 日期相关
     */
    public static class Strings {
        public static final String EMPTY = "";
        public static final char UNDERLINE = '_';
        public static final String ID_FIELD_NAME_DEFAULT = "id";
    }

    /***
     * 公共常量 日期相关
     */
    public static class DateTimes {
        public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    /**
     * multi框架相关常量
     */
    public static class MultiStrings {
//        public static final String ID_FIELD_NAME = "idFieldName";
    }

}
