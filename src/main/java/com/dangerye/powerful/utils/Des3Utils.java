package com.dangerye.powerful.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class Des3Utils {

    public static String encodeECB(String plainData, String key) throws Exception {
        byte[] dataBuffer = plainData.getBytes(StandardCharsets.UTF_8);
        byte[] keyByte = Base64.getDecoder().decode(key);

        DESedeKeySpec spec = new DESedeKeySpec(keyByte);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
        Key desKey = keyFactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, desKey);

        byte[] byteOut = cipher.doFinal(dataBuffer);

        return Base64.getEncoder().encodeToString(byteOut);
    }

    public static String decodeECB(String dataToDecode, String key) throws Exception {
        byte[] dataBuffers = Base64.getDecoder().decode(dataToDecode);
        byte[] keyBuffers = Base64.getDecoder().decode(key);

        DESedeKeySpec spec = new DESedeKeySpec(keyBuffers);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
        Key desKey = keyFactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, desKey);

        byte[] byteOut = cipher.doFinal(dataBuffers);

        return new String(byteOut, StandardCharsets.UTF_8);
    }
}
