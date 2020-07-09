package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;

public class SetWorkPermitRecordCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WorkPermitContext workpermit = (WorkPermitContext) context.getOrDefault(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, null);
        List<Long> recordIds = Collections.singletonList(workpermit.getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
        return false;
    }

}
