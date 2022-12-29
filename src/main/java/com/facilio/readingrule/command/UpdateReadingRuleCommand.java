package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateReadingRuleCommand extends FacilioCommand {
    NewReadingRuleContext rule;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.rule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);

        updateAlarmDetails();
        updateNamespace();
        updateNamespaceFields();
        updateNamespaceStatus();
        updateNamespaceInclusions();


        return false;
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
    private void updateNamespaceStatus() throws Exception {
            List<NSType> nsTypeList = new ArrayList<>();
            nsTypeList.add(NSType.READING_RULE);
            nsTypeList.add(NSType.FAULT_IMPACT_RULE);
            NamespaceAPI.updateNsStatus(rule.getId(), rule.getStatus(), nsTypeList);
    }
    private void updateNamespaceInclusions() throws Exception {
        List<Long> includedAssetIds = rule.getNs().getIncludedAssetIds();
        NameSpaceContext ns = this.rule.getNs();
        if(CollectionUtils.isEmpty(includedAssetIds) ) {
            NamespaceAPI.deleteExistingInclusionRecords(ns);
        }
        NamespaceAPI.addInclusions(ns);
    }
}
