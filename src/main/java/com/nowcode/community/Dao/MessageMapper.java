package com.nowcode.community.Dao;


import com.nowcode.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询会话列表，返回最新的一条消息
    List<Message> selectConversations(int userId, int offset, int limit);
    //查询绘画数量
    int selectConversationCount(int userId);
    //查询每个会话的具体私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    int selectLetterCount(String conversationId);

    //查询未读私信数量
    int selectUnreadLetterCountUnread(int userId, String conversationId);




}
