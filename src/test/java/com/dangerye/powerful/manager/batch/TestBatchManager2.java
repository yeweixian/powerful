package com.dangerye.powerful.manager.batch;

import com.dangerye.powerful.manager.core.BatchManager;
import com.dangerye.powerful.manager.core.UniversalContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestBatchManager2 extends BatchManager {

    @Autowired
    public TestBatchManager2(UniversalContext universalContext) {
        super(universalContext);
    }

    @Override
    protected String getBusinessEvent() {
        return "testBatch2";
    }

    @Override
    protected Object handleBusiness(Map<String, String> paramMap) {
        final String param2 = paramMap.get("testBatch2.param2");
        final Batch2Data batch2Data = new Batch2Data();
        batch2Data.msg = param2;
        return batch2Data;
    }

    @Data
    public static class Batch2Data {
        private String msg;
    }
}
