package com.dangerye.powerful.invoker;

import com.dangerye.powerful.concurrent.AbstractDefaultInvoker;
import com.dangerye.powerful.concurrent.InvokeDefaultContext;
import com.dangerye.powerful.concurrent.InvokeInterceptor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class TestInvoker1 extends AbstractDefaultInvoker<InvokeDefaultContext, Exception> {
    @Override
    protected void increaseLog(InvokeDefaultContext context, Exception exception) {
    }

    @Override
    protected Exception transformException(InvokeDefaultContext context, Exception exception) {
        return exception;
    }

    @Override
    protected <R> R coreCode(InvokeDefaultContext context) throws Exception {
        System.out.println(context.getInvokeSign());
        return null;
    }

    @Override
    protected Collection<InvokeInterceptor<? super InvokeDefaultContext>> invokeInterceptors(InvokeDefaultContext context) {
        return null;
    }
}
