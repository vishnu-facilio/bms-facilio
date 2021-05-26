package com.facilio.bmsconsole.migration;

import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCondition;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class ValidationConfirmationMigration {

    public static void migrateValidationCriteria() throws Exception {
        FacilioModule validationModule = ModuleFactory.getValidationModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(validationModule.getTableName())
                .select(FieldFactory.getValidationFields(validationModule));
        List<ValidationContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(), ValidationContext.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ValidationContext validationContext : list) {
                long criteriaId = validationContext.getCriteriaId();

                long namedCriteriaId = createNamedCriteria(criteriaId, validationContext.getName());
                validationContext.setNamedCriteriaId(namedCriteriaId);

                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(validationModule.getTableName())
                        .fields(FieldFactory.getValidationFields(validationModule))
                        .andCondition(CriteriaAPI.getIdCondition(validationContext.getId(), validationModule));
                updateRecordBuilder.update(FieldUtil.getAsProperties(validationContext));
            }
        }
    }

    private static long createNamedCriteria(long criteriaId, String name) throws Exception {
        NamedCriteria namedCriteria = new NamedCriteria();
        namedCriteria.setPattern("1");
        namedCriteria.setName(name);

        NamedCondition namedCondition = new NamedCondition();
        namedCondition.setName(name);
        namedCondition.setCriteriaId(criteriaId);
        namedCriteria.addCondition("1", namedCondition);

        return NamedCriteriaAPI.addOrUpdateNamedCriteria(namedCriteria);
    }

    public static void migrateConfirmationDialogCriteria() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                .select(FieldFactory.getConfirmationDialogFields());
        List<ConfirmationDialogContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(), ConfirmationDialogContext.class);

        if (CollectionUtils.isNotEmpty(list)) {
            for (ConfirmationDialogContext confirmationDialogContext : list) {
                if (confirmationDialogContext.getCriteriaId() > 0) {
                    long namedCriteriaId = createNamedCriteria(confirmationDialogContext.getCriteriaId(), confirmationDialogContext.getName());
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
