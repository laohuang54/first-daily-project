package com.fth.service;

import com.fth.dto.CommentsDTO;
import com.fth.dto.Result;

public interface ICommentsService {
    Result add(CommentsDTO commentsDTO);

    Result show(Integer essayId);

    Result likeComments(Integer id);
}
