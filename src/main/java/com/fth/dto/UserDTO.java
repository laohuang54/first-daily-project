package com.fth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;

//    private String nickname;

    private String sex;

    private String status;

    private LocalDateTime start;

    private LocalDateTime end;

    private Integer page; //第page页

    private Integer size; //每页size条数据
}
