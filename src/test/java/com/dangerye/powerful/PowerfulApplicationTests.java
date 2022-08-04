package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.communicate.Invoker;
import com.dangerye.powerful.communicate.ThreadContext;
import com.dangerye.powerful.service.TestService;
import com.dangerye.powerful.utils.IpUtils;
import com.dangerye.powerful.utils.LogUtils;
import com.dangerye.powerful.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class PowerfulApplicationTests {

    @Autowired
    private final Map<String, Invoker<Invoker.InvokeContext>> invokerMap = new HashMap<>();

    @Before
    public void before() {
        ThreadContext.init();
        ThreadContext.setTraceId(Objects.toString(System.currentTimeMillis()));
        ThreadContext.setRequestIp(JSON.toJSONString(IpUtils.getLocalIpList()));
    }

    @After
    public void after() {
        ThreadContext.close();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testAutowiredMap() {
        for (Map.Entry<String, Invoker<Invoker.InvokeContext>> entry : invokerMap.entrySet()) {
            final String entryKey = entry.getKey();
            final Invoker<Invoker.InvokeContext> entryValue = entry.getValue();
            System.out.println("entryKey: " + entryKey);
            System.out.println("entryValue: " + entryValue);
        }
    }

    @Test
    public void testSpringContextUtils() throws UnknownHostException {
        TestService service = SpringContextUtils.getBean(TestService.class);
        String test = service.getTestMsg("DangerYe");
        String hostName = Optional.ofNullable(InetAddress.getLocalHost()).map(InetAddress::getHostName).orElse(null);
        LogUtils.info(log, "PRINT_EVENT", "testMsg:{}, hostName:{}", test, hostName);
    }
}
