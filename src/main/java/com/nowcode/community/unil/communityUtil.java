package com.nowcode.community.unil;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
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

    public static String getJSONString(int code, String Msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("Msg",Msg);
        if(map != null){
            for(String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String Msg){
        return getJSONString(code,Msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }
}
