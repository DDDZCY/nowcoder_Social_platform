package com.nowcode.community;



import com.nowcode.community.unil.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testSensitiveFilter{
    @Autowired
    private SensitiveFilter filter;
    @Test
    public void testWordFilter(){
        String s = "我想喝酒驾，我想打架，我想吸毒，我想嫖娼，哈哈哈嫖娼fabc";
        s = filter.filter(s);
        System.out.println(s);
    }

}
