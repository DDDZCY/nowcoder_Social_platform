package com.nowcode.community.Service;


import com.nowcode.community.Dao.DiscussPostMapper;
import com.nowcode.community.entity.DiscussPost;
import com.nowcode.community.unil.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPost(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPost(userId, offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }


    public int addDiscussPost(DiscussPost post){
        if(post != null){
            //转义HTML标记
            post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
            post.setContent(HtmlUtils.htmlEscape(post.getContent()));

            //过滤敏感词
            post.setTitle(sensitiveFilter.filter(post.getTitle()));
            post.setContent(sensitiveFilter.filter(post.getContent()));
            return discussPostMapper.insertDiscussPost(post);
        }

        return -1;
    }

}
