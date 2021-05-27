package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfirmationDialogAPI {

    public static void addConfirmationDialogs(List<ConfirmationDialogContext> confirmationDialogs, long parentId) throws Exception {
        if (CollectionUtils.isEmpty(confirmationDialogs)) {
            return;
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                .fields(FieldFactory.getConfirmationDialogFields());
        for (ConfirmationDialogContext confirmationDialogContext : confirmationDialogs) {
            confirmationDialogContext.setParentId(parentId);
            if (confirmationDialogContext.getCriteria() != null) { // backward compatibility
                WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(confirmationDialogContext.getParentId());
                NamedCriteria namedCriteria = NamedCriteriaAPI.convertCriteriaToNamedCriteria(
                        confirmationDialogContext.getName(),
                        workflowRule.getModuleId(),
                        confirmationDialogContext.getCriteria());
                confirmationDialogContext.setNamedCriteriaId(namedCriteria.getId());
            }
            builder.addRecord(FieldUtil.getAsProperties(confirmationDialogContext));
        }
        builder.save();

        List<Map<String, Object>> records = builder.getRecords();
        for (int i = 0; i < records.size(); i++) {
            confirmationDialogs.get(i).setId((long) records.get(i).get("id"));
        }
    }

    public static List<ConfirmationDialogContext> getConfirmationDialogs(long parentId, boolean fetchChildren) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                .select(FieldFactory.getConfirmationDialogFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
        List<ConfirmationDialogContext> confirmationDialogs = FieldUtil.getAsBeanListFromMapList(builder.get(), ConfirmationDialogContext.class);
        if (fetchChildren && CollectionUtils.isNotEmpty(confirmationDialogs)) {
            List<Long> namedCriteriaIds = confirmationDialogs.stream().filter(confirmationDialog -> confirmationDialog.getNamedCriteriaId() > 0)
                    .map(ConfirmationDialogContext::getNamedCriteriaId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(namedCriteriaIds)) {
                Map<Long, NamedCriteria> criteriaMap = NamedCriteriaAPI.getCriteriaAsMap(namedCriteriaIds);

                for (ConfirmationDialogContext dialogContext : confirmationDialogs) {
                    if (dialogContext.getNamedCriteriaId() > 0) {
                        NamedCriteria namedCriteria = criteriaMap.get(dialogContext.getNamedCriteriaId());
                        dialogContext.setNamedCriteria(namedCriteria);

                        Criteria criteria = NamedCriteriaAPI.convertNamedCriteriaToCriteria(namedCriteria);
                        dialogContext.setCriteria(criteria);
                        dialogContext.setCriteriaId(criteria.getCriteriaId());
                    }
                }
            }
        }
        return confirmationDialogs;
    }

    public static void deleteConfirmationDialogs(long parentId) throws Exception {
        List<ConfirmationDialogContext> confirmationDialogs = getConfirmationDialogs(parentId, false);

        if (CollectionUtils.isNotEmpty(confirmationDialogs)) {
            List<Long> dialogIds = confirmationDialogs.stream().map(ConfirmationDialogContext::getId).collect(Collectors.toList());
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(dialogIds, ModuleFactory.getConfirmationDialogModule()));
            builder.delete();
        }
    }
}
