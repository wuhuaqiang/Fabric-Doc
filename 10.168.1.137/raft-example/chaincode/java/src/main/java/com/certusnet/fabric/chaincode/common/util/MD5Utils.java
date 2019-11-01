package com.certusnet.fabric.chaincode.common.util;

import org.springframework.util.DigestUtils;

public class MD5Utils {
    public static String getMD5(String str, String slat) {
        String base = str + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
