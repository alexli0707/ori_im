package com.walker.learning;

import com.walker.learning.models.AuthTokenMsgContent;
import com.walker.learning.store.UserDao;

/**
 * InitDBApp
 *
 * @author walker lee
 * @date 2019/4/4
 */
public class InitDBApp {

    public static void main(String[] args) {
        String defaultPassword = "123456";
        String defaultUsernamePrefix = "user";
        //批量插入用户
        for (int i = 0; i <= 100; i++) {
            String username = defaultUsernamePrefix + String.valueOf(i);
            UserDao.insert(new AuthTokenMsgContent(username,defaultPassword));
        }

    }
}
