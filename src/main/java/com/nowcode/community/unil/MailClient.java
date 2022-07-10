package com.nowcode.community.unil;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
   private Logger logger = LoggerFactory.getLogger(MailClient.class);

   @Autowired
   private JavaMailSender mailSender;

   @Value("${spring.mail.username}")
   private String from;

   public void sendMail(String to,String subject, String context) throws MessagingException {
       MimeMessage mailMessage = mailSender.createMimeMessage();
       MimeMessageHelper Helper = new MimeMessageHelper(mailMessage);
       Helper.setTo(to);
       Helper.setFrom(from);
       Helper.setSubject(subject);
       Helper.setText(context,true);
       mailSender.send(Helper.getMimeMessage());
   }

}
