package com.dangerye.powerful;

import org.junit.Test;

import javax.crypto.Cipher;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class ReflectTest {

    @Test
    public void testReflectMsg() throws Exception {

        System.out.println("AES: " + Cipher.getMaxAllowedKeyLength("AES"));

        Class class1 = Class.forName("javax.crypto.CryptoAllPermissionCollection");
        System.out.println(class1.getName());

        Constructor classConstructor1 = class1.getDeclaredConstructor();
        classConstructor1.setAccessible(true);
        Object cryptoAllPermissionCollection = classConstructor1.newInstance();

        Field all_allowedInClass1 = class1.getDeclaredField("all_allowed");
        all_allowedInClass1.setAccessible(true);
        System.out.println("all_allowed: " + all_allowedInClass1.getBoolean(cryptoAllPermissionCollection));
        all_allowedInClass1.setBoolean(cryptoAllPermissionCollection, true);
        System.out.println("all_allowed: " + all_allowedInClass1.getBoolean(cryptoAllPermissionCollection));

        Class class2 = Class.forName("javax.crypto.CryptoPermissions");
        System.out.println(class2.getName());

        Constructor classConstructor2 = class2.getDeclaredConstructor();
        classConstructor2.setAccessible(true);
        Object cryptoPermissions = classConstructor2.newInstance();

        Field permsInClass2 = class2.getDeclaredField("perms");
        permsInClass2.setAccessible(true);
        Map map = (Map) permsInClass2.get(cryptoPermissions);
        map.keySet().forEach(System.out::println);
        map.put("*", cryptoAllPermissionCollection);
        map.keySet().forEach(System.out::println);

        Class class3 = Class.forName("javax.crypto.JceSecurityManager");
        System.out.println(class3.getName());

        Field defaultPolicyInClass3 = class3.getDeclaredField("defaultPolicy");
        defaultPolicyInClass3.setAccessible(true);
        System.out.println("defaultPolicyInClass3 modifiers val: " + defaultPolicyInClass3.getModifiers());
        int newVal = defaultPolicyInClass3.getModifiers() & ~Modifier.FINAL;
        System.out.println("defaultPolicyInClass3 modifiers newVal: " + newVal);

        Field modifiersInField = Field.class.getDeclaredField("modifiers");
        modifiersInField.setAccessible(true);
        modifiersInField.setInt(defaultPolicyInClass3, newVal);

        defaultPolicyInClass3.set(null, cryptoPermissions);

        System.out.println("AES: " + Cipher.getMaxAllowedKeyLength("AES"));
    }
}
