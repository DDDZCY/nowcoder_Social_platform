package com.nowcode.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcode.community.Service.UserService;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.CommunityConstant;
import com.nowcode.community.unil.RedisKeyUtil;
import com.nowcode.community.unil.communityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptcha;

    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${server.servlet.context-path}")
    String contextPath;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model , User user) throws MessagingException {
        Map<String ,Object> map = userService.register(user);
        if(map.isEmpty()||map==null){
            model.addAttribute("msg","注册成功，我们已向您的邮箱发送了激活邮件，请尽快前往激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }


    }

    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,
                             @PathVariable("code") String code){
        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功！");
            model.addAttribute("target","/login");
        }
        else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，重复激活");
            model.addAttribute("target","/index");
        }
        else {
            model.addAttribute("msg","激活失败，激活码不正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }


    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response) {
        //生成验证码
        String text = kaptcha.createText();
        BufferedImage img = kaptcha.createImage(text);
        //将验证码存入redis
        String owner = communityUtil.generateUUID();
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(owner);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);

        Cookie cookie = new Cookie("owner",owner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //将图片输出给浏览器
        response.setContentType("image/png");
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String Login(String username, String password, String code, boolean rememberMe,
                        Model model,HttpServletResponse response,
                        @CookieValue("owner") String owner){
        String kaptcha = null;
        if(StringUtils.isNotBlank(owner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(owner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        //验证验证码


        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        //验证账号密码
        long expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.Login(username,password,  expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie  = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge((int) expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }
        else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }

    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String Login(@CookieValue("ticket") String ticket){
        userService.Logout(ticket);
        return "redirect:/login";
    }
}


