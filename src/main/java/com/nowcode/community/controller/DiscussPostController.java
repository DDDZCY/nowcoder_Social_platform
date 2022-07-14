package com.nowcode.community.controller;


import com.nowcode.community.Service.DiscussPostService;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.DiscussPost;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.communityUtil;
import org.apache.tomcat.util.http.parser.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.StringContent;
import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return communityUtil.getJSONString(403,"用户未登录,请先登录！");
        }
        //
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        post.setContent(content);
        post.setTitle(title);
        discussPostService.addDiscussPost(post);

        return communityUtil.getJSONString(0,"发布成功");
    }


}
