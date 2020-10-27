package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Context;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.facilio.bmsconsoleV3.util.FacilityAPI.createSlots;

public class GenerateSlotCommand extends FacilioJob {
    private static final Logger LOGGER = Logger.getLogger(GenerateSlotCommand.class.getName());

    public static final long serialVersionUID = -1436898572846920375L;

    public GenerateSlotCommand() {
    }

    private boolean execute(Context context) throws Exception {
        FacilityContext facility = (FacilityContext) context
                .get(FacilioConstants.ContextNames.FacilityBooking.FACILITY);

        try {
            long startTime = System.currentTimeMillis();
            long endTime = startTime +  (facility.getBookingAdvancePeriodInDays() * 1000 * 60 * 60 * 24);
            LOGGER.log(Level.FINE, "Generating slots for Facility: " + facility.getId());
            createSlots(facility, startTime, endTime);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception creating slot for facility " + facility.getId());
            throw e;
        }

        return false;
    }




    @Override
    public void execute(JobContext jc) throws Exception {
        FacilioContext context = new FacilioContext();
        FacilityContext facility = (FacilityContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY,jc.getJobId(), FacilityContext.class);
        context.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY, facility);
        execute(context);

    }

}
