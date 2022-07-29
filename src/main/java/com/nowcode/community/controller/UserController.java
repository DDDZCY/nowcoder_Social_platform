package com.nowcode.community.controller;


import com.nowcode.community.Service.LikeService;
import com.nowcode.community.Service.UserService;
import com.nowcode.community.annotation.LoginRequired;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.CommunityConstant;
import com.nowcode.community.unil.communityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    String uploadPath;

    @Value("${community.path.domin}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }


    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String updateHeaderUrl(MultipartFile headerImage , Model model){
        if(headerImage == null){
            model.addAttribute("error","你还未上传图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(suffix == null){
            model.addAttribute("error","图片格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = communityUtil.generateUUID() + fileName;
        File dest = new File(uploadPath + "/" + fileName);

        //存储文件
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException(e);
        }
        //更新头像路径
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeaderImage(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        try (

                OutputStream os = response.getOutputStream();
             FileInputStream fileInputStream = new FileInputStream(fileName);)
        {

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1){
                os.write(buffer,0,b);
            }

        } catch (IOException e) {
            logger.error("读取文件失败");
            throw new RuntimeException(e);
        }
    }

    @LoginRequired
    @RequestMapping(path = "/update",method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String NewPassword,Model model,
                                 @CookieValue("ticket") String ticket){
        User user = hostHolder.getUser();
        Map<String , Object> map = userService.updatePassword(user.getId(),oldPassword,NewPassword);
        if(map.containsKey("passwordMsg")){
            userService.Logout(ticket);
            return "redirect:/index";

        }
        else {
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            model.addAttribute("NewPasswordMsg",map.get("NewPasswordMsg"));
            return "/site/setting";
        }

    }

    //获取用户主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("不存在该用户");
        }
        //获赞数量
        int getLikeCount = likeService.findUserGetLikeCount(userId);
        model.addAttribute("getLikeCount",getLikeCount);
        model.addAttribute("user",user);
        return "/site/profile";
    }
}
