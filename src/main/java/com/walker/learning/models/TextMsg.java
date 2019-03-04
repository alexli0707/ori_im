package com.walker.learning.models;

import com.walker.learning.constant.protocol.MsgContentType;

/**
 * TextMsg
 *
 * @author walker lee
 * @date 2019/3/4
 */
public class TextMsg extends BaseMsgContent {

    public TextMsg(String content) {
        this.msgType = MsgContentType.CONTENT_TYPE_TEXT;
        this.content = content;
    }


    @Override
    protected String extraToJsonStr() {
        return "";
    }
}
