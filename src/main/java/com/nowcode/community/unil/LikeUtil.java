package com.nowcode.community.unil;

public class LikeUtil {

    private static final String colon = ":";
    public static final  String prefix_like= "like:entity";
    public static final  String prefix_user= "like:author";
    public static String getLikesKey(int entityType, int entityId){
        return prefix_like + colon + entityType + colon + entityId;
    }

    public static String getAuthorKey(int userId){
        return prefix_user + colon + userId;
    }
}
