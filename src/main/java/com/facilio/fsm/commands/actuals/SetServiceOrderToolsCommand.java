package com.facilio.fsm.commands.actuals;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderCostContext;
import com.facilio.fsm.context.ServiceOrderToolsContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetServiceOrderToolsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderToolsContext> serviceOrderTools = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderTools)){
            List<V3ToolTransactionContext> toolTransactions = new ArrayList<>();
            List<ServiceOrderToolsContext> serviceOrderToolsContexts = new ArrayList<>();
            List<ServiceOrderContext> serviceOrders = new ArrayList<>();
            for(ServiceOrderToolsContext serviceOrderTool : serviceOrderTools){
                if(serviceOrderTool.getReturnTime()!=null && serviceOrderTool.getIssueTime()!=null &&  serviceOrderTool.getIssueTime() > serviceOrderTool.getReturnTime()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Return time cannot be greater than issued time");
                }
                if(serviceOrderTool.getServiceOrder()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service Order cannot be empty");
                }
                if(serviceOrderTool.getQuantity() <= 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                serviceOrders.add(serviceOrderTool.getServiceOrder());
                V3ToolContext tool = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TOOL,serviceOrderTool.getTool().getId(),V3ToolContext.class);
                if(tool==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tool cannot be empty");
                }
                if (serviceOrderTool.getId()<0 && tool.getCurrentQuantity() < serviceOrderTool.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient quantity in inventory!");
                }
                Double duration =null;
                if(serviceOrderTool.getDuration()!=null){
                    duration = serviceOrderTool.getDuration();
                }else{
                    duration = getDuration(serviceOrderTool.getIssueTime(),serviceOrderTool.getReturnTime());
                    if(duration!=null){
                        serviceOrderTool.setDuration(duration);
                    }
                }

                if(duration !=null && duration > 0 && serviceOrderTool.getIssueTime() != null && serviceOrderTool.getIssueTime() >0 && (serviceOrderTool.getReturnTime() == null || serviceOrderTool.getReturnTime() <= 0) ) {
                    Long returnTime = V3InventoryUtil.getReturnTimeFromDurationAndIssueTime(duration, serviceOrderTool.getIssueTime());
                    serviceOrderTool.setReturnTime(returnTime);
                }
                else if(duration !=null && duration > 0 && serviceOrderTool.getReturnTime() != null && serviceOrderTool.getReturnTime() >0 && (serviceOrderTool.getIssueTime() ==null || serviceOrderTool.getIssueTime() <= 0)){
                    Long issueTime = V3InventoryUtil.getIssueTimeFromDurationAndReturnTime(duration, serviceOrderTool.getReturnTime());
                    serviceOrderTool.setIssueTime(issueTime);
                }
                serviceOrderTool.setToolType(tool.getToolType());
                serviceOrderTool.setStoreRoom(tool.getStoreRoom());
                if(tool.getRate()!=null){
                    serviceOrderTool.setRate(tool.getRate());
                }
                if (tool.getRate()!=null && tool.getRate() > 0 && duration !=null && duration >=0) {
                   Double totalCost = tool.getRate() * (duration / 3600) * serviceOrderTool.getQuantity();
                   serviceOrderTool.setTotalCost(totalCost);
                }
                if(serviceOrderTool.getId()<0){
                    V3ToolTransactionContext toolTransaction = setToolTransaction(tool,serviceOrderTool);
                    toolTransactions.add(toolTransaction);
                }
                serviceOrderToolsContexts.add(serviceOrderTool);
            }
            recordMap.put(moduleName,serviceOrderToolsContexts);
            if(CollectionUtils.isNotEmpty(toolTransactions)){
                V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS), FieldUtil.getAsMapList(toolTransactions, V3ToolTransactionContext.class),null,null);
                List<Long> toolIds = serviceOrderToolsContexts.stream().map(serviceOrderTool -> serviceOrderTool.getTool().getId()).collect(Collectors.toList());
                List<Long> toolTypeIds = serviceOrderToolsContexts.stream().map(serviceOrderTool -> serviceOrderTool.getToolType().getId()).collect(Collectors.toList());
                context.put(FacilioConstants.ContextNames.TOOL_IDS, toolIds);
                context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS,toolTypeIds);
                context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransactions);
            }

            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST,serviceOrders);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE,ServiceOrderCostContext.InventoryCostType.TOOLS);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE, ServiceOrderCostContext.InventorySource.ACTUALS);
        }
        return false;
    }
    private Double getDuration(Long issueTime, Long returnTime) {
        if (issueTime != null && returnTime != null) {
            return Double.valueOf((returnTime - issueTime)/1000);
        }
        return null;
    }
    private V3ToolTransactionContext setToolTransaction(V3ToolContext tool,ServiceOrderToolsContext serviceOrderTool){
       V3ToolTransactionContext toolTransaction = new V3ToolTransactionContext();
        toolTransaction.setQuantity(serviceOrderTool.getQuantity());
        toolTransaction.setTransactionType(TransactionType.WORKORDER);
        toolTransaction.setTransactionState(TransactionState.USE);
        toolTransaction.setIsReturnable(false);
        toolTransaction.setRemainingQuantity(serviceOrderTool.getQuantity());
        toolTransaction.setTool(serviceOrderTool.getTool());
        toolTransaction.setToolType(tool.getToolType());
        toolTransaction.setStoreRoom(tool.getStoreRoom());
        toolTransaction.setParentId(serviceOrderTool.getServiceOrder().getId());
        return toolTransaction;
    }
}
