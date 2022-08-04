package com.dangerye.powerful.invoker;

import com.dangerye.powerful.communicate.Invoker;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public final class Test2Invoker extends Invoker<Invoker.InvokeContext> {
    @Override
    protected <R> R coreCode(InvokeContext context) throws Exception {
        return null;
    }

    @Override
    protected Collection<Interceptor<? super InvokeContext>> invokeInterceptors(InvokeContext context) {
        return null;
    }
}
