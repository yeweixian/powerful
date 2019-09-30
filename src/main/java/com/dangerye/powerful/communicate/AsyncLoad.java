package com.dangerye.powerful.communicate;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 同步并行加载数据(减少‘多数据’整合业务的请求响应时间)
 * 1、可以设置超时时间； 2、超时后正在执行的代码通过异常进行中断；
 * 注意点：
 * 由于超时是通过抛异常进行中断，所以 load() 方法中 try catch 代码需要特别注意，以免出错导致线程没被销毁；
 * 相关博文：
 * https://blog.csdn.net/li12412414/article/details/54577034
 */
public final class AsyncLoad<T> {

    private ExecutorService executorService;
    private FutureTask<T> futureTask;

    private AsyncLoad() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public static <T> AsyncLoad<T> load(Callable<T> callable) {
        AsyncLoad<T> asyncLoad = new AsyncLoad<>();
        asyncLoad.futureTask = new FutureTask<>(callable);
        asyncLoad.executorService.execute(asyncLoad.futureTask);
        return asyncLoad;
    }

    public T get(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        T result;
        try {
            result = futureTask.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            futureTask.cancel(true);
            throw e;
        } finally {
            executorService.shutdown();
        }
        return result;
    }

    public T getIgnoreException(long timeout) {
        try {
            return get(timeout);
        } catch (Exception e) {
            System.out.println("Exception: " + Objects.toString(e, "") + ", " +
                    "msg: " + Objects.toString(e.getMessage(), ""));
            return null;
        }
    }
}
