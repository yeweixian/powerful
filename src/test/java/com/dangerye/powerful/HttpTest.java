package com.dangerye.powerful;

import com.dangerye.powerful.communicate.http.HttpInvoker;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.Test;

public class HttpTest {

    @Test
    public void testHttpDome1() {
        HttpUriRequest request = RequestBuilder
                .get("https://api.hearthstonejson.com/v1/latest/zhCN/cards.collectible.json")
                .build();
        String resp = HttpInvoker.execute(null, request, true).get();
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
        String resp = HttpInvoker.execute(null, request, true).get();
        System.out.println("resp" + resp);
    }

    @Test
    public void testSayHello() throws InterruptedException {
        // http://127.0.0.1:8080/user/sayHello?msg=dangerye
        HttpUriRequest request = RequestBuilder
                .get("http://127.0.0.1:8080/user/sayHello?msg=dangerye")
                .build();
        for (int i = 0; i < 3; i++) {
            HttpInvoker.execute(null, request, true).get();
            HttpInvoker.execute(null, request, true).get();
            HttpInvoker.execute(null, request, true).get();
            HttpInvoker.execute(null, request, true).get();
            Thread.sleep(4000);
        }
    }
}
