package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.commands.workorder.V3ExecuteTaskFailureActionCommand;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderModuleSettingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3WorkOrderModuleSettingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * AutoResolveWorkOrderCommand checks for the following conditions and changes the state of WorkOrder to the configured
 * state in Auto Resolve Setting, by setting modifiedTime to maximum ModifiedTime of all Tasks.
 * - if Auto Resolve State is configured in settings [CHECK 1]
 * - if noOfTasks == noOfClosedTasks [CHECK 2]
 */
@Log4j
public class AutoResolveWorkOrderCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // CHECK 1
        FacilioStatus autoResolveState = getAutoResolveState();
        List<TaskContext> taskList = new ArrayList<>();

        if (autoResolveState == null) {
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

        TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK); // need ot check
        if(task == null){
            LOGGER.info("task object is null");
            return false;
        }
        V3WorkOrderContext workOrderContext = (V3WorkOrderContext) V3Util.getRecord(workOrderModule.getName(), task.getParentTicketId(), null);
        if (workOrderContext == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "No such WorkOrder available with ID " + task.getParentTicketId());
        }

        // CHECK 2
        if (!Objects.equals(workOrderContext.getNoOfTasks(), workOrderContext.getNoOfClosedTasks())) {
            LOGGER.info("noOfTasks != noOfClosedTask");
            return false;
        }

        // Fetch All Tasks of the WorkOrder
        taskList = fetchAllTasks(task.getParentTicketId());
        if (CollectionUtils.isEmpty(taskList)) {
            return false;
        }

        long modifiedTimeOfWorkOrder;
        LOGGER.info("task offline modified time: " + task.getOfflineModifiedTime());
        //V3WorkOrderContext workOrderContextToBeUpdated = workOrderContext;
        //workOrderContextToBeUpdated.setId(workOrderContext.getId());
        if(task.getOfflineModifiedTime() != -1) {
            // Get the maximum Modified Time of all tasks
            Long maxModifiedTimeOfTask = getMaximumModifiedTimeOfAllTasks(taskList);
            workOrderContext.setOfflineModifiedTime(maxModifiedTimeOfTask);
            LOGGER.info("Setting maxModifiedTimeOfTask: " + maxModifiedTimeOfTask);
            modifiedTimeOfWorkOrder = maxModifiedTimeOfTask;
        }else {
            modifiedTimeOfWorkOrder = System.currentTimeMillis();
        }
        //workOrderContextToBeUpdated.setModuleState(workOrderContext.getModuleState());
        // Move to the state in Auto Resolve Setting
        StateFlowRulesAPI.updateState(workOrderContext, workOrderModule, autoResolveState, false, context);

        /**
         * Supported Task Deviation here
         */
        if(autoResolveState.getType() == FacilioStatus.StatusType.CLOSED){
            invokeTaskDeviationAction(workOrderContext);
        }

        // Add workorder object into recordMap as its required to execute the workflows and send notification
        Map<String, List<ModuleBaseWithCustomFields>> contextRecordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(contextRecordMap == null || contextRecordMap.isEmpty()) {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(workOrderContext));
            Constants.setRecordMap(context, recordMap);
        }else {
            contextRecordMap.put(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(workOrderContext));
            context.put(FacilioConstants.ContextNames.RECORD_MAP, contextRecordMap);
        }

        // Update WorkOrder modifiedTime here
        updateWorkOrderModifiedTime(modBean, workOrderModule, workOrderContext, modifiedTimeOfWorkOrder);

        context.put(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, true);
        return false;
    }

    private void invokeTaskDeviationAction(V3WorkOrderContext workOrderContext) {
        LOGGER.info("invokeTaskDeviationAction for WorkOrder ID - " + workOrderContext.getId());
        V3ExecuteTaskFailureActionCommand.publishTaskDeviationHandler(workOrderContext);
    }

    /**
     * Helper function to update the workorder modifiedTime. This is done manually here, as we do state update manuaully,
     * where update of record's modifiedTime won't happen.
     * @param modBean
     * @param workOrderModule
     * @param workOrderContext
     * @param lastModifiedTimeOfWorkOrder
     * @throws Exception
     */
    private void updateWorkOrderModifiedTime(ModuleBean modBean, FacilioModule workOrderModule, V3WorkOrderContext workOrderContext, long lastModifiedTimeOfWorkOrder) throws Exception {
        workOrderContext.setModifiedTime(lastModifiedTimeOfWorkOrder);
        List<FacilioField> workOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> workOrderFieldsMap = FieldFactory.getAsMap(workOrderFields);
        V3RecordAPI.updateRecord(workOrderContext, workOrderModule, Collections.singletonList(workOrderFieldsMap.get("modifiedTime")));
    }

    /**
     * Helper function to check if Auto Resolve Setting has any state configured and return the configured State
     *
     * @return FacilioStatus
     */
    private FacilioStatus getAutoResolveState() {
        try {
            V3WorkOrderModuleSettingContext workOrderModuleSettingContext = V3WorkOrderModuleSettingAPI.fetchWorkOrderModuleSettingsAsObject();
            if (workOrderModuleSettingContext != null && workOrderModuleSettingContext.getAutoResolveStateId() != null && workOrderModuleSettingContext.getAutoResolveStateId() > 0) {
                Long autoResolveStateId = workOrderModuleSettingContext.getAutoResolveStateId();
                return TicketAPI.getStatus(autoResolveStateId);
            }
        } catch (Exception e) {
            LOGGER.warn("Exception in fetching the auto resolve state.", e);
        }
        return null;
    }

    /**
     * Helper function to get the Maximum ModifiedTime of all tasks `taskList`
     *
     * @return
     */
    private Long getMaximumModifiedTimeOfAllTasks(List<TaskContext> taskList) {
        Long maxSysModifiedTime = null;
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (TaskContext task : taskList) {
                if (maxSysModifiedTime == null || task.getModifiedTime() > maxSysModifiedTime) {
                    maxSysModifiedTime = task.getModifiedTime();
                }
            }
        }
        return maxSysModifiedTime;
    }

    /**
     * Helper function to fetch all tasks from WorkOrder with ID @param ticketId
     *
     * @param ticketId
     * @throws Exception
     */
    private List<TaskContext> fetchAllTasks(Long ticketId) throws Exception {
        List<TaskContext> taskList = TicketAPI.getRelatedTasks(ticketId);
        if (CollectionUtils.isNotEmpty(taskList)) {
            return taskList;
        }
        return null;
    }
}
