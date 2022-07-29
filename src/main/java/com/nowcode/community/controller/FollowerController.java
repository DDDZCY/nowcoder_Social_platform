package com.nowcode.community.controller;

import com.nowcode.community.Service.FollowerService;
import com.nowcode.community.Service.UserService;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.Page;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.CommunityConstant;
import com.nowcode.community.unil.communityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowerController implements CommunityConstant {

    @Autowired
    private FollowerService followerService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/follower",method = RequestMethod.POST)
    @ResponseBody
    public String follower(int entityType, int entityId){
        User user = hostHolder.getUser();
        if(user == null){
            return communityUtil.getJSONString(-1,"您还未登录");
        }
        followerService.followerOrCancelFollower(user.getId(),entityType,entityId);
        return communityUtil.getJSONString(0,"操作成功");
    }

    //关注详情页
    @RequestMapping(path = "/followee/{userId}", method = RequestMethod.GET)
    public String getFolloweePage(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);

        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setPath("/followee/"+userId);
        page.setLimit(5);
        page.setRows((int) followerService.getFollweeCount(userId,CommunityConstant.ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followerService.getFollowee(userId,page.getOffset(),page.getLimit());
        for(Map<String, Object> map:userList){
            User u = (User) map.get("user");
            //判断是否是该用户粉丝
            int hasFollowed = hasFollowed(u.getId());
            map.put("hasFollowed",hasFollowed);

        }
        model.addAttribute("userList",userList);
        return "/site/followee";
    }

    //粉丝详情页
    @RequestMapping(path = "/follower/{userId}", method = RequestMethod.GET)
    public String getFollowerPage(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);

        page.setPath("/follower/"+userId);
        page.setLimit(5);
        page.setRows((int) followerService.getFollweeCount(userId,CommunityConstant.ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followerService.getFollower(userId,page.getOffset(),page.getLimit());
        for(Map<String, Object> map:userList){
            User u = (User) map.get("user");
            //判断是否是该用户粉丝
            int hasFollowed = hasFollowed(u.getId());
            map.put("hasFollowed",hasFollowed);

        }
        model.addAttribute("userList",userList);
        return "/site/follower";
    }
    private int hasFollowed( int entityId){
        if(hostHolder.getUser() == null){
            return 0;
        }
        int is = followerService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,entityId)? 1: 0;
        return is;
    }

}
