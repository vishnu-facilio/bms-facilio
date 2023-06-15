package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateFacilityCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FacilityContext> facilities = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(facilities)) {
            for(FacilityContext facility : facilities) {
                if(facility.getSlotDuration() == null || facility.getSlotDuration() <= 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slot duration is mandatory for a facility");
                }
                if(facility.getSlotDuration() != null && facility.getSlotDuration() >= 86400) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slot duration should be in hours and minutes only");
                }

                if(facility.getBookingAdvancePeriodInDays() == null || facility.getBookingAdvancePeriodInDays() <= 0){
                    facility.setBookingAdvancePeriodInDays(30l);
                }

            }
        }

        return false;
    }
}
