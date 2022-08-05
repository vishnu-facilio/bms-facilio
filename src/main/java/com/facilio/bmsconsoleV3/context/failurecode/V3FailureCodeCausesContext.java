package com.facilio.bmsconsoleV3.context.failurecode;

import com.facilio.v3.context.V3Context;

public class V3FailureCodeCausesContext extends V3Context {
    public V3FailureCodeContext getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(V3FailureCodeContext failureCode) {
        this.failureCode = failureCode;
    }



    private V3FailureCodeContext failureCode;

    public V3FailureCodeProblemsContext getFailureCodeProblems() {
        return failureCodeProblems;
    }

    public void setFailureCodeProblems(V3FailureCodeProblemsContext failureCodeProblems) {
        this.failureCodeProblems = failureCodeProblems;
    }

    private V3FailureCodeProblemsContext failureCodeProblems;
}
