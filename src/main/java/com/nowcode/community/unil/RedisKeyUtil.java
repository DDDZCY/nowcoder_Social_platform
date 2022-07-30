package com.nowcode.community.unil;

public class RedisKeyUtil {

    private static final String colon = ":";
    public static final  String prefix_like= "like:entity";
    public static final  String prefix_user= "like:author";
    public static final String prefix_follower = "follower";
    public static final String prefix_followee = "followee";
    public static final String prefix_kaptcha = "kaptcha";
    public static final String prefix_ticket = "ticket";
    public static final String prefix_loginUser = "user";
    public static String getLikesKey(int entityType, int entityId){
        return prefix_like + colon + entityType + colon + entityId;
    }

    public static String getAuthorKey(int userId){
        return prefix_user + colon + userId;
    }

    public static String getFollowerKey(int entityType, int entityId){
        return prefix_follower + colon + entityType + colon + entityId;
    }

    public static String getFolloweeKey(int userId, int entityType){
        return prefix_followee + colon + userId + colon + entityType;
    }

    public static String getKaptchaKey(String owner){
        return prefix_kaptcha + colon + owner;
    }
    public static String getTicketKey(String ticket){
        return prefix_ticket + colon + ticket;
    }
    public static String getLoginUserKey(int userId){
        return prefix_loginUser + colon + userId;
    }
}
