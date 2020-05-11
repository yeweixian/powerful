package com.dangerye.powerful.exception;

import org.junit.Test;

public class ExceptionTest {

    @Test
    public void testBuilder() {
        Event event = ExceptionEvent.builder()
                .business("testCode")
                .scene("test")
                .code(-1)
                .message("create event.")
                .build();
        System.out.println(event);
    }
}
