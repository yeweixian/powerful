package com.dangerye.powerful;

import org.junit.Test;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class ProxyTest {

    @Test
    public void test1() {
        Intf intf = (Intf) Proxy.newProxyInstance(ProxyTest.class.getClassLoader(),
                Intf.class.getInterfaces(),
                (proxy, method, args) -> {
                    Type genericReturnType = method.getGenericReturnType();
                    System.out.println("genericReturnType: " + genericReturnType.getTypeName());
                    return null;
                });
        intf.doSomething();
    }

    public interface Intf {
        void doSomething();
    }
}
