package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.util.*;
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
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetManualToolTransactionsCommandV3  extends FacilioCommand {


    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ToolTransactionContext> toolTransactions = recordMap.get(moduleName);
        Boolean issueTransaction = MapUtils.isNotEmpty(bodyParams) && (bodyParams.containsKey("issue") && (boolean) bodyParams.get("issue"));
        Boolean returnTransaction = MapUtils.isNotEmpty(bodyParams) && (bodyParams.containsKey("return") && (boolean) bodyParams.get("return"));
        if(CollectionUtils.isNotEmpty(toolTransactions) && (issueTransaction || returnTransaction) ) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            List<FacilioField> toolTransactionsFields = modBean
                    .getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            Map<String, FacilioField> toolTransactionsFieldsMap = FieldFactory.getAsMap(toolTransactionsFields);
            List<LookupField> lookUpfields = new ArrayList<>();
            lookUpfields.add((LookupField) toolTransactionsFieldsMap.get("tool"));
            List<V3ToolTransactionContext> toolTransactionslist = new ArrayList<>();
            List<V3ToolTransactionContext> toolTransactionsToBeAdded = new ArrayList<>();
            long toolTypesId = -1;
            if (toolTransactions != null && !toolTransactions.isEmpty()) {
                Map<Long, V3BinContext> allBins = V3InventoryAPI.getBinFromToolTransactions(toolTransactions);

                for (V3ToolTransactionContext toolTransaction : toolTransactions) {
                    V3ToolContext tool = getTool(toolTransaction.getTool().getId());
                    V3ToolTypesContext toolTypes = getToolType(tool.getToolType().getId());
                    toolTypesId = toolTypes.getId();
                    V3BinContext bin = null;
                    if(toolTransaction.getBin() != null){
                        bin = allBins.get(toolTransaction.getBin().getId());
                    }
                    if( bin == null || bin.getId() <= 0 ){
                        if(tool.getDefaultBin() != null){
                            bin = tool.getDefaultBin();
                            toolTransaction.setBin(bin);
                            allBins.put(bin.getId(),bin);
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Bin should not be empty");
                        }
                    } else {
                        V3ToolsApi.validateBin(Collections.singleton(bin.getId()),tool);
                    }

                    if (toolTransaction.getTransactionStateEnum() == TransactionState.ISSUE
                                && bin.getQuantity() < toolTransaction.getQuantity()) {
                            throw new IllegalArgumentException("Insufficient quantity in selected Bin!");
                        } else {
                            ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                            if (toolTransaction.getRequestedLineItem() != null && toolTransaction.getRequestedLineItem().getId() > 0) {
                                approvalState = ApprovalState.APPROVED;
                            }

                            if (toolTypes.isRotating()) {
                                List<Long> assetIds = toolTransaction.getAssetIds();
                                List<V3AssetContext> assets = getAssetsList(assetIds);
                                if (assets != null) {
                                    for (V3AssetContext asset : assets) {
                                        if (toolTransaction.getTransactionStateEnum() == TransactionState.ISSUE
                                                && asset.isUsed()) {
                                            throw new IllegalArgumentException("Insufficient quantity in inventory!");
                                        }
                                        V3ToolTransactionContext woTool = new V3ToolTransactionContext();

                                        if (toolTransaction.getTransactionStateEnum() == TransactionState.RETURN) {
                                            asset.setIsUsed(false);
                                            approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                                        } else if (toolTransaction
                                                .getTransactionStateEnum() == TransactionState.ISSUE) {
                                            asset.setIsUsed(true);
                                        }

                                        // if(toolTransaction.getTransactionStateEnum()
                                        // == TransactionState.RETURN){
                                        // pTool.setIsUsed(false);
                                        // } else if
                                        // (toolTransaction.getTransactionStateEnum()
                                        // == TransactionState.ISSUE) {
                                        // pTool.setIsUsed(true);
                                        // }
                                        woTool = setWorkorderItemObj(null,1, tool, toolTransaction, toolTypes,
                                                approvalState, asset, context);
                                        updateAsset(asset);
                                        toolTransactionslist.add(woTool);
                                        toolTransactionsToBeAdded.add(woTool);
                                    }
                                }
                            } else {
                                if(returnTransaction){
                                    SupplementRecord purchasedToolField = (SupplementRecord) Constants.getModBean().getField("purchasedTool", FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
                                    V3ToolTransactionContext parentTransaction = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, Collections.singletonList(toolTransaction.getParentTransactionId()),V3ToolTransactionContext.class,Collections.singletonList(purchasedToolField)).get(0);
                                    if(toolTransaction.getBin() != null && parentTransaction.getBin() != null && toolTransaction.getBin().getId() == parentTransaction.getBin().getId()){
                                        V3PurchasedToolContext pItem = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PURCHASED_TOOL, parentTransaction.getPurchasedTool().getId(),V3PurchasedToolContext.class);
                                        V3ToolTransactionContext woItem = setWorkorderItemObj(pItem, toolTransaction.getQuantity(), tool,
                                                toolTransaction, toolTypes, approvalState, null, context);
                                        toolTransactionslist.add(woItem);
                                        toolTransactionsToBeAdded.add(woItem);
                                    } else {
                                        V3PurchasedToolContext pTool = new V3PurchasedToolContext();
                                        pTool.setTool(tool);
                                        pTool.setBin(toolTransaction.getBin());
                                        pTool.setQuantity(toolTransaction.getQuantity());
                                        pTool.setUnitPrice(parentTransaction.getPurchasedTool().getUnitPrice());
                                        Map<Long, List<UpdateChangeSet>> resultSet = V3RecordAPI.addRecord(false, Collections.singletonList(pTool), FacilioConstants.ContextNames.PURCHASED_TOOL, true);
                                        if(MapUtils.isNotEmpty(resultSet)){
                                            List<Long> recordIds = resultSet.keySet().stream().collect(Collectors.toList());
                                            pTool.setId(recordIds.get(0));
                                        }
                                        V3ToolTransactionContext woItem = setWorkorderItemObj(pTool, toolTransaction.getQuantity(), tool,
                                                toolTransaction, toolTypes, approvalState, null, context);
                                        toolTransactionslist.add(woItem);
                                        toolTransactionsToBeAdded.add(woItem);
                                    }

                                } else {
                                    Boolean getOnlyAvailableQuantity = toolTransaction.getTransactionState()!=TransactionState.RETURN.getValue();
                                    List<V3PurchasedToolContext> purchasedTools = V3InventoryUtil.getPurchasedToolsBasedOnCostType(tool,bin,getOnlyAvailableQuantity);
                                    if(CollectionUtils.isNotEmpty(purchasedTools)){
                                        V3PurchasedToolContext pTool = purchasedTools.get(0);
                                        if(toolTransaction.getQuantity() <= pTool.getCurrentQuantity()){
                                            V3ToolTransactionContext woTool = new V3ToolTransactionContext();
                                            woTool = setWorkorderItemObj(pTool,toolTransaction.getQuantity(), tool, toolTransaction,
                                                    toolTypes, approvalState, null, context);
                                            toolTransactionslist.add(woTool);
                                            toolTransactionsToBeAdded.add(woTool);
                                        } else {
                                            double requiredQuantity = toolTransaction.getQuantity();
                                            for(V3PurchasedToolContext purchasedTool : purchasedTools){
                                                V3ToolTransactionContext woItem = new V3ToolTransactionContext();
                                                double quantityUsedForTheCost = 0;
                                                if(requiredQuantity <= purchasedTool.getCurrentQuantity()){
                                                    quantityUsedForTheCost = requiredQuantity;
                                                }
                                                else if(purchasedTool.getCurrentQuantity()==0 && toolTransaction.getTransactionState()==TransactionState.RETURN.getValue()){
                                                    quantityUsedForTheCost = requiredQuantity;
                                                }
                                                else {
                                                    quantityUsedForTheCost = purchasedTool.getCurrentQuantity();
                                                }

                                                woItem = setWorkorderItemObj(purchasedTool, quantityUsedForTheCost, tool,
                                                        toolTransaction, toolTypes, approvalState, null, context);
                                                requiredQuantity -= quantityUsedForTheCost;
                                                toolTransactionslist.add(woItem);
                                                toolTransactionsToBeAdded.add(woItem);
                                                if (requiredQuantity <= 0) {
                                                    break;
                                                }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                }

                recordMap.put(moduleName, toolTransactionsToBeAdded);

                context.put(FacilioConstants.ContextNames.PARENT_ID, toolTransactions.get(0).getParentId());
                context.put(FacilioConstants.ContextNames.BIN,allBins.values().stream().collect(Collectors.toList()));
                context.put(FacilioConstants.ContextNames.TOOL_ID, toolTransactions.get(0).getTool().getId());
                context.put(FacilioConstants.ContextNames.TOOL_IDS,
                        Collections.singletonList(toolTransactions.get(0).getTool().getId()));
                context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransactionslist);
                context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypesId);
                context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, Collections.singletonList(toolTypesId));
                context.put(FacilioConstants.ContextNames.TRANSACTION_STATE,
                        toolTransactions.get(0).getTransactionStateEnum());
            }
        }
        return false;
    }

    private V3ToolTransactionContext setWorkorderItemObj(V3PurchasedToolContext purchasedTool, double quantity,
                                                         V3ToolContext tool, V3ToolTransactionContext toolTransaction, V3ToolTypesContext toolTypes,
                                                         ApprovalState approvalState, V3AssetContext asset, Context context) throws Exception {
        V3ToolTransactionContext woTool = new V3ToolTransactionContext();

        woTool.setTransactionType(TransactionType.MANUAL.getValue());

        woTool.setTransactionState(toolTransaction.getTransactionStateEnum());
        woTool.setStoreRoom(tool.getStoreRoom());
        woTool.setIsReturnable(true);
        if(purchasedTool != null){
            woTool.setPurchasedTool(purchasedTool);
        }
        if(asset!=null) {
            woTool.setAsset(asset);
        }
        woTool.setQuantity(quantity);
        woTool.setTool(tool);
        woTool.setBin(toolTransaction.getBin());
        if(toolTransaction.getRequestedLineItem() != null) {
            woTool.setRequestedLineItem(toolTransaction.getRequestedLineItem());
        }
        woTool.setToolType(toolTypes);
        woTool.setSysModifiedTime(System.currentTimeMillis());
        woTool.setParentId(toolTransaction.getParentId());
        woTool.setParentTransactionId(toolTransaction.getParentTransactionId());
        woTool.setApprovedState(approvalState);
        woTool.setRemainingQuantity(quantity);

        if(toolTransaction.getTransactionStateEnum() == TransactionState.RETURN) {
            woTool.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
        }
        woTool.setIssuedTo(toolTransaction.getIssuedTo());

        JSONObject newinfo = new JSONObject();

        if (toolTypes.isRotating() && woTool.getTransactionStateEnum() == TransactionState.ISSUE) {

            asset.setLastIssuedToUser(woTool.getIssuedTo());
            if(woTool.getWorkorder() != null) {
                asset.setLastIssuedToWo(woTool.getWorkorder().getId());
            }
            asset.setLastIssuedTime(System.currentTimeMillis());
            V3AssetAPI.updateAsset(asset, asset.getId());


            if(woTool.getTransactionTypeEnum() == TransactionType.MANUAL) {
                User user = AccountUtil.getUserBean().getUser(woTool.getParentId(), true);
                newinfo.put("issuedTo",user.getName());

            }
            CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.ISSUE, newinfo,
                    (FacilioContext) context);
        }
        else if(toolTypes.isRotating() && woTool.getTransactionStateEnum() == TransactionState.RETURN) {
            User user = new User();
            user.setId(-99);
            asset.setLastIssuedToUser(user);
            asset.setLastIssuedToWo(-99l);
            asset.setLastIssuedTime(-99l);
            V3AssetAPI.updateAsset(asset, asset.getId());

            if(woTool.getTransactionTypeEnum() == TransactionType.MANUAL) {
                user = AccountUtil.getUserBean().getUser(woTool.getParentId(), true);
                newinfo.put("returnedBy", user.getName());
            }
            else if(woTool.getTransactionTypeEnum() == TransactionType.WORKORDER) {
                newinfo.put("returnedBy", "WO - #"+ woTool.getParentId());
            }
            CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.RETURN, newinfo,
                    (FacilioContext) context);
        }
        return woTool;
    }

    public static V3ToolContext getTool(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<LookupField>lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) fieldMap.get("storeRoom"));
        SelectRecordsBuilder<V3ToolContext> selectBuilder = new SelectRecordsBuilder<V3ToolContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ToolContext.class)
                .andCustomWhere(module.getTableName() + ".ID = ?", id).fetchSupplements(lookUpfields);

        List<V3ToolContext> stockedTools = selectBuilder.get();

        if (stockedTools != null && !stockedTools.isEmpty()) {
            return stockedTools.get(0);
        }
        return null;
    }

    public static int getEstimatedWorkDuration(long issueTime, long returnTime) {
        long duration = -1;
        if (issueTime != -1 && returnTime != -1) {
            duration = returnTime - issueTime;
        }
        int hours = (int) ((duration / (1000 * 60 * 60)));
        return hours;
    }

    public static V3ToolTypesContext getToolType(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
        List<FacilioField> itemTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

        SelectRecordsBuilder<V3ToolTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<V3ToolTypesContext>()
                .select(itemTypesFields).table(itemTypesModule.getTableName()).moduleName(itemTypesModule.getName())
                .beanClass(V3ToolTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, itemTypesModule));

        List<V3ToolTypesContext> toolTypes = itemTypesselectBuilder.get();
        if (toolTypes != null && !toolTypes.isEmpty()) {
            return toolTypes.get(0);
        }
        return null;
    }

    public static List<V3AssetContext> getAssetsList(List<Long> id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        SelectRecordsBuilder<V3AssetContext> selectBuilder = new SelectRecordsBuilder<V3AssetContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3AssetContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

        List<V3AssetContext> assetList = selectBuilder.get();

        if (assetList != null && !assetList.isEmpty()) {
            return assetList;
        }
        return null;
    }

    private void updateAsset(V3AssetContext asset) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        UpdateRecordBuilder<V3AssetContext> updateBuilder = new UpdateRecordBuilder<V3AssetContext>()
                .module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(asset.getId(), module));
        updateBuilder.update(asset);

        System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");
    }
}
