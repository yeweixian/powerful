package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class ProxyTest {

    @Test
    public void test1() {
        Intf intf = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), new Class[]{Intf.class}, new Handler(null));
        intf.doSomething("now", "InTest1");
    }

    @Test
    public void test2() {
        Intf intf = new Impl();
        intf.doSomething("now", "InTest2");
        Intf proxy = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), intf.getClass().getInterfaces(), new Handler(intf));
        proxy.doSomething("now", "InProxy");
        Class<?>[] classes1 = Intf.class.getClasses();
        Class<?>[] classes2 = Intf.class.getDeclaredClasses();
        Class<?>[] classes3 = Intf.class.getInterfaces();
        Class<?>[] classes4 = intf.getClass().getInterfaces();
        for (Class<?> item : Intf.class.getClasses()) {
            System.out.println(item.getName());
        }
    }

    public interface Intf {
        void doSomething(String when, String where);
    }

    public static class Impl implements Intf {
        @Override
        public void doSomething(String when, String where) {
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
            System.out.println(genericReturnType.equals(Void.TYPE));
            System.out.println("genericReturnType: " + genericReturnType.getTypeName());
            System.out.println("------");
            System.out.println("method: " + method.getName());
            System.out.println("classname: " + method.getDeclaringClass().getSimpleName());
            System.out.println("------");
            System.out.println(JSON.toJSONString(args));
            System.out.println("------");
            try {
                return method.invoke(proxyClass, args);
            } catch (Throwable t) {
                if (t instanceof NullPointerException) {
                    System.out.println("NullPointerException");
                } else {
                    System.out.println("OtherException");
                }
                throw t;
            }
        }
    }
}
