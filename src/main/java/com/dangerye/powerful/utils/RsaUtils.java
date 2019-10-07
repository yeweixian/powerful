package com.dangerye.powerful.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RsaUtils {

    private static final String RSA = "RSA";

    /**
     * 生成公钥与私钥对
     *
     * @return 公钥Key=publicKey 私钥Key=privateKey
     */
    public static Map<String, String> getKeys() throws Exception {
        //rsa算法长度,生成秘钥对时用
        int len = 2048;
        // 获得公钥与私钥对
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
        keyPairGen.initialize(len);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPublicKey rsapublicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKey = Base64.encodeBase64String(rsapublicKey.getEncoded());
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String privateKey = Base64.encodeBase64String(rsaPrivateKey.getEncoded());

        Map<String, String> keys = new HashMap<>(2);
        keys.put("publicKey", publicKey);
        keys.put("privateKey", privateKey);
        return keys;
    }

    /**
     * 从字符串中加载公钥
     */
    private static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("无此算法", e);
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            log.error("公钥非法", e);
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            log.error("公钥数据为空", e);
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从字符串中加载私钥
     */
    private static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("无此算法", e);
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            log.error("私钥非法", e);
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            log.error("私钥数据为空", e);
            throw new Exception("私钥数据为空");
        }
    }

    private static String encrypt(Key key, byte[] data) throws Exception {
        if (key == null) {
            throw new Exception("密钥为空, 请设置");
        }
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] output = cipher.doFinal(data);
            return Base64.encodeBase64String(output);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
            throw new Exception("NoSuchAlgorithmException");
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException", e);
            return null;
        } catch (InvalidKeyException e) {
            log.error("密钥非法,请检查", e);
            throw new Exception("密钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            log.error("长度非法", e);
            throw new Exception("长度非法");
        } catch (BadPaddingException e) {
            log.error("数据已损坏", e);
            throw new Exception("数据已损坏");
        }
    }

    private static String decrypt(Key key, byte[] data) throws Exception {
        if (key == null) {
            throw new Exception("密钥为空, 请设置");
        }
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] output = cipher.doFinal(data);
            return new String(output);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
            throw new Exception("NoSuchAlgorithmException");
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException", e);
            return null;
        } catch (InvalidKeyException e) {
            log.error("密钥非法,请检查", e);
            throw new Exception("密钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            log.error("长度非法", e);
            throw new Exception("长度非法");
        } catch (BadPaddingException e) {
            log.error("数据已损坏", e);
            throw new Exception("数据已损坏");
        }
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String content, String publicKey) {
        String res = "";
        try {
            res = encrypt(loadPublicKeyByStr(publicKey), content.getBytes());
        } catch (Exception e) {
            log.error("公钥加密出错", e);
        }
        return res;
    }

    /**
     * 私钥加密
     */
    public static String privateEncrypt(String content, String privateKey) {
        String res = "";
        try {
            res = encrypt(loadPrivateKeyByStr(privateKey), content.getBytes());
        } catch (Exception e) {
            log.error("私钥加密出错", e);
        }
        return res;
    }

    /**
     * 公钥解密
     */
    public static String publicDecrypt(String content, String publicKey) throws Exception {
        String res;
        try {
            res = decrypt(loadPublicKeyByStr(publicKey), Base64.decodeBase64(content));
        } catch (Exception e) {
            log.error("公钥解密出错", e);
            throw new Exception(e);
        }
        return res;
    }

    /**
     * 私钥解密
     */
    public static String privateDecrypt(String content, String privateKey) throws Exception {
        String res;
        try {
            res = decrypt(loadPrivateKeyByStr(privateKey), Base64.decodeBase64(content));
        } catch (Exception e) {
            log.error("私钥解密出错", e);
            throw new Exception(e);
        }
        return res;
    }
}
