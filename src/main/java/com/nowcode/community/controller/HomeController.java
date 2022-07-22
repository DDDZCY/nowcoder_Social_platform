package com.nowcode.community.controller;


import com.nowcode.community.Dao.DiscussPostMapper;
import com.nowcode.community.Dao.UserMapper;
import com.nowcode.community.entity.DiscussPost;
import com.nowcode.community.entity.Page;
import com.nowcode.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private UserMapper userMapper;
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){

        page.setRows(discussPostMapper.selectDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostMapper.selectDiscussPost(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();

        if(list != null)
        {
            for(DiscussPost post:list){
                Map<String , Object> map= new HashMap<>();
                map.put("Post",post);
                User user = userMapper.selectById(post.getUserId());
                map.put("User",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage()
    {
        return "/error/500";
    }

}
