package com.dangerye.powerful;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class ProxyTest {

    @Test
    public void test1() {
        Intf intf = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), new Class[]{Intf.class}, new Handler());
        intf.doSomething();
    }

    public interface Intf {
        void doSomething();
    }

    public static class Handler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Type genericReturnType = method.getGenericReturnType();
            System.out.println("genericReturnType: " + genericReturnType.getTypeName());
            return null;
        }
    }
}
