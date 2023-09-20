package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTaskContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.context.ServiceTaskStatusContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DelinkTaskOnSADeleteCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> recordIds = Constants.getRecordIds(context);
        List<ServiceAppointmentContext> serviceAppointments = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(recordIds)){
            FacilioContext listContext = V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,recordIds);
            serviceAppointments = Constants.getRecordList(listContext);
        }
        if (CollectionUtils.isNotEmpty(serviceAppointments)) {
            for(ServiceAppointmentContext serviceAppointment : serviceAppointments){
                List<ServiceAppointmentTaskContext> saTasks = serviceAppointment.getServiceTasks();
            if(CollectionUtils.isNotEmpty(saTasks)) {
                List<Long> serviceTaskIds = saTasks.stream().map(row -> (Long) row.getId()).collect(Collectors.toList());
                Map<String, Object> mapping = new HashMap<>();
                ServiceTaskStatusContext status = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.NEW);
                mapping.put("status", status);
                mapping.put("serviceAppointment", null);
                V3Util.updateBulkRecords(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, mapping, serviceTaskIds, null, null, false, false);
            }
            }
        }
        return false;
    }
}
