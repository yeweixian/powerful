package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.communicate.AbstractCollectionInvoker;
import com.dangerye.powerful.communicate.InvokeInterceptorPool;
import com.dangerye.powerful.communicate.Invoker;
import com.dangerye.powerful.utils.LogUtils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class CollectionInvokerTest {

    private static final TestCollectionInvoker1 testCollectionInvoker1 = new TestCollectionInvoker1();
    private static final TestCollectionInterceptor testCollectionInterceptor = new TestCollectionInterceptor();
    private static final TestCollectionInterceptor1 testCollectionInterceptor1 = new TestCollectionInterceptor1();
    private static final TestCollectionFilter testCollectionFilter = new TestCollectionFilter();
    private static final TestCollectionFilter1 testCollectionFilter1 = new TestCollectionFilter1();

    @Test
    public void testInvoker() {
        final TestCollectionContext context = TestCollectionContext.builder()
                .invokeEvent("testInvoker")
                .collection(new ArrayList<>())
                .build();
        testCollectionInvoker1.invoke(context);
        LogUtils.info(log, "testInvoker", "context:{}", JSON.toJSONString(context));
    }

    @Data
    @Builder
    public static class Item {
        private int value;
    }

    @Data
    @Builder
    public static final class TestCollectionContext implements Invoker.CollectionContext<Item> {
        private String invokeEvent;
        private Collection<Item> collection;
        private String beforeSum;
        private String afterSum;
    }

    public static final class TestCollectionInterceptor1 extends Invoker.Interceptor<Invoker.CollectionContext<Item>> {
        @Override
        public void configure(Invoker.CollectionContext<Item> context) {
            LogUtils.info(log, "TestCollectionInterceptor1", "run configure.");
        }

        @Override
        protected <R> R intercept(Invoker.Invocation<R, Invoker.CollectionContext<Item>> invocation) throws Exception {
            final Invoker.CollectionContext<Item> context = invocation.getContext();
            for (int i = 0; i < 10; i++) {
                final Item item = Item.builder().value(RandomUtils.nextInt(0, 20)).build();
                context.getCollection().add(item);
            }
            LogUtils.info(log, "TestCollectionInterceptor1",
                    "begin invoke. collection:{}", JSON.toJSONString(context.getCollection()));
            final R result = invocation.proceed();
            LogUtils.info(log, "TestCollectionInterceptor1",
                    "after invoke. collection:{}", JSON.toJSONString(context.getCollection()));
            return result;
        }

        @Override
        public void close() {
            LogUtils.info(log, "TestCollectionInterceptor1", "run close.");
        }
    }

    public static final class TestCollectionInterceptor extends Invoker.Interceptor<TestCollectionContext> {
        @Override
        public void configure(TestCollectionContext context) {
            LogUtils.info(log, "TestCollectionInterceptor", "run configure.");
        }

        @Override
        protected <R> R intercept(Invoker.Invocation<R, TestCollectionContext> invocation) throws Exception {
            final TestCollectionContext context = invocation.getContext();
            final int beforeSum = context.getCollection().stream().mapToInt(Item::getValue).sum();
            context.setBeforeSum(String.valueOf(beforeSum));
            final R result = invocation.proceed();
            final int afterSum = context.getCollection().stream().mapToInt(Item::getValue).sum();
            context.setAfterSum(String.valueOf(afterSum));
            return result;
        }

        @Override
        public void close() {
            LogUtils.info(log, "TestCollectionInterceptor", "run close.");
        }
    }

    public static final class TestCollectionFilter extends Invoker.CollectionFilter<Item, Invoker.CollectionContext<Item>> {
        @Override
        protected boolean doFilter(Item item, Invoker.CollectionContext<Item> context) {
            return item.getValue() % 3 != 0;
        }
    }

    public static final class TestCollectionFilter1 extends Invoker.CollectionFilter<Item, TestCollectionContext> {
        @Override
        protected boolean doFilter(Item item, TestCollectionContext context) {
            return item.getValue() % 2 != 0;
        }
    }

    public static final class TestCollectionInvoker1 extends AbstractCollectionInvoker<Item, TestCollectionContext> {
        @Override
        protected Collection<Interceptor<? super TestCollectionContext>> invokeInterceptors(TestCollectionContext context) {
            Collection<Interceptor<? super TestCollectionContext>> result = new ArrayList<>();
            result.add(InvokeInterceptorPool.CALL_TIME_INTERCEPTOR);
            result.add(testCollectionInterceptor);
            result.add(InvokeInterceptorPool.CALL_TIME_INTERCEPTOR);
            result.add(testCollectionInterceptor1);
            result.add(InvokeInterceptorPool.CALL_TIME_INTERCEPTOR);
            return result;
        }

        @Override
        protected Collection<CollectionFilter<? super Item, ? super TestCollectionContext>> invokeCollectionFilters(TestCollectionContext context) {
            Collection<CollectionFilter<? super Item, ? super TestCollectionContext>> result = new ArrayList<>();
            result.add(testCollectionFilter);
            result.add(testCollectionFilter1);
            return result;
        }
    }
}
