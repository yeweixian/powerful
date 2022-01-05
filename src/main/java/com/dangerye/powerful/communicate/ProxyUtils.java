package com.dangerye.powerful.communicate;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.concurrent.Callable;

public final class ProxyUtils {
    public static <R> Callable<R> getCallable(final Callable<R> callable,
                                              final Collection<Interceptor> interceptors,
                                              final Context context) {
        Assert.notNull(callable, "callable must not be null");
        Assert.notNull(interceptors, "interceptors must not be null");
        Assert.notNull(context, "context must not be null");
        Callable<R> plugin = callable;
        for (Interceptor interceptor : interceptors) {
            plugin = interceptor.plugin(plugin, context);
        }
        return plugin;
    }

    public interface Context {
    }

    public static abstract class Interceptor {
        protected abstract <R> R intercept(Invocation<R> invocation) throws Exception;

        private <R> Callable<R> plugin(Callable<R> plugin, Context context) {
            return () -> intercept(new Invocation<>(plugin, context));
        }
    }

    public static class Invocation<R> {
        private final Callable<R> callable;
        private final Context context;

        private Invocation(Callable<R> callable, Context context) {
            this.callable = callable;
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public R proceed() throws Exception {
            return callable.call();
        }
    }
}
