package com.lzb.mpmt.service.multiwrapper.util.mybatisplus;

import lombok.NoArgsConstructor;

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
