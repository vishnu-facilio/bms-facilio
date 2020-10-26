package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetFacilityAvailabilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> facilityIds = (List<Long>)Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        Long startDateTime = -1L;
        Long endDateTime = -1L;
        Boolean fetchAvailability = false;
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("startTime") && queryParams.containsKey("endTime")) {
            fetchAvailability = true;
            startDateTime = Long.valueOf(((List<String>)queryParams.get("startTime")).get(0));
            endDateTime = Long.valueOf(((List<String>)queryParams.get("endTime")).get(0));

        }

        if(CollectionUtils.isEmpty(facilityIds)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Facility Id cannot be null");
        }
        for(Long id : facilityIds) {

            FacilityContext facilityContext = (FacilityContext) CommandUtil.getModuleData(context, moduleName,id);
            FacilityAPI.setFacilitySpecialAvailability(facilityContext, startDateTime, endDateTime);
            FacilityAPI.setFacilityWeekDayAvailability(facilityContext);
            if(fetchAvailability) {
                List<SlotContext> slotList = FacilityAPI.getAvailabilitySlots(facilityContext, startDateTime, endDateTime);
                if (CollectionUtils.isNotEmpty(slotList)) {
                    facilityContext.setSlots(slotList);
                }
            }
        }
        return false;
    }
}
