package com.walker.learning.utils;

import com.walker.learning.models.exceptions.IMException;

/**
 * IMExceptionHeleper
 *
 * @author walker lee
 * @date 2019/4/3
 */
public class IMExceptionHeleper {
    public static final IMException ILLEGAL_TOKEN_LENGTH_EXCEPTION = new IMException(101, "token字节长度非法");


}
