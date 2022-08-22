package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.failurecode.*;
import com.facilio.v3.context.CustomModuleDataV3;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class CustomModuleDataFailureClassRelationship extends V3Context {

    private CustomModuleDataV3 record;
    private V3FailureClassContext failureClass;
    private V3FailureCodeContext failureCode;
    private V3FailureCodeProblemsContext failureProblem;
    private V3FailureCodeCausesContext failureCause;
    private V3FailureCodeRemediesContext failureRemedy;
}
