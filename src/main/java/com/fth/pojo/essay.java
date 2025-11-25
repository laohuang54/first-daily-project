package com.fth.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Fallback;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class essay {
    private Integer id;

    private Integer userId;

    private String content;

    private Integer status;

    private Integer liked;

    private Integer comment;

    private String img;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; //发布时间

    private Integer read;

    private String title;
}
