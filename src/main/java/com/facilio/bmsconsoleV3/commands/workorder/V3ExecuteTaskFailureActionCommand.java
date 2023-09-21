package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.FacilioStatus;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * V3ExecuteTaskFailureActionCommand
 */
@Log4j
public class V3ExecuteTaskFailureActionCommand extends FacilioCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = System.currentTimeMillis();
        //List<Long> recordIds = (List<Long>) context.get(ContextNames.RECORD_ID_LIST);
        List<V3WorkOrderContext> workorders = (List<V3WorkOrderContext>) context.get(ContextNames.RECORD_LIST);

        if (workorders != null && !workorders.isEmpty()) {
            List<V3WorkOrderContext> closeActionWorkOrders = getAllCloseActionWorkOrders(context);

            if (CollectionUtils.isNotEmpty(closeActionWorkOrders)) {
                for (V3WorkOrderContext workOrderContext : closeActionWorkOrders) {
                    // check if any of the tasks has failed and publishTaskDeviationHandler
                    List<V3TaskContext> tasks = TicketAPI.getRelatedTasksV3(Collections.singletonList(workOrderContext.getId()), false, false);
                    if (CollectionUtils.isNotEmpty(tasks)) {
                        for (V3TaskContext task : tasks) {
                            if ((task.isFailed() != null && task.isFailed()) && (task.getCreateWoOnFailure() != null && task.getCreateWoOnFailure())) {
                                LOGGER.info("Task #"+ task.getId() +", has failed.");
                                publishTaskDeviationHandler(workOrderContext);
                                break;
                            }
                        }
                    }
                }
            }else{
                LOGGER.info("Workorders are not moved to closed state");
            }
        }
        LOGGER.info("Time take in V3ExecuteTaskFailureActionCommand = " + (System.currentTimeMillis() - startTime));
        return false;
    }

    public static void publishTaskDeviationHandler(V3WorkOrderContext workOrderContext) {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("workOrderId", workOrderContext.getId());

        String key = getTaskDeviationHandlerTopicName(orgId, workOrderContext);
        LOGGER.info("Pushing ims key " + key);
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(key)
                .setOrgId(orgId)
                .setContent(message));
    }

    /**
     * getTaskDeviationHandlerTopicName generates the topic in format
     * task_deviation_createWorkOrder/{ORG_ID}/{WORKORDER_ID}/{RESOURCE_ID}/{ACTUAL_WORK_END_DURATION}/
     * @param orgId
     * @param workOrderContext
     * @return
     */
    public static String getTaskDeviationHandlerTopicName(Long orgId, V3WorkOrderContext workOrderContext){
        StringBuilder builder = new StringBuilder("task_deviation_createWorkOrder/");
        builder.append(orgId).append("/");
        builder.append(workOrderContext.getId());

        if(workOrderContext.getResource() != null && workOrderContext.getResource().getId() > 0L){
            builder.append("/").append(workOrderContext.getResource().getId());
        }
        if(workOrderContext.getActualWorkDuration() != null && workOrderContext.getActualWorkDuration() > 0L){
            builder.append("/").append(workOrderContext.getActualWorkDuration());
        }
        return builder.toString();
    }

    /**
     * getAllCloseActionWorkOrders() method checks if moduleState of the workOrder object is changed to "Closed" state, which is
     * being done by comparing the same workOrder object from context's OLD_RECORD_MAP and RECORD_LIST.
     *
     * @param context should contain ContextNames.OLD_RECORD_MAP & ContextNames.WORK_ORDER
     * @return List of workorders that are moved to "Closed" state else return null
     * @throws Exception
     */
    public static List<V3WorkOrderContext> getAllCloseActionWorkOrders(Context context) throws Exception {
        HashMap<String, HashMap> oldRecordMap = (HashMap<String, HashMap>) context.get(ContextNames.OLD_RECORD_MAP);

        if (MapUtils.isEmpty(oldRecordMap)) {
            LOGGER.info("oldRecordMap is empty");
            return null;
        }

        HashMap<Long, V3WorkOrderContext> workOrderRecordMap = (HashMap<Long, V3WorkOrderContext>) oldRecordMap.get(ContextNames.WORK_ORDER);
        List<V3WorkOrderContext> newWorkOrderList = (List<V3WorkOrderContext>) context.get(ContextNames.RECORD_LIST);

        if (MapUtils.isEmpty(workOrderRecordMap) || CollectionUtils.isEmpty(newWorkOrderList)) {
            LOGGER.info("workOrderRecordMap or newWorkOrderList is empty");
            return null;
        }

        List<V3WorkOrderContext> closeActionWorkOrders = new ArrayList<>();

        if (workOrderRecordMap.size() == newWorkOrderList.size()) {
            for (V3WorkOrderContext newWorkOrderContext : newWorkOrderList) {
                V3WorkOrderContext oldWoContext = workOrderRecordMap.get(newWorkOrderContext.getId());
                if(oldWoContext != null) {
                    FacilioStatus oldModuleState = oldWoContext.getModuleState();
                    FacilioStatus newModuleState = newWorkOrderContext.getModuleState();

                    if (oldModuleState != null && newModuleState != null && !Objects.equals(oldModuleState.getId(), newModuleState.getId())) {
                        FacilioStatus moduleState = TicketAPI.getStatus(newModuleState.getId());
                        if (moduleState.getType().equals(FacilioStatus.StatusType.CLOSED)) {
                            closeActionWorkOrders.add(newWorkOrderContext);
                        }
                    }
                }
            }
        } else {
            LOGGER.info("Size of workorder lists not equal.");
            return null;
        }
        return closeActionWorkOrders;
    }
}
