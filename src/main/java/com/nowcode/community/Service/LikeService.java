package com.nowcode.community.Service;

import com.nowcode.community.config.HostHolder;
import com.nowcode.community.unil.LikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞和取消赞
    public void like(int userId,int entityType, int entityId, int authorId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityKey = LikeUtil.getLikesKey(entityType,entityId);
                String authorKey = LikeUtil.getAuthorKey(authorId);
                boolean is = operations.opsForSet().isMember(entityKey,userId);
                operations.multi();
                if(is){
                    operations.opsForSet().remove(entityKey,userId);
                    operations.opsForValue().decrement(authorKey);
                }
                else {
                    operations.opsForValue().increment(authorKey);
                    operations.opsForSet().add(entityKey,userId);

                }
                return operations.exec();
            }
        });
    }
    //获取点赞数量

    public long getLikeCount(int entityType, int entityId){
        String key = LikeUtil.getLikesKey(entityType,entityId);
        return redisTemplate.opsForSet().size(key);
    }

    //获取点赞状态
    public int getLikeStatus(int userId,int entityType, int entityId){
        String key = LikeUtil.getLikesKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(key,userId) ? 1 : 0;
    }

    //查询某个用户获得的赞
    public int findUserGetLikeCount(int userId){
        String authorKey = LikeUtil.getAuthorKey(userId);
        Integer getLikeCount = (Integer) redisTemplate.opsForValue().get(authorKey);
        return getLikeCount == null? 0: getLikeCount.intValue();
    }

}
