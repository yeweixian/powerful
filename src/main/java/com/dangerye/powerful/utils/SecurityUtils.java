package com.dangerye.powerful.utils;

import com.dangerye.powerful.builder.CollectionBuilder;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public final class SecurityUtils {

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
            Gson gson = new Gson();
            Map<String, Object> testInfo = CollectionBuilder.<String, Object>mapBuilder(new LinkedHashMap<>())
                    .put("extendMap", extendMap)
                    .put("message", message)
                    .put("ciphertext", ciphertext)
                    .put("result", result)
                    .build();
            LogUtils.info(log, "SecurityUtils Test",
                    "testInfo:{}", gson.toJson(testInfo), this.exception);
        }
    }
}
