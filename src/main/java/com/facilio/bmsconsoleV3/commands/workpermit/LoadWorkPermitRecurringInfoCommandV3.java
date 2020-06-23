package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class LoadWorkPermitRecurringInfoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3WorkPermitContext permit = (V3WorkPermitContext)context.get(FacilioConstants.ContextNames.RECORD);
        if(permit != null) {
            if(permit.isRecurring()) {
                List<BusinessHoursContext> businessHours = BusinessHoursAPI.getBusinessHours(Collections.singletonList(permit.getRecurringInfoId()));
                if(CollectionUtils.isNotEmpty(businessHours)) {
                    permit.setRecurringInfo(businessHours.get(0));
                }
            }
        }
        return false;
    }
}
