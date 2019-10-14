package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.builder.CollectionBuilder;
import com.dangerye.powerful.utils.CharFilterUtils;
import com.dangerye.powerful.utils.Des3Utils;
import com.dangerye.powerful.utils.RsaUtils;
import com.dangerye.powerful.utils.SecurityUtils;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Test
    public void testDes3Utils() {
        String text = "Test msg.";
        String key = "abcdefghijklmnopqrstuvwxyz!@#$%";
//        String secret = Des3Utils.encodeECB(text, key);
//        System.out.println("secret: " + secret);
//        String result = Des3Utils.decodeECB(secret, key);
//        System.out.println("result: " + result);
//        String md5Result = DigestUtils.md5DigestAsHex(result.getBytes());
//        System.out.println("md5Result: " + md5Result);
        SecurityUtils.testBuilder(text, CollectionBuilder.<String, Object>mapBuilder().put("key", key).build())
                .encrypt((msg, map) -> Des3Utils.encodeECB(msg, Objects.toString(map.get("key"), key)))
                .decrypt((msg, map) -> Des3Utils.decodeECB(msg, Objects.toString(map.get("key"), key)))
                .test();
    }

    @Test
    public void testRsaUtils1() throws Exception {
        Map<String, String> map = RsaUtils.getKeys();
        String content = "{\"mobile\":\"12345678900\"}";
//        String secret = RsaUtils.publicEncrypt(content, map.get("publicKey"));
//        System.out.println(secret);
//        String result = RsaUtils.privateDecrypt(secret, map.get("privateKey"));
//        System.out.println(result);
        SecurityUtils.testBuilder(content)
                .encrypt(message -> RsaUtils.publicEncrypt(message, map.get("publicKey")))
                .decrypt(ciphertext -> RsaUtils.privateDecrypt(ciphertext, map.get("privateKey")))
                .test();
    }

    @Test
    public void testRsaUtils2() throws Exception {
        Map<String, String> map = RsaUtils.getKeys();
        String content = "{\"mobile\":\"12345678900\"}";
//        String secret = RsaUtils.privateEncrypt(content, map.get("privateKey"));
//        System.out.println(secret);
//        String result = RsaUtils.publicDecrypt(secret, map.get("publicKey"));
//        System.out.println(result);
        SecurityUtils.testBuilder(content)
                .encrypt(message -> RsaUtils.privateEncrypt(message, map.get("privateKey")))
                .decrypt(ciphertext -> RsaUtils.publicDecrypt(ciphertext, map.get("publicKey")))
                .test();
    }
}
