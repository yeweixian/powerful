package com.dangerye.powerful.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public TestService() {
        System.out.println("build testService bean...");
    }

    public String getTestMsg(String msg) {
        return "Hello, " + msg + "!";
    }
}
