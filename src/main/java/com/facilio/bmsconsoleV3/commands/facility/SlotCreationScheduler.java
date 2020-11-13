package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
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
                Long startTime =  System.currentTimeMillis();
                Long endTime = -1L;
                float daysBetween = 0;
                if(lastGeneratedTime != null) {
                    long difference = startTime - lastGeneratedTime;
                    daysBetween = (difference / (1000 * 60 * 60 * 24));
                    if(daysBetween < facility.getBookingAdvancePeriodInDays()) {
                        startTime = lastGeneratedTime;
                        endTime = (long) (startTime + (daysBetween * 1000 * 60 * 60 * 24));
                        LOGGER.log(Level.FINE, "Generating slots for Facility: " + facility.getId());
                        createSlots(facility, startTime, endTime);
                    }
                }
            }
        }

    }

}
