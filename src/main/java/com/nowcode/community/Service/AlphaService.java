package com.nowcode.community.Service;

import com.nowcode.community.Dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("实例化");
    }

    //对象构造后调用
    @PostConstruct
    public void init()
    {
        System.out.println("初始化");
    }

    //对象销毁前调用
    @PreDestroy
    public  void destroy(){
        System.out.println("销毁对象");
    }

    public String toFind(){
        return alphaDao.find();
    }
}
