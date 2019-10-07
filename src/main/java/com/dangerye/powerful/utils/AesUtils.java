package com.dangerye.powerful.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

@Slf4j
public class AesUtils {

    private synchronized static void overrideKeyLength() {
        String errorString = "Failed manually overriding key-length permissions.";
        int newMaxKeyLength = 0;
        try {
            if ((newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES")) < 256) {
                LogUtils.info(log, "AesUtils overrideKeyLength",
                        "run begin, overriding AES key-length permissions...");
                Class c = Class.forName("javax.crypto.CryptoAllPermissionCollection");
                Constructor con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissionCollection = con.newInstance();
                Field f = c.getDeclaredField("all_allowed");
                f.setAccessible(true);
                f.setBoolean(allPermissionCollection, true);
                c = Class.forName("javax.crypto.CryptoPermissions");
                con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissions = con.newInstance();
                f = c.getDeclaredField("perms");
                f.setAccessible(true);
                ((Map) f.get(allPermissions)).put("*", allPermissionCollection);
                c = Class.forName("javax.crypto.JceSecurityManager");
                f = c.getDeclaredField("defaultPolicy");
                f.setAccessible(true);
                Field mf = Field.class.getDeclaredField("modifiers");
                mf.setAccessible(true);
                mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(null, allPermissions);
                newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
                LogUtils.info(log, "AesUtils overrideKeyLength",
                        "run end, overriding AES key-length permissions...");
            }
        } catch (Exception e) {
            LogUtils.error(log, "AesUtils Hack failed",
                    errorString, e);
        }
        if (newMaxKeyLength < 256) {
            LogUtils.warn(log, "AesUtils Hack failed",
                    errorString + "newMaxKeyLength:{}", newMaxKeyLength);
        }
    }
}
