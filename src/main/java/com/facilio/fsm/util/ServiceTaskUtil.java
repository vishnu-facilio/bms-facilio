package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
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
            List<Map<String, Object>> existingTimeSheets = getExistingTimeSheet(serviceAppointmentContext.getId());
            if (CollectionUtils.isEmpty(existingTimeSheets)) {
                TimeSheetTaskContext timeSheetTask = new TimeSheetTaskContext();
                timeSheetTask.setId(taskId);

                TimeSheetContext timeSheetContext = new TimeSheetContext();
                timeSheetContext.setStartTime(currentTime);
                timeSheetContext.setServiceAppointment(serviceAppointmentContext);
                timeSheetContext.setServiceTasks(Collections.singletonList(timeSheetTask));
                timeSheetContext.setServiceOrder(serviceAppointmentContext.getServiceOrder());
                timeSheetContext.setFieldAgent(serviceAppointmentContext.getFieldAgent());

                FacilioModule timeSheet = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);

                V3Util.createRecord(timeSheet,FieldUtil.getAsProperties(timeSheetContext));
                updateTaskAndSA(taskId,currentTime,serviceAppointmentContext);

            } else {
                if (!isTimeSheetExistsForTask(existingTimeSheets,taskId)) {
                    throw new FSMException(FSMErrorCode.TIMESHEET_ALREADY_RUNNING);
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
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null);

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

            ServiceAppointmentContext appointment  = V3RecordAPI.getRecord(serviceAppointmentModuleName,serviceAppointmentContext.getId());

            appointment.setActualStartTime(currentTime);
            ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("inProgress");
            appointment.setStatus(appointmentStatus);
            List<Long> appointmentIds = new ArrayList<>();
            appointmentIds.add(appointment.getId());
            V3RecordAPI.updateRecord(appointment,serviceAppointment,updateSAFields);

        }

    }

    public static List<Map<String, Object>> getExistingTimeSheet(long appointmentId) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        FacilioField taskField = Constants.getModBean().getField("right",FacilioConstants.TimeSheet.TIME_SHEET_TASK);
        Criteria timeSheetCriteria = new Criteria();
        timeSheetCriteria.addAndCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "serviceAppointment", String.valueOf(appointmentId), NumberOperators.EQUALS));
        timeSheetCriteria.addAndCondition(CriteriaAPI.getCondition( Constants.getModBean().getField("endTime",FacilioConstants.TimeSheet.TIME_SHEET),  CommonOperators.IS_EMPTY));


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(taskField))
                .table("TIME_SHEET")
                .innerJoin("TIME_SHEET_TASK_REL")
                .on("TIME_SHEET.ID =TIME_SHEET_TASK_REL.TIME_SHEET_ID")
                .andCriteria(timeSheetCriteria);
        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            return maps;
        }

        return null;

    }

    public static boolean isTimeSheetExistsForTask(List<Map<String,Object>> timeSheets, Long taskId)throws Exception{

        List<Long>taskIds = new ArrayList<>();
        taskIds = timeSheets.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
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
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null);

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
//               if((serviceTask.getStatus()!= ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex()) || (serviceTask.getStatus()!=ServiceTaskContext.ServiceTaskStatus.CANCELLED.getIndex()) || (serviceTask.getStatus()!=ServiceTaskContext.ServiceTaskStatus.ON_HOLD.getIndex())){
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
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null);

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

    public static void stopTimeSheet(Long timeSheetId) throws Exception {


        Long currentTime = DateTimeUtil.getCurrenTime();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord> multiLookUpFields = new ArrayList<>();
        multiLookUpFields.add((MultiLookupField) fieldMap.get("serviceTasks"));

        List<TimeSheetContext> timeSheets = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.TimeSheet.TIME_SHEET,Collections.singletonList(timeSheetId), TimeSheetContext.class,multiLookUpFields);
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
            Long saId = timeSheet.getServiceAppointment().getId();
            updateServiceAppointments(Collections.singletonList(saId),currentTime,FacilioConstants.ServiceAppointment.COMPLETED);


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

        if(completedStatus!=null){
            endStatus.add(completedStatus.getId());
        }
        if(cancelledStatus!=null){
            endStatus.add(cancelledStatus.getId());
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
    public static void updateServiceAppointments(List<Long> appointmentIds, Long currentTime, String status) throws Exception {


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

    }

}
