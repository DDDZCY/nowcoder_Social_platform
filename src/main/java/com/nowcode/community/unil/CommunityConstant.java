package com.nowcode.community.unil;

public interface CommunityConstant {

    /**
    激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
    重复激活
     */
    int ACTIVATION_REPEAT = 1;


    /**
     重复激活
     */
    int ACTIVATION_FAILURE= 2;

    /**
     不记住——登陆凭证合法时间
     */
    int DEFAULT_EXPIRED_SECONDS= 3600 * 12;

    /**
     记住——登陆凭证合法时间
     */
    long REMEMBER_EXPIRED_SECONDS= 3600 * 24 * 120;

    /**
     评论类型：帖子评论
     */
    int ENTITY_TYPE_COMMENT= 1;

    /**
     评论类型：评论的回复
     */
    int ENTITY_TYPE_REPLY= 2;

}
