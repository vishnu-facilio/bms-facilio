package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3InventoryRequestAPI;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class SetWorkOrderToolsCommandV3 extends FacilioCommand {

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkorderToolsContext> workOrderTools = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
        List<FacilioField> workorderToolsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_TOOLS);
        Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(workorderToolsFields);
//        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);

        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) toolFieldsMap.get("tool"));
        lookUpfields.add((LookupField) toolFieldsMap.get("toolType"));

        List<V3WorkorderToolsContext> workorderToolslist = new ArrayList<>();
        List<V3WorkorderToolsContext> toolsToBeAdded = new ArrayList<>();
        long toolTypesId = -1;
        if (CollectionUtils.isNotEmpty(workOrderTools)) {
            long parentId = workOrderTools.get(0).getParentId();

            for (V3WorkorderToolsContext workorderTool : workOrderTools) {
                long parentTransactionId = workorderTool.getParentTransactionId();
                V3WorkOrderContext workorder = getWorkorder(parentId);
                V3ToolContext tool = getStockedTools(workorderTool.getTool().getId());
                toolTypesId = tool.getToolType().getId();
                V3ToolTypesContext toolTypes = getToolType(toolTypesId);
                if(workorderTool.getQuantity() <= 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                if (workorderTool.getRequestedLineItem() != null && workorderTool.getRequestedLineItem().getId() > 0) {
                    if(!V3InventoryRequestAPI.checkQuantityForWoToolNeedingApprovalV3(toolTypes, workorderTool.getRequestedLineItem(), workorderTool)) {
                        throw new IllegalArgumentException("Please check the quantity approved/issued in the request");
                    }
                }
                else if(workorderTool.getParentTransactionId() > 0) {
                    if(!InventoryRequestAPI.checkQuantityForWoTool(workorderTool.getParentTransactionId(), workorderTool.getQuantity())){
                        throw new IllegalArgumentException("Please check the quantity issued");
                    }
                }

                if (workorderTool.getId() > 0) {
//                    if (!eventTypes.contains(EventType.EDIT)) {
//                        eventTypes.add(EventType.EDIT);
//                    }
                    List<V3WorkorderToolsContext> woIt = V3RecordAPI.getRecordsListWithSupplements(workorderToolsModule.getName(),Collections.singletonList(workorderTool.getId()),V3WorkorderToolsContext.class,lookUpfields);

                    if (woIt != null) {
                        V3WorkorderToolsContext wTool = woIt.get(0);
                        if ((wTool.getQuantity() + wTool.getTool().getCurrentQuantity()) < workorderTool
                                .getQuantity()) {
                            throw new IllegalArgumentException("Insufficient quantity in inventory!");
                        } else {
                            ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                            if (workorderTool.getRequestedLineItem() != null && workorderTool.getRequestedLineItem().getId() > 0) {
                                approvalState = ApprovalState.APPROVED;
                            }
                            if (toolTypes.isRotating()) {
                                wTool = setWorkorderToolObj(null, 1, tool, parentId,
                                        workorder, workorderTool, approvalState, wTool.getAsset(), workorderTool.getRequestedLineItem(), parentTransactionId, context);


                            } else {
                                wTool = setWorkorderToolObj(null, workorderTool.getQuantity(), tool, parentId,
                                        workorder, workorderTool, approvalState, null, workorderTool.getRequestedLineItem(), parentTransactionId, context);
                            }

                            // update
                            wTool.setId(workorderTool.getId());
                            workorderToolslist.add(wTool);
                            toolsToBeAdded.add(wTool);
                        }
                    }
                } else {
//                    if (!eventTypes.contains(EventType.CREATE)) {
//                        eventTypes.add(EventType.CREATE);
//                    }
                    if (workorderTool.getRequestedLineItem() == null && workorderTool.getParentTransactionId() <= 0 && tool.getQuantity() < workorderTool.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                    } else {
                        ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                        if (workorderTool.getRequestedLineItem() != null && workorderTool.getRequestedLineItem().getId() > 0) {
                            approvalState = ApprovalState.APPROVED;
                        }
                        if (toolTypes.isRotating()) {
                            List<Long> assetIds = workorderTool.getAssetIds();
                            List<V3AssetContext> assets = getAssetsFromId(assetIds);
                            if (assets != null) {
                                for (V3AssetContext asset : assets) {
                                    if (workorderTool.getRequestedLineItem() == null && workorderTool.getParentTransactionId() <= 0 && asset.isUsed()) {
                                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                                    }
                                    V3WorkorderToolsContext woTool = new V3WorkorderToolsContext();
                                    asset.setIsUsed(true);
                                    woTool = setWorkorderToolObj(null, 1, tool, parentId, workorder,
                                            workorderTool, approvalState, asset, workorderTool.getRequestedLineItem(), parentTransactionId, context);
                                    updatePurchasedTool(asset);
                                    workorderToolslist.add(woTool);
                                    toolsToBeAdded.add(woTool);
                                }
                            }
                        } else {
                            V3WorkorderToolsContext woTool = new V3WorkorderToolsContext();
                            woTool = setWorkorderToolObj(null, workorderTool.getQuantity(), tool, parentId,
                                    workorder, workorderTool, approvalState, null, workorderTool.getRequestedLineItem(), parentTransactionId, context);
                            workorderToolslist.add(woTool);
                            toolsToBeAdded.add(woTool);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(toolsToBeAdded)) {
                Map<String,Object> data = workOrderTools.get(0).getData();
                toolsToBeAdded.get(0).setData(data);
                recordMap.put(moduleName, toolsToBeAdded);
            }
            if(CollectionUtils.isNotEmpty(workorderToolslist)) {
                List<Long> recordIds = new ArrayList<>();
                for(V3WorkorderToolsContext ws : workorderToolslist){
                    recordIds.add(ws.getId());
                }
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
                context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORKORDER_TOOLS);
            }
            context.put(FacilioConstants.ContextNames.PARENT_ID, workOrderTools.get(0).getParentId());
            context.put(FacilioConstants.ContextNames.PARENT_ID_LIST,
                    Collections.singletonList(workOrderTools.get(0).getParentId()));
            context.put(FacilioConstants.ContextNames.TOOL_ID, workOrderTools.get(0).getTool().getId());
            context.put(FacilioConstants.ContextNames.TOOL_IDS,
                    Collections.singletonList(workOrderTools.get(0).getTool().getId()));
            context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderToolslist);
            context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);
            context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypesId);
            context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, Collections.singletonList(toolTypesId));
            context.put(FacilioConstants.ContextNames.WO_TOOLS_LIST, workorderToolslist);
        }

        return false;
    }

    private V3WorkorderToolsContext setWorkorderToolObj(V3PurchasedToolContext purchasedtool, double quantity,
                                                      V3ToolContext tool, long parentId, V3WorkOrderContext workorder, V3WorkorderToolsContext workorderTools,
                                                      ApprovalState approvalState, V3AssetContext asset, V3InventoryRequestLineItemContext lineItem, long parentTransactionId, Context context) throws Exception{
        V3WorkorderToolsContext woTool = new V3WorkorderToolsContext();
        Long issueTime = workorderTools.getIssueTime();
        Long returnTime = workorderTools.getReturnTime();
        woTool.setIssueTime(issueTime);
        woTool.setReturnTime(returnTime);
        woTool.setTransactionState(TransactionState.USE);
        if(lineItem != null) {
            woTool.setRequestedLineItem(lineItem);
            woTool.setParentTransactionId(ToolsApi.getToolTransactionsForRequestedLineItem(lineItem.getId()).getId());
        }
        Double duration = workorderTools.getDuration();
        if (issueTime != null && issueTime >0 && returnTime !=null && returnTime >0) {
            duration = V3InventoryUtil.getWorkorderActualsDuration(issueTime, returnTime, workorder);
        }
        if(duration !=null && duration > 0 && issueTime != null && issueTime >0 && (returnTime == null || returnTime <= 0) ) {
            returnTime = V3InventoryUtil.getReturnTimeFromDurationAndIssueTime(duration, issueTime);
            woTool.setReturnTime(returnTime);
        }
        else if(duration !=null && duration > 0 && returnTime != null && returnTime >0 && (issueTime ==null || issueTime <= 0)){
            issueTime = V3InventoryUtil.getIssueTimeFromDurationAndReturnTime(duration, returnTime);
            woTool.setIssueTime(issueTime);
        }
        woTool.setTransactionType(TransactionType.WORKORDER);
        woTool.setIsReturnable(false);
        if (purchasedtool != null) {
            woTool.setPurchasedTool(purchasedtool);
        }
        if(asset!=null) {
            woTool.setAsset(asset);
        }
        woTool.setDuration(duration);
        woTool.setApprovedState(approvalState);
        woTool.setRemainingQuantity(quantity);
        woTool.setQuantity(quantity);
        woTool.setTool(tool);
        woTool.setStoreRoom(tool.getStoreRoom());
        woTool.setToolType(tool.getToolType());
        woTool.setSysModifiedTime(System.currentTimeMillis());
        woTool.setParentId(parentId);
        Double rate = null;
        rate = tool.getRate();
        woTool.setRate(rate);
        Double costOccurred = 0.0;
        if (tool.getRate()!=null && tool.getRate() > 0 && duration !=null && duration >=0) {
            costOccurred = tool.getRate() * (duration / 3600) * woTool.getQuantity();
        }
        woTool.setCost(costOccurred);
        if(workorder!=null) {
            woTool.setWorkorder(workorder);
            if(workorder.getAssignedTo()!=null) {
                woTool.setIssuedTo(workorder.getAssignedTo());
            }
        }

        if(parentTransactionId != -1) {
            woTool.setParentTransactionId(parentTransactionId);
        }

        JSONObject newinfo = new JSONObject();

        if (tool.getToolType() != null) {
            V3ToolTypesContext toolType = V3ToolsApi.getToolTypes(tool.getToolType().getId());
            if(toolType != null && toolType.isRotating() && asset != null && woTool.getTransactionStateEnum() == TransactionState.USE) {
                if(woTool.getIssuedTo() != null) {
                    asset.setLastIssuedToUser(woTool.getIssuedTo());
                }
                if(woTool.getWorkorder() != null) {
                    asset.setLastIssuedToWo(woTool.getWorkorder().getId());
                }
                asset.setLastIssuedTime(System.currentTimeMillis());
                AssetsAPI.updateAssetV3(asset, asset.getId());


                if(woTool.getTransactionTypeEnum() == TransactionType.WORKORDER) {
                    newinfo.put("woId", woTool.getParentId());
                }
                CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.USE, newinfo,
                        (FacilioContext) context);
            }
        }
        return woTool;
    }

    public static V3WorkOrderContext getWorkorder(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkOrderContext workOrder = V3RecordAPI.getRecord(module.getName(),id,V3WorkOrderContext.class);
        if (workOrder != null) {
            return workOrder;
        }
        return null;
    }

    public static V3ToolContext getStockedTools(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord>lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) fieldMap.get("storeRoom"));

        List<V3ToolContext> stockedTools = V3RecordAPI.getRecordsListWithSupplements(module.getName(),Collections.singletonList(id),V3ToolContext.class,lookUpfields);

        if (stockedTools != null && !stockedTools.isEmpty()) {
            return stockedTools.get(0);
        }
        return null;
    }
    public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
        double duration = -1;
        if (issueTime != -1 && returnTime != -1) {
            duration = returnTime - issueTime;
        }

        double hours = duration / (60*60*1000);
        return Math.round(hours*100.0)/100.0;
    }

    public static V3ToolTypesContext getToolType(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);

        V3ToolTypesContext toolType  = V3RecordAPI.getRecord(toolTypesModule.getName(),id,V3ToolTypesContext.class);

        if (toolType != null) {
            return toolType;
        }
        return null;
    }

    public static List<V3AssetContext> getAssetsFromId(List<Long> id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);

        List<V3AssetContext> assets  = V3RecordAPI.getRecordsList(module.getName(),id,V3AssetContext.class);

        if (assets != null && !assets.isEmpty()) {
            return assets;
        }
        return null;
    }

    private void updatePurchasedTool(V3AssetContext asset) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

        V3RecordAPI.updateRecord(asset, module, fields);

        System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");
    }
}
