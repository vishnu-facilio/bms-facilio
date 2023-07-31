package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ExecuteAllWorkflowsCommand;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ToolQuantityRollUpCommandV3  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub

        List<? extends V3ToolTransactionContext> toolTransactions = (List<V3ToolTransactionContext>) context
                .get(FacilioConstants.ContextNames.RECORD_LIST);

        List<Long> toolIds = (List<Long>) context
                .get(FacilioConstants.ContextNames.TOOL_IDS);

        if (toolTransactions != null && !toolTransactions.isEmpty()) {
            // temp check, to be changed
            if (toolTransactions.get(0) instanceof V3ToolTransactionContext) {
                Set<Long> uniqueToolIds = new HashSet<Long>();

                for (V3ToolTransactionContext consumable : toolTransactions) {
                    if (consumable.getTransactionStateEnum() != TransactionState.USE || consumable.getParentTransactionId() <= 0) {
                        uniqueToolIds.add(consumable.getTool().getId());
                    }
                }
                constructToolAndTypeIds(uniqueToolIds, context);
            }
        }
        else if(CollectionUtils.isNotEmpty(toolIds)) {
            Set<Long> uniqueToolIds = new HashSet<Long>();
            for (Long id : toolIds) {
                uniqueToolIds.add(id);
            }
            constructToolAndTypeIds(uniqueToolIds,context);
        }
        return false;
    }

    public static void constructToolAndTypeIds(Set<Long> uniqueToolIds, Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);


        long toolTypeId = -1;
        List<Long> toolTypesIds = new ArrayList<>();
        if (uniqueToolIds != null && !uniqueToolIds.isEmpty()) {
            List<V3ToolContext> toolRecords = new ArrayList<V3ToolContext>();
            Map<Long, List<UpdateChangeSet>> changes = new HashMap<Long, List<UpdateChangeSet>>();

            for (long stId : uniqueToolIds) {
                Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(toolFields);
                List<LookupField>lookUpfields = new ArrayList<>();
                lookUpfields.add((LookupField) fieldsAsMap.get("toolType"));

                SelectRecordsBuilder<V3ToolContext> selectBuilder = new SelectRecordsBuilder<V3ToolContext>()
                        .select(toolFields).table(module.getTableName()).moduleName(module.getName())
                        .beanClass(V3ToolContext.class).andCondition(CriteriaAPI.getIdCondition(stId, module))
                        .fetchSupplements(lookUpfields)
                        ;

                List<V3ToolContext> tools = selectBuilder.get();
                V3ToolContext tool = new V3ToolContext();
                if (tools != null && !tools.isEmpty()) {
                    tool = tools.get(0);
                    toolTypeId = tool.getToolType().getId();
                    tool.setQuantity(getTotalQuantity(stId));
                    double availableQty = 0;
                    availableQty = getTotalQuantityConsumed(stId);
                    tool.setCurrentQuantity(availableQty);
                    tool.setLastPurchasedDate(System.currentTimeMillis());
                    toolTypesIds.add(tool.getToolType().getId());
                    if(tool.getCurrentQuantity()!=null &&  tool.getMinimumQuantity()!=null && (tool.getCurrentQuantity() <= tool.getMinimumQuantity())) {
                        tool.setIsUnderstocked(true);
                    }
                    else {
                        tool.setIsUnderstocked(false);
                    }
                }


                UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                        .module(module).fields(modBean.getAllFields(module.getName()))
                        .andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
                updateBuilder.withChangeSet(V3ToolContext.class);
                updateBuilder.update(tool);
                Map<Long, List<UpdateChangeSet>> recordChanges = updateBuilder.getChangeSet();
                changes.put(tool.getId(), recordChanges.get(tool.getId()));
                toolRecords.add(tool);


                context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypeId);
            }
            notifyToolOutOfStock(toolRecords,changes);

        }
        context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
    }

    private static void notifyToolOutOfStock(List<V3ToolContext> toolRecords, Map<Long, List<UpdateChangeSet>> changes) throws Exception {
        Map<String, Map<Long,List<UpdateChangeSet>>> finalChangeMap = new HashMap<String, Map<Long,List<UpdateChangeSet>>>();
        finalChangeMap.put(FacilioConstants.ContextNames.TOOL, changes);
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE,
                WorkflowRuleContext.RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE));
        FacilioContext context = c.getContext();
        context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, finalChangeMap);
        context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.TOOL, toolRecords));
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
        c.execute();
    }

    public static double getTotalQuantityConsumed(long toolId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        List<FacilioField> toolTransactionsFields = modBean
                .getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        Map<String, FacilioField> toolTransactionFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(toolTransactionsModule.getTableName())
                .andCustomWhere(
                        toolTransactionsModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(toolTransactionsModule),
                        String.valueOf(toolTransactionsModule.getModuleId()), NumberOperators.EQUALS));
//                .andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3, -1);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("addition", "sum(case WHEN TRANSACTION_STATE = 1 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("issues", "sum(case WHEN TRANSACTION_STATE = 2 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("returns", "sum(case WHEN TRANSACTION_STATE = 3 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("used", "sum(case WHEN TRANSACTION_STATE = 4 AND ( PARENT_TRANSACTION_ID <= 0 OR PARENT_TRANSACTION_ID IS NULL ) THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("transferredFrom", "sum(case WHEN TRANSACTION_STATE = 9 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        fields.add(FieldFactory.getField("transferredTo", "sum(case WHEN TRANSACTION_STATE = 10 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));

        builder.select(fields);

        builder.andCondition(CriteriaAPI.getCondition(toolTransactionFieldMap.get("tool"), String.valueOf(toolId),
                PickListOperators.IS));

        List<Map<String, Object>> rs = builder.get();
        if (rs != null && rs.size() > 0) {
            double addition = 0, issues = 0, returns = 0, used = 0, transferredFrom=0, transferredTo=0;
            addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
            issues = rs.get(0).get("issues") != null ? (double) rs.get(0).get("issues") : 0;
            used = rs.get(0).get("used") != null ? (double) rs.get(0).get("used") : 0;
            returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
            transferredFrom= rs.get(0).get("transferredFrom") != null ? (double) rs.get(0).get("transferredFrom") : 0;
            transferredTo= rs.get(0).get("transferredTo") != null ? (double) rs.get(0).get("transferredTo") : 0;
            issues += used;
            issues +=transferredFrom;

            return ((addition + returns+ transferredTo) - issues);
        }
        return 0d;
    }

    public static double getTotalQuantity(long toolId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        List<FacilioField> toolTransactionsFields = modBean
                .getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        Map<String, FacilioField> toolTransactionFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(toolTransactionsModule.getTableName())
                .andCustomWhere(
                        toolTransactionsModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(toolTransactionsModule),
                        String.valueOf(toolTransactionsModule.getModuleId()), NumberOperators.EQUALS));

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("addition", "sum(case WHEN TRANSACTION_STATE = 1 THEN QUANTITY ELSE 0 END)",
                FieldType.DECIMAL));
        builder.select(fields);

        builder.andCondition(CriteriaAPI.getCondition(toolTransactionFieldMap.get("tool"), String.valueOf(toolId),
                PickListOperators.IS));

        List<Map<String, Object>> rs = builder.get();
        if (rs != null && rs.size() > 0) {
            double addition = 0;
            addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
            return (addition);
        }
        return 0d;
    }
}
