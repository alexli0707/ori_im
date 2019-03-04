package com.walker.learning.models;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * BaseMsgContent
 * <p>
 * 基础消息
 *
 * @author walker lee
 * @date 2019/3/4
 */
public class BaseMsgContent {
    private static Gson sGson = new Gson();
    protected int msgType;
    protected String content;
    protected Object extra;


    protected String extraToJsonStr() {
        return null;
    }

    public String toJsonStr() {
        HashMap<String, String> msgMap = new HashMap<String, String>();
        msgMap.put("type", String.valueOf(msgType));
        msgMap.put("content", content);
        msgMap.put("extra", this.extraToJsonStr());
        return sGson.toJson(msgMap);
    }

}
