package com.nowcode.community.controller;

import com.nowcode.community.Service.MessageService;
import com.nowcode.community.Service.UserService;
import com.nowcode.community.config.HostHolder;
import com.nowcode.community.entity.Message;
import com.nowcode.community.entity.Page;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.communityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import java.util.*;

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
        Integer.valueOf("abc");
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
        List<Integer> ids = findUnreadLetter(letterList);
        if(!ids.isEmpty()){
            messageService.updateLetterStatus(ids,1);
        }


        model.addAttribute("letters",letters);
        model.addAttribute("target", getTargetUser(conversationId));
        return "/site/letter-detail";

    }
    private List<Integer> findUnreadLetter(List<Message> messageList){
        List<Integer> ids = new ArrayList<>();
        if(messageList != null){
            for(Message message : messageList){
                if(message.getToId() == hostHolder.getUser().getId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
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

    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){

        User target = userService.findUserByName(toName);
        if(StringUtils.isEmpty(toName)){
            return communityUtil.getJSONString(1,"未填写发送人!");
        }
        if(target == null){
            return communityUtil.getJSONString(1,"不存在该用户");
        }
        if(StringUtils.isEmpty(content)){
            return communityUtil.getJSONString(1,"发送信息不能为空!");
        }
        if(hostHolder.getUser().getId() == target.getId() ){
            return communityUtil.getJSONString(1,"不能给自己发私信!");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(userService.findUserByName(toName).getId());
        message.setContent(content);
        message.setCreateTime(new Date());
        String conversationId = message.getFromId() < message.getToId() ? message.getFromId() +"_"+ message.getToId() :
                message.getToId()+"_"+message.getFromId();
        message.setConversationId(conversationId);
        messageService.addLetter(message);
        return communityUtil.getJSONString(0);
    }

}
