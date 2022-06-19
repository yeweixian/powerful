package com.dangerye.powerful.communicate;

import java.util.Map;
import java.util.concurrent.Callable;

public interface Invoker {

    interface Context {
        String getSupplier();

        Map<String, Object> getParamMap();
    }

    abstract class Interceptor {
        protected abstract <R> R intercept(final Invocation<R> invocation) throws Exception;

        <R> Callable<R> plugin(final Callable<R> plugin, final Context context) {
            return () -> intercept(new Invocation<>(plugin, context));
        }
    }

    final class Invocation<R> {
        private final Callable<R> callable;
        private final Context context;

        private Invocation(final Callable<R> callable, final Context context) {
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
