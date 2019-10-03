package com.dangerye.powerful.utils;

import com.dangerye.powerful.communicate.ThreadContext;
import org.slf4j.Logger;

import java.util.Objects;

public class LogUtils {

    private static String getEventMsg(String eventName, String msg) {
        return "[" + eventName + "]" +
                " traceId:" + Objects.toString(ThreadContext.getTraceId(), "") +
                ", requestIp:" + Objects.toString(ThreadContext.getRequestIp(), "") +
                ", msg=" + msg;
    }

    public static void debug(Logger logger, String eventName, String msg, Object... msgParams) {
        if (logger.isDebugEnabled()) {
            logger.debug(getEventMsg(eventName, msg), msgParams);
        }
    }

    public static void info(Logger logger, String eventName, String msg, Object... msgParams) {
        if (logger.isInfoEnabled()) {
            logger.info(getEventMsg(eventName, msg), msgParams);
        }
    }

    public static void warn(Logger logger, String eventName, String msg, Object... msgParams) {
        if (logger.isWarnEnabled()) {
            logger.warn(getEventMsg(eventName, msg), msgParams);
        }
    }

    public static void error(Logger logger, String eventName, String msg, Object... msgParams) {
        if (logger.isErrorEnabled()) {
            logger.error(getEventMsg(eventName, msg), msgParams);
        }
    }
}
