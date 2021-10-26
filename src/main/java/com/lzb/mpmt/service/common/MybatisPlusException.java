package com.lzb.mpmt.service.common;

import lombok.NoArgsConstructor;

import javax.servlet.ServletException;

@NoArgsConstructor
public class MybatisPlusException extends RuntimeException{
    public MybatisPlusException(String message) {
        super(message);
    }

    public MybatisPlusException(Throwable rootCause) {
        super(rootCause);
    }

    public MybatisPlusException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
