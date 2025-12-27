package com.fth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {
    private Integer id;

    private Integer essayId;

    private Integer userId;

    private String content;

    private Integer status;

    private Integer liked;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */

    private Long parentId;

    private Integer answerId;

    private LocalDateTime createTime;
}
