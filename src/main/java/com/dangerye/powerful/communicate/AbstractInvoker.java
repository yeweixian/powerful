package com.dangerye.powerful.communicate;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class AbstractInvoker<T, C extends InvokeContext<? extends T>, E extends Throwable> extends Invoker<T, C> {

    protected abstract E transformException(final C context, final Exception exception);

    private void writeLog(C context, Exception exception) {
        if (log.isWarnEnabled()) {
            final String invokeEvent = context.getInvokeEvent();
            log.warn("[Invoker.Fail] msg = invokeEvent:{} invoker fail. ", invokeEvent, exception);
        }
    }

    public final <R> R get(final C context) {
        try {
            return super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
            return null;
        }
    }

    public final <R> R get(final C context, final Consumer<E> consumer) {
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
        try {
            return super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
            final E exception = transformException(context, e);
            throw function.apply(exception);
        }
    }

    public final <R, TE extends Throwable> R getElseThrow(final C context, final Function<E, ? extends TE> function) throws TE {
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
