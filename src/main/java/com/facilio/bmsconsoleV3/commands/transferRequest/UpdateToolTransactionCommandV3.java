package com.facilio.bmsconsoleV3.commands.transferRequest;

import java.util.*;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3StoreroomApi;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateToolTransactionCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequests = recordMap.get(moduleName);
        if (!Objects.isNull(context.get(FacilioConstants.ContextNames.TOOL_TYPES)) && transferRequests.get(0).getIsStaged() && !transferRequests.get(0).getIsCompleted() && !transferRequests.get(0).getIsShipped()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            FacilioModule purchasedToolModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
            List<FacilioField> purchasedToolsFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
            FacilioModule transferRequestPurchasedToolModule = modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_TOOLS);
            List<FacilioField> transferRequestPurchasedToolsFields = modBean.getAllFields(FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_TOOLS);
            List<V3TransferRequestLineItemContext> toolTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.TOOL_TYPES);
            for(V3TransferRequestLineItemContext toolTypeLineItem : toolTypesList) {
                List<V3ToolTransactionContext> toolTransactiosnToBeAdded = new ArrayList<>();
                List<V3TransferRequestPurchasedTool> transferRequestPurchasedTools = new ArrayList<>();



                V3ToolTypesContext toolType = toolTypeLineItem.getToolType();
                double quantityTransferred = toolTypeLineItem.getQuantity();
                long storeroomId = (long)context.get(FacilioConstants.ContextNames.STORE_ROOM_ID);
                V3StoreRoomContext storeRoom = V3StoreroomApi.getStoreRoom(storeroomId);
                V3ToolContext tool = V3ToolsApi.getTool(toolType,storeRoom);

                List<V3PurchasedToolContext> purchasedTools = V3InventoryUtil.getPurchasedToolsBasedOnCostType(tool,null,true);

                if (purchasedTools != null && !purchasedTools.isEmpty()) {
                    V3PurchasedToolContext pTool = purchasedTools.get(0);
                    double requiredQuantity = -(quantityTransferred);
                    if (pTool.getCurrentQuantity() >= quantityTransferred) {
                        double newQuantity =pTool.getCurrentQuantity() - quantityTransferred;
                        V3TransferRequestPurchasedTool transferRequestPurchasedItem = setTransferRequestPurchasedTool(tool,transferRequests.get(0),pTool.getRate(),pTool.getUnitPrice(),quantityTransferred);
                        V3ToolTransactionContext woItem = setWorkorderToolObj(pTool, quantityTransferred, tool, toolType,newQuantity,purchasedToolsFields,purchasedToolModule,transferRequests.get(0).getId());
                        toolTransactiosnToBeAdded.add(woItem);
                        transferRequestPurchasedTools.add(transferRequestPurchasedItem);
                    } else {
                        for (V3PurchasedToolContext purchasedTool : purchasedTools) {
                            V3ToolTransactionContext woTool;
                            double quantityUsedForTheCost = 0;
                            if (purchasedTool.getCurrentQuantity() + requiredQuantity >= 0) {
                                quantityUsedForTheCost = requiredQuantity;
                            } else {
                                quantityUsedForTheCost = -(purchasedTool.getCurrentQuantity());
                            }
                            double newQuantity =purchasedTool.getCurrentQuantity()+quantityUsedForTheCost;
                            V3TransferRequestPurchasedTool transferRequestPurchasedTool = setTransferRequestPurchasedTool(tool,transferRequests.get(0),pTool.getRate(),purchasedTool.getUnitPrice(),-(quantityUsedForTheCost));
                            woTool = setWorkorderToolObj(purchasedTool, -(quantityUsedForTheCost), tool, toolType, newQuantity, purchasedToolsFields, purchasedToolModule,transferRequests.get(0).getId());
                            toolTransactiosnToBeAdded.add(woTool);
                            transferRequestPurchasedTools.add(transferRequestPurchasedTool);
                            requiredQuantity -= quantityUsedForTheCost;
                            if (requiredQuantity == 0) {
                                break;
                            }
                        }
                    }
                }


                V3Util.createRecordList(toolTransactionsModule, FieldUtil.getAsMapList(toolTransactiosnToBeAdded,V3ToolTransactionContext.class),null,null);
                InsertRecordBuilder<V3TransferRequestPurchasedTool> insertRecordBuilder = new InsertRecordBuilder<V3TransferRequestPurchasedTool>()
                        .module(transferRequestPurchasedToolModule).fields(transferRequestPurchasedToolsFields).addRecords(transferRequestPurchasedTools);
                insertRecordBuilder.save();
            }
        }
        return false;
    }
    private V3ToolTransactionContext setWorkorderToolObj(V3PurchasedToolContext purchasedTool,double quantity, V3ToolContext tool, V3ToolTypesContext toolTypes,double newQuantity,List<FacilioField> fields,FacilioModule module,Long id) throws Exception {
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("currentQuantity"));
        UpdateRecordBuilder<V3PurchasedToolContext> updateBuilder = new UpdateRecordBuilder<V3PurchasedToolContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(purchasedTool.getId(), module));
        Map<String, Object> map = new HashMap<>();
        map.put("currentQuantity", newQuantity);
        updateBuilder.updateViaMap(map);
        V3ToolTransactionContext woTool = new V3ToolTransactionContext();
        woTool.setTransactionState(TransactionState.TRANSFERRED_FROM);
        woTool.setIsReturnable(false);
        woTool.setQuantity(quantity);
        woTool.setTransactionType(TransactionType.STOCK.getValue());
        woTool.setParentId(id);
        woTool.setTool(tool);
        woTool.setStoreRoom(tool.getStoreRoom());
        woTool.setToolType(toolTypes);
        woTool.setSysModifiedTime(System.currentTimeMillis());
        woTool.setApprovedState(1);
        woTool.setRemainingQuantity(0);
        return woTool;
    }

    private V3TransferRequestPurchasedTool setTransferRequestPurchasedTool(V3ToolContext tool, V3TransferRequestContext transferRequest, Double rate, Double unitPrice, Double quantityTransferred){
        V3TransferRequestPurchasedTool transferRequestPurchasedTool = new V3TransferRequestPurchasedTool();
        transferRequestPurchasedTool.setTransferRequest(transferRequest);
        transferRequestPurchasedTool.setTool(tool);
        transferRequestPurchasedTool.setQuantity(quantityTransferred);
        transferRequestPurchasedTool.setRate(rate);
        transferRequestPurchasedTool.setUnitPrice(unitPrice);
        return transferRequestPurchasedTool;
    }
}
