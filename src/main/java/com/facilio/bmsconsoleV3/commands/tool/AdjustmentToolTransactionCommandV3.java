package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3InventoryAPI;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdjustmentToolTransactionCommandV3  extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ToolTransactionContext> toolTransactions = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(toolTransactions) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("adjustQuantity") && (boolean) bodyParams.get("adjustQuantity")) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule purchasedToolModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
            List<FacilioField> purchasedToolFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);


            List<V3ToolTransactionContext> toolTransactionsToBeAdded = new ArrayList<>();
            long toolTypeId = -1;
            if (toolTransactions != null && !toolTransactions.isEmpty()) {
                Map<Long, V3BinContext> binMap = V3InventoryAPI.getBinFromToolTransactions(toolTransactions);
                for (V3ToolTransactionContext toolTransaction : toolTransactions) {
                    V3ToolContext tool = V3ToolsApi.getTool(toolTransaction.getTool().getId());
                    toolTypeId = tool.getToolType().getId();
                    V3ToolTypesContext toolType = V3ToolsApi.getToolTypes(toolTypeId);
                    V3BinContext bin = null;
                    if(toolTransaction.getBin() != null){
                        bin = binMap.get(toolTransaction.getBin().getId());
                    }
                    if( bin == null || bin.getId() <= 0 ){
                        if(tool.getDefaultBin() != null){
                            bin = tool.getDefaultBin();
                            toolTransaction.setBin(bin);
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Bin should not be empty");
                        }
                    } else {
                        V3ToolsApi.validateBin(Collections.singleton(bin.getId()),tool);
                    }
                    if (toolTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
                            && toolType.isRotating()) {
                        throw new IllegalArgumentException("Not Applicable for Rotating Tools!");
                    } else if (toolTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_INCREASE
                            && toolType.isRotating()) {
                        throw new IllegalArgumentException("Not Applicable for Rotating Tools!");
                    } else if (toolTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
                            && bin.getQuantity() < toolTransaction.getQuantity()) {
                        throw new IllegalArgumentException("Invalid Adjustment Quantity!");
                    } else if (toolTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
                            && !toolType.isRotating()) {
                        if (toolTransaction.getQuantity() <= tool.getCurrentQuantity()) {
                            List<V3PurchasedToolContext> purchasedTools = V3InventoryUtil.getPurchasedToolsBasedOnCostType(tool,bin,true);
                            if (purchasedTools != null && !purchasedTools.isEmpty()) {
                                V3PurchasedToolContext pTool = purchasedTools.get(0);
                                double requiredQuantity = -(toolTransaction.getQuantity());
                                if (requiredQuantity + pTool.getCurrentQuantity() >= 0) {
                                    V3ToolTransactionContext woTool = new V3ToolTransactionContext();
                                    woTool = setWorkorderToolObj(pTool, toolTransaction.getQuantity(), tool,
                                            toolTransaction, toolType);
                                    toolTransactionsToBeAdded.add(woTool);
                                } else {
                                    for (V3PurchasedToolContext purchasedTool : purchasedTools) {
                                        V3ToolTransactionContext woTool = new V3ToolTransactionContext();
                                        double quantityUsedForTheCost = 0;
                                        if (purchasedTool.getCurrentQuantity() + requiredQuantity >= 0) {
                                            quantityUsedForTheCost = requiredQuantity;
                                        } else {
                                            quantityUsedForTheCost = -(purchasedTool.getCurrentQuantity());
                                        }
                                        woTool = setWorkorderToolObj(purchasedTool, -(quantityUsedForTheCost), tool,
                                                toolTransaction, toolType);
                                        requiredQuantity -= quantityUsedForTheCost;
                                        toolTransactionsToBeAdded.add(woTool);
                                        if (requiredQuantity == 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    } else if (toolTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_INCREASE
                            && !toolType.isRotating()) {
                        V3PurchasedToolContext pT = toolTransaction.getPurchasedTool();
                        pT.setTool(tool);
                        pT.setToolType(toolType);
                        pT.setCostDate(System.currentTimeMillis());
                        pT.setBin(toolTransaction.getBin());
                        toolType.setLastPurchasedDate(pT.getCostDate());
                        tool.setLastPurchasedDate(pT.getCostDate());
                        toolType.setLastPurchasedPrice(pT.getUnitPrice());
                        tool.setLastPurchasedPrice(pT.getUnitPrice());
                        addPurchasedTool(purchasedToolModule, purchasedToolFields, pT);
                        V3ToolTransactionContext woTool = new V3ToolTransactionContext();
                        woTool = setWorkorderToolObj(toolTransaction.getPurchasedTool(), toolTransaction.getQuantity(), tool,
                                toolTransaction, toolType);
                        toolTransactionsToBeAdded.add(woTool);
                    }

                }

                recordMap.put(moduleName, toolTransactionsToBeAdded);
                context.put(FacilioConstants.ContextNames.BIN,binMap.values().stream().collect(Collectors.toList()));
                context.put(FacilioConstants.ContextNames.PARENT_ID, toolTransactions.get(0).getParentId());
                context.put(FacilioConstants.ContextNames.TOOL_ID, toolTransactions.get(0).getTool().getId());
                context.put(FacilioConstants.ContextNames.TOOL_IDS,
                        Collections.singletonList(toolTransactions.get(0).getTool().getId()));
                context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransactionsToBeAdded);
                context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypeId);
                context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, Collections.singletonList(toolTypeId));
                context.put(FacilioConstants.ContextNames.TRANSACTION_STATE,
                        toolTransactions.get(0).getTransactionStateEnum());
            }
        }
        return false;
    }

    private V3ToolTransactionContext setWorkorderToolObj(V3PurchasedToolContext purchasedTool, double quantity,
                                                          V3ToolContext tool, V3ToolTransactionContext toolTransactions, V3ToolTypesContext toolTypes){
        V3ToolTransactionContext woTool = new V3ToolTransactionContext();
        woTool.setTransactionState(toolTransactions.getTransactionStateEnum());
        woTool.setIsReturnable(false);
        if (purchasedTool != null) {
            woTool.setPurchasedTool(purchasedTool);
        }
        woTool.setQuantity(quantity);
        woTool.setTransactionType(TransactionType.STOCK.getValue());
        woTool.setTool(tool);
        woTool.setStoreRoom(tool.getStoreRoom());
        woTool.setBin(toolTransactions.getBin());
        woTool.setToolType(toolTypes);
        woTool.setSysModifiedTime(System.currentTimeMillis());
        woTool.setParentId(purchasedTool.getId());
        woTool.setParentTransactionId(toolTransactions.getParentTransactionId());
        woTool.setApprovedState(1);
        woTool.setRemarks(toolTransactions.getRemarks());
        woTool.setRemainingQuantity(0.0);
        return woTool;
    }

    private void addPurchasedTool(FacilioModule module, List<FacilioField> fields, V3PurchasedToolContext parts)
            throws Exception {
        InsertRecordBuilder<V3PurchasedToolContext> readingBuilder = new InsertRecordBuilder<V3PurchasedToolContext>()
                .module(module).fields(fields).addRecord(parts);
        readingBuilder.save();
    }

}

