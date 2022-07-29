package com.nowcode.community.controller;

import com.nowcode.community.Service.LikeService;
import com.nowcode.community.annotation.LoginRequired;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.communityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String likeOrUnlike(int entityType, int entityId,int authorId){

        User user = hostHolder.getUser();
        if(user == null){
            return communityUtil.getJSONString(-1,"您还未登录");

        }
        likeService.like(user.getId(),entityType,entityId,authorId);

        long likeCount = likeService.getLikeCount(entityType,entityId);

        int likeStatus = likeService.getLikeStatus(user.getId(), entityType,entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        System.out.println(communityUtil.getJSONString(0,null, map));
        return communityUtil.getJSONString(0,null, map);
    }

}
