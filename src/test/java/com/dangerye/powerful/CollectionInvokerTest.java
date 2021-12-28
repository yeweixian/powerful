package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.collection.CollectionInvoker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionInvokerTest {

    @Test
    public void testInvoker() {
        final ArrayList<Integer> integerList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            integerList.add(RandomUtils.nextInt(0, 10));
        }
        final List<Item> list = CollectionInvoker.changeList(integerList,
                item -> Item.builder().value(item).build());
        System.out.println("before invoke: " + JSON.toJSONString(list));
        final CollectionInvoker.AbstractFilter<Item, Map<String, Object>> filter
                = new CollectionInvoker.AbstractFilter<Item, Map<String, Object>>() {
            @Override
            protected boolean doFilter(Item item, Map<String, Object> context) {
                return item.getValue() % 3 != 0;
            }
        };
        final CollectionInvoker.InvokerInterceptor<Item, Map<String, Object>> interceptor
                = new CollectionInvoker.InvokerInterceptor<Item, Map<String, Object>>() {
            @Override
            protected void intercept(CollectionInvoker.Invocation<Item, Map<String, Object>> invocation) {
                final int beforeSum = invocation.getCollection().stream().mapToInt(Item::getValue).sum();
                invocation.getContext().put("beforeSum", beforeSum);
                invocation.proceed();
                final int afterSum = invocation.getCollection().stream().mapToInt(Item::getValue).sum();
                invocation.getContext().put("afterSum", afterSum);
            }
        };
        final HashMap<String, Object> context = Maps.newHashMap();
        final CollectionInvoker<Item, Map<String, Object>> invoker = CollectionInvoker.<Item, Map<String, Object>>builder()
                .context(context)
                .interceptors(Lists.newArrayList(interceptor))
                .filters(Lists.newArrayList(filter))
                .build();
        invoker.invoke(list);
        System.out.println("after invoke: " + JSON.toJSONString(list));
        System.out.println("context: " + JSON.toJSONString(context));
    }

    @Data
    @Builder
    public static class Item {
        private int value;
    }
}
