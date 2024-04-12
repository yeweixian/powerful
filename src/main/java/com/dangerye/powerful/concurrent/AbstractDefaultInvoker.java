package com.dangerye.powerful.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class AbstractDefaultInvoker<C extends InvokeDefaultContext, E extends Throwable> extends Invoker<C> {

    protected abstract void increaseLog(final C context, final Exception exception);

    protected abstract E transformException(final C context, final Exception exception);

    private void writeLog(C context, Exception exception) {
        if (log.isDebugEnabled()) {
            final String invokeSign = context.getInvokeSign();
            log.debug("[Invoker.Fail] msg = invokeSign:{} invoker fail. ", invokeSign, exception);
        }
        increaseLog(context, exception);
    }

    public final <R> R get(final C context) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(context.getInvokeSign(), "invokeSign must not be null");
        try {
            return super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
            return null;
        }
    }

    public final <R> R get(final C context, final Consumer<E> consumer) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(context.getInvokeSign(), "invokeSign must not be null");
        try {
            return super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
            if (consumer != null) {
                final E exception = transformException(context, e);
                consumer.accept(exception);
            }
            return null;
        }
    }

    public final <R, TE extends Throwable> R getOrThrow(final C context, final Function<E, ? extends TE> function) throws TE {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(context.getInvokeSign(), "invokeSign must not be null");
        try {
            return super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
            final E exception = transformException(context, e);
            throw function.apply(exception);
        }
    }

    public final <R, TE extends Throwable> R getElseThrow(final C context, final Function<E, ? extends TE> function) throws TE {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(context.getInvokeSign(), "invokeSign must not be null");
        R result = null;
        try {
            result = super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
            final E exception = transformException(context, e);
            throw function.apply(exception);
        }
        if (result != null) {
            return result;
        } else {
            throw function.apply(null);
        }
    }
}
