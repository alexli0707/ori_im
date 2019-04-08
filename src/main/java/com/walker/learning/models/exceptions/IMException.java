package com.walker.learning.models.exceptions;

/**
 * IMException
 * <p>
 * IM 相关异常
 *
 * @author walker lee
 * @date 2019/4/3
 */
public class IMException extends RuntimeException {

    private int code;
    private String errorMsg;

    public IMException(int code, String errorMsg) {
        super(errorMsg);
        this.code = code;
        this.errorMsg = errorMsg;
    }


    public int getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
