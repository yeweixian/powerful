package com.dangerye.powerful.exception;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEvent implements Event {

    private String business;
    private String scene;
    private int code;
    private String message;

    protected AbstractEvent(String business, String scene, int code, String message) {
        this.business = business;
        this.scene = scene;
        this.code = code;
        this.message = message;
    }

    protected abstract int getDefaultCode();

    protected abstract String getDefaultMessage();

    @Override
    public String getBusiness() {
        return business;
    }

    @Override
    public String getScene() {
        return scene;
    }

    @Override
    public int getCode() {
        return code != 0 ? code : getDefaultCode();
    }

    @Override
    public String getMessage() {
        return StringUtils.isNotBlank(message) ? message : getDefaultMessage();
    }
}
