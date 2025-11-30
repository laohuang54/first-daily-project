package com.fth.service;

import com.fth.dto.EssayDTO;
import com.fth.dto.Result;

public interface IEssayService {
    Result addEssay(EssayDTO essay, String imgUrl);
}
