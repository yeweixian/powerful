package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.communicate.AbstractCollectionInvoker;
import com.dangerye.powerful.communicate.InvokeContext;
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
            list.add(Item.builder().value(RandomUtils.nextInt(0, 20)).build());
        }
        System.out.println("before invoke: " + JSON.toJSONString(list));
        final TestCollectionContext context = TestCollectionContext.builder()
                .invokeEvent("testBusinessEvent")
                .target(list)
                .build();
        testCollectionInvoker.invoke(context);
        System.out.println("after invoke: " + JSON.toJSONString(list));
        System.out.println("context: " + JSON.toJSONString(context));
    }

    @Data
    @Builder
    public static final class TestCollectionContext implements InvokeContext<Collection<Item>> {
        private String invokeEvent;
        private Collection<Item> target;
        private String beforeSum;
        private String afterSum;
    }

    public static final class TestCollectionInvoker extends AbstractCollectionInvoker<Item, TestCollectionContext> {
        @Override
        protected Collection<Interceptor<? super TestCollectionContext>> invokeInterceptors(TestCollectionContext context) {
            return Lists.newArrayList(new TestCollectionInterceptor());
        }

        @Override
        protected Collection<CollectionFilter<? super Item, ? super TestCollectionContext>> invokeCollectionFilters(TestCollectionContext context) {
            return Lists.newArrayList(new TestCollectionFilter(), new TestCollectionFilter1());
        }
    }

    public static final class TestCollectionInterceptor extends Invoker.Interceptor<TestCollectionContext> {
        @Override
        protected <R> R intercept(Invoker.Invocation<R, TestCollectionContext> invocation) throws Exception {
            final TestCollectionContext context = invocation.getContext();
            final int beforeSum = context.getTarget().stream().mapToInt(Item::getValue).sum();
            context.setBeforeSum(String.valueOf(beforeSum));
            final R result = invocation.proceed();
            final int afterSum = context.getTarget().stream().mapToInt(Item::getValue).sum();
            context.setAfterSum(String.valueOf(afterSum));
            return result;
        }

        @Override
        public void configure(TestCollectionContext context) {
            System.out.println("configure : TestCollectionInterceptor");
        }

        @Override
        public void close() {
            System.out.println("close : TestCollectionInterceptor");
        }
    }

    public static final class TestCollectionFilter extends Invoker.CollectionFilter<Item, TestCollectionContext> {
        @Override
        public void configure(TestCollectionContext context) {
            System.out.println("configure : TestCollectionFilter");
            super.configure(context);
        }

        @Override
        public void close() throws Exception {
            System.out.println("close : TestCollectionFilter");
            super.close();
        }

        @Override
        protected boolean doFilter(Item item, TestCollectionContext context) {
            return item.getValue() % 3 != 0;
        }
    }

    public static final class TestCollectionFilter1 extends Invoker.CollectionFilter<Item, TestCollectionContext> {
        @Override
        public void configure(TestCollectionContext context) {
            System.out.println("configure : TestCollectionFilter1");
            super.configure(context);
        }

        @Override
        public void close() throws Exception {
            System.out.println("close : TestCollectionFilter1");
            super.close();
        }

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
