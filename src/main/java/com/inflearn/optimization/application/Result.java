package com.inflearn.optimization.application;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {
    int count;
    private T data;

    public Result(T data) {
        this.data = data;
    }
}