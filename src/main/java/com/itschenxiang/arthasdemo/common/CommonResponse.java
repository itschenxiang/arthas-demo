package com.itschenxiang.arthasdemo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonResponse<T> {
    private String code;
    private String message;
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>("0", null, data);
    }
}
