package com.dangerye.powerful.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sign {
    private int code;
    private String msg;
    private String originalCode;
    private String originalMsg;
    private Throwable cause;
}
