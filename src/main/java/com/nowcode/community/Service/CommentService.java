package com.nowcode.community.Service;


import com.nowcode.community.Dao.CommentMapper;
import com.nowcode.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public int addComment(Comment comment){
        return commentMapper.insertComment(comment);
    }
}
