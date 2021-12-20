package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

public class SortTest {

    @Test
    public void testSortList() {
        final List<SortDemo> sortList = Lists.newArrayList(
                SortDemo.builder()
                        .name("重庆市")
                        .sort("CQS")
                        .build(),
                SortDemo.builder()
                        .name("广州市")
                        .sort("GZS")
                        .build(),
                SortDemo.builder()
                        .name("亳州市")
                        .sort("#ZS")
                        .build(),
                SortDemo.builder()
                        .name("河源市")
                        .sort("HYS")
                        .build(),
                SortDemo.builder()
                        .name("黑龙市")
                        .sort("HLS")
                        .build(),
                SortDemo.builder()
                        .name("我不清楚")
                        .sort("#")
                        .build(),
                SortDemo.builder()
                        .name("河肥市")
                        .sort("HFS")
                        .build(),
                SortDemo.builder()
                        .name("长春市")
                        .sort("CCS")
                        .build(),
                SortDemo.builder()
                        .name("长肥市")
                        .sort("ZFS")
                        .build(),
                SortDemo.builder()
                        .name("重石市")
                        .sort("ZSS")
                        .build()
        );
        final Comparator<SortDemo> comparator = (o1, o2) -> {
            if (StringUtils.isBlank(o1.getName())
                    || StringUtils.isBlank(o1.getSort())
                    || "#".equals(o1.getSort())) {
                return 1;
            }
            if (StringUtils.isBlank(o2.getName())
                    || StringUtils.isBlank(o2.getSort())
                    || "#".equals(o2.getSort())) {
                return -1;
            }
            final char[] sortChars1 = o1.getSort().toCharArray();
            final char[] sortChars2 = o2.getSort().toCharArray();
            final char[] nameChars1 = o1.getName().toCharArray();
            final char[] nameChars2 = o2.getName().toCharArray();
            final int minLength = NumberUtils.min(sortChars1.length, sortChars2.length, nameChars1.length, nameChars2.length);
            for (int i = 0; i < minLength; i++) {
                final char sc1 = sortChars1[i];
                final char sc2 = sortChars2[i];
                final int sortCompare = CharUtils.compare(sc1, sc2);
                if (sortCompare != 0) {
                    return sortCompare;
                }
                final char nc1 = nameChars1[i];
                final char nc2 = nameChars2[i];
                final int nameCompare = CharUtils.compare(nc1, nc2);
                if (nameCompare != 0) {
                    return nameCompare;
                }
            }
            return 0;
        };
        sortList.sort(comparator);
        System.out.println(JSON.toJSONString(sortList, SerializerFeature.DisableCircularReferenceDetect));
    }

    @Data
    @Builder
    public static class SortDemo {
        private String name;
        private String sort;
    }
}
