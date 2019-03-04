package com.walker.learning.im;

import com.walker.learning.utils.LoggerHelper;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * SelectionKeyHandler
 *
 * @author walker lee
 * @date 2019/2/27
 */
public abstract class SelectionKeyHandler {
    public abstract void handler(SelectionKey key, Selector selector);

    public int selectionKeyState(SelectionKey key) {
        if (key.isAcceptable()) {
            return SelectionKey.OP_ACCEPT;
        } else if (key.isReadable()) {
            return SelectionKey.OP_READ;
        } else if (key.isConnectable()) {
            return SelectionKey.OP_CONNECT;
        } else {
            LoggerHelper.getLogger(SelectionKeyHandler.class).warn("unknow selection key");
        }
        return -1;
    }
}
