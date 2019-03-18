package com.example.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 沐沐 on 2018/10/12.
 * 算法公共类
 */

public class Algorithm {

    /**
     * SHA1加密
     * Created by zh_xu on 2016/12/28.
     */

    public static class SHA1 {
        public static String SHA_people(byte[] decript) {
            try {
                MessageDigest digest = java.security.MessageDigest.getInstance("SHA");
                digest.update(decript);
                byte messageDigest[] = digest.digest();
                // Create Hex String
                StringBuffer hexString = new StringBuffer();
                // 字节数组转换为 十六进制 数
                for (int i = 0; i < messageDigest.length; i++) {
                    String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                    if (shaHex.length() < 2) {
                        hexString.append(0);
                    }
                    hexString.append(shaHex);
                }
                return hexString.toString();

            } catch (NoSuchAlgorithmException e) {
            }
            return "";
        }

        public static String SHA_people(String decript) {
            return SHA_people(decript.getBytes());
        }
    }
}
