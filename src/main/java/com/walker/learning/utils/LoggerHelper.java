package com.walker.learning.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoggerHelper
 *
 * @author walker lee
 * @date 2019/2/20
 */
public class LoggerHelper {


    public static Logger getLogger(Class clz) {
        return LoggerFactory.getLogger(clz);
    }
}
