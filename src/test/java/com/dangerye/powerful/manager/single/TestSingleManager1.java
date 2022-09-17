package com.dangerye.powerful.manager.single;

import com.dangerye.powerful.manager.core.SingleManager;
import com.dangerye.powerful.manager.core.UniversalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestSingleManager1 extends SingleManager {

    @Autowired
    public TestSingleManager1(UniversalContext universalContext) {
        super(universalContext);
    }

    @Override
    protected String getBusinessEvent() {
        return "testSingle1";
    }

    @Override
    protected String handleBusiness(String param) {
        final UniversalContext universalContext = getUniversalContext();
        final Single1Param single1Param = universalContext.parseParam(param, Single1Param.class);
        return universalContext.returnSuccessResponse(single1Param);
    }

    public static class Single1Param {
        private String param1;
    }
}
