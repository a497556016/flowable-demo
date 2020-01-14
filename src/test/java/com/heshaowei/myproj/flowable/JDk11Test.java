package com.heshaowei.myproj.flowable;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JDk11Test {

    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        System.out.println(formatter.parseLocalDate("2019-12-26 14:00").toString(formatter));
    }
}
