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

        Class cryptoAllPermissionCollectionClass = Class.forName("javax.crypto.CryptoAllPermissionCollection");
        System.out.println(cryptoAllPermissionCollectionClass.getName());

        Constructor cryptoAllPermissionCollectionConstructor = cryptoAllPermissionCollectionClass.getDeclaredConstructor();
        cryptoAllPermissionCollectionConstructor.setAccessible(true);
        Object cryptoAllPermissionCollection = cryptoAllPermissionCollectionConstructor.newInstance();

        Field all_allowedInCryptoAllPermissionCollection = cryptoAllPermissionCollectionClass.getDeclaredField("all_allowed");
        all_allowedInCryptoAllPermissionCollection.setAccessible(true);
        System.out.println("all_allowed: " + all_allowedInCryptoAllPermissionCollection.getBoolean(cryptoAllPermissionCollection));
        all_allowedInCryptoAllPermissionCollection.setBoolean(cryptoAllPermissionCollection, true);
        System.out.println("all_allowed: " + all_allowedInCryptoAllPermissionCollection.getBoolean(cryptoAllPermissionCollection));

        Class cryptoPermissionsClass = Class.forName("javax.crypto.CryptoPermissions");
        System.out.println(cryptoPermissionsClass.getName());

        Constructor cryptoPermissionsConstructor = cryptoPermissionsClass.getDeclaredConstructor();
        cryptoPermissionsConstructor.setAccessible(true);
        Object cryptoPermissions = cryptoPermissionsConstructor.newInstance();

        Field permsInCryptoPermissions = cryptoPermissionsClass.getDeclaredField("perms");
        permsInCryptoPermissions.setAccessible(true);
        Map map = (Map) permsInCryptoPermissions.get(cryptoPermissions);
        map.keySet().forEach(System.out::println);
        map.put("*", cryptoAllPermissionCollection);
        map.keySet().forEach(System.out::println);

        Class jceSecurityManagerClass = Class.forName("javax.crypto.JceSecurityManager");
        System.out.println(jceSecurityManagerClass.getName());

        Field defaultPolicyInJceSecurityManager = jceSecurityManagerClass.getDeclaredField("defaultPolicy");
        defaultPolicyInJceSecurityManager.setAccessible(true);
        System.out.println("defaultPolicyInClass3 modifiers val: " + defaultPolicyInJceSecurityManager.getModifiers());
        int newVal = defaultPolicyInJceSecurityManager.getModifiers() & ~Modifier.FINAL;
        System.out.println("defaultPolicyInClass3 modifiers newVal: " + newVal);

        Field modifiersInField = Field.class.getDeclaredField("modifiers");
        modifiersInField.setAccessible(true);
        modifiersInField.setInt(defaultPolicyInJceSecurityManager, newVal);

        defaultPolicyInJceSecurityManager.set(null, cryptoPermissions);

        System.out.println("AES: " + Cipher.getMaxAllowedKeyLength("AES"));
    }
}
