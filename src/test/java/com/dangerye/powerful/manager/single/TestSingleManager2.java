package com.dangerye.powerful.manager.single;

import com.dangerye.powerful.manager.core.SingleManager;
import com.dangerye.powerful.manager.core.UniversalContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestSingleManager2 extends SingleManager {

    @Autowired
    public TestSingleManager2(UniversalContext universalContext) {
        super(universalContext);
    }

    @Override
    protected String getBusinessEvent() {
        return "testSingle2";
    }

    @Override
    protected String handleBusiness(String param) {
        final UniversalContext universalContext = getUniversalContext();
        final Single2Param single2Param = universalContext.parseParam(param, Single2Param.class);
        return universalContext.returnSuccessResponse(single2Param);
    }

    @Data
    public static class Single2Param {
        private String param2;
    }
}
