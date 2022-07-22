package com.nowcode.community;

import com.nowcode.community.Dao.DiscussPostMapper;
import com.nowcode.community.Dao.MessageMapper;
import com.nowcode.community.Dao.UserMapper;
import com.nowcode.community.entity.DiscussPost;
import com.nowcode.community.entity.Message;
import com.nowcode.community.entity.User;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testMappers {
    @Autowired
    private UserMapper userMapper;
    @Test
    public void testSelect(){
        User user = userMapper.selectById(102);
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder103@sina.com");
        System.out.println(user);

        user = userMapper.selectByUsername("guanyu");
        System.out.println(user);
    }

    @Test
    public  void testInsert(){
        User user = new User();
        user.setUsername("西南科技大学");
        user.setCreateTime(new Date());
        user.setHeaderUrl("http://swust.edu.en");
        user.setEmail("dt@qq.com");
        user.setPassword("123456");
        user.setSalt("abcd");
        user.setType(1);
        user.setActivationCode("hello");
        int row = userMapper.insertUser(user);
        System.out.println(row);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int row = userMapper.updateHeader(150,"http://SWUST");
        System.out.println(row);

        row = userMapper.updatePassword(150,"acbcde");
        System.out.println(row);

        row = userMapper.updateStatus(150,2);
        System.out.println(row);
    }
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testDiscuss(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPost(149,0,10);
        for(DiscussPost post:list ){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    private static final Logger logger= LoggerFactory.getLogger(testMappers.class);
    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
    }

    @Test
    public void testSelectDiscussPost(){
       DiscussPost discussPost = discussPostMapper.selectDiscussPostById(275);
       System.out.println(discussPost);

    }


    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testLetter(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for(Message message :list){
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112",0,20);
        for(Message message :list){
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectUnreadLetterCountUnread(111,null);
        System.out.println(count);
    }
}
