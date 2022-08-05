package com.facilio.bmsconsoleV3.context.failurecode;

import com.facilio.v3.context.V3Context;


public class V3FailureCodeProblemsContext extends V3Context {
    public V3FailureCodeContext getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(V3FailureCodeContext failureCode) {
        this.failureCode = failureCode;
    }

    public V3FailureClassContext getFailureClass() {
        return failureClass;
    }

    public void setFailureClass(V3FailureClassContext failureClass) {
        this.failureClass = failureClass;
    }

    private V3FailureCodeContext failureCode;
    private V3FailureClassContext failureClass;

}
