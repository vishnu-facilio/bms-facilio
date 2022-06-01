package com.facilio.readingrule.faultimpact;

import com.facilio.constants.FacilioConstants;
import com.facilio.v3.util.V3Util;

public class FaultImpactAPI {

    public static FaultImpactContext getFaultImpactContext(Long id) throws Exception {
        return (FaultImpactContext) V3Util.getRecord(FacilioConstants.FaultImpact.MODULE_NAME, id, null);
    }
}
