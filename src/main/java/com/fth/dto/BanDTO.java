package com.fth.dto;

import cn.hutool.core.bean.BeanUtil;
import com.fth.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanDTO {
    private Integer id;

    private Integer status;

    private String reason;
}
