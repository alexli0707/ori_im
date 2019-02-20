package com.walker.learning.models;

import java.io.Serializable;

/**
 * AuthMsgContent
 *
 * @author walker lee
 * @date 2019/1/16
 */
public class AuthMsgContent implements Serializable {

    public String username;
    public String password;

    public AuthMsgContent(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
