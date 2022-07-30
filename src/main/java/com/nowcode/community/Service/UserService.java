package com.nowcode.community.Service;

import com.nowcode.community.Dao.LoginTicketMapper;
import com.nowcode.community.Dao.UserMapper;
import com.nowcode.community.entity.LoginTicket;
import com.nowcode.community.entity.User;
import com.nowcode.community.unil.CommunityConstant;
import com.nowcode.community.unil.MailClient;
import com.nowcode.community.unil.RedisKeyUtil;
import com.nowcode.community.unil.communityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domin}")
    private String domin;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    public User findUserById(int id){
        User user = getCache(id);
        if(user == null) {
            user = initCache(id);
        }
        return user;
    }

    public User findUserByName(String name){
        return userMapper.selectByUsername(name);
    }

    public Map<String,Object> register(User user) throws MessagingException {
        Map<String,Object> map = new HashMap<>();


        //空值处理
        if(StringUtils.isAllBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isAllBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isAllBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证账号
        User u = userMapper.selectByUsername(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号被使用");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(communityUtil.generateUUID().substring(0,5));
        user.setPassword(communityUtil.md5(user.getPassword()+user.getSalt()));
        user.setActivationCode(communityUtil.generateUUID());
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        String url = domin + contextPath +"/activation"+"/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail("3617338626@qq.com","激活邮件",content);
        return map;

    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }
        else if(user.getActivationCode().equals(code)){

            userMapper.updateStatus(userId,1);
            clear(userId);
            return ACTIVATION_SUCCESS;
        }
        else return ACTIVATION_FAILURE;
    }

    public Map<String,Object> Login(String username, String password,long expired){
        Map<String,Object> map = new HashMap<>();

        //空值判断
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByUsername(username);
        if(user.getUsername() == null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }

        //验证激活状态
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }

        //验证密码
        password = communityUtil.md5(password+user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","密码有误");
            return map;
        }

        //输出登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(communityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis()+ expired*1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void Logout(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }

    public LoginTicket findLoginTicketByTicket(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    public void updateHeaderUrl(int id,String NewUrl){

        userMapper.updateHeader(id,NewUrl);
        clear(id);
    }

    public Map<String,Object> updatePassword(int id,String oldPassword,String NewPassword){
        Map<String,Object> map = new HashMap<>();
        //判空处理
        if(oldPassword == null){
            map.put("oldPasswordMsg","旧密码不能为空");
            return map;
        }
        if(NewPassword == null){
            map.put("NewPasswordMsg","新密码不能为空");
            return map;
        }
        //验证原始密码
        String password = userMapper.selectById(id).getPassword();
        oldPassword = communityUtil.md5(oldPassword + userMapper.selectById(id).getSalt());
        if(!password.equals(oldPassword)){
            map.put("oldPasswordMsg","原始密码错误！");
            return map;
        }
        NewPassword = communityUtil.md5(NewPassword + userMapper.selectById(id).getSalt());
        userMapper.updatePassword(id,NewPassword);
        clear(id);
        map.put("passwordMsg","修改成功!");
        return map;

    }

    //从缓存中去数据
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getLoginUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    //更新缓存
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getLoginUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user);
        return user;
    }
    //去除缓存
    private void clear(int userId){
        String userKey = RedisKeyUtil.getLoginUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
