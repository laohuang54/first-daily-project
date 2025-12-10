package com.fth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fth.dto.CommentsDTO;
import com.fth.dto.Result;
import com.fth.mapper.CommentsMapper;
import com.fth.pojo.Comments;
import com.fth.service.ICommentsService;
import com.fth.utils.UserHolder;
import com.fth.vo.CommentsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentsService implements ICommentsService {
    @Autowired
    private CommentsMapper commentsMapper;

    @Override
    public Result add(CommentsDTO commentsDTO) {
        log.info("评论发布开始"+commentsDTO);
        Comments com=new Comments();
        BeanUtil.copyProperties(commentsDTO,com);
        Integer userId= UserHolder.getUserId();
        com.setUserId(userId);
        //保存评论
        commentsMapper.add(com);
        log.info("评论发布成功");
        return Result.ok("评论发布成功");
    }

    @Override
    public Result show(Integer essayId) {
        log.info("根据文章id查询评论：{}",essayId);
        List<CommentsVO> show = commentsMapper.show(essayId);
        return Result.ok(show);
    }
}