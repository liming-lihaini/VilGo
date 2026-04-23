package com.village.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 */
public class DateUtils {

    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 获取当前日期时间字符串
     */
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
    }

    /**
     * 格式化日期时间
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
    }
}