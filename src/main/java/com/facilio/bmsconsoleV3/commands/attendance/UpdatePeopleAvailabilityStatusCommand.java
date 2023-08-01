package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdatePeopleAvailabilityStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String ModName = FacilioConstants.Attendance.ATTENDANCE_TRANSACTION;
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        FacilioModule peopleModule  = Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> peopleFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(peopleFields);
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<AttendanceTransaction> transactionsCreated = recordMap.get(ModName);
        if(CollectionUtils.isNotEmpty(transactionsCreated) && eventType == EventType.CREATE){
            AttendanceTransaction transaction = transactionsCreated.get(0);
            if(transaction.getPeople() != null) {
                long peopleId = transaction.getPeople().getId();
                V3PeopleContext people = new V3PeopleContext();
                people.setId(peopleId);
                if (transaction.getTransactionType() == AttendanceTransaction.Type.CHECK_IN) {
                        people.setStatus(V3PeopleContext.Status.AVAILABLE.getIndex());
                } else if (transaction.getTransactionType() == AttendanceTransaction.Type.CHECK_OUT) {
                        people.setStatus(V3PeopleContext.Status.NOT_AVAILABLE.getIndex());
                }
                if(people.getStatus() != null){
                    V3RecordAPI.updateRecord(people, peopleModule, Collections.singletonList(fieldMap.get(FacilioConstants.ContextNames.STATUS)));

                }
            }
        }
        return false;
    }
}
