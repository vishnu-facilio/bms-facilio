package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class UpdateReadingRuleCommand extends FacilioCommand {
    NewReadingRuleContext rule;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.rule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);

        updateReadingRule();
        updateAlarmDetails();
        updateNamespace();
        updateNamespaceFields();

        return false;
    }

    private void updateReadingRule() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, SQLException {
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getNewReadingRuleFields())
                .table(ModuleFactory.getNewReadingRuleModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(rule.getId()), NumberOperators.EQUALS));
        updateBuilder.update(FieldUtil.getAsProperties(rule));
    }

    private void updateAlarmDetails() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, SQLException {
        if(rule.getAlarmDetails() != null) {
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .fields(FieldFactory.getRuleAlarmDetailsFields())
                    .table(ModuleFactory.getRuleAlarmDetailsModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(rule.getAlarmDetails().getId()), NumberOperators.EQUALS));
            updateBuilder.update(FieldUtil.getAsProperties(rule.getAlarmDetails()));
        }
    }

    private void updateNamespace() throws Exception {
        NameSpaceContext ns = rule.getNs();
        if(ns != null) {
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .fields(NamespaceModuleAndFieldFactory.getNamespaceFields())
                    .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(ns.getId()), NumberOperators.EQUALS));
            updateBuilder.update(FieldUtil.getAsProperties(ns));
        }
    }

    private void updateNamespaceFields() throws Exception {
        List<NameSpaceField> fields = rule.getNs().getFields();
        NewReadingRuleAPI.addNamespaceFields(rule.getNs().getId(), rule.getMatchedResources(), fields);
    }
}
