package com.facilio.fsm.commands.serviceTasks;

import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceAppointmentTaskContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceTaskUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DeleteSATaskRelCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> taskIds = (List<Long>) context.get("recordIds");

        if (CollectionUtils.isNotEmpty(taskIds)) {
            FacilioField status = Constants.getModBean().getField("status", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
            Collection<SupplementRecord> lookUpfields = new ArrayList<>();
            lookUpfields.add((LookupField) status);
            List<ServiceTaskContext> serviceTasks = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, taskIds, ServiceTaskContext.class, lookUpfields);
            if (CollectionUtils.isNotEmpty(serviceTasks)) {
                for (ServiceTaskContext serviceTask : serviceTasks) {
                    if (serviceTask.getStatus() != null && (serviceTask.getStatus().getName().equals(FacilioConstants.ContextNames.ServiceTaskStatus.NEW) || serviceTask.getStatus().getName().equals(FacilioConstants.ContextNames.ServiceTaskStatus.SCHEDULED) || serviceTask.getStatus().getName().equals(FacilioConstants.ContextNames.ServiceTaskStatus.DISPATCHED))) {
                        //Check if this is the only task associated with Service Appointment
                        if (serviceTask.getServiceAppointment() != null && serviceTask.getServiceAppointment().getId() > 0) {
                            List<Long> tasks = ServiceTaskUtil.getTasksFromServiceAppointment(serviceTask.getServiceAppointment().getId());
                            if (CollectionUtils.isNotEmpty(tasks)) {
                                if (tasks.size() < 2) {
                                    throw new FSMException(FSMErrorCode.NOT_ENOUGH_TASK_IN_SA);
                                }
                            }
                        }
                    }
                    else {
                        throw new FSMException(FSMErrorCode.TASK_RECORD_LOCKED);
                    }
                }
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("SERVICE_TASK_ID", "right", StringUtils.join(taskIds,","), NumberOperators.EQUALS));
                V3RecordAPI.deleteRecords(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK, criteria, false);

            }
        }

        return false;
    }
}
