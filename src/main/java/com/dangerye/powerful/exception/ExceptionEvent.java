package com.dangerye.powerful.exception;

public final class ExceptionEvent extends AbstractEvent {

    private static final int DEFAULT_CODE = -1000;
    private static final String DEFAULT_MSG = "系统异常，请稍后重试！";

    private ExceptionEvent(String business, String scene, int code, String message) {
        super(business, scene, code, message);
    }

    @Override
    protected int getDefaultCode() {
        return DEFAULT_CODE;
    }

    @Override
    protected String getDefaultMessage() {
        return DEFAULT_MSG;
    }
}
