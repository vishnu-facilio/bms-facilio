package com.facilio.bmsconsoleV3.context.failurecode;

import com.facilio.v3.context.V3Context;

public class V3FailureCodeRemediesContext extends V3Context {
    public V3FailureCodeContext getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(V3FailureCodeContext failureCode) {
        this.failureCode = failureCode;
    }



    private V3FailureCodeContext failureCode;


    public V3FailureCodeCausesContext getFailureCodeCauses() {
        return failureCodeCauses;
    }

    public void setFailureCodeCauses(V3FailureCodeCausesContext failureCodeCauses) {
        this.failureCodeCauses = failureCodeCauses;
    }

    private V3FailureCodeCausesContext failureCodeCauses;
}
