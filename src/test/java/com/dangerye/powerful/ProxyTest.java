package com.dangerye.powerful;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class ProxyTest {

    @Test
    public void test1() {
        Intf intf = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), new Class[]{Intf.class}, new Handler(null));
        intf.doSomething();
    }

    @Test
    public void test2() {
        Intf intf = new Impl();
        intf.doSomething();
        Intf proxy = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), intf.getClass().getInterfaces(), new Handler(intf));
        proxy.doSomething();
        Class<?>[] classes1 = Intf.class.getClasses();
        Class<?>[] classes2 = Intf.class.getDeclaredClasses();
        Class<?>[] classes3 = Intf.class.getInterfaces();
        Class<?>[] classes4 = intf.getClass().getInterfaces();
        for (Class<?> item : Intf.class.getClasses()) {
            System.out.println(item.getName());
        }
    }

    public interface Intf {
        void doSomething();
    }

    public static class Impl implements Intf {
        @Override
        public void doSomething() {
            System.out.println("impl class run...");
        }
    }

    public static class Handler implements InvocationHandler {

        private Intf proxyClass;

        public Handler(Intf proxyClass) {
            this.proxyClass = proxyClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Type genericReturnType = method.getGenericReturnType();
            System.out.println("genericReturnType: " + genericReturnType.getTypeName());
            return method.invoke(proxyClass, args);
        }
    }
}
