package com.nowcode.community.controller;


import com.nowcode.community.Service.CommentService;
import com.nowcode.community.Service.DiscussPostService;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.Comment;
import com.nowcode.community.unil.CommunityConstant;
import com.nowcode.community.unil.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(Comment comment, @PathVariable("discussPostId") int discussPostId){

        //判空处理
        if(comment == null){
            throw new RuntimeException();
        }

        //过滤文本
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //补充属性
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        //添加评论
        commentService.addComment(comment);

        //更改评论数
        int count = discussPostService.findDiscussPostDetail(discussPostId).getCommentCount();

        if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            count ++;
            discussPostService.updateCommentCount(discussPostId,count);
        }

        return "redirect:/discuss/detail/" + discussPostId;

    }


}
