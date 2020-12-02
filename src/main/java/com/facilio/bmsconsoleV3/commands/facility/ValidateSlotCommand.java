package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateSlotCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<SlotContext> slots = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(slots)) {
            for(SlotContext slot : slots){
                if(slot.getBookingCount() > 0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slot cannot be edited as it has bookings");
                }
            }
        }

            return false;
    }
}
