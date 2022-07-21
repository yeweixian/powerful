package com.dangerye.powerful.trial;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Trial01<C> {

    private final CodeFunction<C> codeFunction;

    protected Trial01() {
        this.codeFunction = new CodeFunction<C>() {
            @Override
            public <P, R> R execute(P param, C context) {
                R result = null;
                Exception exception = null;
                final Collection<Interceptor<C>> interceptors = interceptorCollection(param, context);
                final Collection<Configure<C>> collection = new ArrayList<>();
                if (interceptors != null) {
                    for (Interceptor<C> interceptor : interceptors) {
                        if (interceptor != null) {
                            collection.add(interceptor);
                        }
                    }
                }
                try (CloseableContext<C> closeableContext = new CloseableContext<>(collection)) {
                    closeableContext.configure(context);
                    if (interceptors != null) {
                        for (Interceptor<C> interceptor : interceptors) {
                            if (interceptor != null) {
                                param = interceptor.before(param, context);
                            }
                        }
                    }
                    result = coreCode(param, context);
                } catch (Exception e) {
                    exception = e;
                }
                if (interceptors != null) {
                    for (Interceptor<C> interceptor : interceptors) {
                        if (interceptor != null) {
                            interceptor.after(result, context, exception);
                        }
                    }
                }
                return result;
            }
        };
    }

    protected abstract <P, R> R coreCode(P param, C context) throws Exception;

    protected abstract <P> Collection<Interceptor<C>> interceptorCollection(P param, C context);

    public interface Configure<C> extends AutoCloseable {
        void configure(C context);
    }

    public interface Interceptor<C> extends Configure<C> {
        <P> P before(P param, C context);

        <R> void after(R result, C context, Exception exception);
    }

    private interface CodeFunction<C> {
        <P, R> R execute(P param, C context);
    }

    private static final class CloseableContext<C> implements Configure<C> {

        private final Collection<Configure<C>> collection;

        private CloseableContext(Collection<Configure<C>> collection) {
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
}
