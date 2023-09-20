package com.facilio.fsm.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.activity.ServiceAppointmentActivityType;
import com.facilio.fsm.context.*;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

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

    public static void dispatchServiceAppointment(Context dispatchContext) throws Exception {
        Long appointmentId = (Long) dispatchContext.get(FacilioConstants.ContextNames.RECORD_ID);
        Long peopleId = (Long) dispatchContext.get(FacilioConstants.ServiceAppointment.FIELD_AGENT_ID);
        Long scheduledStartTime = (Long) dispatchContext.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME);
        Long scheduledEndTime = (Long) dispatchContext.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME);
        Boolean skipValidation = (Boolean) dispatchContext.get(FacilioConstants.ServiceAppointment.SKIP_VALIDATION);
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
                List<ModuleBaseWithCustomFields> oldSARecords = new ArrayList<>();
                List<Map<String,Object>> updateSARecordList = new ArrayList<>();
                if (existingAppointment != null) {
                    oldSARecords.add(existingAppointment);
                    Map<String,Object> updateSAProps = FieldUtil.getAsProperties(existingAppointment);
                    V3PeopleContext fieldAgent = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId, V3PeopleContext.class);
                    updateSAProps.put("fieldAgent",fieldAgent);
                    ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("dispatched");
                    if (appointmentStatus == null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Missing in-progress state");
                    }
                    updateSAProps.put("status",appointmentStatus);
                    if(scheduledStartTime != null && scheduledStartTime > 0){
                        updateSAProps.put("scheduledStartTime",scheduledStartTime);
                    }
                    if(scheduledEndTime != null && scheduledEndTime > 0){
                        updateSAProps.put("scheduledEndTime",scheduledEndTime);
                    }
                    JSONObject bodyParams = new JSONObject();
                    if(skipValidation != null && !skipValidation){
                        bodyParams.put(FacilioConstants.ServiceAppointment.SKIP_VALIDATION,false);
                    }
                    updateSARecordList.add(updateSAProps);
                    V3Util.processAndUpdateBulkRecords(serviceAppointment, oldSARecords, updateSARecordList, bodyParams, null, null, null, null, null, null, null, true,false,null);
                    JSONObject info = new JSONObject();
                    info.put("fieldAgent",fieldAgent.getName());
                    info.put("doneBy", AccountUtil.getCurrentUser().getName());
                    CommonCommandUtil.addActivityToContext(existingAppointment.getId(), -1, ServiceAppointmentActivityType.DISPATCH, info, (FacilioContext) dispatchContext);
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
                            ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.DISPATCHED);
                            updateProps.put("status",taskStatus);
                            updateRecordList.add(updateProps);
                        }
                        V3Util.processAndUpdateBulkRecords(serviceTask, oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false,null);
                    }
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
                                    newTask.setId(saTask.getId());
                                    tasks.add(newTask);
                                }
                            }
                            recordProps.put("serviceTasks",FieldUtil.getAsMapList(tasks,TimeSheetTaskContext.class));
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
                                ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
                                updateProps.put("status",taskStatus);
                                updateProps.put("actualStartTime",currentTime);
                                updateRecordList.add(updateProps);
                            }
                            V3Util.processAndUpdateBulkRecords(Constants.getModBean().getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK), oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false,null);
                        }
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

                    SelectRecordsBuilder<ServiceTaskContext> existingTasksBuilder = new SelectRecordsBuilder<ServiceTaskContext>()
                            .module(serviceTask)
                            .beanClass(ServiceTaskContext.class)
                            .select(Collections.singletonList(stFieldMap.get(FacilioConstants.ContextNames.STATUS)))
                            .andCondition(CriteriaAPI.getCondition(stFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT), String.valueOf(appointmentId), NumberOperators.EQUALS));
                    List<ServiceTaskContext> existingServiceTasks = existingTasksBuilder.get();

                    if (CollectionUtils.isNotEmpty(existingServiceTasks)) {
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
                            if(CollectionUtils.isNotEmpty(existingServiceTasks)){
                              List<Long> taskIds = existingServiceTasks.stream().map(ServiceTaskContext::getId).collect(Collectors.toList());
                                List<ModuleBaseWithCustomFields> oldRecords = new ArrayList<>();
                                List<Map<String,Object>> updateRecordList = new ArrayList<>();
                                for(Long taskId : taskIds){
                                    FacilioContext taskContext = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(taskId));
                                    ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(taskContext).get(0);
                                    if(existingTask.getStatus()!=null && !existingTask.getStatus().getName().equals(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED)
                                     && !existingTask.getStatus().getName().equals(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED )) {
                                        oldRecords.add(existingTask);
                                        Map<String, Object> updateProps = FieldUtil.getAsProperties(existingTask);
                                        ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED);
                                        updateProps.put("status", taskStatus);
                                        updateProps.put("actualEndTime", currentTime);
                                        updateRecordList.add(updateProps);
                                    }
                                }
                                V3Util.processAndUpdateBulkRecords(serviceTask, oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false,null);

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
                                if(existingTask.getStatus()!=null && !existingTask.getStatus().getName().equals(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED)
                                        && !existingTask.getStatus().getName().equals(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED )) {
                                    oldRecords.add(existingTask);
                                    Map<String,Object> updateProps = FieldUtil.getAsProperties(existingTask);
                                    ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED);
                                    updateProps.put("status",taskStatus);
                                    updateProps.put("actualEndTime",currentTime);
                                    updateRecordList.add(updateProps);
                                }
                            }
                            V3Util.processAndUpdateBulkRecords(serviceTask, oldRecords, updateRecordList, null, null, null, null, null, null, null, null, true,false,null);
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
    }

    public static TripContext startTripForAppointment(Long appointmentId, LocationContext location) throws Exception {
        TripContext trip = null;
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

                    List<TripContext> ongoingTrips = getOngoingTrips(agentId,null);
                    if(CollectionUtils.isNotEmpty(ongoingTrips)){
                        List<Long> ongoingServiceAppointments = new ArrayList<>();
                        for (TripContext ongoingTrip : ongoingTrips){
                            if(ongoingTrip.getServiceAppointment() != null){
                                ongoingServiceAppointments.add(ongoingTrip.getServiceAppointment().getId());
                            }
                        }
                        throw new FSMException(FSMErrorCode.TRIP_CANNOT_BE_STARTED,StringUtils.join(ongoingServiceAppointments,","));
                    } else {
                        TripContext newTrip = new TripContext();
                        newTrip.setServiceAppointment(existingAppointment);
                        newTrip.setStartTime(DateTimeUtil.getCurrenTime());
                        newTrip.setPeople(existingAppointment.getFieldAgent());
                        newTrip.setServiceOrder(existingAppointment.getServiceOrder());
                        newTrip.setStatus(getTripStatus(FacilioConstants.Trip.IN_PROGRESS));
                        if(location != null){
                            if (location != null && location.getLat() != -1 && location.getLng() != -1) {
                                if(location.getName() == null) {
                                    location.setName(existingAppointment.getName() + "Trip_Location_" + DateTimeUtil.getCurrenTime());
                                }
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
                        FacilioContext tripContext = V3Util.createRecord(tripModule,FieldUtil.getAsProperties(newTrip));;
                        Map<String, List> recordMap = (Map<String, List>) tripContext.get(Constants.RECORD_MAP);
                        trip = (TripContext) recordMap.get(FacilioConstants.Trip.TRIP).get(0);
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
        return trip;
    }

    public static List<TripContext> endTripForAppointment(Long appointmentId, LocationContext location) throws Exception {
        List<TripContext> OngoingTrips = null;
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


                    OngoingTrips = getOngoingTrips(agentId,existingAppointment.getId());
                    if(CollectionUtils.isNotEmpty(OngoingTrips)){
                        for(TripContext OngoingTrip : OngoingTrips){
                            OngoingTrip.setEndTime(DateTimeUtil.getCurrenTime());
                            Long duration = OngoingTrip.getEndTime() - OngoingTrip.getStartTime();
                            OngoingTrip.setTripDuration(duration/1000);
                            OngoingTrip.setStatus(getTripStatus(FacilioConstants.Trip.COMPLETED));
                            if(location != null){
                                if (location != null && location.getLat() != -1 && location.getLng() != -1) {
                                    if(location.getName() == null) {
                                        location.setName(existingAppointment.getName() + "Trip_Location_" + DateTimeUtil.getCurrenTime());
                                    }
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
                            TripUtil.ConstructLocationHistoryImage(OngoingTrip);
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
        return OngoingTrips;
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
                .andCondition(CriteriaAPI.getCondition(TripFieldMap.get(FacilioConstants.ContextNames.ENDTIME), CommonOperators.IS_EMPTY))
                .fetchSupplement((SupplementRecord) TripFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        if(appointmentId != null && appointmentId > 0){
            tripBuilder.andCondition(CriteriaAPI.getCondition(TripFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),String.valueOf(appointmentId), NumberOperators.EQUALS));
        }
        List<TripContext> ongoingTrips = tripBuilder.get();
        if(CollectionUtils.isNotEmpty(ongoingTrips)){
            return ongoingTrips;
        }
        return null;
    }

    public static List<TimeSheetContext> getOngoingTimeSheets(Long peopleId, Long appointmentId) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule timeSheetModule = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<FacilioField> timeSheetFields = moduleBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String,FacilioField> timeSheetFieldMap = FieldFactory.getAsMap(timeSheetFields);
        SelectRecordsBuilder<TimeSheetContext> timeSheetBuilder = new SelectRecordsBuilder<TimeSheetContext>()
                .module(timeSheetModule)
                .beanClass(TimeSheetContext.class)
                .select(timeSheetFields)
                .andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.ENDTIME),CommonOperators.IS_EMPTY))
                .fetchSupplement((SupplementRecord) timeSheetFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));;
        if(peopleId != null && peopleId >0){
            timeSheetBuilder.andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get("fieldAgent"),String.valueOf(peopleId),NumberOperators.EQUALS));
        }
        if(appointmentId != null && appointmentId > 0){
            timeSheetBuilder.andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),String.valueOf(appointmentId), NumberOperators.EQUALS));
        }
        List<TimeSheetContext> ongoingTimeSheets = timeSheetBuilder.get();
        if(CollectionUtils.isNotEmpty(ongoingTimeSheets)){
            return ongoingTimeSheets;
        }
        return null;
    }

    public static List<TimeSheetContext> getTimeSheetsForTimeRange(long peopleId, long startTime, Long endTime) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule timeSheetModule = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<FacilioField> timeSheetFields = moduleBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String,FacilioField> timeSheetFieldMap = FieldFactory.getAsMap(timeSheetFields);
        Criteria timeCriteria = new Criteria();
        if(endTime != null && endTime > 0) {
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.START_TIME), startTime + "," + endTime, DateOperators.BETWEEN));
            timeCriteria.addOrCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.ENDTIME), startTime + "," + endTime, DateOperators.BETWEEN));
        } else {
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.START_TIME), String.valueOf(startTime), DateOperators.IS_BEFORE));
            Criteria startTimeCriteria = new Criteria();
            startTimeCriteria.addAndCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.ENDTIME), String.valueOf(startTime), DateOperators.IS_AFTER));
            startTimeCriteria.addOrCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.ENDTIME), CommonOperators.IS_EMPTY));
            timeCriteria.andCriteria(startTimeCriteria);
        }
        SelectRecordsBuilder<TimeSheetContext> timeSheetBuilder = new SelectRecordsBuilder<TimeSheetContext>()
                .module(timeSheetModule)
                .beanClass(TimeSheetContext.class)
                .select(timeSheetFields)
                .andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get("fieldAgent"),String.valueOf(peopleId),NumberOperators.EQUALS))
                .andCriteria(timeCriteria)
                ;
        List<TimeSheetContext> timeSheets = timeSheetBuilder.get();
        if(CollectionUtils.isNotEmpty(timeSheets)){
            return timeSheets;
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
//        updateFields.add(timeSheetFieldMap.get(FacilioConstants.TimeSheet.TIME_SHEET_STATUS));

        UpdateRecordBuilder<TimeSheetContext> timeSheetBuilder = new UpdateRecordBuilder<TimeSheetContext>()
                .module(timeSheetModule)
                .fields(timeSheetFields)
                .andCondition(CriteriaAPI.getIdCondition(ts.getId(),timeSheetModule));

        Map<String,Object> updateProps = new HashMap<>();
        updateProps.put(FacilioConstants.ContextNames.ENDTIME,endTime);
        updateProps.put(FacilioConstants.ContextNames.DURATION,(endTime - ts.getStartTime())/1000);

        timeSheetBuilder.updateViaMap(updateProps);
    }

    public static long getAppointmentDuration(long peopleId,long appointmentId) throws Exception {
        long duration = 0;
        List<TimeSheetContext> closedTimeSheets = getClosedTimeSheets(peopleId,appointmentId);
        if(CollectionUtils.isNotEmpty(closedTimeSheets)){
            for (TimeSheetContext ts : closedTimeSheets){
                if (ts.getDuration() != null) {
                    duration += ts.getDuration();
                }
            }
        }
        return duration;
    }

    public static List<TripContext> getTripsForTimeRange(long peopleId, long startTime, Long endTime) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule tripModule = moduleBean.getModule(FacilioConstants.Trip.TRIP);
        List<FacilioField> tripFields = moduleBean.getAllFields(FacilioConstants.Trip.TRIP);
        Map<String,FacilioField> tripFieldMap = FieldFactory.getAsMap(tripFields);
        Criteria timeCriteria = new Criteria();
        if(endTime != null && endTime > 0) {
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(tripFieldMap.get(FacilioConstants.ContextNames.START_TIME), startTime + "," + endTime, DateOperators.BETWEEN));
            timeCriteria.addOrCondition(CriteriaAPI.getCondition(tripFieldMap.get(FacilioConstants.ContextNames.ENDTIME), startTime + "," + endTime, DateOperators.BETWEEN));
        } else {
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(tripFieldMap.get(FacilioConstants.ContextNames.START_TIME), String.valueOf(startTime), DateOperators.IS_BEFORE));
            Criteria startTimeCriteria = new Criteria();
            startTimeCriteria.addAndCondition(CriteriaAPI.getCondition(tripFieldMap.get(FacilioConstants.ContextNames.ENDTIME), String.valueOf(startTime), DateOperators.IS_AFTER));
            startTimeCriteria.addOrCondition(CriteriaAPI.getCondition(tripFieldMap.get(FacilioConstants.ContextNames.ENDTIME), CommonOperators.IS_EMPTY));
            timeCriteria.andCriteria(startTimeCriteria);
        }
        SelectRecordsBuilder<TripContext> tripBuilder = new SelectRecordsBuilder<TripContext>()
                .module(tripModule)
                .beanClass(TripContext.class)
                .select(tripFields)
                .andCondition(CriteriaAPI.getCondition(tripFieldMap.get("people"),String.valueOf(peopleId),NumberOperators.EQUALS))
                .andCriteria(timeCriteria)
                ;
        List<TripContext> tripContexts = tripBuilder.get();
        if(CollectionUtils.isNotEmpty(tripContexts)){
            return tripContexts;
        }
        return null;
    }

    public static List<TimeSheetContext> getClosedTimeSheets(Long peopleId, Long appointmentId) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule timeSheetModule = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<FacilioField> timeSheetFields = moduleBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String,FacilioField> timeSheetFieldMap = FieldFactory.getAsMap(timeSheetFields);
        SelectRecordsBuilder<TimeSheetContext> timeSheetBuilder = new SelectRecordsBuilder<TimeSheetContext>()
                .module(timeSheetModule)
                .beanClass(TimeSheetContext.class)
                .select(timeSheetFields)
                .andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.START_TIME),CommonOperators.IS_NOT_EMPTY))
                .andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ContextNames.ENDTIME),CommonOperators.IS_NOT_EMPTY));
        if(peopleId != null && peopleId >0){
            timeSheetBuilder.andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get("fieldAgent"),String.valueOf(peopleId),NumberOperators.EQUALS));
        }
        if(appointmentId != null && appointmentId > 0){
            timeSheetBuilder.andCondition(CriteriaAPI.getCondition(timeSheetFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),String.valueOf(appointmentId), NumberOperators.EQUALS));
        }
        List<TimeSheetContext> closedTimeSheets = timeSheetBuilder.get();
        if(CollectionUtils.isNotEmpty(closedTimeSheets)){
            return closedTimeSheets;
        }
        return null;
    }

    public static String generateAlphaNumericCode(String moduleName)throws Exception{
        String code = null;
        long localId = getModuleLocalId(moduleName);
        switch (moduleName){
            case FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT:
                code = "SA-" +(localId+ 1);
                break;
            case FacilioConstants.TimeSheet.TIME_SHEET:
                code = "TS-" +(localId+ 1);
                break;
            case FacilioConstants.TimeOff.TIME_OFF:
                code = "TO-" +(localId+ 1);
                break;
            case FacilioConstants.Trip.TRIP:
                code = "TP-" +(localId+ 1);
                break;
            case FacilioConstants.Territory.TERRITORY:
                code = "TR-" +(localId+ 1);
                break;
        }
        return code;

    }


    public static long getModuleLocalId(String moduleName) throws Exception {

        FacilioModule module = ModuleFactory.getModuleLocalIdModule();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getModuleLocalIdFields())
                .andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleName, StringOperators.IS));

        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (props != null && !props.isEmpty()) {
            return (long) props.get(0).get("localId");
        }
        return -1;

    }
    public static PriorityContext getPriority(String status) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<PriorityContext> builder = new SelectRecordsBuilder<PriorityContext>()
                .moduleName(FacilioConstants.Priority.PRIORITY)
                .beanClass(PriorityContext.class)
                .select(modBean.getAllFields(FacilioConstants.Priority.PRIORITY))
                .andCondition(CriteriaAPI.getCondition("PRIORITY","priority",status,StringOperators.IS));

        PriorityContext priority = builder.fetchFirst();
        return priority;
    }

    public static TimeSheetStatusContext getTimeSheetStatus(String status) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<TimeSheetStatusContext> builder = new SelectRecordsBuilder<TimeSheetStatusContext>()
                .moduleName(FacilioConstants.TimeSheet.TIME_SHEET_STATUS)
                .beanClass(TimeSheetStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET_STATUS))
                .andCondition(CriteriaAPI.getCondition("STATUS","status",status,StringOperators.IS));

        TimeSheetStatusContext timeSheetStatus = builder.fetchFirst();
        return timeSheetStatus;
    }

    public static TripStatusContext getTripStatus(String status) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<TripStatusContext> builder = new SelectRecordsBuilder<TripStatusContext>()
                .moduleName(FacilioConstants.Trip.TRIP_STATUS)
                .beanClass(TripStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.Trip.TRIP_STATUS))
                .andCondition(CriteriaAPI.getCondition("STATUS","status",status,StringOperators.IS));

        TripStatusContext tripStatus = builder.fetchFirst();
        return tripStatus;
    }
    public static void updateEstimatedCost(Map<Long,Double> appointmentCostMap, EventType eventType)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
       List<Long> appointmentIds= appointmentCostMap.keySet().stream().collect(Collectors.toList());
        FacilioContext serviceAppointmentContext =V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,appointmentIds);
        List<ModuleBaseWithCustomFields> oldServiceAppointmentList = Constants.getRecordList(serviceAppointmentContext);
        List<ServiceAppointmentContext> serviceAppointmentList = Constants.getRecordList(serviceAppointmentContext);
        List<ServiceAppointmentContext> updatedServiceAppointmentList = new ArrayList<>();
        for(ServiceAppointmentContext serviceAppointment : serviceAppointmentList){
            ServiceAppointmentContext updatedServiceAppointment =FieldUtil.cloneBean(serviceAppointment,ServiceAppointmentContext.class);
            Double estimatedCost = updatedServiceAppointment.getEstimatedCost()!=null ? updatedServiceAppointment.getEstimatedCost() : 0;
            Double newEstimatedCost = eventType == EventType.DELETE ? estimatedCost - appointmentCostMap.get(serviceAppointment.getId()) : estimatedCost + appointmentCostMap.get(serviceAppointment.getId()) ;
            updatedServiceAppointment.setEstimatedCost(newEstimatedCost);
            updatedServiceAppointmentList.add(updatedServiceAppointment);
        }
        V3Util.processAndUpdateBulkRecords(serviceAppointmentModule,oldServiceAppointmentList,FieldUtil.getAsMapList(updatedServiceAppointmentList,ServiceAppointmentContext.class),null, null, null, null, null, null, null, null, true,false,null);
    }
    public static void updateActualCost(Map<Long,Double> appointmentCostMap, EventType eventType)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<Long> appointmentIds= appointmentCostMap.keySet().stream().collect(Collectors.toList());
        FacilioContext serviceAppointmentContext =V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,appointmentIds);
        List<ModuleBaseWithCustomFields> oldServiceAppointmentList = Constants.getRecordList(serviceAppointmentContext);
        List<ServiceAppointmentContext> serviceAppointmentList = Constants.getRecordList(serviceAppointmentContext);
        List<ServiceAppointmentContext> updatedServiceAppointmentList = new ArrayList<>();
        for(ServiceAppointmentContext serviceAppointment : serviceAppointmentList){
            ServiceAppointmentContext updatedServiceAppointment =FieldUtil.cloneBean(serviceAppointment,ServiceAppointmentContext.class);
            Double actualCost = updatedServiceAppointment.getActualCost()!=null ? updatedServiceAppointment.getActualCost() : 0;
            Double newActualCost = eventType == EventType.DELETE ? actualCost - appointmentCostMap.get(serviceAppointment.getId()) : actualCost + appointmentCostMap.get(serviceAppointment.getId()) ;
            updatedServiceAppointment.setActualCost(newActualCost);
            updatedServiceAppointmentList.add(updatedServiceAppointment);
        }
        V3Util.processAndUpdateBulkRecords(serviceAppointmentModule,oldServiceAppointmentList,FieldUtil.getAsMapList(updatedServiceAppointmentList,ServiceAppointmentContext.class),null, null, null, null, null, null, null, null, true,false,null);
    }
    public static void updateEstimatedCostDuringUpdate( List<UpdateChangeSet> changes,ServiceAppointmentContext serviceAppointment,String moduleName)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes,"totalCost", moduleName)){
            FacilioField totalCost = modBean.getField("totalCost",moduleName);
            List<UpdateChangeSet> totalCostChange = changes.stream().filter(change-> change.getFieldId() == totalCost.getFieldId()).collect(Collectors.toList());
            Double oldTotalCost = totalCostChange.get(0).getOldValue()!=null ? (Double) totalCostChange.get(0).getOldValue() : 0 ;
            Double newTotalCost =  totalCostChange.get(0).getNewValue()!=null ? (Double) totalCostChange.get(0).getNewValue() : 0;
            Double estimatedCost = serviceAppointment.getEstimatedCost()!=null ? serviceAppointment.getEstimatedCost() : 0;
            Double newEstimatedCost = estimatedCost - oldTotalCost + newTotalCost;
            serviceAppointment.setEstimatedCost(newEstimatedCost);
            V3Util.processAndUpdateSingleRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,serviceAppointment.getId(), FieldUtil.getAsJSON(serviceAppointment),null, null, null, null, null, null, null, null,null);
        }
    }
    public static void updateActualCostDuringUpdate( List<UpdateChangeSet> changes,ServiceAppointmentContext serviceAppointment,String moduleName)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes,"totalCost", moduleName)){
            FacilioField totalCost = modBean.getField("totalCost",moduleName);
            List<UpdateChangeSet> totalCostChange = changes.stream().filter(change-> change.getFieldId() == totalCost.getFieldId()).collect(Collectors.toList());
            Double oldTotalCost =  totalCostChange.get(0).getOldValue()!=null ? (Double) totalCostChange.get(0).getOldValue() : 0;
            Double newTotalCost =  totalCostChange.get(0).getNewValue() !=null ? (Double) totalCostChange.get(0).getNewValue() : 0;
            Double actualCost = serviceAppointment.getActualCost()!=null ? serviceAppointment.getActualCost() : 0;
            Double newActualCost = actualCost - oldTotalCost + newTotalCost;
            serviceAppointment.setActualCost(newActualCost);
            V3Util.processAndUpdateSingleRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,serviceAppointment.getId(), FieldUtil.getAsJSON(serviceAppointment),null, null, null, null, null, null, null, null,null);
        }
    }
    public static Map<Long,Double> getAppointmentEstimatedCostMapForItems(List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems){
        Map<Long,Double> appointmentCostMap = new HashMap<>();
        for(ServiceOrderPlannedItemsContext serviceOrderPlannedItem : serviceOrderPlannedItems){
            if(serviceOrderPlannedItem.getServiceAppointment()!=null && serviceOrderPlannedItem.getTotalCost()!=null && serviceOrderPlannedItem.getTotalCost()>0){
                Long serviceAppointmentId = serviceOrderPlannedItem.getServiceAppointment().getId();
                if(appointmentCostMap.containsKey(serviceAppointmentId)){
                    Double totalCost = appointmentCostMap.get(serviceAppointmentId);
                    appointmentCostMap.put(serviceAppointmentId,totalCost+serviceOrderPlannedItem.getTotalCost());
                }else{
                    appointmentCostMap.put(serviceOrderPlannedItem.getServiceAppointment().getId(),serviceOrderPlannedItem.getTotalCost());
                }
            }
        }
        return appointmentCostMap;
    }
    public static Map<Long,Double> getAppointmentCostMapForItems(List<ServiceOrderItemsContext> serviceOrderItems){
        Map<Long,Double> appointmentCostMap = new HashMap<>();
        for(ServiceOrderItemsContext serviceOrderItem : serviceOrderItems){
            if(serviceOrderItem.getServiceAppointment()!=null && serviceOrderItem.getTotalCost()!=null && serviceOrderItem.getTotalCost()>0){
                Long serviceAppointmentId = serviceOrderItem.getServiceAppointment().getId();
                if(appointmentCostMap.containsKey(serviceAppointmentId)){
                    Double totalCost = appointmentCostMap.get(serviceAppointmentId);
                    appointmentCostMap.put(serviceAppointmentId,totalCost+serviceOrderItem.getTotalCost());
                }else{
                    appointmentCostMap.put(serviceOrderItem.getServiceAppointment().getId(),serviceOrderItem.getTotalCost());
                }
            }
        }
        return appointmentCostMap;
    }
    public static Map<Long,Double> getAppointmentEstimatedCostMapForTools(List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools){
        Map<Long,Double> appointmentCostMap = new HashMap<>();
        for(ServiceOrderPlannedToolsContext serviceOrderPlannedTool : serviceOrderPlannedTools){
            if(serviceOrderPlannedTool.getServiceAppointment()!=null && serviceOrderPlannedTool.getTotalCost()!=null && serviceOrderPlannedTool.getTotalCost()>0){
                Long serviceAppointmentId = serviceOrderPlannedTool.getServiceAppointment().getId();
                if(appointmentCostMap.containsKey(serviceAppointmentId)){
                    Double totalCost = appointmentCostMap.get(serviceAppointmentId);
                    appointmentCostMap.put(serviceAppointmentId,totalCost+serviceOrderPlannedTool.getTotalCost());
                }else{
                    appointmentCostMap.put(serviceOrderPlannedTool.getServiceAppointment().getId(),serviceOrderPlannedTool.getTotalCost());
                }
            }
        }
        return appointmentCostMap;
    }
    public static Map<Long,Double> getAppointmentCostMapForTools(List<ServiceOrderToolsContext> serviceOrderTools){
        Map<Long,Double> appointmentCostMap = new HashMap<>();
        for(ServiceOrderToolsContext serviceOrderTool : serviceOrderTools){
            if(serviceOrderTool.getServiceAppointment()!=null && serviceOrderTool.getTotalCost()!=null && serviceOrderTool.getTotalCost()>0){
                Long serviceAppointmentId = serviceOrderTool.getServiceAppointment().getId();
                if(appointmentCostMap.containsKey(serviceAppointmentId)){
                    Double totalCost = appointmentCostMap.get(serviceAppointmentId);
                    appointmentCostMap.put(serviceAppointmentId,totalCost+serviceOrderTool.getTotalCost());
                }else{
                    appointmentCostMap.put(serviceOrderTool.getServiceAppointment().getId(),serviceOrderTool.getTotalCost());
                }
            }
        }
        return appointmentCostMap;
    }
    public static Map<Long,Double> getAppointmentEstimatedCostMapForServices(List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices){
        Map<Long,Double> appointmentCostMap = new HashMap<>();
        for(ServiceOrderPlannedServicesContext serviceOrderPlannedService : serviceOrderPlannedServices){
            if(serviceOrderPlannedService.getServiceAppointment()!=null && serviceOrderPlannedService.getTotalCost()!=null && serviceOrderPlannedService.getTotalCost()>0){
                Long serviceAppointmentId = serviceOrderPlannedService.getServiceAppointment().getId();
                if(appointmentCostMap.containsKey(serviceAppointmentId)){
                    Double totalCost = appointmentCostMap.get(serviceAppointmentId);
                    appointmentCostMap.put(serviceAppointmentId,totalCost+serviceOrderPlannedService.getTotalCost());
                }else{
                    appointmentCostMap.put(serviceOrderPlannedService.getServiceAppointment().getId(),serviceOrderPlannedService.getTotalCost());
                }
            }
        }
        return appointmentCostMap;
    }
    public static List<TripContext> getAssociatedTrips( Long appointmentId) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule TripModule = moduleBean.getModule(FacilioConstants.Trip.TRIP);
        List<FacilioField> TripFields = moduleBean.getAllFields(FacilioConstants.Trip.TRIP);
        Map<String,FacilioField> TripFieldMap = FieldFactory.getAsMap(TripFields);
        List<LookupField> lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) TripFieldMap.get("status"));
        SelectRecordsBuilder<TripContext> tripBuilder = new SelectRecordsBuilder<TripContext>()
                .module(TripModule)
                .beanClass(TripContext.class)
                .select(TripFields)
                .fetchSupplements(lookUpfields)
//                .andCondition(CriteriaAPI.getCondition(TripFieldMap.get(FacilioConstants.ContextNames.PEOPLE),String.valueOf(peopleId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(TripFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),String.valueOf(appointmentId),NumberOperators.EQUALS));

        List<TripContext> allTrips = tripBuilder.get();
        if(CollectionUtils.isNotEmpty(allTrips)){
            return allTrips;
        }
        return null;
    }

    public static JSONObject fetchAllTripDetails(Long appointmentId)throws Exception {
        List<TripContext> allTrips = getAssociatedTrips(appointmentId);
        if (CollectionUtils.isNotEmpty(allTrips)) {
            long totalDuration = 0;
            Double totalDistance = 0.0;
            long count = 0;
            for (TripContext trip : allTrips) {
                count = count+1;
                if (trip.getStatus() != null && trip.getStatus().getStatus().equals(FacilioConstants.Trip.COMPLETED)) {
                    if(trip.getTripDuration() >0){
                        totalDuration += trip.getTripDuration();
                    }
                    if(trip.getTripDistance() != null) {
                        totalDistance += trip.getTripDistance();
                    }
                }

            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(FacilioConstants.ContextNames.COUNT, count);
            jsonObject.put(FacilioConstants.Trip.TOTAL_DURATION, totalDuration);
            jsonObject.put(FacilioConstants.Trip.TOTAL_DISTANCE, totalDistance);

          return jsonObject;
        }
        return null;
    }
    public static Map<Long,Double> getAppointmentCostMapForServices(List<ServiceOrderServiceContext> serviceOrderServices){
        Map<Long,Double> appointmentCostMap = new HashMap<>();
        for(ServiceOrderServiceContext serviceOrderService : serviceOrderServices){
            if(serviceOrderService.getServiceAppointment()!=null && serviceOrderService.getTotalCost()!=null && serviceOrderService.getTotalCost()>0){
                Long serviceAppointmentId = serviceOrderService.getServiceAppointment().getId();
                if(appointmentCostMap.containsKey(serviceAppointmentId)){
                    Double totalCost = appointmentCostMap.get(serviceAppointmentId);
                    appointmentCostMap.put(serviceAppointmentId,totalCost+serviceOrderService.getTotalCost());
                }else{
                    appointmentCostMap.put(serviceOrderService.getServiceAppointment().getId(),serviceOrderService.getTotalCost());
                }
            }
        }
        return appointmentCostMap;

    }
}
