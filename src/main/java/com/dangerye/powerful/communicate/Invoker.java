package com.dangerye.powerful.communicate;

import org.apache.commons.collections4.Predicate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

public abstract class Invoker<C> {

    private final CodeFunction<C> codeFunction;

    protected Invoker() {
        this.codeFunction = new CodeFunction<C>() {
            @Override
            public <R> R execute(C context) throws Exception {
                //Assert.notNull(context, "context must not be null");
                //Assert.notNull(context.getTarget(), "target must not be null");
                //Assert.notNull(context.getInvokeEvent(), "invokeEvent must not be null");
                final Collection<Interceptor<? super C>> interceptors = invokeInterceptors(context);
                try (CloseableContext<? super C> closeableContext = new CloseableContext<>(getConfigures(interceptors))) {
                    closeableContext.configure(context);
                    final Callable<R> core = () -> coreCode(context);
                    final Callable<R> proxy = getProxy(core, interceptors, context);
                    return proxy.call();
                }
            }
        };
    }

    protected final <R> R execute(C context) throws Exception {
        return codeFunction.execute(context);
    }

    protected final Collection<Configure<? super C>> getConfigures(final Collection<? extends Configure<? super C>> collection) {
        final Collection<Configure<? super C>> result = new ArrayList<>();
        if (collection != null) {
            for (Configure<? super C> configure : collection) {
                if (configure != null) {
                    result.add(configure);
                }
            }
        }
        return result;
    }

    private <R> Callable<R> getProxy(final Callable<R> codeFunction, final Collection<Interceptor<? super C>> interceptors, C context) {
        Callable<R> plugin = codeFunction;
        if (interceptors != null) {
            for (Interceptor<? super C> interceptor : interceptors) {
                if (interceptor != null) {
                    plugin = interceptor.plugin(plugin, context);
                }
            }
        }
        return plugin;
    }

    protected abstract <R> R coreCode(final C context) throws Exception;

    protected abstract Collection<Interceptor<? super C>> invokeInterceptors(final C context);

    public interface Configure<C> extends AutoCloseable {
        void configure(C context);
    }

    @FunctionalInterface
    private interface CodeFunction<C> {
        <R> R execute(C context) throws Exception;
    }

    public static final class CloseableContext<C> implements Configure<C> {
        private final Collection<Configure<? super C>> collection;

        public CloseableContext(Collection<Configure<? super C>> collection) {
            this.collection = collection;
        }

        @Override
        public void configure(C context) {
            if (collection != null) {
                for (Configure<? super C> configure : collection) {
                    if (configure != null) {
                        configure.configure(context);
                    }
                }
            }
        }

        @Override
        public void close() throws Exception {
            if (collection != null) {
                for (Configure<? super C> configure : collection) {
                    if (configure != null) {
                        configure.close();
                    }
                }
            }
        }
    }

    public static abstract class CollectionFilter<I, C> implements Predicate<I>, Configure<C> {
        private final ThreadLocal<C> threadLocal = new ThreadLocal<>();

        @Override
        public void configure(C context) {
            threadLocal.set(context);
        }

        @Override
        public void close() throws Exception {
            threadLocal.remove();
        }

        @Override
        public boolean evaluate(I item) {
            if (item == null) {
                return false;
            }
            final C context = threadLocal.get();
            Assert.notNull(context, "context must not be null");
            return doFilter(item, context);
        }

        protected abstract boolean doFilter(I item, C context);
    }

    public static abstract class Interceptor<C> implements Configure<C> {
        protected abstract <R> R intercept(final Invocation<R, C> invocation) throws Exception;

        private <R> Callable<R> plugin(final Callable<R> plugin, C context) {
            return () -> intercept(new Invocation<>(plugin, context));
        }
    }

    public static final class Invocation<R, C> {
        private final Callable<R> codeFunction;
        private final C context;

        private Invocation(Callable<R> codeFunction, C context) {
            this.codeFunction = codeFunction;
            this.context = context;
        }

        public C getContext() {
            return context;
        }

        public R proceed() throws Exception {
            return codeFunction.call();
        }
    }
}
