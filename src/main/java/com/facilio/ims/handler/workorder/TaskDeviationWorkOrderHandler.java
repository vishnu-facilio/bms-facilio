package com.facilio.ims.handler.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.ImsHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

@Log4j
public class TaskDeviationWorkOrderHandler extends ImsHandler {

    @Override
    public void processMessage(Message message) {
        try {
            long startTime = System.currentTimeMillis();
            LOGGER.info("TaskDeviationWorkOrderHandler:");
            long workOrderId = getWorkOrderId(message);
            if(workOrderId < 0L){
                LOGGER.error("Invalid WorkOrder ID " + workOrderId);
                return;
            }

            V3WorkOrderContext workOrderContext = (V3WorkOrderContext) V3Util.getRecord(FacilioConstants.ContextNames.WORK_ORDER, workOrderId, null);

            if(workOrderContext == null){
                LOGGER.error("Invalid WorkOrder ");
                return;
            }

            List<V3TaskContext> tasks = TicketAPI.getRelatedTasksV3(Collections.singletonList(workOrderId), false, true);
            if (tasks != null && !tasks.isEmpty()) {
                for (V3TaskContext task : tasks) {
                    if ((task.isFailed() != null && task.isFailed()) && (task.getCreateWoOnFailure() != null && task.getCreateWoOnFailure())) {
                        long startTimeBeforeWoCreation = System.currentTimeMillis();
                        createdTaskDeviationWorkOrder(workOrderContext, task);
                        LOGGER.info("Time take to create wo for task #"+ task +", startTimeBeforeWoCreation = " + (System.currentTimeMillis() - startTimeBeforeWoCreation));
                    }
                }
            }
            LOGGER.info("Time take in TaskDeviationWorkOrderHandler = " + (System.currentTimeMillis() - startTime));
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Helper method to fetch the WorkOrder ID from the message.
     * @param message
     * @return
     */
    private long getWorkOrderId(Message message) {
        String key = message.getKey();
        String[] split = StringUtils.split(key, "/");
        long workOrderId = Long.parseLong(split[2]);
        return workOrderId;
    }

    /**
     * createdTaskDeviationWorkOrder() method
     * - creates a workOrder with if the task is deviated for first time, for the next consecutive times,
     * message will be added as a comment.
     * - For the next consecutive times we check if the task has already got deviated based on @taskUniqueId
     * which is constructed as follows,
     * '{PM_V1_ID/PM_V2_ID/WORKORDER_ID}_{WORKORDER_RESOURCE_ID}_{TASK_UNIQUE_ID}'
     * By this way we will have
     *
     * @param workOrderContext
     * @param taskContext
     * @throws Exception
     */
    public void createdTaskDeviationWorkOrder(V3WorkOrderContext workOrderContext, V3TaskContext taskContext) throws Exception {
        Long pmId = null;
        if (workOrderContext.getPm() != null) {
            pmId = workOrderContext.getPm().getId();
        } else if (workOrderContext.getPmV2() != null) {
            pmId = workOrderContext.getPmV2();
        }

        // We take pmId, or else workOrderId. WorkOrder ID case won't happen until task deviation support is given to normal workorder creation
        String id = (pmId != null) ? String.valueOf(pmId) : String.valueOf(workOrderContext.getId());
        String taskUniqueId = id + "_" + workOrderContext.getResource().getId() + "_" + taskContext.getUniqueId();
        WorkOrderContext deviationWo = WorkOrderAPI.getOpenWorkOrderForDeviationTemplate(taskUniqueId);
        LOGGER.info("Deviated Task: #" + taskContext.getId());

        if (deviationWo != null) {
            LOGGER.info("Updating workorder(#" + deviationWo.getId() + ") due to task deviation.");
            NoteContext note = new NoteContext();
            note.setBody(getCommentBody(taskContext, workOrderContext));
            note.setParentId(deviationWo.getId());
            note.setCreatedTime(System.currentTimeMillis());

            FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
            FacilioContext noteContext = addNote.getContext();
            noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
            noteContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
            noteContext.put(FacilioConstants.ContextNames.NOTE, note);
            addNote.execute();
        } else {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

            V3WorkOrderContext taskDeviationContext = constructTaskDeviationWorkOrder(workOrderModule, workOrderContext, taskContext, taskUniqueId);
            // Create WorkOrder
            V3Util.createRecord(workOrderModule, FieldUtil.getAsProperties(taskDeviationContext));
        }

    }

    /**
     * getCommentBody constructs the comment using various values that are required.
     *
     * @param taskContext
     * @param workOrderContext
     * @return
     */
    private String getCommentBody(V3TaskContext taskContext, V3WorkOrderContext workOrderContext){
        StringBuilder builder = new StringBuilder();
        builder.append("Task ")
                .append("'")
                .append(taskContext.getSubject())
                .append("'")
                .append(" has been closed with the value ")
                .append("'")
                .append(taskContext.getInputValue())
                .append("'")
                .append(" from WorkOrder #")
                .append(workOrderContext.getLocalId())
                .append(".");
        return builder.toString();
    }

    /**
     * constructTaskDeviationWorkOrder() method copies the @workOrderContext to @taskDeviationContext and updates
     * the fields with new set of values and also resets few fields, which isn't required for the deviated workOrder.
     *
     * @param workOrderModule
     * @param workOrderContext
     * @param taskContext
     * @param taskUniqueId
     * @return
     * @throws Exception
     */
    private V3WorkOrderContext constructTaskDeviationWorkOrder(FacilioModule workOrderModule, V3WorkOrderContext workOrderContext, V3TaskContext taskContext, String taskUniqueId) throws Exception {
        String workOrderSubject = "Deviation Work Order - " + taskContext.getSubject();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Deviation WorkOrder created from ")
                .append("Parent WorkOrder: #").append(workOrderContext.getLocalId())
                .append(" - ").append(workOrderContext.getSubject())
                .append("\n")
                .append("Task Name: ").append(taskContext.getSubject())
                .append("\n")
                .append("Task Value: ").append(taskContext.getInputValue())
                .append("\n");
        String workOrderDescription = stringBuilder.toString();

        V3WorkOrderContext taskDeviationContext = FieldUtil.cloneBean(workOrderContext, V3WorkOrderContext.class);

        // Updating taskDeviationContext with new set of values
        taskDeviationContext.setSubject(workOrderSubject);
        taskDeviationContext.setDescription(workOrderDescription);
        taskDeviationContext.setDeviationTaskUniqueId(taskUniqueId);
        taskDeviationContext.setParentWO(workOrderContext); // Establish a parent/child relation
        taskDeviationContext.setCreatedTime(workOrderContext.getActualWorkEnd());
        taskDeviationContext.setSourceType(V3TicketContext.SourceType.TASK_DEVIATION.getIntVal());

        // Resetting the taskDeviationContext with default/null values
        taskDeviationContext.setId(0);
        taskDeviationContext.setLocalId(0);
        taskDeviationContext.setTasks(null);
        taskDeviationContext.setTaskString(null);
        taskDeviationContext.setTaskList(null);
        taskDeviationContext.setJobPlan(null);
        taskDeviationContext.setNoOfAttachments(0);
        taskDeviationContext.setNoOfNotes(0);
        taskDeviationContext.setNoOfTasks(0);
        taskDeviationContext.setNoOfClosedTasks(0);
        taskDeviationContext._setStateFlowId(-1L);
        taskDeviationContext.setModuleState(null);
        taskDeviationContext.setStatus(null);

        return taskDeviationContext;
    }
}
