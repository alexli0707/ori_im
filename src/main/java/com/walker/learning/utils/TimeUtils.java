package com.walker.learning.utils;

/**
 * TimeUtils
 *
 * @author walker lee
 * @date 2019/2/22
 */
public class TimeUtils {

    public static int getNowUnixTimeStamp(){
        long rest=System.currentTimeMillis()/1000L;
        return (int)rest;
    }
}
