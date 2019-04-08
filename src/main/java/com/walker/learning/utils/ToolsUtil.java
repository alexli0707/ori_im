package com.walker.learning.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * ToolsUtil
 *
 * @author walker lee
 * @date 2019/4/4
 */
public class ToolsUtil {

    /**
     * 生成32位的uuid
     *
     * @return
     */
    public static String generateUUID() {
        //获取UUID并转化为String对象
        String uuid = UUID.randomUUID().toString();
        //因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        uuid = uuid.replace("-", "");
        return uuid;
    }


    public static String md5String(String stringToMd5) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(stringToMd5.getBytes());
        byte[] digest = md.digest();
        String md5String = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return md5String;
    }

    public static void main(String[] args) {
        System.out.println(generateUUID());
    }
}
