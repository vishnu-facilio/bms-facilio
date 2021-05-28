package com.facilio.bmsconsole.migration;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCondition;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ValidationConfirmationMigration {

    public static void migrateValidationCriteria() throws Exception {
        FacilioModule validationModule = ModuleFactory.getValidationModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(validationModule.getTableName())
                .select(FieldFactory.getValidationFields(validationModule))
                .andCondition(CriteriaAPI.getCondition("NAMED_CRITERIA_ID", "namedCriteriaId", "", CommonOperators.IS_EMPTY));
        List<ValidationContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(), ValidationContext.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ValidationContext validationContext : list) {
                long criteriaId = validationContext.getCriteriaId();
                long ruleId = validationContext.getRuleId();
                WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(ruleId);

                long namedCriteriaId = createNamedCriteria(criteriaId,
                        StringUtils.isNotEmpty(validationContext.getName()) ? validationContext.getName() : validationContext.getErrorMessage(),
                        workflowRule.getModuleId());
                validationContext.setNamedCriteriaId(namedCriteriaId);

                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(validationModule.getTableName())
                        .fields(FieldFactory.getValidationFields(validationModule))
                        .andCondition(CriteriaAPI.getIdCondition(validationContext.getId(), validationModule));
                updateRecordBuilder.update(FieldUtil.getAsProperties(validationContext));
            }
        }
    }

    private static long createNamedCriteria(long criteriaId, String name, long moduleId) throws Exception {
        NamedCriteria namedCriteria = new NamedCriteria();
        namedCriteria.setPattern("1");
        namedCriteria.setName(name);
        namedCriteria.setNamedCriteriaModuleId(moduleId);

        NamedCondition namedCondition = new NamedCondition();
        namedCondition.setName(name);
        namedCondition.setType(NamedCondition.Type.CRITERIA);
        namedCondition.setCriteriaId(criteriaId);
        namedCriteria.addCondition("1", namedCondition);

        return NamedCriteriaAPI.addOrUpdateNamedCriteria(namedCriteria);
    }

    public static void migrateConfirmationDialogCriteria() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                .select(FieldFactory.getConfirmationDialogFields())
                .andCondition(CriteriaAPI.getCondition("NAMED_CRITERIA_ID", "namedCriteriaId", "", CommonOperators.IS_EMPTY));;
        List<ConfirmationDialogContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(), ConfirmationDialogContext.class);

        if (CollectionUtils.isNotEmpty(list)) {
            for (ConfirmationDialogContext confirmationDialogContext : list) {
                if (confirmationDialogContext.getCriteriaId() > 0) {
                    WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(confirmationDialogContext.getParentId());
                    long namedCriteriaId = createNamedCriteria(confirmationDialogContext.getCriteriaId(), confirmationDialogContext.getName(), workflowRule.getModuleId());
                    confirmationDialogContext.setNamedCriteriaId(namedCriteriaId);

                    GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                            .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                            .fields(FieldFactory.getConfirmationDialogFields())
                            .andCondition(CriteriaAPI.getIdCondition(confirmationDialogContext.getId(),
                                    ModuleFactory.getConfirmationDialogModule()));
                    updateRecordBuilder.update(FieldUtil.getAsProperties(confirmationDialogContext));
                }
            }
        }
    }
}
