package com.facilio.bmsconsoleV3.commands.servicerequest;

import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class setSourceTypeCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3ServiceRequestContext> requests = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(requests)) {
            for (V3ServiceRequestContext serviceRequestContext : requests) {
                if (serviceRequestContext.getSourceType() < 1) {
                    serviceRequestContext.setSourceType(EmailFromAddress.SourceType.NOTIFICATION.getIntValue());
                }
            }
        }

        return false;
    }
}
