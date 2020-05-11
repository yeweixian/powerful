package com.dangerye.powerful.exception;

import org.apache.http.util.Args;

import java.util.function.Function;

public final class BusinessAssert {

    public static void handleEvent(Event event, Function<Event, String> dynamicMessageFunc) {
        Args.notNull(event, "event");
        String msg = dynamicMessageFunc.apply(event);
        throw new BusinessException(Sign.builder()
                .code(event.getCode())
                .msg(msg)
                .build());
    }
}
