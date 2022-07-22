package com.nowcode.community.controller;

import com.nowcode.community.Service.MessageService;
import com.nowcode.community.Service.UserService;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.Message;
import com.nowcode.community.entity.Page;
import com.nowcode.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list" , method = RequestMethod.GET)
    public String getLetterList(Page page, Model model){
        User user = hostHolder.getUser();
        //分页信息
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setLimit(5);
        page.setPath("/letter/list");
        //获取会话列表
        List<Message> messageList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if( messageList != null){
            for(Message message:messageList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation" ,message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findUnreadLetterCountUnread(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        int unreadLetterCount = messageService.findUnreadLetterCountUnread(user.getId(), null);
        model.addAttribute("unreadLetterCount",unreadLetterCount);
        return "/site/letter";

    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,
                                  Model model, Page page){
        //分页信息
        page.setLimit(6);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letter/detail/"+ conversationId);

        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letters != null){
            for(Message letter: letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        model.addAttribute("target", getTargetUser(conversationId));
        return "/site/letter-detail";

    }

    private User getTargetUser(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }
        else return userService.findUserById(id0);
    }

}
