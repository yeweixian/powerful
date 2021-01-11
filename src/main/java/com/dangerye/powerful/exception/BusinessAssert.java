package com.dangerye.powerful.exception;

import org.apache.http.util.Args;

import java.util.function.Function;

public final class BusinessAssert {

    public static BusinessException fail(Event event, Function<Event, String> dynamicMessageFunc) {
        Args.notNull(event, "event");
        String msg = dynamicMessageFunc.apply(event);
        return new BusinessException(Sign.builder()
                .code(event.getCode())
                .msg(msg)
                .build());
    }

    public static BusinessException fail(Event event, Function<Event, String> dynamicMessageFunc,
                                         String originalCode, String originalMsg) {
        Args.notNull(event, "event");
        String msg = dynamicMessageFunc.apply(event);
        return new BusinessException(Sign.builder()
                .code(event.getCode())
                .msg(msg)
                .originalCode(originalCode)
                .originalMsg(originalMsg)
                .build());
    }

    public static void handleEvent(Event event, Function<Event, String> dynamicMessageFunc) {
        throw fail(event, dynamicMessageFunc);
    }

    public static void handleEvent(Event event, Function<Event, String> dynamicMessageFunc,
                                   String originalCode, String originalMsg) {
        throw fail(event, dynamicMessageFunc, originalCode, originalMsg);
    }
}
