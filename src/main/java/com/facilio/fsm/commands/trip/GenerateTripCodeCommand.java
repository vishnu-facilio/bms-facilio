package com.facilio.fsm.commands.trip;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class GenerateTripCodeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        String moduleName = (String) context.get("moduleName");
        List<TripContext> trips = (List<TripContext>) recordMap.get(context.get("moduleName"));
        if(CollectionUtils.isNotEmpty(trips)) {
            for (TripContext trip : trips) {
                trip.setCode(ServiceAppointmentUtil.generateAlphaNumericCode(moduleName));
            }
        }
        return false;
    }
}
