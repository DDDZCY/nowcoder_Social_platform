package com.nowcode.community.Dao;


import org.springframework.stereotype.Repository;

@Repository
public class AlphaDaoHibernate implements AlphaDao{

    public String find() {
        return "Hibernate";
    }
}
