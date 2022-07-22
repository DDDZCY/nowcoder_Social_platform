package com.nowcode.community.Service;


import com.nowcode.community.Dao.MessageMapper;
import com.nowcode.community.entity.Message;
import com.nowcode.community.unil.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public  List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findUnreadLetterCountUnread(int userId, String conversationId){
        return messageMapper.selectUnreadLetterCountUnread(userId,conversationId);
    }

    public int addLetter(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertLetter(message);
    }

    public int updateLetterStatus(List<Integer> ids, int status){
        return messageMapper.updateStatus(ids, status);
    }
}
