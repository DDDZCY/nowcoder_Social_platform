package com.nowcode.community.Dao;

import com.nowcode.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    //@Param用于取别名
    //如果只有一个参数，并且在<if>里面用就必须取别名；
    int selectDiscussPostRows(@Param("userId") int user_id);

    int insertDiscussPost(DiscussPost post);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int count);
}
