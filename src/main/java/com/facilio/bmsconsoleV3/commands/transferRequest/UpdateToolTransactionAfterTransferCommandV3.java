package com.facilio.bmsconsoleV3.commands.transferRequest;

import java.util.*;

import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
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

public class UpdateToolTransactionAfterTransferCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequests = recordMap.get(moduleName);
        if (!Objects.isNull(context.get(FacilioConstants.ContextNames.TOOL_TYPES)) && transferRequests.get(0).getIsStaged() && transferRequests.get(0).getIsCompleted()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Long storeRoomId = transferRequests.get(0).getTransferToStore().getId();
            List<V3TransferRequestLineItemContext> toolTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.TOOL_TYPES);
            for(V3TransferRequestLineItemContext toolTypeLineItem : toolTypesList) {
            List<V3ToolTransactionContext> toolTransactiosnToBeAdded = new ArrayList<>();
            long toolTypeId = toolTypeLineItem.getToolType().getId();
            V3ToolContext tool = V3ToolsApi.getToolsForTypeAndStore(storeRoomId, toolTypeId);
            Long toolId = tool.getId();
            Long lastPurchasedDate = null;
            double lastPurchasedPrice = 0;
            List<V3PurchasedToolContext> purchasedTools = new ArrayList<>();
            String purchasedToolModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_TOOLS;
            FacilioModule module = modBean.getModule(purchasedToolModuleName);
            List<FacilioField> fields = modBean.getAllFields(purchasedToolModuleName);
            SelectRecordsBuilder<V3TransferRequestPurchasedTool> selectRecordsBuilder = new SelectRecordsBuilder<V3TransferRequestPurchasedTool>()
                    .module(module)
                    .beanClass(V3TransferRequestPurchasedTool.class)
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_ID", "transferRequest", String.valueOf(transferRequests.get(0).getId()), NumberOperators.EQUALS));
            List<V3TransferRequestPurchasedTool> records = selectRecordsBuilder.get();
                for (V3TransferRequestPurchasedTool record : records) {
                    V3PurchasedToolContext purchasedTool = setPurchasedTool(record, tool);
                    lastPurchasedDate = purchasedTool.getCostDate();
                    lastPurchasedPrice = purchasedTool.getQuantity() * purchasedTool.getUnitPrice();
                    purchasedTools.add(purchasedTool);
                    //Tool Transactions
                    V3ToolTransactionContext woTool = setToolTransaction(record, purchasedTool, tool,transferRequests.get(0).getId());
                    toolTransactiosnToBeAdded.add(woTool);
                }
                String toolModuleName = FacilioConstants.ContextNames.PURCHASED_TOOL;
                module = modBean.getModule(toolModuleName);
                fields = modBean.getAllFields(toolModuleName);
                InsertRecordBuilder<V3PurchasedToolContext> readingBuilder = new InsertRecordBuilder<V3PurchasedToolContext>()
                        .module(module).fields(fields).addRecords(purchasedTools);
                readingBuilder.save();
                //Tool Transaction Insertion
                FacilioModule toolTransactions = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

                V3Util.createRecordList(toolTransactions, FieldUtil.getAsMapList(toolTransactiosnToBeAdded,V3ToolTransactionContext.class),null,null);
                //Updating last purchased price and date of tool
                toolModuleName = FacilioConstants.ContextNames.TOOL;
                module = modBean.getModule(toolModuleName);
                fields = modBean.getAllFields(toolModuleName);
                List<FacilioField> updatedFields = new ArrayList<>();
                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                updatedFields.add(fieldsMap.get("lastPurchasedDate"));
                updatedFields.add(fieldsMap.get("lastPurchasedPrice"));

                Map<String, Object> map = new HashMap<>();
                map.put("lastPurchasedDate", lastPurchasedDate);
                map.put("lastPurchasedPrice", lastPurchasedPrice);

                UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                        .module(module).fields(updatedFields)
                        .andCondition(CriteriaAPI.getIdCondition(toolId, module));
                updateBuilder.updateViaMap(map);
        }
        }
        return false;
    }

    private V3PurchasedToolContext setPurchasedTool(V3TransferRequestPurchasedTool record, V3ToolContext tool){
        V3PurchasedToolContext purchasedTool= new V3PurchasedToolContext();
        purchasedTool.setQuantity(record.getQuantity());
        purchasedTool.setCurrentQuantity(record.getQuantity());
        purchasedTool.setTool(tool);
        purchasedTool.setToolType(tool.getToolType());
        purchasedTool.setRate(record.getRate());
        purchasedTool.setUnitPrice(record.getUnitPrice());
        purchasedTool.setSysModifiedTime(System.currentTimeMillis());
        purchasedTool.setSysCreatedTime(System.currentTimeMillis());
        purchasedTool.setCostDate(System.currentTimeMillis());

        return purchasedTool;
    }
    private V3ToolTransactionContext setToolTransaction(V3TransferRequestPurchasedTool record, V3PurchasedToolContext purchasedTool, V3ToolContext tool, Long id){
        V3ToolTransactionContext woTool = new V3ToolTransactionContext();
        woTool.setTransactionState(TransactionState.TRANSFERRED_TO);
        woTool.setIsReturnable(false);
        if (purchasedTool != null) {
            woTool.setPurchasedTool(purchasedTool);
        }
        woTool.setQuantity(record.getQuantity());
        woTool.setTransactionType(TransactionType.STOCK.getValue());
        woTool.setTool(tool);
        woTool.setStoreRoom(tool.getStoreRoom());
        woTool.setToolType(tool.getToolType());
        woTool.setSysModifiedTime(System.currentTimeMillis());
        woTool.setParentId(id);
        woTool.setApprovedState(1);
        woTool.setRemainingQuantity(0.0);
        return woTool;
    }
}
