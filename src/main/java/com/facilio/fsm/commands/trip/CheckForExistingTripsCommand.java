package com.facilio.fsm.commands.trip;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class CheckForExistingTripsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<TripContext> trips = (List<TripContext>) recordMap.get(context.get("moduleName"));

        if(CollectionUtils.isNotEmpty(trips)){
            for(TripContext trip : trips){
                if(trip.getPeople() != null && trip.getStartTime() > 0){
                    List<TimeSheetContext> records = ServiceAppointmentUtil.getTimeSheetsForTimeRange(trip.getPeople().getId(), trip.getStartTime(), trip.getEndTime());
                    if(CollectionUtils.isNotEmpty(records)){
                        throw new FSMException(FSMErrorCode.SA_TRIP_ALREADY_RUNNING);
                    }
                } else {
                    throw new FSMException(FSMErrorCode.TRIP_NOT_ENOUGH_DETAILS);
                }
            }
        }
        return false;
    }
}
