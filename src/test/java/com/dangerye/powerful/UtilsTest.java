package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.builder.CollectionBuilder;
import com.dangerye.powerful.utils.CharFilterUtils;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class UtilsTest {

    @Test
    public void testCharFilterUtils() {
        String testString = "823@#4423@dsFweRgsd^&*!~";
        System.out.println(CharFilterUtils.filterChar(testString, " "));
        System.out.println(CharFilterUtils.filterChar(testString, ""));
        System.out.println(CharFilterUtils.filterChar(testString, null));
    }

    @Test
    public void testCollectionBuilder() {
        Set<String> set1 = CollectionBuilder.<String>setBuilder()
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        Set<String> set2 = CollectionBuilder.<String>setBuilder(new TreeSet<>())
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        System.out.println(JSON.toJSONString(set1));
        System.out.println(JSON.toJSONString(set2));

        List<String> list1 = CollectionBuilder.<String>listBuilder()
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        List<String> list2 = CollectionBuilder.<String>listBuilder(new LinkedList<>())
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        System.out.println(JSON.toJSONString(list1));
        System.out.println(JSON.toJSONString(list2));

        Map<String, String> map1 = CollectionBuilder.<String, String>mapBuilder()
                .put("5", "I")
                .put("2", "Love")
                .put("0", "You")
                .build();
        Map<String, String> map2 = CollectionBuilder.<String, String>mapBuilder(new TreeMap<>())
                .put("5", "I")
                .put("2", "Love")
                .put("0", "You")
                .build();
        System.out.println(JSON.toJSONString(map1));
        System.out.println(JSON.toJSONString(map2));
    }
}
