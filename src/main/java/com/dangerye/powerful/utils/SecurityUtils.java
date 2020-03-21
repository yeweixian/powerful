package com.dangerye.powerful.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public final class SecurityUtils {

    public static String encrypt(final String message, final String secretKey) {
        if (StringUtils.isBlank(message)) return null;
        String randomStr = String.valueOf(RandomUtils.nextInt(10000, 99999));
        char[] randomMD5 = DigestUtils.md5Hex(randomStr).toCharArray();
        char[] secretMD5 = DigestUtils.md5Hex(secretKey).toCharArray();

        StringBuilder encrypt1 = new StringBuilder();
        char[] msgChars = message.toCharArray();
        for (int i = 0; i < msgChars.length; i++) {
            int k = i % 32;
            encrypt1
                    .append(randomMD5[k])
                    .append((char) (msgChars[i] ^ randomMD5[k]));
        }

        StringBuilder encrypt2 = new StringBuilder();
        msgChars = encrypt1.toString().toCharArray();
        for (int i = 0; i < msgChars.length; i++) {
            int k = i % 32;
            encrypt2
                    .append((char) (msgChars[i] ^ secretMD5[k]));
        }

        return Base64.encodeBase64String(encrypt2.toString().getBytes());
    }

    public static String decrypt(final String ciphertext, final String secretKey) {
        if (StringUtils.isBlank(ciphertext)) return null;
        char[] secretMD5 = DigestUtils.md5Hex(secretKey).toCharArray();

        StringBuilder decrypt2 = new StringBuilder();
        char[] msgChars = new String(Base64.decodeBase64(ciphertext)).toCharArray();
        for (int i = 0; i < msgChars.length; i++) {
            int k = i % 32;
            decrypt2
                    .append((char) (msgChars[i] ^ secretMD5[k]));
        }

        StringBuilder decrypt1 = new StringBuilder();
        msgChars = decrypt2.toString().toCharArray();
        for (int i = 0; i < msgChars.length; i += 2) {
            decrypt1
                    .append((char) (msgChars[i + 1] ^ msgChars[i]));
        }

        return decrypt1.toString();
    }

    public static TestBuilder testBuilder(String message) {
        return new TestBuilder(message, null);
    }

    public static TestBuilder testBuilder(String message, Map<String, Object> extendMap) {
        return new TestBuilder(message, extendMap);
    }

    public interface EncryptFunction {
        String encrypt(String message, Map<String, Object> extendMap) throws Exception;
    }

    public interface EncryptFunctionWithoutExtend {
        String encrypt(String message) throws Exception;
    }

    public interface DecryptFunction {
        String decrypt(String ciphertext, Map<String, Object> extendMap) throws Exception;
    }

    public interface DecryptFunctionWithoutExtend {
        String decrypt(String ciphertext) throws Exception;
    }

    public static class TestBuilder {
        private String message;
        private String ciphertext;
        private String result;
        private Map<String, Object> extendMap;
        private Exception exception;

        private TestBuilder(String message, Map<String, Object> extendMap) {
            if (StringUtils.isBlank(message)) throw new IllegalArgumentException("Message must not be blank.");
            this.message = message;
            this.extendMap = extendMap;
        }

        public TestBuilder encrypt(EncryptFunction function) {
            if (this.exception != null) return this;
            try {
                this.ciphertext = function.encrypt(this.message, this.extendMap);
            } catch (Exception e) {
                this.exception = e;
            }
            return this;
        }

        public TestBuilder decrypt(DecryptFunction function) {
            if (this.exception != null) return this;
            try {
                this.result = function.decrypt(this.ciphertext, this.extendMap);
            } catch (Exception e) {
                this.exception = e;
            }
            return this;
        }

        public TestBuilder encrypt(EncryptFunctionWithoutExtend function) {
            if (this.exception != null) return this;
            try {
                this.ciphertext = function.encrypt(this.message);
            } catch (Exception e) {
                this.exception = e;
            }
            return this;
        }

        public TestBuilder decrypt(DecryptFunctionWithoutExtend function) {
            if (this.exception != null) return this;
            try {
                this.result = function.decrypt(this.ciphertext);
            } catch (Exception e) {
                this.exception = e;
            }
            return this;
        }

        public void test() {
            LogUtils.info(log, "SecurityUtils Test",
                    "extendMap:{}, message:{}, ciphertext:{}, result:{}",
                    JSON.toJSONString(extendMap), message, ciphertext, result, exception);
        }
    }
}
