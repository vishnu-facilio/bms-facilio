package com.facilio.fsm.util;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceAppointmentUtil {

    public static List<ServiceAppointmentTicketStatusContext> getStatusList(List<String> statusList) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ServiceAppointmentTicketStatusContext> builder = new SelectRecordsBuilder<ServiceAppointmentTicketStatusContext>()
                .moduleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS)
                .beanClass(ServiceAppointmentTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS))
                .orderBy("ID");

        if(CollectionUtils.isNotEmpty(statusList)){
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.STATUS), StringUtils.join(statusList,","), StringOperators.IS));
        }

        List<ServiceAppointmentTicketStatusContext> statuses = builder.get();
        if (CollectionUtils.isNotEmpty(statuses)) {
            return statuses;
        }
        return null;
    }
    public static ServiceAppointmentTicketStatusContext getStatus(String status) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ServiceAppointmentTicketStatusContext> builder = new SelectRecordsBuilder<ServiceAppointmentTicketStatusContext>()
                .moduleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS)
                .beanClass(ServiceAppointmentTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.STATUS),status, StringOperators.IS))
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

    public static Map<String,ServiceAppointmentTicketStatusContext> getStatusMap(List<String> statusList) throws Exception {
        List<ServiceAppointmentTicketStatusContext> statuses = getStatusList(statusList);
        if(CollectionUtils.isNotEmpty(statuses)) {
            return statuses.stream().collect(Collectors.toMap(ServiceAppointmentTicketStatusContext::getStatus, Function.identity()));
        }
        return null;
    }

    public static void dispatchServiceAppointment(long appointmentId,long peopleId) throws Exception {
        if(appointmentId > 0 && peopleId > 0) {
            String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
            FacilioModule serviceTask = moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
            List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
            List<FacilioField> updateFields = new ArrayList<>();
            updateFields.add(saFieldMap.get("fieldAgent"));
            updateFields.add(saFieldMap.get("status"));

            FacilioContext context = V3Util.getSummary(serviceAppointmentModuleName, Collections.singletonList(appointmentId));

            if (Constants.getRecordList(context) != null) {
                ServiceAppointmentContext existingAppointment = (ServiceAppointmentContext) Constants.getRecordList(context).get(0);
                if (existingAppointment != null) {

                    V3PeopleContext fieldAgent = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId, V3PeopleContext.class);

                    existingAppointment.setFieldAgent(fieldAgent);
                    ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("dispatched");
                    if (appointmentStatus == null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Missing in-progress state");
                    }
                    existingAppointment.setStatus(appointmentStatus);
                    V3RecordAPI.updateRecord(existingAppointment, serviceAppointment, updateFields);

                    FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

                    GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                            .select(Collections.singleton(taskField))
                            .table("SERVICE_APPOINTMENT_TASK_REL")
                            .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
                    List<Map<String, Object>> maps = selectBuilder.get();
                    List<Long> taskIds = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(maps)) {
                        taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
                        List<ModuleBaseWithCustomFields> oldRecords = new ArrayList<>();
                        List<Map<String,Object>> updateRecordList = new ArrayList<>();
                        for(Long taskId : taskIds){
                            FacilioContext taskContext = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(taskId));
                            ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(taskContext).get(0);
                            oldRecords.add(existingTask);
                            Map<String,Object> updateProps = FieldUtil.getAsProperties(existingTask);
                            updateProps.put("status",ServiceTaskContext.ServiceTaskStatus.DISPATCHED.getIndex());
                            updateRecordList.add(updateProps);
                            V3Util.processAndUpdateBulkRecords(serviceTask, oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false);

                        }
//                        ServiceTaskContext task = new ServiceTaskContext();
//                        task.setId(taskIds.get(0));
//                        task.setStatus(ServiceTaskContext.ServiceTaskStatus.DISPATCHED.getIndex());
//                        V3Util.updateBulkRecords(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, FieldUtil.getAsProperties(task), taskIds, true, false);
                    }
//                    if (existingAppointment.getServiceOrder() != null) {
//                        ServiceOrderAPI.dispatchServiceOrder(existingAppointment.getServiceOrder().getId());
//                    } else {
//                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "No ServiceOrder is mapped to Service Appointment");
//                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid appointment or field agent details");
        }
    }

    public static void startServiceAppointment(long appointmentId) throws Exception {
        if(appointmentId > 0) {
            String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
            List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

            FacilioModule people = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
            List<FacilioField> peopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
            Map<String, FacilioField> peopleFieldMap = FieldFactory.getAsMap(peopleFields);

            FacilioModule timeSheetModule = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);

            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
            List<FacilioField> updateFields = new ArrayList<>();
            updateFields.add(saFieldMap.get("status"));
            updateFields.add(saFieldMap.get("actualStartTime"));

            Long currentTime = DateTimeUtil.getCurrenTime();

            FacilioContext context = V3Util.getSummary(serviceAppointmentModuleName, Collections.singletonList(appointmentId));

            if (Constants.getRecordList(context) != null) {
                ServiceAppointmentContext existingAppointment = (ServiceAppointmentContext) Constants.getRecordList(context).get(0);
                if (existingAppointment != null) {
                    if(existingAppointment.getFieldAgent() != null) {
                        long agentId = existingAppointment.getFieldAgent().getId();

                        //creating timesheet while starting work
                        List<TimeSheetContext> ongoingTimeSheets = getOngoingTimeSheets(agentId,appointmentId);
                        if(CollectionUtils.isNotEmpty(ongoingTimeSheets)){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Cannot start another work when timesheet is running");
                        } else {
                            TimeSheetContext newTimeSheet = new TimeSheetContext();
                            newTimeSheet.setStartTime(currentTime);
                            newTimeSheet.setServiceAppointment(existingAppointment);
                            newTimeSheet.setFieldAgent(existingAppointment.getFieldAgent());
                            newTimeSheet.setServiceOrder(existingAppointment.getServiceOrder());
                            Map<String, Object> recordProps = FieldUtil.getAsProperties(newTimeSheet);
                            List<TimeSheetTaskContext> tasks = new ArrayList<>();
                            if(CollectionUtils.isNotEmpty(existingAppointment.getServiceTasks())){
                                for(ServiceAppointmentTaskContext saTask : existingAppointment.getServiceTasks()){
                                    TimeSheetTaskContext newTask = new TimeSheetTaskContext();
                                    newTask.setRight(saTask.getRight());
                                    tasks.add(newTask);
                                }
                            }
                            recordProps.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,FieldUtil.getAsMapList(tasks,TimeSheetTaskContext.class));
                            V3Util.createRecord(timeSheetModule,recordProps);
                        }

                        //updating agent status to on-site on work start
                        V3PeopleContext agent = new V3PeopleContext();
                        agent.setId(agentId);
                        agent.setStatus(V3PeopleContext.Status.ON_SITE.getIndex());
                        V3RecordAPI.updateRecord(agent, people, Collections.singletonList(peopleFieldMap.get(FacilioConstants.ContextNames.STATUS)));

                        existingAppointment.setActualStartTime(currentTime);
                        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("inProgress");
                        if (appointmentStatus == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Missing in-progress state");
                        }
                        existingAppointment.setStatus(appointmentStatus);
                        V3RecordAPI.updateRecord(existingAppointment, serviceAppointment, updateFields);

                        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

                        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                                .select(Collections.singleton(taskField))
                                .table("SERVICE_APPOINTMENT_TASK_REL")
                                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
                        List<Map<String, Object>> maps = selectBuilder.get();
                        List<Long> taskIds = new ArrayList<>();
                        List<ModuleBaseWithCustomFields> oldRecords = new ArrayList<>();
                        List<Map<String,Object>> updateRecordList = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(maps)) {
                            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
                            for(Long taskId : taskIds){
                                FacilioContext taskContext = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(taskId));
                                ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(taskContext).get(0);
                                oldRecords.add(existingTask);
                                Map<String,Object> updateProps = FieldUtil.getAsProperties(existingTask);
                                updateProps.put("status",ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex());
                                updateProps.put("actualStartTime",currentTime);
                                updateRecordList.add(updateProps);
                                V3Util.processAndUpdateBulkRecords(Constants.getModBean().getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK), oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false);
                            }
//                            ServiceTaskContext task = new ServiceTaskContext();
//                            task.setId(taskIds.get(0));
//                            task.setStatus(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex());
//                            task.setActualStartTime(currentTime);
//                            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task), taskIds, true, false);
                        }
//                        if (existingAppointment.getServiceOrder() != null) {
//                            ServiceOrderAPI.startServiceOrder(existingAppointment.getServiceOrder().getId(),currentTime);
//                        } else {
//                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "No ServiceOrder is mapped to Service Appointment");
//                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid appointment details");
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
        updateFields.add(saFieldMap.get("actualDuration"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        FacilioModule serviceTask = moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> serviceTaskFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String,FacilioField> stFieldMap = FieldFactory.getAsMap(serviceTaskFields);

        FacilioModule people = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> peopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> peopleFieldMap = FieldFactory.getAsMap(peopleFields);

        FacilioContext context = V3Util.getSummary(serviceAppointmentModuleName, Collections.singletonList(appointmentId));

        if (Constants.getRecordList(context) != null) {
            ServiceAppointmentContext existingAppointment = (ServiceAppointmentContext) Constants.getRecordList(context).get(0);
            if (existingAppointment != null) {
                if(existingAppointment.getFieldAgent() != null) {
                    long agentId = existingAppointment.getFieldAgent().getId();

//                    List<Integer> closedTaskStates = new ArrayList<>();
//                    closedTaskStates.add(ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex());
//                    closedTaskStates.add(ServiceTaskContext.ServiceTaskStatus.CANCELLED.getIndex());
//
//                    List<Integer> openTaskStates = new ArrayList<>();
//                    openTaskStates.add(ServiceTaskContext.ServiceTaskStatus.NEW.getIndex());
//                    openTaskStates.add(ServiceTaskContext.ServiceTaskStatus.SCHEDULED.getIndex());
//                    openTaskStates.add(ServiceTaskContext.ServiceTaskStatus.DISPATCHED.getIndex());
//                    openTaskStates.add(ServiceTaskContext.ServiceTaskStatus.ON_HOLD.getIndex());

                    SelectRecordsBuilder<ServiceTaskContext> existingTasksBuilder = new SelectRecordsBuilder<ServiceTaskContext>()
                            .module(serviceTask)
                            .beanClass(ServiceTaskContext.class)
                            .select(Collections.singletonList(stFieldMap.get(FacilioConstants.ContextNames.STATUS)))
                            .andCondition(CriteriaAPI.getCondition(stFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT), String.valueOf(appointmentId), NumberOperators.EQUALS));
                    List<ServiceTaskContext> existingServiceTasks = existingTasksBuilder.get();

                    if (CollectionUtils.isNotEmpty(existingServiceTasks)) {
//                        Map<Integer, List<ServiceTaskContext>> taskMap = existingServiceTasks.stream().collect(Collectors.groupingBy(task -> task.getStatus()));
//                        if (openTaskStates.stream().anyMatch(taskMap::containsKey)) {
//                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "move tasks to in-progress state to complete appointment");
//                        } else {
                            List<TimeSheetContext> ongoingTimeSheets = getOngoingTimeSheets(agentId,appointmentId);
                            if(CollectionUtils.isNotEmpty(ongoingTimeSheets)){
                                for(TimeSheetContext ts : ongoingTimeSheets){
                                    closeOngoingTimeSheets(ts,currentTime);
                                }
                            }

                            long workDuration = getAppointmentDuration(agentId,appointmentId);
                            existingAppointment.setActualDuration(workDuration);
                            existingAppointment.setActualEndTime(currentTime);
                            ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("completed");
                            if (appointmentStatus == null) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Missing completed state");
                            }
                            existingAppointment.setStatus(appointmentStatus);
                            V3RecordAPI.updateRecord(existingAppointment, serviceAppointment, updateFields);

//                            List<ServiceTaskContext> inProgressTasks = taskMap.get(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex());
//                            if(CollectionUtils.isNotEmpty(inProgressTasks)){
//                                List<Long> taskIds = inProgressTasks.stream().map(ServiceTaskContext::getId).collect(Collectors.toList());
                            if(CollectionUtils.isNotEmpty(existingServiceTasks)){
                              List<Long> taskIds = existingServiceTasks.stream().map(ServiceTaskContext::getId).collect(Collectors.toList());
                                List<ModuleBaseWithCustomFields> oldRecords = new ArrayList<>();
                                List<Map<String,Object>> updateRecordList = new ArrayList<>();
                                for(Long taskId : taskIds){
                                    FacilioContext taskContext = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(taskId));
                                    ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(taskContext).get(0);
                                    oldRecords.add(existingTask);
                                    Map<String,Object> updateProps = FieldUtil.getAsProperties(existingTask);
                                    updateProps.put("status",ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex());
                                    updateProps.put("actualEndTime",currentTime);
                                    updateRecordList.add(updateProps);
                                    V3Util.processAndUpdateBulkRecords(serviceTask, oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false);
                                }
//                                ServiceTaskContext task = new ServiceTaskContext();
//                                task.setId(taskIds.get(0));
//                                task.setStatus(ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex());
//                                task.setActualStartTime(currentTime);
//                                V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task), taskIds, true, false);
                            }

                            V3PeopleContext agent = existingAppointment.getFieldAgent();
                            if(agent.isCheckedIn()) {
                                agent.setStatus(V3PeopleContext.Status.AVAILABLE.getIndex());
                                V3RecordAPI.updateRecord(agent, people, Collections.singletonList(peopleFieldMap.get(FacilioConstants.ContextNames.STATUS)));
                            }

//                        }
                    } else {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service tasks cannot be empty");
                    }
                }
            }
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

        FacilioModule serviceTask = moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> serviceTaskFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String,FacilioField> stFieldMap = FieldFactory.getAsMap(serviceTaskFields);

        FacilioModule people = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> peopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> peopleFieldMap = FieldFactory.getAsMap(peopleFields);

        FacilioContext context = V3Util.getSummary(serviceAppointmentModuleName, Collections.singletonList(appointmentId));

        if (Constants.getRecordList(context) != null) {
            ServiceAppointmentContext existingAppointment = (ServiceAppointmentContext) Constants.getRecordList(context).get(0);
            if (existingAppointment != null) {

                    SelectRecordsBuilder<ServiceTaskContext> existingTasksBuilder = new SelectRecordsBuilder<ServiceTaskContext>()
                            .module(serviceTask)
                            .beanClass(ServiceTaskContext.class)
                            .select(Collections.singletonList(stFieldMap.get(FacilioConstants.ContextNames.STATUS)))
                            .andCondition(CriteriaAPI.getCondition(stFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT), String.valueOf(appointmentId), NumberOperators.EQUALS));
                    List<ServiceTaskContext> existingServiceTasks = existingTasksBuilder.get();

                    if (CollectionUtils.isNotEmpty(existingServiceTasks)) {
                        if(existingAppointment.getFieldAgent() != null) {
                            long agentId = existingAppointment.getFieldAgent().getId();
                            List<TimeSheetContext> ongoingTimeSheets = getOngoingTimeSheets(agentId, appointmentId);
                            if (CollectionUtils.isNotEmpty(ongoingTimeSheets)) {
                                for (TimeSheetContext ts : ongoingTimeSheets) {
                                    closeOngoingTimeSheets(ts, currentTime);
                                }
                            }

                            long workDuration = getAppointmentDuration(agentId, appointmentId);
                            existingAppointment.setActualDuration(workDuration);
                        }
                        existingAppointment.setActualEndTime(currentTime);
                        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.CANCELLED);
                        if (appointmentStatus == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Missing cancelled state");
                        }
                        existingAppointment.setStatus(appointmentStatus);
                        V3RecordAPI.updateRecord(existingAppointment, serviceAppointment, updateFields);

                        if(CollectionUtils.isNotEmpty(existingServiceTasks)){
                            List<Long> taskIds = existingServiceTasks.stream().map(ServiceTaskContext::getId).collect(Collectors.toList());
                            List<ModuleBaseWithCustomFields> oldRecords = new ArrayList<>();
                            List<Map<String,Object>> updateRecordList = new ArrayList<>();
                            for(Long taskId : taskIds){
                                FacilioContext taskContext = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(taskId));
                                ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(taskContext).get(0);
                                oldRecords.add(existingTask);
                                Map<String,Object> updateProps = FieldUtil.getAsProperties(existingTask);
                                updateProps.put("status",ServiceTaskContext.ServiceTaskStatus.CANCELLED.getIndex());
                                updateProps.put("actualEndTime",currentTime);
                                updateRecordList.add(updateProps);
                                V3Util.processAndUpdateBulkRecords(serviceTask, oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false);

                            }
//                            ServiceTaskContext task = new ServiceTaskContext();
//                            task.setId(taskIds.get(0));
//                            task.setStatus(ServiceTaskContext.ServiceTaskStatus.CANCELLED.getIndex());
//                            task.setActualStartTime(currentTime);
//                            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task), taskIds, true, false);
                        }

                        V3PeopleContext agent = existingAppointment.getFieldAgent();
                        if( agent != null && agent.isCheckedIn()) {
                            agent.setStatus(V3PeopleContext.Status.AVAILABLE.getIndex());
                            V3RecordAPI.updateRecord(agent, people, Collections.singletonList(peopleFieldMap.get(FacilioConstants.ContextNames.STATUS)));
                        }

//                        }
                    } else {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service tasks cannot be empty");
                    }
            }
        }

//        ServiceAppointmentContext appointment = new ServiceAppointmentContext();
//        appointment.setId(appointmentId);
//        appointment.setActualEndTime(currentTime);
//        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("cancelled");
//        if(appointmentStatus == null){
//            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing in-progress state");
//        }
//        appointment.setStatus(appointmentStatus);
//        V3RecordAPI.updateRecord(appointment,serviceAppointment,updateFields);
//
//        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);
//
//        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//                .select(Collections.singleton(taskField))
//                .table("SERVICE_APPOINTMENT_TASK_REL")
//                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS));
//        List<Map<String, Object>> maps = selectBuilder.get();
//        List<Long> taskIds = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(maps)) {
//            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
//            ServiceTaskContext task  = new ServiceTaskContext();
//            task.setStatus(ServiceTaskContext.ServiceTaskStatus.CANCELLED.getIndex());
//            task.setActualEndTime(currentTime);
//            V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(task),taskIds, true,false);
//        }
    }

    public static void startTripForAppointment(Long appointmentId, TripContext tripData) throws Exception {
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        FacilioModule people = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> peopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> peopleFieldMap = FieldFactory.getAsMap(peopleFields);

        FacilioModule tripModule = moduleBean.getModule(FacilioConstants.Trip.TRIP);

        FacilioContext context = V3Util.getSummary(serviceAppointmentModuleName, Collections.singletonList(appointmentId));

        if(Constants.getRecordList(context) != null) {
            ServiceAppointmentContext existingAppointment = (ServiceAppointmentContext) Constants.getRecordList(context).get(0);
            if (existingAppointment != null) {
                if(existingAppointment.getFieldAgent() != null) {
                    long agentId = existingAppointment.getFieldAgent().getId();

                    //updating agent status to en-route on trip start
                    V3PeopleContext agent = new V3PeopleContext();
                    agent.setId(agentId);
                    agent.setStatus(V3PeopleContext.Status.EN_ROUTE.getIndex());
                    V3RecordAPI.updateRecord(agent, people, Collections.singletonList(peopleFieldMap.get(FacilioConstants.ContextNames.STATUS)));

                    if (!checkForOngoingTrip(agentId)){

                        TripContext newTrip = new TripContext();
                        newTrip.setServiceAppointment(existingAppointment);
                        newTrip.setStartTime(DateTimeUtil.getCurrenTime());
                        newTrip.setPeople(existingAppointment.getFieldAgent());
                        newTrip.setServiceOrder(existingAppointment.getServiceOrder());
                        if(tripData != null && tripData.getStartLocation() != null){
                            LocationContext location = tripData.getStartLocation();
                            if (location != null && location.getLat() != -1 && location.getLng() != -1) {
                                location.setName(existingAppointment.getName()+"Trip_Location_"+DateTimeUtil.getCurrenTime());
                                Context locationContext = new FacilioContext();
                                Constants.setRecord(locationContext, location);
                                if (location.getId() > 0) {
                                    locationContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
                                    FacilioChain updateLocation = FacilioChainFactory.updateLocationChain();
                                    updateLocation.execute(locationContext);
                                } else {
                                    FacilioChain addLocation = FacilioChainFactory.addLocationChain();
                                    addLocation.execute(locationContext);

                                    long recordId = Constants.getRecordId(locationContext);
                                    location.setId(recordId);
                                    newTrip.setStartLocation(location);
                                }
                            } else {
                                newTrip.setStartLocation(null);
                            }
                        }
                        //Creating new trip record
                        V3Util.createRecord(tripModule,FieldUtil.getAsProperties(newTrip));
                    }
                }
                //updating service appointment status to en-route only if it is in scheduled or dispatched state
                ServiceAppointmentTicketStatusContext enRouteStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.EN_ROUTE);
                if(enRouteStatus == null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing enRoute state");
                }
                existingAppointment.setStatus(enRouteStatus);
                V3RecordAPI.updateRecord(existingAppointment, serviceAppointment, Collections.singletonList(saFieldMap.get(FacilioConstants.ContextNames.STATUS)));

            }
        }
    }

    public static void endTripForAppointment(Long appointmentId, TripContext tripData) throws Exception {
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        FacilioModule people = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> peopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> peopleFieldMap = FieldFactory.getAsMap(peopleFields);

        FacilioModule tripModule = moduleBean.getModule(FacilioConstants.Trip.TRIP);

        FacilioContext context = V3Util.getSummary(serviceAppointmentModuleName, Collections.singletonList(appointmentId));
        if(Constants.getRecordList(context) != null) {
            ServiceAppointmentContext existingAppointment = (ServiceAppointmentContext) Constants.getRecordList(context).get(0);
            if (existingAppointment != null) {
                if(existingAppointment.getFieldAgent() != null) {
                    long agentId = existingAppointment.getFieldAgent().getId();
                    // updating agent status to
                    V3PeopleContext agent = new V3PeopleContext();
                    agent.setId(agentId);
                    Attendance attendance = AttendanceAPI.getAttendanceForToday(agentId);
                    agent.setStatus(V3PeopleContext.Status.NOT_AVAILABLE.getIndex());
                    if(attendance != null && attendance.getCheckInTime() != null && attendance.getCheckOutTime() == null){
                        agent.setStatus(V3PeopleContext.Status.AVAILABLE.getIndex());
                    }
                    V3RecordAPI.updateRecord(agent, people, Collections.singletonList(peopleFieldMap.get(FacilioConstants.ContextNames.STATUS)));


                    List<TripContext> OngoingTrips = getOngoingTrips(agentId,existingAppointment.getId());
                    if(CollectionUtils.isNotEmpty(OngoingTrips)){
                        for(TripContext OngoingTrip : OngoingTrips){
                            OngoingTrip.setEndTime(DateTimeUtil.getCurrenTime());
                            if(tripData != null && tripData.getEndLocation() != null){
                                LocationContext location = tripData.getEndLocation();
                                if (location != null && location.getLat() != -1 && location.getLng() != -1) {
                                    location.setName(existingAppointment.getName()+"Trip_Location_"+DateTimeUtil.getCurrenTime());
                                    Context locationContext = new FacilioContext();
                                    Constants.setRecord(locationContext, location);
                                    if (location.getId() > 0) {
                                        locationContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
                                        FacilioChain updateLocation = FacilioChainFactory.updateLocationChain();
                                        updateLocation.execute(locationContext);
                                    } else {
                                        FacilioChain addLocation = FacilioChainFactory.addLocationChain();
                                        addLocation.execute(locationContext);

                                        long recordId = Constants.getRecordId(locationContext);
                                        location.setId(recordId);
                                        OngoingTrip.setEndLocation(location);
                                    }
                                } else {
                                    OngoingTrip.setEndLocation(null);
                                }
                            }
                            V3RecordAPI.updateRecord(OngoingTrip,tripModule,Constants.getModBean().getAllFields(FacilioConstants.Trip.TRIP));
                        }
                    }
                }
                if(existingAppointment.getActualEndTime() != null){
                    ServiceAppointmentTicketStatusContext completedStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.COMPLETED);
                    if(completedStatus == null){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing completed state");
                    }
                    existingAppointment.setStatus(completedStatus);
                } else {
                    if(existingAppointment.getActualStartTime() != null){
                        ServiceAppointmentTicketStatusContext inProgressStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.IN_PROGRESS);
                        if(inProgressStatus == null){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing in-progress state");
                        }
                        existingAppointment.setStatus(inProgressStatus);
                    } else {
                        ServiceAppointmentTicketStatusContext dispatchStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.DISPATCHED);
                        if(dispatchStatus == null){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing dispatch state");
                        }
                        existingAppointment.setStatus(dispatchStatus);
                    }
                }
                V3RecordAPI.updateRecord(existingAppointment, serviceAppointment, Collections.singletonList(saFieldMap.get(FacilioConstants.ContextNames.STATUS)));
            }
        }
    }

    public static boolean checkForOngoingTrip(long peopleId) throws Exception {
        List<TripContext> ongoingTrips = getOngoingTrips(peopleId,null);
        if(CollectionUtils.isNotEmpty(ongoingTrips)){
            return true;
        }
        return false;
    }

    public static List<TripContext> getOngoingTrips(long peopleId, Long appointmentId) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule TripModule = moduleBean.getModule(FacilioConstants.Trip.TRIP);
        List<FacilioField> TripFields = moduleBean.getAllFields(FacilioConstants.Trip.TRIP);
        Map<String,FacilioField> TripFieldMap = FieldFactory.getAsMap(TripFields);
        SelectRecordsBuilder<TripContext> tripBuilder = new SelectRecordsBuilder<TripContext>()
                .module(TripModule)
                .beanClass(TripContext.class)
                .select(TripFields)
                .andCondition(CriteriaAPI.getCondition(TripFieldMap.get(FacilioConstants.ContextNames.PEOPLE),String.valueOf(peopleId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(TripFieldMap.get(FacilioConstants.ContextNames.ENDTIME), CommonOperators.IS_EMPTY));
        if(appointmentId != null && appointmentId > 0){
            tripBuilder.andCondition(CriteriaAPI.getCondition(TripFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),String.valueOf(appointmentId), NumberOperators.EQUALS));
        }
        List<TripContext> ongoingTrips = tripBuilder.get();
        if(CollectionUtils.isNotEmpty(ongoingTrips)){
            return ongoingTrips;
        }
        return null;
    }

    public static List<TimeSheetContext> getOngoingTimeSheets(long peopleId, Long appointmentId) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule timeSheetModule = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<FacilioField> timeSheetFields = moduleBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String,FacilioField> timeSheetFieldMap = FieldFactory.getAsMap(timeSheetFields);
        SelectRecordsBuilder<TimeSheetContext> timeSheetBuilder = new SelectRecordsBuilder<TimeSheetContext>()
                .module(timeSheetModule)
                .beanClass(TimeSheetContext.class)
                .select(timeSheetFields)
                .andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get("fieldAgent"),String.valueOf(peopleId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.ENDTIME),CommonOperators.IS_EMPTY));
        if(appointmentId != null && appointmentId > 0){
            timeSheetBuilder.andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),String.valueOf(appointmentId), NumberOperators.EQUALS));
        }
        return null;
    }

    public static void closeOngoingTimeSheets(TimeSheetContext ts, long endTime) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule timeSheetModule = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<FacilioField> timeSheetFields = moduleBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String,FacilioField> timeSheetFieldMap = FieldFactory.getAsMap(timeSheetFields);

        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(timeSheetFieldMap.get(FacilioConstants.ContextNames.ENDTIME));
        updateFields.add(timeSheetFieldMap.get(FacilioConstants.ContextNames.DURATION));

        UpdateRecordBuilder<TimeSheetContext> timeSheetBuilder = new UpdateRecordBuilder<TimeSheetContext>()
                .module(timeSheetModule)
                .fields(timeSheetFields)
                .andCondition(CriteriaAPI.getIdCondition(ts.getId(),timeSheetModule));

        Map<String,Object> updateProps = new HashMap<>();
        updateProps.put(FacilioConstants.ContextNames.ENDTIME,endTime);
        updateProps.put(FacilioConstants.ContextNames.DURATION,(ts.getStartTime() - endTime));

        timeSheetBuilder.updateViaMap(updateProps);
    }

    private static long getAppointmentDuration(long peopleId,long appointmentId) throws Exception {
        long duration = 0;
        List<TimeSheetContext> ongoingTimeSheets = getOngoingTimeSheets(peopleId,appointmentId);
        if(CollectionUtils.isNotEmpty(ongoingTimeSheets)){
            for (TimeSheetContext ts : ongoingTimeSheets){
                if (ts.getDuration() != null) {
                    duration += ts.getDuration();
                }
            }
        }
        return duration;
    }
}
