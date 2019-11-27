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
        String resp = HttpInvoker.execute(null, request, null);
        System.out.println("resp" + resp);
    }
}
