package com.nowcode.community.controller.interceptor;


import com.nowcode.community.Service.UserService;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.LoginTicket;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从request中获取ticket
        String ticket = CookieUtil.getCookiesValue(request,"ticket");
        //验证ticket
        if(ticket != null){
            LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);
            if(loginTicket != null && loginTicket.getStatus() != 1 && loginTicket.getExpired().after(new Date())){
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
            modelAndView.addObject("loginUserHeaderUrl",user.getHeaderUrl());
            modelAndView.addObject("loginUserUsername",user.getUsername());
            modelAndView.addObject("loginUserUserId",user.getId());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
