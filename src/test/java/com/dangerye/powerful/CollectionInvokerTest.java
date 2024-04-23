package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.concurrent.AbstractCollectionInvoker;
import com.dangerye.powerful.concurrent.InvokeCollectionContext;
import com.dangerye.powerful.concurrent.InvokeCollectionItemPredicate;
import com.dangerye.powerful.concurrent.InvokeInterceptor;
import com.dangerye.powerful.concurrent.InvokeInterceptorPool;
import com.dangerye.powerful.concurrent.InvokeInvocation;
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

    private static final TestCollectionInvoker testCollectionInvoker = new TestCollectionInvoker();

    @Test
    public void testInvoker() {
        final TestCollectionContext context = TestCollectionContext.builder()
                .invokeSign("testInvoker")
                .collection(new ArrayList<>())
                .build();
        testCollectionInvoker.invoke(context);
        LogUtils.info(log, "testInvoker", "context:{}", JSON.toJSONString(context));
    }

    @Data
    @Builder
    public static class Item {
        private int value;
    }

    @Data
    @Builder
    public static final class TestCollectionContext implements InvokeCollectionContext<Item> {
        private String invokeSign;
        private Collection<Item> collection;
        private String beforeSum;
        private String afterSum;
    }

    public static final class TestCollectionInitInterceptor extends InvokeInterceptor<InvokeCollectionContext<Item>> {
        @Override
        public void configure(InvokeCollectionContext<Item> context) {
            LogUtils.info(log, "TestCollectionInitInterceptor", "TestCollectionInitInterceptor configure.");
        }

        @Override
        public void close() throws Exception {
            LogUtils.info(log, "TestCollectionInitInterceptor", "TestCollectionInitInterceptor close.");
        }

        @Override
        protected <R> R intercept(InvokeInvocation<R, InvokeCollectionContext<Item>> invocation) throws Exception {
            final InvokeCollectionContext<Item> context = invocation.getContext();
            for (int i = 0; i < 10; i++) {
                final Item item = Item.builder().value(RandomUtils.nextInt(0, 20)).build();
                context.getCollection().add(item);
            }
            LogUtils.info(log, "TestCollectionInitInterceptor", "begin invoke. collection:{}", JSON.toJSONString(context.getCollection()));
            final R result = invocation.proceed();
            LogUtils.info(log, "TestCollectionInitInterceptor", "after invoke. collection:{}", JSON.toJSONString(context.getCollection()));
            return result;
        }
    }

    public static final class TestCollectionSumInterceptor extends InvokeInterceptor<TestCollectionContext> {
        @Override
        public void configure(TestCollectionContext context) {
            LogUtils.info(log, "TestCollectionSumInterceptor", "TestCollectionSumInterceptor configure.");
        }

        @Override
        public void close() throws Exception {
            LogUtils.info(log, "TestCollectionSumInterceptor", "TestCollectionSumInterceptor close.");
        }

        @Override
        protected <R> R intercept(InvokeInvocation<R, TestCollectionContext> invocation) throws Exception {
            final TestCollectionContext context = invocation.getContext();
            final int beforeSum = context.getCollection().stream().mapToInt(Item::getValue).sum();
            context.setBeforeSum(String.valueOf(beforeSum));
            final R result = invocation.proceed();
            final int afterSum = context.getCollection().stream().mapToInt(Item::getValue).sum();
            context.setAfterSum(String.valueOf(afterSum));
            return result;
        }
    }

    public static final class FilterMultiple3Predicate extends InvokeCollectionItemPredicate<Item, InvokeCollectionContext<Item>> {
        @Override
        protected boolean doFilter(Item item, InvokeCollectionContext<Item> context) {
            return item.getValue() % 3 != 0;
        }
    }

    public static final class FilterMultiple2Predicate extends InvokeCollectionItemPredicate<Item, InvokeCollectionContext<Item>> {
        @Override
        protected boolean doFilter(Item item, InvokeCollectionContext<Item> context) {
            return item.getValue() % 2 != 0;
        }
    }

    public static final class TestCollectionInvoker extends AbstractCollectionInvoker<Item, TestCollectionContext> {
        @Override
        protected void increaseLog(TestCollectionContext context, Exception exception) {
        }

        @Override
        protected Collection<InvokeInterceptor<? super TestCollectionContext>> invokeInterceptors(TestCollectionContext context) {
            final Collection<InvokeInterceptor<? super TestCollectionContext>> collection = new ArrayList<>();
            collection.add(new TestCollectionSumInterceptor());
            collection.add(new TestCollectionInitInterceptor());
            collection.add(InvokeInterceptorPool.DEFAULT_INVOKE_TIME_INTERCEPTOR);
            return collection;
        }

        @Override
        protected Collection<InvokeCollectionItemPredicate<? super Item, ? super TestCollectionContext>> invokeCollectionItemPredicates(TestCollectionContext context) {
            final Collection<InvokeCollectionItemPredicate<? super Item, ? super TestCollectionContext>> collection = new ArrayList<>();
            collection.add(new FilterMultiple2Predicate());
            collection.add(new FilterMultiple3Predicate());
            return collection;
        }
    }
}
