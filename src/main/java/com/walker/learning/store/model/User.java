package com.walker.learning.store.model;

import com.google.gson.annotations.SerializedName;

/**
 * User
 *
 * @author walker lee
 * @date 2019/4/8
 */
public class User {

    private int id;
    private String username;
    private String nickname;
    private String salt;
    @SerializedName("encrypted_pwd")
    private String encryptedPwd;


    public User(int id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSalt() {
        return salt;
    }

    public String getEncryptedPwd() {
        return encryptedPwd;
    }
}
