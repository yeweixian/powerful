package com.dangerye.powerful;

import com.dangerye.powerful.service.TestService;
import com.dangerye.powerful.utils.SpringContextUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PowerfulApplicationTests {

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
