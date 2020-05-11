package com.dangerye.powerful.exception;

public class BusinessException extends RuntimeException {

    private int code;
    private String originalCode;
    private String originalMsg;

    public BusinessException(Sign sign) {
        super(sign.getMsg(), sign.getCause());
        this.code = sign.getCode();
        this.originalCode = sign.getOriginalCode();
        this.originalMsg = sign.getOriginalMsg();
    }
}
