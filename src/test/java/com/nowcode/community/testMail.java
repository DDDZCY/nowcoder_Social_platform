package com.nowcode.community;

import com.nowcode.community.unil.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testMail {
    @Autowired
    private MailClient mailClient;
    @Test
    public void mailTest() throws MessagingException {
        mailClient.sendMail("3617338626@qq.com","test","邮箱测试");
    }

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void HtmlMailTest() throws MessagingException {
        Context context = new Context();
        context.setVariable("username","啊翠");
        String text = templateEngine.process("/mail/demo",context);
        System.out.println(text);
        mailClient.sendMail("3249676951@qq.com","test",text);

    }

}
