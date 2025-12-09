package com.fth.vo;

import com.fth.pojo.Essay;
import com.fth.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO extends User {
    private List<Essay> essays;
}
