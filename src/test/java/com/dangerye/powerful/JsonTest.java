package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonTest {

    @Test
    public void testParseJson() {
        final Demo demo = Demo.builder()
                .base("string")
                .array(Lists.newArrayList("A", "B", "C", "D", "E", "F", "G"))
                .map(ImmutableMap.<String, String>builder()
                        .put("key1", "val1")
                        .put("key2", "val2")
                        .put("key3", "val3")
                        .build())
                .mapList(Lists.newArrayList(
                        ImmutableMap.<String, String>builder()
                                .put("key1", "val4")
                                .put("key2", "val5")
                                .put("key3", "val6")
                                .build(),
                        ImmutableMap.<String, String>builder()
                                .put("key1", "val7")
                                .put("key2", "val8")
                                .put("key3", "val9")
                                .build()))
                .build();
        final String json = JSON.toJSONString(demo);
        final Map<String, String> parse = JsonParser.<Map<String, String>>builder()
                .setJsonString(json)
                .parseObj();
        Optional.ofNullable(parse)
                .ifPresent(map -> map.forEach((key, val) -> {
                    final String printString = "key:%s, val:%s";
                    System.out.println(String.format(printString, key, val));
                }));
    }

    public static class JsonParser<T> {

        private String jsonString;

        private JsonParser() {
        }

        public static <T> JsonParser<T> builder() {
            return new JsonParser<>();
        }

        public JsonParser<T> setJsonString(String jsonString) {
            this.jsonString = jsonString;
            return this;
        }

        public T parseObj() {
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            try {
                final Type type = new TypeReference<T>() {
                }.getType();
                return JSON.parseObject(jsonString, type);
            } catch (Exception e) {
                throw new IllegalArgumentException("jsonString format exception");
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Demo {
        private String base;
        private List<String> array;
        private Map<String, String> map;
        private List<Map<String, String>> mapList;
    }
}
