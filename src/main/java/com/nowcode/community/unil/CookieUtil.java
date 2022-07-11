package com.nowcode.community.unil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getCookiesValue(HttpServletRequest request, String name){
        if(request != null && name!= null){
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for(Cookie cookie : cookies){
                    if(cookie.getName().equals(name)){
                        return cookie.getValue();
                    }
                }
            }

        }
        return null;
    }

}
