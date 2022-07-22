package com.nowcode.community.Dao;

import com.nowcode.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entity_type,int entity_id, int offset, int limit);

    int selectCommentCountByEntity(int entity_type,int entity_id);

    int insertComment(Comment comment);
}
