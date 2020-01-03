package com.dangerye.powerful.communicate;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncLoadTest {

    @Test
    public void testCompletableFuture() {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("thread 1 start");
            try {
                TimeUnit.SECONDS.sleep(9);
            } catch (InterruptedException ignored) {
            }
            System.out.println("thread 1 end");
            return 1;
        });
        System.out.println(cf.getNow(null));
    }

    @Test
    public void testAsyncLoad() throws InterruptedException {
        System.out.println("start");
        long time = System.currentTimeMillis();
        AsyncLoad<Integer> asyncLoad1 = AsyncLoad.load(() -> {
            System.out.println("thread 1 start");
            Thread.sleep(9000);
            System.out.println("thread 1 end");
            return 1;
        });
        AsyncLoad<Integer> asyncLoad2 = AsyncLoad.load(() -> {
            System.out.println("thread 2 start");
            Thread.sleep(7000);
            System.out.println("thread 2 end");
            return 2;
        });
        AsyncLoad<Integer> asyncLoad3 = AsyncLoad.load(() -> {
            System.out.println("thread 3 start");
            Thread.sleep(5000);
            System.out.println("thread 3 end");
            return 3;
        });
        AsyncLoad<Integer> asyncLoad4 = AsyncLoad.load(() -> {
            System.out.println("thread 4 start");
            Thread.sleep(3000);
            System.out.println("thread 4 end");
            return 4;
        });
        AsyncLoad<Integer> asyncLoad5 = AsyncLoad.load(() -> {
            System.out.println("thread 5 start");
//            Thread.sleep(1000);
            throw new Exception("test");
//            System.out.println("thread 5 end");
//            return 5;
        });

        System.out.println(asyncLoad1.getIgnoreException(2000));
        System.out.println(asyncLoad2.getIgnoreException(2000));
        System.out.println(asyncLoad3.getIgnoreException(2000));
        System.out.println(asyncLoad4.getIgnoreException(2000));
        System.out.println(asyncLoad5.getIgnoreException(2000));

        System.out.println("end. time : " + (System.currentTimeMillis() - time) + "ms");

        Thread.sleep(10000);
    }
}
