package com.dangerye.powerful.communicate;

import org.apache.commons.collections4.Predicate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Invoker<T, C extends InvokeContext<? extends T>> {

    private final CodeFunction<C> codeFunction;

    protected Invoker() {
        this.codeFunction = new CodeFunction<C>() {
            @Override
            public <R> R execute(C context) throws Exception {
                Assert.notNull(context, "context must not be null");
                Assert.notNull(context.getTarget(), "target must not be null");
                final Collection<Interceptor<C>> interceptors = invokeInterceptors(context);
                try (CloseableContext<C> closeableContext = new CloseableContext<>(getConfigures(interceptors))) {
                    closeableContext.configure(context);
                    final CodeFunction<C> core = Invoker.this::coreCode;
                    final CodeFunction<C> proxy = getProxy(core, interceptors);
                    return proxy.execute(context);
                }
            }
        };
    }

    protected final Collection<Configure<C>> getConfigures(final Collection<? extends Configure<C>> collection) {
        final Collection<Configure<C>> result = new ArrayList<>();
        if (collection != null) {
            for (Configure<C> configure : collection) {
                if (configure != null) {
                    result.add(configure);
                }
            }
        }
        return result;
    }

    private CodeFunction<C> getProxy(final CodeFunction<C> codeFunction, final Collection<Interceptor<C>> interceptors) {
        CodeFunction<C> plugin = codeFunction;
        if (interceptors != null) {
            for (Interceptor<C> interceptor : interceptors) {
                if (interceptor != null) {
                    plugin = interceptor.plugin(plugin);
                }
            }
        }
        return plugin;
    }

    protected abstract <R> R coreCode(final C context) throws Exception;

    protected abstract Collection<Interceptor<C>> invokeInterceptors(final C context);

    public interface Configure<C> extends AutoCloseable {
        void configure(C context);
    }

    @FunctionalInterface
    private interface CodeFunction<C> {
        <R> R execute(C context) throws Exception;
    }

    public static final class CloseableContext<C> implements Configure<C> {
        private final Collection<Configure<C>> collection;

        public CloseableContext(Collection<Configure<C>> collection) {
            this.collection = collection;
        }

        @Override
        public void configure(C context) {
            if (collection != null) {
                for (Configure<C> configure : collection) {
                    if (configure != null) {
                        configure.configure(context);
                    }
                }
            }
        }

        @Override
        public void close() throws Exception {
            if (collection != null) {
                for (Configure<C> configure : collection) {
                    if (configure != null) {
                        configure.close();
                    }
                }
            }
        }
    }

    public static abstract class CollectionFilter<I, C extends InvokeContext<? extends Collection<? extends I>>>
            implements Predicate<I>, Configure<C> {
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
        protected abstract <R> R intercept(final Invocation<C> invocation) throws Exception;

        private CodeFunction<C> plugin(final CodeFunction<C> plugin) {
            return new CodeFunction<C>() {
                @Override
                public <R> R execute(C context) throws Exception {
                    return intercept(new Invocation<>(plugin, context));
                }
            };
        }
    }

    public static final class Invocation<C> {
        private final CodeFunction<C> codeFunction;
        private final C context;

        private Invocation(CodeFunction<C> codeFunction, C context) {
            this.codeFunction = codeFunction;
            this.context = context;
        }

        public C getContext() {
            return context;
        }

        public <R> R proceed() throws Exception {
            return codeFunction.execute(context);
        }
    }
}
