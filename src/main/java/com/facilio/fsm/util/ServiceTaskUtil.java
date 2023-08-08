package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.*;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;

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

//
//                InsertRecordBuilder<TimeSheetContext> insertRecordBuilder = new InsertRecordBuilder<TimeSheetContext>()
//                        .module(timeSheet)
//                        .table(timeSheet.getTableName())
//                        .fields(moduleBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET));
//                insertRecordBuilder.insert(timeSheetContext);


            } else {
                if (!isTimeSheetExistsForTask(existingTimeSheets,taskId)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot start or resume another work when timesheet is running");
                }
                else {
                    //updating service task status
                    updateTaskAndSA(taskId,currentTime,serviceAppointmentContext);

//
//                    String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
//                    FacilioModule serviceTask = moduleBean.getModule(serviceTaskModuleName);
//                    List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);
//
//                    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
//                    List<FacilioField> updateFields = new ArrayList<>();
//                    updateFields.add(fieldMap.get("status"));
//                    updateFields.add(fieldMap.get("actualStartTime"));
//
//                    ServiceTaskContext task = new ServiceTaskContext();
//                    task.setId(taskId);
//                    task.setActualStartTime(currentTime);
//                    task.setStatus(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex());
//
//                    List<Long> taskIds = new ArrayList<>();
//                    taskIds.add(taskId);
//                    V3Util.updateBulkRecords(serviceTaskModuleName, FieldUtil.getAsProperties(task), taskIds,true,false);
//                    V3RecordAPI.updateRecord(task, serviceTask, updateFields);

//                    ServiceAppointmentTicketStatusContext status = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS, serviceAppointmentContext.getStatus().getId());
//                    if (status.getTypeCode() == 1) {
//
//                            //updating service appointment status
//                            String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
//                            FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
//                            List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
//
//                            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
//                            List<FacilioField> updateSAFields = new ArrayList<>();
//                            updateSAFields.add(saFieldMap.get("status"));
//                            updateSAFields.add(saFieldMap.get("actualStartTime"));
//
//
//                            ServiceAppointmentContext appointment = new ServiceAppointmentContext();
//                            appointment.setId(serviceAppointmentContext.getId());
//                            appointment.setActualStartTime(currentTime);
//                            ServiceAppointmentTicketStatusContext appointmentStatus = ServiceAppointmentUtil.getStatus("inProgress");
//                            appointment.setStatus(appointmentStatus);
//                            List<Long> appointmentIds = new ArrayList<>();
//                            appointmentIds.add(appointment.getId());
//                        V3Util.updateBulkRecords(serviceAppointmentModuleName, FieldUtil.getAsProperties(appointment), appointmentIds,true,false);
//
////                        V3RecordAPI.updateRecord(appointment, serviceAppointment, updateSAFields);
//                        }

                }
            }
        }

    }
    public static void updateTaskAndSA(Long taskId,Long currentTime,ServiceAppointmentContext serviceAppointmentContext)throws Exception{
        ModuleBean moduleBean = Constants.getModBean();

        //updating task
        String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        FacilioModule serviceTask = moduleBean.getModule(serviceTaskModuleName);
        List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get("status"));
        updateFields.add(fieldMap.get("actualStartTime"));

        FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,Collections.singletonList(taskId));
        ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);

        existingTask.setActualStartTime(currentTime);
        existingTask.setStatus(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex());
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null);



//        ServiceTaskContext task = new ServiceTaskContext();
//        task.setId(taskId);

//        List<Long> taskIds = new ArrayList<>();
//        taskIds.add(taskId);
//        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(task), null, null, null, null, null,null, null, null);

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
//            V3Util.processAndUpdateSingleRecord(serviceAppointmentModuleName,appointment.getId(), FieldUtil.getAsJSON(appointment), null, null, null, null, null,null, null, null);


//                        V3RecordAPI.updateRecord(appointment, serviceAppointment, updateSAFields);
        }

    }

    public static List<Map<String, Object>> getExistingTimeSheet(long appointmentId) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule timeSheet = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        FacilioField appointmentField = Constants.getModBean().getField("serviceAppointment", FacilioConstants.TimeSheet.TIME_SHEET);
        FacilioField actualEndTime = Constants.getModBean().getField("endTime", FacilioConstants.TimeSheet.TIME_SHEET);
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

    public static void moveToCloseState(Long taskId,int status) throws Exception {

        String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceTask = moduleBean.getModule(serviceTaskModuleName);
        List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get("status"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        //updating service task status
        FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,Collections.singletonList(taskId));
        ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);

        existingTask.setStatus(status);
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null);


//        V3Util.updateBulkRecords(serviceTaskModuleName, FieldUtil.getAsProperties(task), taskIds,true,false);
//        V3RecordAPI.updateRecord(task, serviceTask, updateFields);

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

//        FacilioField taskField = Constants.getModBean().getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);

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
            List<ServiceTaskContext> tasks = new ArrayList<>();
            for(Map<String,Object> map:maps){
                tasks.add((ServiceTaskContext) map.get("right"));
            }
            return tasks;


        }
        return null;
    }
    public static boolean checkIfAllTasksclosed(List<ServiceTaskContext> serviceTasks)throws Exception{
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask:serviceTasks){
//               if((serviceTask.getStatus()!= ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex()) || (serviceTask.getStatus()!=ServiceTaskContext.ServiceTaskStatus.CANCELLED.getIndex()) || (serviceTask.getStatus()!=ServiceTaskContext.ServiceTaskStatus.ON_HOLD.getIndex())){
                if((serviceTask.getStatus()== ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex()) ){

                    return false;
               }
            }
        }
    return true;
    }

    public static void completeServiceTask(Long taskId) throws Exception {

        String serviceTaskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule serviceTask = moduleBean.getModule(serviceTaskModuleName);
        List<FacilioField> fields = moduleBean.getAllFields(serviceTaskModuleName);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get("status"));

        Long currentTime = DateTimeUtil.getCurrenTime();

        //updating service task status

        FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,Collections.singletonList(taskId));
        ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);

        existingTask.setStatus(ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex());
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null,null, null, null);


//        V3Util.updateBulkRecords(serviceTaskModuleName, FieldUtil.getAsProperties(task), taskIds,true,false);


//        V3RecordAPI.
//        cord(task, serviceTask, updateFields);

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

}
