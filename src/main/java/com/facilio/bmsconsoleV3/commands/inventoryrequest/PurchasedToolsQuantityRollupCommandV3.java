package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
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

}
