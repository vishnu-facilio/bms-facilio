package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class updateServiceAppointment  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long taskId = (Long) context.get(Constants.RECORD_ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> serviceAppointmentFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<FacilioField> serviceTaskFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(serviceAppointmentFields);
        Map<String, FacilioField> serviceTaskMap = FieldFactory.getAsMap(serviceTaskFields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getServiceAppointmentsTasksFields())
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK_ID", "right", String.valueOf(taskId), NumberOperators.EQUALS));

        Map<String, Object> criteriaProps = selectBuilder.fetchFirst();

        if(MapUtils.isNotEmpty(criteriaProps)) {
            context.put("serviceAppointmentId", String.valueOf(criteriaProps.get("serviceAppointment")));
            GenericSelectRecordBuilder serviceAppointmentBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getServiceAppointmentsTasksFields())
                    .table("SERVICE_APPOINTMENT_TASK_REL")
                    .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(criteriaProps.get("serviceAppointment")), NumberOperators.EQUALS));
//                    .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", null,));

            List<Map<String, Object>> serviceAppointmentTaskList = serviceAppointmentBuilder.get();

            List<Long> taskList = new ArrayList<>();

            for (Map<String, Object> appointmentTasks : serviceAppointmentTaskList) {
                taskList.add((Long) appointmentTasks.get("serviceTask"));
            }

        if (CollectionUtils.isNotEmpty(taskList)) {
            SelectRecordsBuilder<ServiceTaskContext> selectRecordsBuilder = new SelectRecordsBuilder<ServiceTaskContext>();
            selectRecordsBuilder.select(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK))
                    .module(serviceTaskModule)
                    .beanClass(ServiceTaskContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(taskList, serviceTaskModule))
                    .andCondition(CriteriaAPI.getCondition(serviceTaskMap.get("status"),ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex().toString(),StringOperators.IS ));

            List<ServiceTaskContext> serviceTasksList = selectRecordsBuilder.get();
            if (CollectionUtils.isNotEmpty(serviceTasksList)) {
                if(serviceAppointmentTaskList.size() == serviceTasksList.size()){
                    Map<String, Object> valMap = new HashMap<>();
                    valMap.put("isAllTasksClosed", true);
                    UpdateRecordBuilder<ServiceAppointmentContext> updateRecordBuilder = new UpdateRecordBuilder<>();
                    updateRecordBuilder.module(serviceAppointmentModule)
                            .fields(Collections.singletonList(fieldMap.get("isAllTasksClosed")))
                            .andCondition(CriteriaAPI.getIdCondition(String.valueOf(criteriaProps.get("serviceAppointment")), serviceAppointmentModule))
                            .updateViaMap(valMap);
                } else {
                    return false;
                }
            }
        }
    }
        return false;
    }
}
