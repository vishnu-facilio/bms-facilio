package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateToolStockTransactionsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_IDS);
        ShipmentContext shipment = (ShipmentContext) context.get(FacilioConstants.ContextNames.SHIPMENT);

        if (toolIds != null && !toolIds.isEmpty()) {
            long toolTypeId = (long) context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
            FacilioModule Toolmodule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
            List<FacilioField> Toolfields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

            SelectRecordsBuilder<ToolContext> toolselectBuilder = new SelectRecordsBuilder<ToolContext>()
                    .select(Toolfields).table(Toolmodule.getTableName()).moduleName(Toolmodule.getName())
                    .beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(toolIds, Toolmodule));

            List<ToolContext> tools = toolselectBuilder.get();
            ToolContext tool = (ToolContext) context.get(FacilioConstants.ContextNames.RECORD);
            // if (tools != null && !tools.isEmpty()) {
            // tool = tools.get(0);
            // }

            FacilioModule ToolTypemodule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
            List<FacilioField> ToolTypefields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

            SelectRecordsBuilder<ToolTypesContext> toolTypesselectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
                    .select(ToolTypefields).table(ToolTypemodule.getTableName()).moduleName(ToolTypemodule.getName())
                    .beanClass(ToolTypesContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(toolTypeId, ToolTypemodule));

            List<ToolTypesContext> toolTypes = toolTypesselectBuilder.get();
            ToolTypesContext toolType = null;
            if (toolTypes != null && !toolTypes.isEmpty()) {
                toolType = toolTypes.get(0);
            }

            if (toolType == null) {
                throw new IllegalArgumentException("No such tool found");
            }

            List<ToolTransactionContext> toolTransaction = new ArrayList<>();

            if (toolType.isRotating()) {

                List<PurchasedToolContext> pts = (List<PurchasedToolContext>) context
                        .get(FacilioConstants.ContextNames.PURCHASED_TOOL);

                if (pts != null && !pts.isEmpty()) {
                    for (PurchasedToolContext pt : pts) {
                        ToolTransactionContext transaction = new ToolTransactionContext();
                        transaction.setPurchasedTool(pt);
                        transaction.setTool(pt.getTool());
                        transaction.setStoreRoom(pt.getTool().getStoreRoom());
                        transaction.setQuantity(1);
                        transaction.setParentId(pt.getId());
                        transaction.setIsReturnable(false);
                        if (shipment == null) {
                            transaction.setTransactionType(TransactionType.STOCK.getValue());
                            transaction.setTransactionState(TransactionState.ADDITION.getValue());
                        } else {
                            transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
                            transaction.setTransactionState(TransactionState.ADDITION.getValue());
                            transaction.setShipment(shipment.getId());
                        }

                        transaction.setToolType(toolType);
                        transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);

                        SelectRecordsBuilder<ToolTransactionContext> transactionsselectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
                                .select(fields).table(module.getTableName()).moduleName(module.getName())
                                .beanClass(ToolTransactionContext.class)
                                .andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
                                        String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.IS))
                                .andCondition(CriteriaAPI.getCondition(fieldMap.get("purchasedTool"),
                                        String.valueOf(pt.getId()), PickListOperators.IS));
                        List<ToolTransactionContext> transactions = transactionsselectBuilder.get();
                        if (transactions != null && !transactions.isEmpty()) {
                            ToolTransactionContext toolTr = transactions.get(0);
                            toolTr.setQuantity(1);

                            JSONObject toolTransactionJson = FieldUtil.getAsJSON(toolTr);
                            Long toolTransactionId = toolTr.getId();
                            V3Util.updateBulkRecords(module.getName(),FacilioUtil.getAsMap(toolTransactionJson), Collections.singletonList(toolTransactionId),false);
                        } else {
                            toolTransaction.add(transaction);
                        }
                    }
                }
            } else {
                ToolTransactionContext transaction = new ToolTransactionContext();
                transaction.setTool(tool);
                transaction.setStoreRoom(tool.getStoreRoom());
                transaction.setQuantity(tool.getQuantity());
                transaction.setParentId(tool.getId());
                transaction.setIsReturnable(false);
                transaction.setToolType(toolType);
                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);

                if (shipment == null) {
                    transaction.setTransactionType(TransactionType.STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                } else {
                    transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                    transaction.setShipment(shipment.getId());
                }
                toolTransaction.add(transaction);

                /*
                 * SelectRecordsBuilder<ToolTransactionContext>
                 * transactionsselectBuilder = new
                 * SelectRecordsBuilder<ToolTransactionContext>()
                 * .select(fields).table(module.getTableName()).moduleName(
                 * module.getName()) .beanClass(ToolTransactionContext.class)
                 * .andCondition(CriteriaAPI.getCondition(fieldMap.get(
                 * "transactionState"),
                 * String.valueOf(TransactionState.ADDITION.getValue()),
                 * EnumOperators.IS)) ;
                 *
                 * List<ToolTransactionContext> transactions =
                 * transactionsselectBuilder.get(); if (transactions != null &&
                 * !transactions.isEmpty()) { ToolTransactionContext it =
                 * transactions.get(0); it.setQuantity(tool.getQuantity());
                 * UpdateRecordBuilder<ToolTransactionContext> updateBuilder =
                 * new UpdateRecordBuilder<ToolTransactionContext>()
                 * .module(module).fields(modBean.getAllFields(module.getName())
                 * ) .andCondition(CriteriaAPI.getIdCondition(it.getId(),
                 * module)); updateBuilder.update(it); } else {
                 * toolTransaction.add(transaction); }
                 */
            }
            V3Util.createRecordList(module, FieldUtil.getAsMapList(toolTransaction,V3ToolTransactionContext.class),null,null);
            context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
        } else {
            V3ToolContext tool = (V3ToolContext) context.get(FacilioConstants.ContextNames.TOOL);
            V3AssetContext asset = (V3AssetContext) context.get(FacilioConstants.ContextNames.ROTATING_ASSET);
            if (tool != null) {
                V3ToolContext t = V3ToolsApi.getTool(tool.getId());
                double q = t.getQuantity() >= 0 ? t.getQuantity() : 0;
                q += 1;
                t.setQuantity(q);
                V3ToolTransactionContext transaction = new V3ToolTransactionContext();
                transaction.setTool(t);
                transaction.setStoreRoom(t.getStoreRoom());
                transaction.setQuantity(1.0);
                transaction.setParentId(t.getId());
                transaction.setIsReturnable(false);
                transaction.setToolType(t.getToolType());
                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                transaction.setAsset(asset);
                if (shipment == null) {
                    transaction.setTransactionType(TransactionType.STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                } else {
                    transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                    transaction.setShipment(shipment.getId());
                }

                updateToolQty(t);
                V3Util.createRecord(module, FacilioUtil.getAsMap(FieldUtil.getAsJSON(transaction)),null,null);

                context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(transaction));
                context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, Collections.singletonList(t.getToolType().getId()));
            }
        }
        return false;
    }

    private void updateToolQty(V3ToolContext tool) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);

        UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>().module(module)
                .fields(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
        updateBuilder.update(tool);
    }
}
