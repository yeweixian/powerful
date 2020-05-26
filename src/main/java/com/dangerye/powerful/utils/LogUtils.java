package com.dangerye.powerful.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.dangerye.powerful.communicate.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LogUtils {

    private static String getEventMsg(String eventName, String msg) {
        return "[" + eventName + "]" +
                " traceId:" + Objects.toString(ThreadContext.getTraceId(), "") +
                ", requestIp:" + Objects.toString(ThreadContext.getRequestIp(), "") +
                ", msg=" + msg;
    }

    public static void setLevel(String className, String level) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger l = lc.getLogger(className);
        if (l != null) {
            l.setLevel(Level.toLevel(level));
        }
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
