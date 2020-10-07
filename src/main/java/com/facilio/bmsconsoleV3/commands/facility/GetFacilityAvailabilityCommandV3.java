package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class GetFacilityAvailabilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long facilityId = (Long)context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_ID);
        if(facilityId == null || facilityId <= 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Facility Id cannot be null");
        }
        FacilityContext facilityContext = (FacilityContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, facilityId, FacilityContext.class);
        FacilityAPI.setFacilitySpecialAvailability(facilityContext);
        FacilityAPI.setFacilityWeekDayAvailability(facilityContext);
        if(CollectionUtils.isNotEmpty(facilityContext.getFacilitySpecialAvailabilities())) {

        }

        return false;
    }
}
