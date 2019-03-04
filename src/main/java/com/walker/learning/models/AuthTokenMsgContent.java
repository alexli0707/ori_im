package com.walker.learning.models;

import java.io.Serializable;

/**
 * AuthTokenMsgContent
 *
 * @author walker lee
 * @date 2019/2/22
 */
public class AuthTokenMsgContent implements Serializable {
    public String token;

    public AuthTokenMsgContent(String token) {
        this.token = token;
    }
}
