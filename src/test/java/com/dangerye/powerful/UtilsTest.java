package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.builder.CollectionBuilder;
import com.dangerye.powerful.collection.Ring;
import com.dangerye.powerful.collection.SortList;
import com.dangerye.powerful.utils.CharFilterUtils;
import com.dangerye.powerful.utils.Des3Utils;
import com.dangerye.powerful.utils.RsaUtils;
import com.dangerye.powerful.utils.SecurityUtils;
import lombok.Data;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UtilsTest {

    private static final int test1 = getRandomInt();
    private static final int test2 = getRandomInt();

    private static int getRandomInt() {
        return RandomUtils.nextInt(0, 10000);
    }

    @Test
    public void testFinal() {
        for (int i = 0; i < 100; i++) {
            System.out.println("round: " + i
                    + ", test1: " + test1
                    + ", test2: " + test2
                    + ", moth: " + getRandomInt()
                    + ".");
        }
    }

    @Test
    public void testCharFilterUtils() {
        String testString = "823@#4423@dsFweRgsd^&*!~";
        System.out.println(CharFilterUtils.filterChar(testString, " "));
        System.out.println(CharFilterUtils.filterChar(testString, ""));
        System.out.println(CharFilterUtils.filterChar(testString, null));
    }

    @Test
    public void testSortList() {
        List<Integer> randomList = new ArrayList<>();
        SortList<Integer> sortList = new SortList<>(5, (nextItem, newItem) -> newItem.compareTo(nextItem) > 0);
        for (int i = 0; i < 10; i++) {
            int random = RandomUtils.nextInt(0, 10000);
            randomList.add(random);
            sortList.add(random);
        }
        System.out.println(JSON.toJSONString(randomList));
        System.out.println(JSON.toJSONString(sortList.toList()));
    }

    @Test
    public void testRing() {
        Ring<Integer> ring = new Ring<>();
        for (int i = 0; i < 100; i++) {
            ring.add(i + 1);
        }
        System.out.println("winner: " + Objects.toString(ring.kill(7), ""));
    }

    @Test
    public void testSecret() {
        String str = "cy&sindy love 1314";
        int total1 = 0, total0 = 0;
        String md5Str = DigestUtils.md5Hex(str);
        for (byte b : md5Str.getBytes(Charsets.UTF_8)) {
            int zoo = 0, one = 0;
            String bStr = Integer.toBinaryString(b);
            for (char c : bStr.toCharArray()) {
                if (Objects.equals(c, '0')) {
                    zoo++;
                } else if (Objects.equals(c, '1')) {
                    one++;
                } else {
                    throw new NullPointerException();
                }
            }
            if (bStr.length() < 8) {
                zoo += 8 - bStr.length();
            }
            System.out.println(bStr);
            System.out.println(String.format("zoo:%d, one:%d", zoo, one));
            total1 += one;
            total0 += zoo;
        }
        System.out.println(String.format("zoo:%d, one:%d", total0, total1));
    }

    @Test
    public void testCollectionBuilder() {
        Set<String> set1 = CollectionBuilder.<String>hashSetBuilder()
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        Set<String> set2 = CollectionBuilder.<String>treeSetBuilder()
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        System.out.println(JSON.toJSONString(set1));
        System.out.println(JSON.toJSONString(set2));

        List<String> list1 = CollectionBuilder.<String>arrayListBuilder()
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        List<String> list2 = CollectionBuilder.<String>linkedListBuilder()
                .add("Hello")
                .add("DangerYe")
                .add("Test")
                .add("Code")
                .build();
        System.out.println(JSON.toJSONString(list1));
        System.out.println(JSON.toJSONString(list2));

        Map<String, String> map1 = CollectionBuilder.<String, String>hashMapBuilder()
                .put("5", "I")
                .put("2", "Love")
                .put("0", "You")
                .build();
        Map<String, String> map2 = CollectionBuilder.<String, String>treeMapBuilder()
                .put("5", "I")
                .put("2", "Love")
                .put("0", "You")
                .build();
        System.out.println(JSON.toJSONString(map1));
        System.out.println(JSON.toJSONString(map2));

        TestDemo testDemo = new TestDemo();
        testDemo.setId(666);
        testDemo.setAge(30);
        testDemo.setName("dangerye");
        testDemo.setExtend(map1);

        System.out.println(JSON.toJSONString(CollectionBuilder.hashMapBuilder()
                .put("extend", testDemo.getExtend())
                .put("user", testDemo)
                .build(), SerializerFeature.DisableCircularReferenceDetect));
    }

    @Test
    public void testDes3Utils() {
        String text = "Test msg.";
        String key = Base64.getEncoder().encodeToString("abcdefghijklmnopqrstuvwxyz!@#$%".getBytes());
//        String secret = Des3Utils.encodeECB(text, key);
//        System.out.println("secret: " + secret);
//        String result = Des3Utils.decodeECB(secret, key);
//        System.out.println("result: " + result);
//        String md5Result = DigestUtils.md5DigestAsHex(result.getBytes());
//        System.out.println("md5Result: " + md5Result);
        SecurityUtils.testBuilder(text, CollectionBuilder.<String, Object>hashMapBuilder().put("key", key).build())
                .encrypt((msg, map) -> Des3Utils.encodeECB(msg, Objects.toString(map.get("key"), key)))
                .decrypt((msg, map) -> Des3Utils.decodeECB(msg, Objects.toString(map.get("key"), key)))
                .test();
    }

    @Test
    public void testRandomUtils() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Random Int: " + RandomUtils.nextInt(10000, 99999));
        }
    }

    @Test
    public void testMD5Utils() {
        String text = "Test msg.";
        System.out.println(org.springframework.util.DigestUtils.md5DigestAsHex(text.getBytes()));
        System.out.println(org.apache.commons.codec.digest.DigestUtils.md5Hex(text.getBytes()));
    }

    @Test
    public void testSecurityUtils() {
        String msg = "/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home/bin/java";
        String key = DigestUtils.md5Hex("danger.ye");
        SecurityUtils.testBuilder(msg)
                .encrypt(message -> SecurityUtils.encrypt(message, key))
                .decrypt(ciphertext -> SecurityUtils.decrypt(ciphertext, key))
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

    @Data
    private static class TestDemo {
        private long id;
        private String name;
        private int age;
        private Map<String, String> extend;
    }
}
