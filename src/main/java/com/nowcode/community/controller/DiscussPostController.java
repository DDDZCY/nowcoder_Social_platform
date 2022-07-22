package com.nowcode.community.controller;


import com.nowcode.community.Service.DiscussPostService;
import com.nowcode.community.Service.UserService;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.Comment;
import com.nowcode.community.entity.DiscussPost;
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

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

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

    @RequestMapping(path = "/detail/{DiscussId}",method = RequestMethod.GET)
    public String getDiscussPostDetail(@PathVariable("DiscussId") int discussId, Model model, Page page){

        DiscussPost post = discussPostService.findDiscussPostDetail(discussId);

        model.addAttribute("post",post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //分页处理初始化
        page.setLimit(5);
        page.setPath("/discuss/detail/"+ discussId);
        page.setRows(post.getCommentCount());

        //获取评论
        List<Comment> commentList = discussPostService.findComments(
                ENTITY_TYPE_COMMENT,post.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for (Comment comment:commentList){
                //评论列表
                Map<String,Object> map= new HashMap<>();
                map.put("comment",comment);
                map.put("commentUser",userService.findUserById(comment.getUserId()));
                //回复列表
                List<Comment> replies = discussPostService.findComments(ENTITY_TYPE_REPLY,comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String, Object>> replyList = new ArrayList<>();
                if(replies != null){
                    for(Comment reply:replies){
                        Map<String,Object> replyComment= new HashMap<>();
                        replyComment.put("reply",reply);
                        //回复人
                        replyComment.put("replyUser",userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target = reply.getTargetId() == 0 ? null:userService.findUserById(reply.getTargetId());
                        replyComment.put("target",target);

                        replyList.add(replyComment);
                    }
                    map.put("replyVo",replyList);
                }
                //回复数量
                int replyCount = discussPostService.getCommentCount(ENTITY_TYPE_REPLY,comment.getId());
                map.put("replyCount",replyCount);
                commentVoList.add(map);
            }

        }

        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }

}
