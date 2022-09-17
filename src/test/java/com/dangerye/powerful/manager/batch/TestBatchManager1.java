package com.dangerye.powerful.manager.batch;

import com.dangerye.powerful.manager.core.BatchManager;
import com.dangerye.powerful.manager.core.UniversalContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestBatchManager1 extends BatchManager {

    @Autowired
    public TestBatchManager1(UniversalContext universalContext) {
        super(universalContext);
    }

    @Override
    protected String getBusinessEvent() {
        return "testBatch1";
    }

    @Override
    protected Object handleBusiness(Map<String, String> paramMap) {
        final String param1 = paramMap.get("testBatch1.param1");
        final Batch1Data batch1Data = new Batch1Data();
        batch1Data.msg = param1;
        return batch1Data;
    }

    @Data
    public static class Batch1Data {
        private String msg;
    }
}
