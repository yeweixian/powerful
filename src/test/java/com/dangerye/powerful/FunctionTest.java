package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FunctionTest {

    @Test
    public void test1() {
        Map<String, Object> map = new HashMap<>();
        map.put("test", 1);
        handle(consumer -> {
            consumer.accept(map);
            System.out.println(JSON.toJSONString(map));
        });
    }

    private void handle(Consumer<Consumer<Map<String, Object>>> testCode) {
        if (testCode != null) {
            testCode.accept(map -> map.put("handle", 1));
        }
    }
}
