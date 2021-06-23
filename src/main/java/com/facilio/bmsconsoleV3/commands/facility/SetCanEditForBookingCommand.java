package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetCanEditForBookingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3FacilityBookingContext> bookings = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(bookings)) {
            for(V3FacilityBookingContext booking : bookings){
                booking.setCanEdit(true);
            }
        }
        return false;
    }
}

