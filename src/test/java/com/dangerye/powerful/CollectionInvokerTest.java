package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.communicate.AbstractCollectionInvoker;
import com.dangerye.powerful.communicate.Invoker;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class CollectionInvokerTest {

    private static final TestCollectionInvoker testCollectionInvoker = new TestCollectionInvoker();

    @Test
    public void testInvoker() {
        final List<Item> list = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            list.add(Item.builder().value(RandomUtils.nextInt(0, 10)).build());
        }
        System.out.println("before invoke: " + JSON.toJSONString(list));
        final TestCollectionContext context = TestCollectionContext.builder()
                .businessEvent("testBusinessEvent")
                .build();
        testCollectionInvoker.execute(list, context);
        System.out.println("after invoke: " + JSON.toJSONString(list));
        System.out.println("context: " + JSON.toJSONString(context));
    }

    @Data
    @Builder
    public static final class TestCollectionContext implements Invoker.CollectionContext {
        private String businessEvent;
        private String beforeSum;
        private String afterSum;

        @Override
        public String getBusinessEvent() {
            return businessEvent;
        }
    }

    public static final class TestCollectionInvoker extends AbstractCollectionInvoker<Item, TestCollectionContext> {
        @Override
        protected Collection<CollectionInterceptor<Item, TestCollectionContext>> collectionBusinessInterceptors(TestCollectionContext context) {
            return Lists.newArrayList(new TestCollectionInterceptor());
        }

        @Override
        protected Collection<CollectionFilter<Item, TestCollectionContext>> collectionBusinessFilters(TestCollectionContext context) {
            return Lists.newArrayList(new TestCollectionFilter(), new TestCollectionFilter1());
        }
    }

    public static final class TestCollectionInterceptor extends Invoker.CollectionInterceptor<Item, TestCollectionContext> {
        @Override
        protected void intercept(Invoker.CollectionInvocation<Item, TestCollectionContext> invocation) {
            final int beforeSum = invocation.getCollection().stream().mapToInt(Item::getValue).sum();
            invocation.getContext().setBeforeSum(String.valueOf(beforeSum));
            invocation.proceed();
            final int afterSum = invocation.getCollection().stream().mapToInt(Item::getValue).sum();
            invocation.getContext().setAfterSum(String.valueOf(afterSum));
        }
    }

    public static final class TestCollectionFilter extends Invoker.CollectionFilter<Item, TestCollectionContext> {
        @Override
        protected boolean doFilter(Item item, TestCollectionContext context) {
            return item.getValue() % 3 != 0;
        }
    }

    public static final class TestCollectionFilter1 extends Invoker.CollectionFilter<Item, TestCollectionContext> {
        @Override
        protected boolean doFilter(Item item, TestCollectionContext context) {
            return item.getValue() % 2 != 0;
        }
    }

    @Data
    @Builder
    public static class Item {
        private int value;
    }
}
