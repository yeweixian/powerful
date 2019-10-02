package com.dangerye.powerful.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String getTestMsg(String msg) {
        return "Hello, " + msg + "!";
    }
}
