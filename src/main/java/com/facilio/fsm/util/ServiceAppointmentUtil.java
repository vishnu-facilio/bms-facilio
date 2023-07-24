package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceAppointmentUtil {
    public static void validateDispatch(Long appointmentId, Map<String, Object> mapping) throws Exception{

    }
    public static ServiceAppointmentTicketStatusContext getStatus(String status) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<ServiceAppointmentTicketStatusContext> builder = new SelectRecordsBuilder<ServiceAppointmentTicketStatusContext>()
                .moduleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS)
                .beanClass(ServiceAppointmentTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS))
                .andCustomWhere("STATUS = ?", status)
                .orderBy("ID");

        List<ServiceAppointmentTicketStatusContext> statuses = builder.get();
        if (CollectionUtils.isNotEmpty(statuses)) {
            return statuses.get(0);
        }
        return null;
    }

    public static List<ServiceAppointmentTicketStatusContext> getStatusOfStatusType(ServiceAppointmentTicketStatusContext.StatusType statusType) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SelectRecordsBuilder<ServiceAppointmentTicketStatusContext> builder = new SelectRecordsBuilder<ServiceAppointmentTicketStatusContext>()
                .moduleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS)
                .beanClass(ServiceAppointmentTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS))
                .andCustomWhere("STATUS_TYPE = ?", statusType.getIndex())
                .orderBy("ID");
        List<ServiceAppointmentTicketStatusContext> statuses = builder.get();
        return statuses;
    }

    public static void scheduleServiceAppointment(Long appointmentId, Long startTime, Long endTime) throws Exception {
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(saFieldMap.get("scheduledStartTime"));
        updateFields.add(saFieldMap.get("scheduledStartTime"));
        updateFields.add(saFieldMap.get("status"));

        ServiceAppointmentContext appointment = new ServiceAppointmentContext();
        appointment.setId(appointmentId);
        appointment.setScheduledStartTime(startTime);
        appointment.setScheduledEndTime(endTime);
        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("scheduled");
        appointment.setStatus(appointmentStatus);
        V3RecordAPI.updateRecord(appointment,serviceAppointment,updateFields);

        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(taskField))
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
            ServiceTaskContext task  = new ServiceTaskContext();
            task.setStatus(ServiceTaskContext.ServiceTaskStatus.SCHEDULED.getIndex());
            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task),taskIds, true,false);
        }
    }

    public static void dispatchServiceAppointment(Long appointmentId,Long peopleId) throws Exception {
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(saFieldMap.get("fieldAgent"));
        updateFields.add(saFieldMap.get("status"));

        V3PeopleContext fieldAgent = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,peopleId);

        ServiceAppointmentContext appointment = new ServiceAppointmentContext();
        appointment.setId(appointmentId);
        appointment.setFieldAgent(fieldAgent);
        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("dispatched");
        appointment.setStatus(appointmentStatus);
        V3RecordAPI.updateRecord(appointment,serviceAppointment,updateFields);

        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(taskField))
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
            ServiceTaskContext task  = new ServiceTaskContext();
            task.setStatus(ServiceTaskContext.ServiceTaskStatus.DISPATCHED.getIndex());
            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task),taskIds, true,false);
        }
    }

    public static void startServiceAppointment(Long appointmentId) throws Exception {
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(saFieldMap.get("status"));
        updateFields.add(saFieldMap.get("actualStartTime"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        ServiceAppointmentContext appointment = new ServiceAppointmentContext();
        appointment.setId(appointmentId);
        appointment.setActualStartTime(currentTime);
        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("inProgress");
        appointment.setStatus(appointmentStatus);
        V3RecordAPI.updateRecord(appointment,serviceAppointment,updateFields);

        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(taskField))
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
            ServiceTaskContext task  = new ServiceTaskContext();
            task.setStatus(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex());
            task.setActualStartTime(currentTime);
            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task),taskIds, true,false);
        }
    }
    public static void completeServiceAppointment(Long appointmentId) throws Exception {
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(saFieldMap.get("status"));
        updateFields.add(saFieldMap.get("actualEndTime"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        ServiceAppointmentContext appointment = new ServiceAppointmentContext();
        appointment.setId(appointmentId);
        appointment.setActualEndTime(currentTime);
        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("completed");
        appointment.setStatus(appointmentStatus);
        V3RecordAPI.updateRecord(appointment,serviceAppointment,updateFields);

        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(taskField))
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
            ServiceTaskContext task  = new ServiceTaskContext();
            task.setStatus(ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex());
            task.setActualEndTime(currentTime);
            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task),taskIds, true,false);
        }
    }
    public static void cancelServiceAppointment(Long appointmentId) throws Exception {
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(saFieldMap.get("status"));
        updateFields.add(saFieldMap.get("actualEndTime"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        ServiceAppointmentContext appointment = new ServiceAppointmentContext();
        appointment.setId(appointmentId);
        appointment.setActualEndTime(currentTime);
        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("cancelled");
        appointment.setStatus(appointmentStatus);
        V3RecordAPI.updateRecord(appointment,serviceAppointment,updateFields);

        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(taskField))
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
            ServiceTaskContext task  = new ServiceTaskContext();
            task.setStatus(ServiceTaskContext.ServiceTaskStatus.CANCELLED.getIndex());
            task.setActualEndTime(currentTime);
            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task),taskIds, true,false);
        }
    }

}
