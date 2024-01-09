package com.facilio.fsm.commands.timeOff;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TimeOffContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdatePeopleStatusBasedOnTimeOff extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String modName = FacilioConstants.TimeOff.TIME_OFF;
        FacilioModule peopleModule  = Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> peopleFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(peopleFields);

        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<TimeOffContext> timeOffList = recordMap.get(modName);
        if(CollectionUtils.isNotEmpty(timeOffList)){
            TimeOffContext timeOff = timeOffList.get(0);
            if(timeOff.getCurrentTime() >=timeOff.getStartTime() && timeOff.getCurrentTime()<timeOff.getEndTime()){
                if(timeOff.getPeople()!= null) {
                    List<FacilioField> fields = new ArrayList<>();
                    long peopleId = timeOff.getPeople().getId();
                    V3PeopleContext people = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId, V3PeopleContext.class);
                    if (people.getStatusEnum() == null || people.getStatusEnum().equals(V3PeopleContext.Status.AVAILABLE)) {
                        fields.add(fieldMap.get(FacilioConstants.ContextNames.STATUS));
                        people.setStatus(V3PeopleContext.Status.NOT_AVAILABLE.getIndex());
                        V3RecordAPI.updateRecord(people, peopleModule, fields);
                    }
                }
            }

        }
        return false;
    }
}
