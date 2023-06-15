package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.WeekDayAvailability;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FacilitySlotUpdateCheckCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String,List> oldRecordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        List<FacilityContext> facilities = recordMap.get(moduleName);
        Map<Long,Object> oldFacilities = (Map<Long, Object>) oldRecordMap.get(moduleName);
        Long bookingCanceledCount = 0L;
        if(CollectionUtils.isNotEmpty(facilities)) {
            for(FacilityContext facility : facilities) {
                FacilityContext oldRecordContext = (FacilityContext) oldFacilities.get(facility.getId());
                if(!facility.getSlotDuration().equals(oldRecordContext.getSlotDuration()))
                {
                    Long startTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis(), false);
                    Long endTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis(), false)  + (facility.getBookingAdvancePeriodInDays() * 1000 * 60 * 60 * 24);
                    bookingCanceledCount= FacilityAPI.cancelBookingAndDeleteSlotsAfterStartDate(facility,startTime);
                    FacilityAPI.createSlots(facility,startTime,endTime,false);
                }
                else
                {
                    Long startTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis(), false);
                    Long endTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis(), false)  + (facility.getBookingAdvancePeriodInDays() * 1000 * 60 * 60 * 24);
                    bookingCanceledCount= FacilityAPI.editWeekDaysSlots(facility,startTime,endTime,true);
                }
                facility.setBookingCanceledCount(bookingCanceledCount);
            }
        }

        return false;
    }
}
