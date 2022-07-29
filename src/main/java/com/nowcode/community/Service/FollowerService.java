package com.nowcode.community.Service;


import com.nowcode.community.entity.User;
import com.nowcode.community.unil.CommunityConstant;
import com.nowcode.community.unil.LikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowerService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    public void followerOrCancelFollower(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = LikeUtil.getFollowerKey(entityType,entityId);
                String followeeKey = LikeUtil.getFolloweeKey(userId,entityType);
                boolean hasFollowed = hasFollowed(userId,entityType,entityId);
                operations.multi();
                if(hasFollowed){
                    operations.opsForZSet().remove(followeeKey,entityId);
                    operations.opsForZSet().remove(followerKey,userId);
                }
                else{
                    operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                    operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                }
                return operations.exec();
            }
        });
    }

    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followerKey = LikeUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().score(followerKey,userId) != null;
    }

    //获取粉丝数量

    public long getFollowerCount(int entityType, int entityId){
        String followerKey = LikeUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //获取关注的数量
    public long getFollweeCount(int userId, int entityType){
        String followeeKey = LikeUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //获取某人关注的人
    public List<Map<String, Object>> getFollowee(int userId, int offset, int limit){
        String followeeKey = LikeUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset+limit-1);
        if(targetIds == null){
            return null;
        }
        List<Map<String, Object>> list= new ArrayList<>();
        for(Integer id: targetIds){
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey,id);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    //获取某人的粉丝
    public List<Map<String, Object>> getFollower(int userId, int offset, int limit){
        String followerKey = LikeUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset+limit-1);
        if(targetIds == null){
            return null;
        }
        List<Map<String, Object>> list= new ArrayList<>();
        for(Integer id: targetIds){
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey,id);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

}
