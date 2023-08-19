package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.*;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.fsm.util.ServiceTaskUtil.updateServiceTasks;

public class UpdateSAandTasks extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        if (CollectionUtils.isNotEmpty(serviceOrders)) {
            for (ServiceOrderContext serviceOrder : serviceOrders) {
                List<Long> serviceTaskIds = fetchServiceTasks(serviceOrder.getId());
                if (CollectionUtils.isNotEmpty(serviceTaskIds)) {

                    Long currentTime = DateTimeUtil.getCurrenTime();

                    String serviceOrderStatus = ServiceOrderAPI.getServiceOrderStatus(serviceOrder.getStatus().getId());

                    Set<Long> appointmentIds = fetchAppointmentIds(serviceTaskIds);

                    switch(serviceOrderStatus){

                        case FacilioConstants.ServiceOrder.COMPLETED:
                            updateServiceTasks(serviceTaskIds, FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED, currentTime);
                            if(CollectionUtils.isNotEmpty(appointmentIds)){
                                updateServiceAppointments(appointmentIds, currentTime, FacilioConstants.ServiceAppointment.COMPLETED);
                            }
                            break;
                        case FacilioConstants.ServiceOrder.CANCELLED:
                            updateServiceTasks(serviceTaskIds,  FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED, currentTime);
                            if(CollectionUtils.isNotEmpty(appointmentIds)) {
                                updateServiceAppointments(appointmentIds, currentTime, FacilioConstants.ServiceAppointment.CANCELLED);
                            }
                            break;
                    }

                    }
                }
                }

        return false;
        }


    public static void updateServiceAppointments(Set<Long> appointmentIds, Long currentTime, String status) throws Exception {


        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus(status);


        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(saFieldMap.get("status"));
        updateFields.add(saFieldMap.get("actualEndTime"));

        List<String> saStatus= new ArrayList<>();
        saStatus.add(FacilioConstants.ServiceAppointment.COMPLETED);
        saStatus.add(FacilioConstants.ServiceAppointment.CANCELLED);

        Map<String, ServiceAppointmentTicketStatusContext> statusMap=ServiceAppointmentUtil.getStatusMap(saStatus);

        List<Long>endStatus = new ArrayList<>();
        endStatus.add(statusMap.get(FacilioConstants.ServiceAppointment.CANCELLED).getId());
        endStatus.add(statusMap.get(FacilioConstants.ServiceAppointment.COMPLETED).getId());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(appointmentIds, serviceAppointment));
        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status",StringUtils.join(endStatus,","),NumberOperators.NOT_EQUALS));



        UpdateRecordBuilder<ServiceAppointmentContext> updateBuilder = new UpdateRecordBuilder<ServiceAppointmentContext>()
                .module(serviceAppointment)
                .fields(updateFields)
                .andCriteria(criteria);

        Map<String, Object> updateProps = new HashMap<>();
        updateProps.put("status", FieldUtil.getAsProperties(appointmentStatus));
        updateProps.put("actualEndTime", currentTime);

        updateBuilder.updateViaMap(updateProps);

        for(Long saId : appointmentIds){
            List<TimeSheetContext> ongoingTimeSheets = ServiceAppointmentUtil.getOngoingTimeSheets(null,saId);
            if(CollectionUtils.isNotEmpty(ongoingTimeSheets)){
                for(TimeSheetContext ts : ongoingTimeSheets){
                    ServiceAppointmentUtil.closeOngoingTimeSheets(ts,currentTime);
                }
            }
        }

    }

    public static List<Long> fetchServiceTasks(Long serviceOrderId)throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(FieldFactory.getIdField(taskModule)))
                .table("Service_Task")
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER", "serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            List<Long> taskIds = maps.stream().map(row -> (long) row.get("id")).collect(Collectors.toList());
            return taskIds;
        }
        return null;
    }

    public static Set<Long> fetchAppointmentIds(List<Long> taskIds) throws Exception {
        FacilioField appointmentField = Constants.getModBean().getField("left", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(appointmentField))
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK_ID", "right", StringUtils.join(taskIds,","), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            Set<Long> appointmentIds = new HashSet<>(maps.stream().map(row -> (long) row.get("left")).collect(Collectors.toList()));
            return appointmentIds;
        }
        return null;
    }



}
