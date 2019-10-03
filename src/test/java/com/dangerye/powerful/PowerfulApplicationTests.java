package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.communicate.ThreadContext;
import com.dangerye.powerful.service.TestService;
import com.dangerye.powerful.utils.IpUtils;
import com.dangerye.powerful.utils.SpringContextUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PowerfulApplicationTests {

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
    public void testSpringContextUtils() {
        TestService service = SpringContextUtils.getBean(TestService.class);
        String test = service.getTestMsg("DangerYe");
        System.out.println("testMsg: " + test);
    }
}
