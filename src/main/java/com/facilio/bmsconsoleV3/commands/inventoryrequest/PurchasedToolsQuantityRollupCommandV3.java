package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.*;

import static com.facilio.bmsconsoleV3.commands.inventoryrequest.PurchasedItemsQuantityRollUpCommandV3.rollupAllTransactions;

public class PurchasedToolsQuantityRollupCommandV3 extends FacilioCommand {

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> toolTransactionsFields = modBean
                .getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        Map<String, FacilioField> toolTransactionsFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

        List<FacilioField> fields = new ArrayList<FacilioField>();

        FacilioField totalQuantityConsumedField = new FacilioField();
        totalQuantityConsumedField.setName("totalQuantityConsumed");
        totalQuantityConsumedField.setColumnName("sum(" + toolTransactionsFieldMap.get("quantityConsumed") + ")");
        fields.add(totalQuantityConsumedField);

        List<? extends V3ToolTransactionContext> toolTransactionContext = (List<V3ToolTransactionContext>) context
                .get(FacilioConstants.ContextNames.RECORD_LIST);
        Set<Long> uniquePurchasedToolIds = new HashSet<Long>();
        Set<Long> uniqueAssetId = new HashSet<Long>();
        Set<Long> uniqueToolIds = new HashSet<Long>();
        if (toolTransactionContext != null && !toolTransactionContext.isEmpty()) {
            for (V3ToolTransactionContext consumable : toolTransactionContext) {
                if (consumable.getTransactionStateEnum() != TransactionState.USE || consumable.getParentTransactionId() <= 0) {
                    if (consumable.getPurchasedTool() != null) {
                        uniquePurchasedToolIds.add(consumable.getPurchasedTool().getId());
                    } else if (consumable.getAsset() != null) {
                        uniqueAssetId.add(consumable.getAsset().getId());
                    }
                    uniqueToolIds.add(consumable.getTool().getId());
                }
            }
        }

        if (!uniquePurchasedToolIds.isEmpty()) {
            FacilioModule purchasedToolModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
            List<FacilioField> purchasedToolFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
            for (Long id : uniquePurchasedToolIds) {
                Map<String, Double> quantityRollup = rollupAllTransactions(id, "purchasedTool",FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
                double currentQuantity = quantityRollup.get(FacilioConstants.ContextNames.CURRENT_QUANTITY);
                double adjustmentDecrease = quantityRollup.get(FacilioConstants.ContextNames.ADJUSTMENT_DECREASE);
                V3PurchasedToolContext purchasedTool = new V3PurchasedToolContext();
                SelectRecordsBuilder<V3PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedToolContext>()
                        .select(purchasedToolFields).table(purchasedToolModule.getTableName())
                        .moduleName(purchasedToolModule.getName()).beanClass(V3PurchasedToolContext.class)
                        .andCondition(CriteriaAPI.getIdCondition(id, purchasedToolModule));
                List<V3PurchasedToolContext> purchasedTools = selectBuilder.get();
                if (purchasedTools != null && !purchasedTools.isEmpty()) {
                    purchasedTool = purchasedTools.get(0);
                    purchasedTool.setCurrentQuantity(currentQuantity);
                    Double totalQuantity = purchasedTool.getQuantity() - adjustmentDecrease;
                    purchasedTool.setQuantity(totalQuantity);
                    UpdateRecordBuilder<V3PurchasedToolContext> updateBuilder = new UpdateRecordBuilder<V3PurchasedToolContext>()
                            .module(purchasedToolModule).fields(modBean.getAllFields(purchasedToolModule.getName()))
                            .andCondition(CriteriaAPI.getIdCondition(id, purchasedToolModule));
                    updateBuilder.update(purchasedTool);
                }
            }
            List<Long> inventoryIds = new ArrayList<Long>();
            inventoryIds.addAll(uniqueToolIds);
            context.put(FacilioConstants.ContextNames.TOOL_IDS, inventoryIds);
        } else if (uniqueAssetId != null && !uniqueAssetId.isEmpty()) {
            Set<Long> uniqueToolTypeIds = new HashSet<Long>();
            FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
            for (Long id : uniqueToolIds) {
                Map<String, Double> quantityRollup = rollupAllTransactions(id, "tool",FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
                double totalConsumed = quantityRollup.get(FacilioConstants.ContextNames.CURRENT_QUANTITY);
                V3ToolContext tool = V3ToolsApi.getTool(id);
                tool.setQuantity(totalConsumed);
                tool.setCurrentQuantity(totalConsumed);
                UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                        .module(toolModule).fields(modBean.getAllFields(toolModule.getName()))
                        .andCondition(CriteriaAPI.getIdCondition(id, toolModule));
                updateBuilder.update(tool);
                uniqueToolTypeIds.add(tool.getToolType().getId());
            }
            List<Long> toolTypeIds = new ArrayList<Long>();
            toolTypeIds.addAll(uniqueToolTypeIds);
            context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypeIds);
            context.put(FacilioConstants.ContextNames.TOOL_IDS, null);
        }
        return false;
    }
    public static Map<String, Double> rollupAllTransactions(long inventoryCostId, String fieldName, String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule consumableModule = modBean.getModule(moduleName);
        List<FacilioField> consumableFields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> consumableFieldMap = FieldFactory.getAsMap(consumableFields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(consumableModule.getTableName())
                .andCustomWhere(consumableModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(consumableModule),String.valueOf(consumableModule.getModuleId()), NumberOperators.EQUALS));
//                .andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3);
        // builder.andCondition(CriteriaAPI.getCondition(consumableFieldMap.get("approvedState"),
        // String.valueOf(1), NumberOperators.EQUALS))
        // .orCondition(CriteriaAPI.getCondition(consumableFieldMap.get("approvedState"),
        // String.valueOf(3), NumberOperators.EQUALS));

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("addition", "sum(case WHEN TRANSACTION_STATE = 1 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("issues", "sum(case WHEN TRANSACTION_STATE = 2 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("used", "sum(case WHEN TRANSACTION_STATE = 4 AND (PARENT_TRANSACTION_ID <= 0 OR PARENT_TRANSACTION_ID IS NULL) THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("returns", "sum(case WHEN TRANSACTION_STATE = 3 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("adjustments_increase", "sum(case WHEN TRANSACTION_STATE = 7 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("adjustments_decrease", "sum(case WHEN TRANSACTION_STATE = 8 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("transferredFrom", "sum(case WHEN TRANSACTION_STATE = 9 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("transferredTo", "sum(case WHEN TRANSACTION_STATE = 10 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("hardReserve", "sum(case WHEN TRANSACTION_STATE = 11 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        builder.select(fields);

        builder.andCondition(CriteriaAPI.getCondition(consumableFieldMap.get(fieldName),
                String.valueOf(inventoryCostId), PickListOperators.IS));

        List<Map<String, Object>> rs = builder.get();

        Double currentQuantity = 0.0;
        Double adjustments_decrease = 0.0;
        if (rs != null && rs.size() > 0) {
            double addition = 0, issues = 0, returns = 0, used = 0 , adjustments_increase = 0, transferredFrom=0, transferredTo=0, hardReserve=0;
            addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
            issues = rs.get(0).get("issues") != null ? (double) rs.get(0).get("issues") : 0;
            returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
            used = rs.get(0).get("used") != null ? (double) rs.get(0).get("used") : 0;
            adjustments_decrease = rs.get(0).get("adjustments_decrease") != null ? (double) rs.get(0).get("adjustments_decrease") : 0;
            adjustments_increase = rs.get(0).get("adjustments_increase") != null ? (double) rs.get(0).get("adjustments_increase") : 0;
            transferredFrom= rs.get(0).get("transferredFrom") != null ? (double) rs.get(0).get("transferredFrom") : 0;
            transferredTo= rs.get(0).get("transferredTo") != null ? (double) rs.get(0).get("transferredTo") : 0;
            hardReserve = rs.get(0).get("hardReserve") != null ? (double) rs.get(0).get("hardReserve") : 0;
            issues += used;
            issues +=transferredFrom;

            currentQuantity = ((addition + returns + adjustments_increase + transferredTo) - issues - adjustments_decrease - hardReserve);
        }
        Map<String,Double> rollupQuantity = new HashMap<>();
        rollupQuantity.put(FacilioConstants.ContextNames.CURRENT_QUANTITY,currentQuantity);
        rollupQuantity.put(FacilioConstants.ContextNames.ADJUSTMENT_DECREASE,adjustments_decrease);
        return rollupQuantity;
    }
}
