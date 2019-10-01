package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.builder.CollectionBuilder;
import com.dangerye.powerful.utils.CharFilterUtils;
import org.junit.Test;

import java.util.Set;

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
        Set<String> set = CollectionBuilder.<String>setBuilder()
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        System.out.println(JSON.toJSONString(set));
    }
}
