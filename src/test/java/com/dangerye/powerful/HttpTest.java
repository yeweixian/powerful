package com.dangerye.powerful;

import com.dangerye.powerful.communicate.http.HttpInvoker;
import org.junit.Test;

public class HttpTest {

    @Test
    public void testHttpDome1() {
        String resp = HttpInvoker.execute(null, null, null);
        System.out.println("resp" + resp);
    }
}
