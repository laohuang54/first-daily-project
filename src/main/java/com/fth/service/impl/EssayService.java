package com.fth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fth.dto.EssayDTO;
import com.fth.dto.Result;
import com.fth.mapper.EssayMapper;
import com.fth.pojo.Essay;
import com.fth.service.IEssayService;
import com.fth.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EssayService implements IEssayService {
    @Autowired
    private EssayMapper essayMapper;
    public Result addEssay(EssayDTO essayDTO, String imgUrl) {
        Essay essay= BeanUtil.copyProperties(essayDTO,Essay.class);
        essay.setUserId(UserHolder.getUser());
        essayMapper.insert(essay);
        return Result.ok();
    }
}
