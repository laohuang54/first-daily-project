package com.fth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EssayDTO {
    private Integer id;
    private String title;
    private String content;
    private MultipartFile img;
}
