package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.facilio.bmsconsoleV3.util.FacilityAPI.createSlots;

public class SlotCreationScheduler extends FacilioJob {
    private static final Logger LOGGER = Logger.getLogger(SlotCreationScheduler.class.getName());


    @Override
    public void execute(JobContext jc) throws Exception {
        // TODO Auto-generated method stub

        List<FacilityContext> facilities = FacilityAPI.getFacilityList();
        if (CollectionUtils.isNotEmpty(facilities)) {
            for (FacilityContext facility : facilities) {
                Long lastGeneratedTime = facility.getSlotGeneratedUpto();
                Long startTime = DateTimeUtil.getDayStartTimeOf(lastGeneratedTime, false) + (1000 * 60 * 60 * 24);
                Long endTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis(), false)  + (facility.getBookingAdvancePeriodInDays() * 1000 * 60 * 60 * 24);
                LOGGER.log(Level.FINE, "Generating slots for Facility: " + facility.getId());
                createSlots(facility, startTime, endTime,true);

            }
        }

    }

}
