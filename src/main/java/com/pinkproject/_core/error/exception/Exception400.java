package com.pinkproject._core.error.exception;


import com.pinkproject._core.utils.ApiUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 유효성 검사 실패, 잘못된 파라메터 요청
public class Exception400 extends RuntimeException {

    public Exception400(String msg) {
        super(msg);
    }
}