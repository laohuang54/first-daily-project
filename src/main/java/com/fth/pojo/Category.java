package com.fth.pojo;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Integer id;

    private String name;
}
