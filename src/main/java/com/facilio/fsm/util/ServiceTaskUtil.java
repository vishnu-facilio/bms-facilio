package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
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
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceTaskUtil {
    public static void moveToInProgress(Long taskId) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        Long currentTime = DateTimeUtil.getCurrenTime();

        //fetching service appointment from service task

        ServiceAppointmentContext serviceAppointmentContext = getServiceAppointmentFromTask(taskId);
        if (serviceAppointmentContext != null) {

            //create Timesheet entry
            List<TimeSheetContext> existingTimeSheets = getExistingTimeSheet(serviceAppointmentContext.getFieldAgent().getId());
            if (CollectionUtils.isEmpty(existingTimeSheets)) {
                TimeSheetTaskContext timeSheetTask = new TimeSheetTaskContext();
                timeSheetTask.setId(taskId);

                TimeSheetContext timeSheetContext = new TimeSheetContext();
                timeSheetContext.setStartTime(currentTime);
                timeSheetContext.setServiceAppointment(serviceAppointmentContext);
                timeSheetContext.setServiceTasks(Collections.singletonList(timeSheetTask));
                timeSheetContext.setServiceOrder(serviceAppointmentContext.getServiceOrder());
                timeSheetContext.setFieldAgent(serviceAppointmentContext.getFieldAgent());
                timeSheetContext.setStatus(ServiceAppointmentUtil.getTimeSheetStatus(FacilioConstants.TimeSheet.IN_PROGRESS));

                FacilioModule timeSheet = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);

                V3Util.createRecord(timeSheet,FieldUtil.getAsProperties(timeSheetContext));
                updateTaskAndSA(taskId,currentTime,serviceAppointmentContext);

            } else {
                if (!isTimeSheetExistsForTask(existingTimeSheets,taskId)) {
                    JSONObject errorData = new JSONObject();
                    errorData.put(FacilioConstants.TimeSheet.TIME_SHEET,existingTimeSheets);
                    throw new FSMException(FSMErrorCode.TIMESHEET_ALREADY_RUNNING).setRelatedData(errorData);
                }
                else {
                    //updating service task status
                    updateTaskAndSA(taskId,currentTime,serviceAppointmentContext);


                }
            }
        }

    }
    public static void updateTaskAndSA(Long taskId,Long currentTime,ServiceAppointmentContext serviceAppointmentContext)throws Exception{
        ModuleBean moduleBean = Constants.getModBean();

        //updating task
        String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get("status"));
        updateFields.add(fieldMap.get("actualStartTime"));

        FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,Collections.singletonList(taskId));
        ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);

        existingTask.setActualStartTime(currentTime);
        ServiceTaskStatusContext inProgress = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        existingTask.setStatus(inProgress);
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null,null);

        //updating sa
        ServiceAppointmentTicketStatusContext status = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS, serviceAppointmentContext.getStatus().getId());
        if (status.getTypeCode() == 1) {

            //updating service appointment status
            String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
            FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
            List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
            List<FacilioField> updateSAFields = new ArrayList<>();
            updateSAFields.add(saFieldMap.get("status"));
            updateSAFields.add(saFieldMap.get("actualStartTime"));

            FacilioContext appointmentContext = V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, Collections.singletonList(serviceAppointmentContext.getId()));

            ServiceAppointmentContext appointment = (ServiceAppointmentContext) Constants.getRecordList(appointmentContext).get(0);
            if (appointment != null) {
                appointment.setActualStartTime(currentTime);
                ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("inProgress");
                appointment.setStatus(appointmentStatus);
                V3Util.processAndUpdateSingleRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, appointment.getId(), FieldUtil.getAsJSON(appointment), null, null, null, null, null, null, null, null, null);

                //updating field agent status to on-site

                if (appointment.getFieldAgent() != null) {
                    Long fieldAgentId = appointment.getFieldAgent().getId();
                    V3PeopleContext people = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, fieldAgentId,V3PeopleContext.class);
                    if (people != null) {
                        if (people.getStatus() != null){
                            if (people.getStatus() != V3PeopleContext.Status.ON_SITE.getIndex() && people.getStatus() != V3PeopleContext.Status.EN_ROUTE.getIndex()) {
                                V3PeopleAPI.updatePeopleStatus(fieldAgentId, V3PeopleContext.Status.ON_SITE.getIndex());
                            }
                        } else {
                            V3PeopleAPI.updatePeopleStatus(fieldAgentId, V3PeopleContext.Status.ON_SITE.getIndex());
                        }
                    }
                }
            }
        }

    }

    public static List<TimeSheetContext> getExistingTimeSheet(long fieldAgentId) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<FacilioField> selectFields = Constants.getModBean().getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);

        Criteria timeSheetCriteria = new Criteria();
        timeSheetCriteria.addAndCondition(CriteriaAPI.getCondition("PEOPLE_ID", "fieldAgent", String.valueOf(fieldAgentId), NumberOperators.EQUALS));
        timeSheetCriteria.addAndCondition(CriteriaAPI.getCondition( Constants.getModBean().getField("endTime",FacilioConstants.TimeSheet.TIME_SHEET),  CommonOperators.IS_EMPTY));

        List<SupplementRecord> lookUpfields = new ArrayList<>();
        lookUpfields.add((MultiLookupField) fieldMap.get("serviceTasks"));
        lookUpfields.add((SupplementRecord) fieldMap.get("serviceAppointment"));

        SelectRecordsBuilder<TimeSheetContext> selectRecordsBuilder = new SelectRecordsBuilder<TimeSheetContext>()
                .module(module)
                .select(selectFields)
                .beanClass(TimeSheetContext.class)
                .fetchSupplements(lookUpfields)
                .andCriteria(timeSheetCriteria);
        List<TimeSheetContext> props = selectRecordsBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return props;

        }
        return null;

    }

    public static boolean isTimeSheetExistsForTask(List<TimeSheetContext> timeSheets, Long taskId)throws Exception{


        List<Long> taskIds = timeSheets.stream().map(TimeSheetContext::getId).collect(Collectors.toList());
        if(taskIds.contains(taskId)){
            return true;
        }

        return false;
    }

    public static void moveToCloseState(Long taskId,String status) throws Exception {

        String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get("status"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        //updating service task status
        FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,Collections.singletonList(taskId));
        ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);
        ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(status);
        existingTask.setStatus(taskStatus);
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null,null);

        //fetch all tasks mapped with ServiceAppointment
        ServiceAppointmentContext serviceAppointmentContext = getServiceAppointmentFromTask(taskId);
        if(serviceAppointmentContext != null) {
            FacilioContext appointmentContext = V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,Collections.singletonList(serviceAppointmentContext.getId()));
            ServiceAppointmentContext appointment  = (ServiceAppointmentContext) Constants.getRecordList(appointmentContext).get(0);

            long agentId = appointment.getFieldAgent().getId();
            long appointmentId = appointment.getId();

            boolean isComplete = checkIfAllTasksclosed(getAllTasksFromServiceAppointment(appointmentId));
            if(isComplete){
                List<TimeSheetContext> ongoingTimeSheets = ServiceAppointmentUtil.getOngoingTimeSheets(agentId,appointmentId);
                if(CollectionUtils.isNotEmpty(ongoingTimeSheets)){
                    for(TimeSheetContext ts : ongoingTimeSheets){
                        ServiceAppointmentUtil.closeOngoingTimeSheets(ts,currentTime);
                    }
                }
            }
            long workDuration = ServiceAppointmentUtil.getAppointmentDuration(agentId, appointmentId);
            appointment.setActualDuration(workDuration);
            V3Util.processAndUpdateSingleRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,appointmentId, FieldUtil.getAsJSON(appointment), null, null, null, null, null, null, null, null,null);
        }
    }

    public static ServiceAppointmentContext getServiceAppointmentFromTask(Long taskId) throws Exception {
        String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioField appointmentField = Constants.getModBean().getField("serviceAppointment", serviceTaskModuleName);
        FacilioModule serviceTask = moduleBean.getModule(serviceTaskModuleName);
        List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(taskId, serviceTask));

        List<LookupField> lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) fieldMap.get("serviceAppointment"));

        SelectRecordsBuilder<ServiceTaskContext> selectRecordsBuilder = new SelectRecordsBuilder<ServiceTaskContext>()
                .module(serviceTask)
                .select(Collections.singleton(appointmentField))
                .beanClass(ServiceTaskContext.class)
                .fetchSupplements(lookUpfields)
                .andCriteria(criteria);
        List<ServiceTaskContext> props = selectRecordsBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            ServiceAppointmentContext serviceAppointmentContext = props.get(0).getServiceAppointment();
            return serviceAppointmentContext;

        }
        return null;
    }
    public static List<ServiceTaskContext> getAllTasksFromServiceAppointment(Long appointmentId)throws Exception {


        List<FacilioField> taskFields =
                Constants.getModBean().getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(taskFields)
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .innerJoin("Service_Task")
                .on("SERVICE_APPOINTMENT_TASK_REL.SERVICE_TASK_ID=Service_Task.ID")
                .andCondition((CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS)));
        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            List<Long> tasks = new ArrayList<>();
            for(Map<String,Object> map:maps){
                tasks.add((Long) map.get("right"));
            }
            List<ServiceTaskContext> serviceTasks = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,tasks);
            return serviceTasks;


        }
        return null;
    }
    public static boolean checkIfAllTasksclosed(List<ServiceTaskContext> serviceTasks)throws Exception{
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask:serviceTasks){
                if(serviceTask.getStatus()!=null){
                  ServiceTaskStatusContext taskStatus =  V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS,serviceTask.getStatus().getId(),ServiceTaskStatusContext.class);
                  if(StringUtils.equals(taskStatus.getName(),FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS)){
                      return false;
                  }
                }
            }
        }
    return true;
    }

    public static void completeServiceTask(Long taskId) throws Exception {

        String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get("status"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        //updating service task status

        FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,Collections.singletonList(taskId));
        ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);
        ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED);
        existingTask.setStatus(taskStatus);
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null,null);

        //fetch all tasks mapped with ServiceAppointment
        ServiceAppointmentContext serviceAppointmentContext = getServiceAppointmentFromTask(taskId);
        if(serviceAppointmentContext != null) {
            long agentId = serviceAppointmentContext.getFieldAgent().getId();
            long appointmentId = serviceAppointmentContext.getId();

            boolean isComplete = checkIfAllTasksclosed(getAllTasksFromServiceAppointment(serviceAppointmentContext.getId()));
            if(isComplete){
                List<TimeSheetContext> ongoingTimeSheets = ServiceAppointmentUtil.getOngoingTimeSheets(agentId,appointmentId);
                if(CollectionUtils.isNotEmpty(ongoingTimeSheets)){
                    for(TimeSheetContext ts : ongoingTimeSheets){
                        ServiceAppointmentUtil.closeOngoingTimeSheets(ts,currentTime);
                    }
                }
            }
        }
    }

    public static void stopTimeSheet(List<Long> timeSheetIds) throws Exception {


        Long currentTime = DateTimeUtil.getCurrenTime();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord> multiLookUpFields = new ArrayList<>();
        multiLookUpFields.add((MultiLookupField) fieldMap.get("serviceTasks"));

        List<TimeSheetContext> timeSheets = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.TimeSheet.TIME_SHEET,timeSheetIds, TimeSheetContext.class,multiLookUpFields);
        if(CollectionUtils.isNotEmpty(timeSheets)){
            //closing Time Sheet

            TimeSheetContext timeSheet= timeSheets.get(0);
            ServiceAppointmentUtil.closeOngoingTimeSheets(timeSheet,currentTime);
            //complete associated tasks
            List<TimeSheetTaskContext> tasks = timeSheet.getServiceTasks();
            if(CollectionUtils.isNotEmpty(tasks)){
                List<Long> taskIds = new ArrayList<>();
               for(TimeSheetTaskContext task :tasks){
                   taskIds.add(task.getId());
               }
                updateServiceTasks(taskIds,FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED,currentTime);
            }
            //complete associated service appointment
            if(timeSheet.getServiceAppointment() != null){
                Long appointmentId = timeSheet.getServiceAppointment().getId();
                if (getAppointmentStatus(appointmentId)) {
                    updateServiceAppointments(appointmentId, currentTime, FacilioConstants.ServiceAppointment.COMPLETED);
                    //updating field agent status
                    if (timeSheet != null && timeSheet.getFieldAgent() != null) {

                        Long fieldAgentId = timeSheet.getFieldAgent().getId();
                        V3PeopleContext people = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, fieldAgentId,V3PeopleContext.class);
                        if (people != null) {
                            if (people.getStatus() != null) {
                                if (people.getStatus() != V3PeopleContext.Status.EN_ROUTE.getIndex()) {
                                    V3PeopleAPI.updatePeopleAvailability(people,currentTime);
                                }
                            }else{
                                V3PeopleAPI.updatePeopleAvailability(people,currentTime);
                            }
                        }
                    }

                }
            }
        }
    }
    public static void updateServiceTasks(List<Long> taskIds, String status, Long currentTime) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> taskFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);

        Map<String, FacilioField> taskFieldMap = FieldFactory.getAsMap(taskFields);
        List<FacilioField> updateTaskFields = new ArrayList<>();
        updateTaskFields.add(taskFieldMap.get("status"));
        updateTaskFields.add(taskFieldMap.get("actualEndTime"));
        ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(status);
        List<Long> endStatus = new ArrayList<>();
        ServiceTaskStatusContext completedStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED);
        ServiceTaskStatusContext cancelledStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED);
        ServiceTaskStatusContext onHoldStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD);

        if(completedStatus!=null){
            endStatus.add(completedStatus.getId());
        }
        if(cancelledStatus!=null){
            endStatus.add(cancelledStatus.getId());
        }
        if(onHoldStatus != null){
            endStatus.add(onHoldStatus.getId());
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(taskIds, taskModule));
        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status",StringUtils.join(endStatus,","),NumberOperators.NOT_EQUALS));


        UpdateRecordBuilder<ServiceTaskContext> updateBuilder = new UpdateRecordBuilder<ServiceTaskContext>()
                .module(taskModule)
                .fields(updateTaskFields)
                .andCriteria(criteria);

        Map<String, Object> updateProps = new HashMap<>();
        updateProps.put("status", FieldUtil.getAsProperties(taskStatus));
        updateProps.put("actualEndTime", currentTime);

        updateBuilder.updateViaMap(updateProps);

    }
    public static void updateServiceAppointments(Long appointmentId, Long currentTime, String status) throws Exception {


        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);

        ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus(status);
        ServiceAppointmentContext appointment = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,appointmentId);
        if(appointment != null) {

            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

            List<FacilioField> updateFields = new ArrayList<>();
            updateFields.add(saFieldMap.get("status"));
            updateFields.add(saFieldMap.get("actualEndTime"));
            if(appointment.getFieldAgent() != null) {
                updateFields.add(saFieldMap.get("actualDuration"));

            }


            List<String> saStatus = new ArrayList<>();
            saStatus.add(FacilioConstants.ServiceAppointment.COMPLETED);
            saStatus.add(FacilioConstants.ServiceAppointment.CANCELLED);

            Map<String, ServiceAppointmentTicketStatusContext> statusMap = ServiceAppointmentUtil.getStatusMap(saStatus);

            List<Long> endStatus = new ArrayList<>();
            endStatus.add(statusMap.get(FacilioConstants.ServiceAppointment.CANCELLED).getId());
            endStatus.add(statusMap.get(FacilioConstants.ServiceAppointment.COMPLETED).getId());

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getIdCondition(appointmentId, serviceAppointment));
            criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(endStatus, ","), NumberOperators.NOT_EQUALS));


            UpdateRecordBuilder<ServiceAppointmentContext> updateBuilder = new UpdateRecordBuilder<ServiceAppointmentContext>()
                    .module(serviceAppointment)
                    .fields(updateFields)
                    .andCriteria(criteria);

            Map<String, Object> updateProps = new HashMap<>();
            updateProps.put("status", FieldUtil.getAsProperties(appointmentStatus));
            updateProps.put("actualEndTime", currentTime);

            if(appointment.getFieldAgent() != null) {
                long workDuration = ServiceAppointmentUtil.getAppointmentDuration(appointment.getFieldAgent().getId(), appointmentId);
                updateProps.put("actualDuration", workDuration);
            }
            updateBuilder.updateViaMap(updateProps);
        }
    }



    public static boolean getAppointmentStatus(Long appointmentId)throws Exception {

        FacilioField status = Constants.getModBean().getField("name",FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS);

        List<String>taskStatus = new ArrayList<>();
        taskStatus.add(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED);
        taskStatus.add(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(status))
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .innerJoin("Service_Task")
                .on("SERVICE_APPOINTMENT_TASK_REL.SERVICE_TASK_ID=Service_Task.ID")
                .innerJoin("Service_Task_Status")
                .on("Service_Task.STATUS=Service_Task_Status.ID")
                .andCondition((CriteriaAPI.getCondition("SERVICE_APPOINTMENT_TASK_REL.SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS)))
                .andCondition((CriteriaAPI.getCondition("Service_Task_Status.NAME", "name", StringUtils.join(taskStatus,","), StringOperators.ISN_T)));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            return false;
        }
        return true;
    }


    public static FacilioContext getTaskList(Long timeSheetId,int page, int perPage)throws Exception{
        List<Long> taskIds = getTaskListFromTimeSheet(timeSheetId);
        if(CollectionUtils.isNotEmpty(taskIds)) {
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule serviceTask = moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getIdCondition(taskIds, serviceTask));
            FacilioContext taskList = V3Util.fetchList(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, true, "hidden-all", null,
                    true, null, null, null,
                    null, page, perPage, false, null, criteria,null);
            return taskList;
        }
        return null;
    }
    public static List<Long> getTaskListFromTimeSheet(Long timeSheetId)throws Exception {

        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.TimeSheet.TIME_SHEET_TASK);


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(taskField))
                .table("TIME_SHEET_TASK_REL")
                .andCondition((CriteriaAPI.getCondition("TIME_SHEET_ID", "left", String.valueOf(timeSheetId), NumberOperators.EQUALS)));
        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
        }
        return taskIds;
    }
    public static List<Long> getTasksFromServiceAppointment(Long appointmentId)throws Exception {
        List<FacilioField> taskFields =
                Constants.getModBean().getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(taskFields)
                .table("SERVICE_APPOINTMENT_TASK_REL")
                .andCondition((CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(appointmentId), NumberOperators.EQUALS)));
        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());

        }
        return taskIds;
    }
}
