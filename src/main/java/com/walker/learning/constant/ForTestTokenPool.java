package com.walker.learning.constant;

/**
 * ForTestTokenPool
 * <p>
 * 测试用token池
 *
 * @author walker lee
 * @date 2019/2/22
 */
public class ForTestTokenPool {
//    public static final HashMap<Integer, String> SID_TO_TOKEN_MAP = new HashMap<Integer, String>() {{
//        put(1, "1");
//        put(2, "2");
//        put(3, "3");
//    }};
//
//    public static final HashMap STOKEN_TO_ID_MAP = new HashMap<String, Integer>() {{
//        put("1", 1);
//        put("2", 2);
//        put("3", 3);
//    }};


    public static String getTokenById(int id) {
        return String.valueOf(id);
    }


    public static Integer getIdByToken(String token) {
        return Integer.parseInt(token);
    }


    public static void main(String[] args) {
        System.out.println(ForTestTokenPool.getTokenById(1));
    }


}
