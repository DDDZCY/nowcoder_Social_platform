package com.nowcode.community;

import com.nowcode.community.Dao.AlphaDao;
import com.nowcode.community.Service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextAware;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class CommunityApplicationTests implements ApplicationContextAware {
	private org.springframework.context.ApplicationContext applicationContext;

	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
		this.applicationContext =  applicationContext;
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void testApp(){
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.find());
	}
	@Test
	public void testService(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testConfigBean(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

}
