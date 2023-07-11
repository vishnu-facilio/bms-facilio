package com.facilio.remotemonitoring.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BureauCloseIssueReasonOptionContext extends V3Context {
    private String name;
    private Long bureauEvaluationId;

}
