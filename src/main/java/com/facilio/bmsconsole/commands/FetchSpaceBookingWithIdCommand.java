package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchSpaceBookingWithIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> spaceIds = (List<Long>) context.get(FacilioConstants.ContextNames.ID);
        FacilioContext summary=V3Util.getSummary(FacilioConstants.ContextNames.SPACE_BOOKING, spaceIds);
        List<V3SpaceBookingContext> bookingList = Constants.getRecordListFromContext(summary,FacilioConstants.ContextNames.SPACE_BOOKING);
        context.put("spacebookingList",bookingList);
        return false;
    }
}
