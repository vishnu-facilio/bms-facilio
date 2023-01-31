package com.facilio.bmsconsoleV3.commands;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.*;

public class FetchCustomKioskDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id=(Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
        FacilioContext V3CustomKioskContext = V3Util.getSummary(FacilioConstants.ContextNames.CUSTOM_KIOSK, Collections.singletonList(id));
        context.put(FacilioConstants.ContextNames.SUMMARY_CONTEXT,V3CustomKioskContext);
        return false;
    }
}