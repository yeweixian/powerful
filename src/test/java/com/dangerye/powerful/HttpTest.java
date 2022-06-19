package com.dangerye.powerful;

import com.dangerye.powerful.communicate.http.HttpContext;
import com.dangerye.powerful.communicate.http.HttpInvoker;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.Test;

public class HttpTest {

    private static final HttpInvoker httpInvoker = new HttpInvoker();

    @Test
    public void testHttpDome() {
        Integer integer = change();
        System.out.println(integer);
    }

    @SuppressWarnings("unchecked")
    public <R> R change() {
        return (R) getInteger();
    }

    public Integer getInteger() {
        return null;
    }

    @Test
    public void testHttpDome1() {
        HttpUriRequest request = RequestBuilder
                .get("https://api.hearthstonejson.com/v1/latest/zhCN/cards.collectible.json")
                .build();
        final HttpContext context = HttpContext.builder()
                .supplier("HttpTest.testHttpDome1")
                .httpRequest(request)
                .build();
        String resp = httpInvoker.get(context);
        System.out.println("resp" + resp);
    }

    @Test
    public void testHttpDome2() {
        HttpUriRequest request = RequestBuilder
                .get("https://hsreplay.net/analytics/query/list_decks_by_win_rate/")
                .addParameter("GameType", "RANKED_STANDARD")
                .addParameter("RankRange", "ALL")
                .addParameter("Region", "ALL")
                .addParameter("TimeRange", "LAST_30_DAYS")
                .build();
        final HttpContext context = HttpContext.builder()
                .supplier("HttpTest.testHttpDome2")
                .httpRequest(request)
                .build();
        String resp = httpInvoker.get(context);
        System.out.println("resp" + resp);
    }

    @Test
    public void testSayHello() throws InterruptedException {
        // http://127.0.0.1:8080/user/sayHello?msg=dangerye
        HttpUriRequest request = RequestBuilder
                .get("http://127.0.0.1:8080/user/sayHello?msg=dangerye")
                .build();
        final HttpContext context = HttpContext.builder()
                .supplier("HttpTest.testSayHello")
                .httpRequest(request)
                .build();
        for (int i = 0; i < 3; i++) {
            httpInvoker.get(context);
            httpInvoker.get(context);
            httpInvoker.get(context);
            httpInvoker.get(context);
            Thread.sleep(4000);
        }
    }
}
