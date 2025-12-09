package com.fth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignVO{
    private Integer id;

    private Long signedCount; // 当月已签到天数

    private Integer continueSignCount; // 连续签到天数

}
