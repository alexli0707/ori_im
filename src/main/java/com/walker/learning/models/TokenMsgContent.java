package com.walker.learning.models;

import java.io.Serializable;

/**
 * TokenMsgContent
 *
 * @author walker lee
 * @date 2019/4/8
 */
public class TokenMsgContent implements Serializable {
    public String token;
    public int id;

    public TokenMsgContent(int id, String token) {
        this.id = id;
        this.token = token;
    }
}
