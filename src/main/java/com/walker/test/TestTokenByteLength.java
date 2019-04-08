package com.walker.test;

/**
 * TestTokenByteLength
 *
 * @author walker lee
 * @date 2019/4/3
 */
public class TestTokenByteLength {

    public static void main(String[] args) {
        String token1 = "abcd13f";
        String token2 = "123456@";

        System.out.println(token1.getBytes().length);
        System.out.println(token2.getBytes().length);

    }
}
