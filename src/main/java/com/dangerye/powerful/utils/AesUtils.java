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
                Class cryptoAllPermissionCollectionClass = Class.forName("javax.crypto.CryptoAllPermissionCollection");

                Constructor cryptoAllPermissionCollectionConstructor = cryptoAllPermissionCollectionClass.getDeclaredConstructor();
                cryptoAllPermissionCollectionConstructor.setAccessible(true);
                Object cryptoAllPermissionCollection = cryptoAllPermissionCollectionConstructor.newInstance();

                Field all_allowedInCryptoAllPermissionCollection = cryptoAllPermissionCollectionClass.getDeclaredField("all_allowed");
                all_allowedInCryptoAllPermissionCollection.setAccessible(true);
                all_allowedInCryptoAllPermissionCollection.setBoolean(cryptoAllPermissionCollection, true);

                Class cryptoPermissionsClass = Class.forName("javax.crypto.CryptoPermissions");

                Constructor cryptoPermissionsConstructor = cryptoPermissionsClass.getDeclaredConstructor();
                cryptoPermissionsConstructor.setAccessible(true);
                Object cryptoPermissions = cryptoPermissionsConstructor.newInstance();

                Field permsInCryptoPermissions = cryptoPermissionsClass.getDeclaredField("perms");
                permsInCryptoPermissions.setAccessible(true);
                ((Map) permsInCryptoPermissions.get(cryptoPermissions)).put("*", cryptoAllPermissionCollection);

                Class jceSecurityManagerClass = Class.forName("javax.crypto.JceSecurityManager");

                Field defaultPolicyInJceSecurityManager = jceSecurityManagerClass.getDeclaredField("defaultPolicy");
                defaultPolicyInJceSecurityManager.setAccessible(true);
                int modifiers = defaultPolicyInJceSecurityManager.getModifiers() & ~Modifier.FINAL;

                Field modifiersInField = Field.class.getDeclaredField("modifiers");
                modifiersInField.setAccessible(true);
                modifiersInField.setInt(defaultPolicyInJceSecurityManager, modifiers);

                defaultPolicyInJceSecurityManager.set(null, cryptoPermissions);

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
