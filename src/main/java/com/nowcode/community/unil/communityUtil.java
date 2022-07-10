package com.nowcode.community.unil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class communityUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    //md5加密

    public static String md5(String key){
        if(StringUtils.isAllBlank(key))return null;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
