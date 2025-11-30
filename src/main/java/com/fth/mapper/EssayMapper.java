package com.fth.mapper;

import com.fth.dto.EssayDTO;
import com.fth.pojo.Essay;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EssayMapper {

    void insert(Essay essay);
}
