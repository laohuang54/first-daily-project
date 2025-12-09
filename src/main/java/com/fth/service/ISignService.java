package com.fth.service;

import com.fth.dto.Result;

public interface ISignService {
    Result sign();

    Result showSign(String time);
}
