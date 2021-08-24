package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class JsonTest {

    @Test
    public void testApiExportJson() throws Exception {
        final ImmutableMap<String, String> origin = ImmutableMap.<String, String>builder()
                .put("type", "")
                .put("source", "")
                .put("key", "Access-Control-Allow-Origin")
                .put("value", "https://*.vip.com,http://*.vip.com,https://*.vipglobal.hk,http://*.vipglobal.hk")
                .put("target", "Header")
                .build();
        final ImmutableMap<String, String> getMethods = ImmutableMap.<String, String>builder()
                .put("type", "")
                .put("source", "")
                .put("key", "Access-Control-Allow-Methods")
                .put("value", "GET")
                .put("target", "Header")
                .build();
        final ImmutableMap<String, String> postMethods = ImmutableMap.<String, String>builder()
                .put("type", "")
                .put("source", "")
                .put("key", "Access-Control-Allow-Methods")
                .put("value", "POST")
                .put("target", "Header")
                .build();
        final ImmutableMap<String, String> headers = ImmutableMap.<String, String>builder()
                .put("type", "")
                .put("source", "")
                .put("key", "Access-Control-Allow-Headers")
                .put("value", "Origin,Accept,Referer,X-Requested-With,Content-Type,Authorization")
                .put("target", "Header")
                .build();
        final ImmutableMap<String, String> credentials = ImmutableMap.<String, String>builder()
                .put("type", "")
                .put("source", "")
                .put("key", "Access-Control-Allow-Credentials")
                .put("value", "true")
                .put("target", "Header")
                .build();
        final List<Object> getCorsResponseHeader = Lists.newArrayList(origin, getMethods, headers, credentials);
        final List<Object> postCorsResponseHeader = Lists.newArrayList(origin, postMethods, headers, credentials);
        final FileInputStream inputStream
                = new FileInputStream("/Users/dangerye/Downloads/janus-mapi-user.vip.com#api_export.json");
        final List<Map<String, Object>> mapList = JSON.parseObject(inputStream, new TypeReference<List<Map<String, Object>>>() {
        }.getType());
        for (Map<String, Object> objectMap : mapList) {
            final Object signMethod = objectMap.get("signMethod");
            if (Objects.equals(signMethod, 3)) {
                final Object inboundMethod = objectMap.get("inboundMethod");
                if (Objects.equals(inboundMethod, "GET")) {
                    objectMap.put("cors", 1);
                    objectMap.put("corsResponseHeader", getCorsResponseHeader);
                } else if (Objects.equals(inboundMethod, "POST")) {
                    objectMap.put("cors", 1);
                    objectMap.put("corsResponseHeader", postCorsResponseHeader);
                } else {
                    throw new Exception("inboundMethod error");
                }
            }
        }
        System.out.println(JSON.toJSONString(mapList, SerializerFeature.DisableCircularReferenceDetect));
    }

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
