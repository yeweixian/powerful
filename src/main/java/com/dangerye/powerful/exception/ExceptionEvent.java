package com.dangerye.powerful.exception;

import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

public final class ExceptionEvent extends AbstractEvent {

    private static final int DEFAULT_CODE = -1000;
    private static final String DEFAULT_MSG = "系统异常，请稍后重试！";

    private ExceptionEvent(String business, String scene, int code, String message) {
        super(business, scene, code, message);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected int getDefaultCode() {
        return DEFAULT_CODE;
    }

    @Override
    protected String getDefaultMessage() {
        return DEFAULT_MSG;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @ToString
    public static class Builder {
        private String business;
        private String scene;
        private int code;
        private String message;

        private Builder() {
        }

        public ExceptionEvent build() {
            return new ExceptionEvent(business, scene, code, message);
        }
    }
}
