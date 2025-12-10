package com.fth.vo;

import com.fth.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CommentsVO extends User {
    private String content;

    private LocalDateTime createTime;
}
