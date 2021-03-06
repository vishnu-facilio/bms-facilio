package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ValidateSpecialAvailabilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FacilitySpecialAvailabilityContext> splAvailabilities = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(splAvailabilities)) {
            DateTimeFormatter formatter = DateTimeUtil.getDateTimeFormat("dd/MM/YYYY");
            for(FacilitySpecialAvailabilityContext splAvailability : splAvailabilities){
                if(splAvailability.getFacility() != null) {
                    FacilityContext facility = (FacilityContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, splAvailability.getFacility().getId(), FacilityContext.class);
                    if (facility != null) {
                        if (splAvailability.getStartDate() <= facility.getSlotGeneratedUpto() || splAvailability.getEndDate() <= facility.getSlotGeneratedUpto()) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slots are already created for the selected dates. Please select a date after " + formatter.format(DateTimeUtil.getDateTime(facility.getSlotGeneratedUpto())));
                        }
                    }
                }
            }
        }

        return false;
    }
}
