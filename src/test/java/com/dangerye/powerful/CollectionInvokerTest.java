package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.collection.CollectionInvoker;
import com.dangerye.powerful.collection.NewCollectionInvoker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionInvokerTest {

    @Test
    public void testNewInvoker() {
        final ArrayList<Integer> integerList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            integerList.add(RandomUtils.nextInt(0, 10));
        }
        final List<Item> list = CollectionInvoker.changeList(integerList,
                item -> Item.builder().value(item).build());
        System.out.println("before invoke: " + JSON.toJSONString(list));
        final NewCollectionInvoker.AbstractFilter<Item, Map<String, Object>> filter
                = new NewCollectionInvoker.AbstractFilter<Item, Map<String, Object>>() {
            @Override
            protected boolean doFilter(Item item, Map<String, Object> context) {
                return item.getValue() % 3 != 0;
            }
        };
        final NewCollectionInvoker.InvokerInterceptor<Item, Map<String, Object>> interceptor
                = invocation -> {
            final int beforeSum = invocation.getCollection().stream().mapToInt(Item::getValue).sum();
            invocation.getContext().put("beforeSum", beforeSum);
            invocation.proceed();
            final int afterSum = invocation.getCollection().stream().mapToInt(Item::getValue).sum();
            invocation.getContext().put("afterSum", afterSum);
        };
        final HashMap<String, Object> context = Maps.newHashMap();
        final NewCollectionInvoker<Item, Map<String, Object>> invoker = NewCollectionInvoker.<Item, Map<String, Object>>builder()
                .context(context)
                .interceptors(Lists.newArrayList(interceptor))
                .filters(Lists.newArrayList(filter))
                .build();
        invoker.invoke(list);
        System.out.println("after invoke: " + JSON.toJSONString(list));
        System.out.println("context: " + JSON.toJSONString(context));
    }

    @Test
    public void testInvoker() {
        final ArrayList<Integer> integerList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            integerList.add(RandomUtils.nextInt(0, 10));
        }
        final List<Item> list = CollectionInvoker.changeList(integerList,
                item -> Item.builder().value(item).build());
        System.out.println("before invoke: " + JSON.toJSONString(list));
        final HashMap<String, Object> context = Maps.newHashMap();
        CollectionInvoker.AbstractFilter<Item, Map<String, Object>> predicate
                = new CollectionInvoker.AbstractFilter<Item, Map<String, Object>>() {
            @Override
            protected boolean doFilter(Item item, Map<String, Object> context) {
                return item.getValue() % 3 != 0;
            }
        };
        CollectionInvoker.InvokerInterceptor<Item, Map<String, Object>> interceptor
                = new CollectionInvoker.InvokerInterceptor<Item, Map<String, Object>>() {
            @Override
            public void beforeInvoke(Collection<Item> collection, Map<String, Object> context) {
                final int beforeSum = collection.stream().mapToInt(Item::getValue).sum();
                context.put("beforeSum", beforeSum);
            }

            @Override
            public void afterInvoke(Collection<Item> collection, Map<String, Object> context) {
                final int afterSum = collection.stream().mapToInt(Item::getValue).sum();
                context.put("afterSum", afterSum);
            }
        };
        CollectionInvoker<Item, Map<String, Object>> invoker = CollectionInvoker.<Item, Map<String, Object>>builder()
                .context(context)
                .interceptors(Lists.newArrayList(interceptor))
                .filters(Lists.newArrayList(predicate))
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
