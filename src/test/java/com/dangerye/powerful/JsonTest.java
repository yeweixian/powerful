package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

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
        final Map<String, String> parse = JSON.parseObject(json, new TypeReference<Map<String, String>>() {
        }.getType());
        Optional.ofNullable(parse)
                .ifPresent(map -> {
                    map.forEach((key, val) -> {
                        final String printString = "key:%s, val:%s";
                        System.out.println(String.format(printString, key, val));
                    });
                    final List<String> array = JSON.parseObject(map.get("array"), new TypeReference<List<String>>() {
                    }.getType());
                    Optional.ofNullable(array)
                            .ifPresent(list -> list.forEach(System.out::println));
                    final Map<String, String> demoMap = JSON.parseObject(map.get("map"), new TypeReference<Map<String, String>>() {
                    }.getType());
                    Optional.ofNullable(demoMap)
                            .ifPresent(objMap -> objMap.forEach((key, val) -> {
                                final String printString = "key:%s, val:%s";
                                System.out.println(String.format(printString, key, val));
                            }));
                    final List<Map<String, String>> mapList = JSON.parseObject(map.get("mapList"), new TypeReference<List<Map<String, String>>>() {
                    }.getType());
                    Optional.ofNullable(mapList)
                            .ifPresent(list -> list.forEach(objMap -> objMap.forEach((key, val) -> {
                                final String printString = "key:%s, val:%s";
                                System.out.println(String.format(printString, key, val));
                            })));
                });
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
