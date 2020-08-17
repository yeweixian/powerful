package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyTest {

    @Test
    public void test1() {
        Intf intf = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), new Class[]{Intf.class}, new Handler(null));
        intf.doSomething("now", "InTest1");
    }

    @Test
    public void test2() {
        Intf intf = new Impl();
//        intf.doSomething("now", "InTest2");
        intf = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), intf.getClass().getInterfaces(), new Handler(intf));
        intf = (Intf) Proxy.newProxyInstance(Handler.class.getClassLoader(), intf.getClass().getInterfaces(), new Handler(intf));
        try {
            intf.doSomething("now", "InProxy");
        } catch (NullPointerException npe) {
            System.out.println("------ NullPointerException");
            npe.printStackTrace();
        } catch (Exception e) {
            System.out.println("------ Exception");
            e.printStackTrace();
        }
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
            throw new NullPointerException();
        }
    }

    public static class Handler implements InvocationHandler {

        private Intf proxyClass;

        public Handler(Intf proxyClass) {
            this.proxyClass = proxyClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            Type genericReturnType = method.getGenericReturnType();
//            System.out.println(genericReturnType.equals(Void.TYPE));
//            System.out.println("genericReturnType: " + genericReturnType.getTypeName());
            System.out.println("------");
            System.out.println("method: " + method.getName());
            System.out.println("classname: " + method.getDeclaringClass().getSimpleName());
            System.out.println("------");
            System.out.println(JSON.toJSONString(args));
            System.out.println("------");
//            try {
            try {
                return method.invoke(proxyClass, args);
            } catch (InvocationTargetException ite) {
                throw ite.getCause();
            }
//            } catch (Throwable t) {
//                Throwable cause = t.getCause();
//                if (cause instanceof NullPointerException) {
//                    System.out.println("NullPointerException");
//                    throw cause;
//                } else {
//                    System.out.println("OtherException");
//                }
//                if (t instanceof NullPointerException) {
//                    System.out.println("NullPointerException");
//                } else {
//                    System.out.println("OtherException");
//                }
//                throw t;
//            }
        }
    }
}
