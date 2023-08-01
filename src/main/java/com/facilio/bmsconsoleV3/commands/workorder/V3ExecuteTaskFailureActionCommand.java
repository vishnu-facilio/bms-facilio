package com.facilio.bmsconsoleV3.commands.workorder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioStatus;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

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
            if (isAllCloseAction(context)) {
                for (V3WorkOrderContext workOrderContext : workorders) {
                    publishTaskDeviationHandler(workOrderContext);
                }
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

        String topicName = getTaskDeviationHandlerTopicName(orgId, workOrderContext);
        LOGGER.info("Pushing wms topic " + topicName);
        WmsBroadcaster.getBroadcaster().sendMessage(new Message()
                .setTopic(topicName)
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
     * isAllCloseAction() method checks if moduleState of the workOrder object is changed to "Closed" state, which is
     * being done by comparing the same workOrder object from context's OLD_RECORD_MAP and RECORD_LIST.
     *
     * @param context should contain ContextNames.OLD_RECORD_MAP & ContextNames.WORK_ORDER
     * @return true - if all workOrders in the list are moved to "Closed" state else returns false
     * @throws Exception
     */
    public static boolean isAllCloseAction(Context context) throws Exception {
        HashMap<String, HashMap> oldRecordMap = (HashMap<String, HashMap>) context.get(ContextNames.OLD_RECORD_MAP);

        if (MapUtils.isEmpty(oldRecordMap)) {
            return false;
        }

        HashMap<Long, V3WorkOrderContext> workOrderRecordMap = (HashMap<Long, V3WorkOrderContext>) oldRecordMap.get(ContextNames.WORK_ORDER);
        List<V3WorkOrderContext> newWorkOrderList = (List<V3WorkOrderContext>) context.get(ContextNames.RECORD_LIST);

        if (MapUtils.isEmpty(workOrderRecordMap) || CollectionUtils.isEmpty(newWorkOrderList)) {
            return false;
        }

        boolean isAllCloseAction = true;

        if (workOrderRecordMap.size() == newWorkOrderList.size()) {
            for (V3WorkOrderContext workOrderContext : newWorkOrderList) {
                V3WorkOrderContext oldWoContext = workOrderRecordMap.get(workOrderContext.getId());
                FacilioStatus oldModuleState = oldWoContext.getModuleState();
                FacilioStatus newModuleState = workOrderContext.getModuleState();
                if (oldModuleState != null && newModuleState != null && !Objects.equals(oldModuleState.getId(), newModuleState.getId())) {
                    FacilioStatus moduleState = TicketAPI.getStatus(newModuleState.getId());
                    if (!moduleState.getType().equals(FacilioStatus.StatusType.CLOSED)) {
                        isAllCloseAction = false;
                    }
                }
            }
        } else {
            isAllCloseAction = false;
        }
        return isAllCloseAction;
    }
}
