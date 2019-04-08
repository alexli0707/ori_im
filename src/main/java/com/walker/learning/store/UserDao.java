package com.walker.learning.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.constant.RedisConstants;
import com.walker.learning.models.AuthTokenMsgContent;
import com.walker.learning.store.core.RedisConnInstance;
import com.walker.learning.store.core.SQLConnInstance;
import com.walker.learning.store.model.User;
import com.walker.learning.utils.ToolsUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDao
 *
 * @author walker lee
 * @date 2019/4/8
 */
public class UserDao {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();


    public static int insert(AuthTokenMsgContent authTokenMsgContent) {
        String salt = ToolsUtil.generateUUID();
        try {
            String encryptedPwd = ToolsUtil.md5String(salt + authTokenMsgContent.password);
            String rawSql = String.format("insert into user (`username`,`salt`,`encrypted_pwd`) VALUES ('%s','%s','%s');", authTokenMsgContent.username, salt, encryptedPwd);
            return SQLConnInstance.getInstance().executeUpdateRawSql(rawSql);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static boolean valid(AuthTokenMsgContent authTokenMsgContent) {
        String query = "SELECT * FROM user WHERE username=(?);";
        try {
            ResultSet rs = SQLConnInstance.getInstance().executeQueryRawSql(query, authTokenMsgContent.username);
            if (rs.next()) {
                String salt = rs.getString("salt");
                String encryptedPwd = rs.getString("encrypted_pwd");
                try {
                    String toValidPassword = ToolsUtil.md5String(salt + authTokenMsgContent.password);
                    if (toValidPassword.equals(encryptedPwd)) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return false;
                }

            } else {
                //找不到该用户
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User getByUsername(String username) {
        String query = "SELECT * FROM user WHERE username=(?);";
        ResultSet rs = null;
        try {
            rs = SQLConnInstance.getInstance().executeQueryRawSql(query, username);
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("nickname"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean validToken(double clientId, String token) {
        String userJson = RedisConnInstance.getInstance().get(RedisConstants.getTokenKey(token));
        if (null == userJson) {
            return false;
        }
        User user = GSON.fromJson(userJson, User.class);
        if (user.getId() == clientId) {
            return true;
        } else {
            return false;
        }
    }


}
