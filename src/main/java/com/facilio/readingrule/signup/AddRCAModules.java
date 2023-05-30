package com.facilio.readingrule.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddRCAModules extends SignUpData {

    @Override
    public void addData() throws Exception {
        try {
            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(addRootCauseModule()));
            addModuleChain.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FacilioChain addGroupModule = TransactionChainFactory.addSystemModuleChain();
            addGroupModule.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(addRCAGroupModule()));
            addGroupModule.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FacilioChain addRCAConditionScoreModule = TransactionChainFactory.addSystemModuleChain();
            addRCAConditionScoreModule.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(addRCAConditionScoreModule()));
            addRCAConditionScoreModule.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FacilioChain addRCAReadingsModule = TransactionChainFactory.addSystemModuleChain();
            addRCAReadingsModule.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(addRCAReadingsModule()));
            addRCAReadingsModule.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FacilioChain addRCAFaultRelModule = TransactionChainFactory.addSystemModuleChain();
            addRCAFaultRelModule.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(addRCAFaultRelModule()));
            addRCAFaultRelModule.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private FacilioModule addRootCauseModule() throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.ReadingRules.RCA.RCA_MODULE,
                "ReadingRuleRCA",
                "ReadingRule_RCA",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField ruleId = FieldFactory.getDefaultField("ruleId", "Reading Rule Id", "RULE_ID", FieldType.NUMBER);
        fields.add(ruleId);

        NumberField dataInterval = FieldFactory.getDefaultField("dataSetInterval", "DataSetInterval", "DATA_SET_INTERVAL", FieldType.NUMBER);
        fields.add(dataInterval);

        NumberField ruleInterval = FieldFactory.getDefaultField("ruleInterval", "RuleInterval", "RULE_INTERVAL", FieldType.NUMBER);
        fields.add(ruleInterval);

        module.setFields(fields);
        return module;
    }

    private FacilioModule addRCAGroupModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE,
                "Reading Rule RCA Group",
                "ReadingRule_RCA_Group",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("rcaId", "RCA ID", "RCA_ID", FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));

        fields.add(FieldFactory.getDefaultField("desc", "Description", "DESCRIPTION", FieldType.STRING));

        fields.add(FieldFactory.getDefaultField("criteriaId", "Criteria Id", "CRITERIA_ID", FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN));

        module.setFields(fields);

        return module;
    }

    private FacilioModule addRCAConditionScoreModule() {
        FacilioModule module = new FacilioModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE,
                "Reading Rule RCA Condition Score",
                "ReadingRule_RCA_Condition_Score",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField rcaGrpId = FieldFactory.getDefaultField("groupId", "RCA Group Id", "RCA_GROUP_ID", FieldType.NUMBER, true);
        fields.add(rcaGrpId);

        NumberField criteriaId = FieldFactory.getDefaultField("criteriaId", "Criteria Id", "CRITERIA_ID", FieldType.NUMBER);
        fields.add(criteriaId);

        NumberField score = FieldFactory.getDefaultField("score", "Score", "SCORE", FieldType.NUMBER);
        fields.add(score);

        module.setFields(fields);
        return module;
    }

    private FacilioModule addRCAReadingsModule() throws Exception {

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS_MODULE,
                "Reading Rule RCA Readings",
                "ReadingRule_RCA_Score_Readings",
                FacilioModule.ModuleType.BASE_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField parentIdField = FieldFactory.getDefaultField("parentId", "Fault Id", "PARENT_ID", FieldType.NUMBER, true);
        fields.add(parentIdField);

        NumberField rcaRuleIdField = FieldFactory.getDefaultField("rcaRuleId", "RCA Rule", "RCA_RULE_ID", FieldType.NUMBER);
        fields.add(rcaRuleIdField);

        NumberField rcaFaultIdField = FieldFactory.getDefaultField("rcaFaultId", "RCA Fault", "RCA_FAULT_ID", FieldType.NUMBER);
        fields.add(rcaFaultIdField);

        NumberField rcaGroupIdField = FieldFactory.getDefaultField("rcaGroupId", "RCA Group Id", "RCA_GROUP_ID", FieldType.NUMBER);
        fields.add(rcaGroupIdField);

        NumberField rcaConditionIdField = FieldFactory.getDefaultField("rcaConditionId", "RCA Condition Id", "RCA_CONDITION_ID", FieldType.NUMBER);
        fields.add(rcaConditionIdField);

        NumberField score = FieldFactory.getDefaultField("score", "RCA Score", "SCORE", FieldType.NUMBER);
        fields.add(score);

        List<FacilioField> defaultReadingFields = FieldFactory.getDefaultReadingFields(module);
        defaultReadingFields.removeIf(field -> field.getName().equals("parentId"));
        fields.addAll(defaultReadingFields);

        module.setFields(fields);

        return module;
    }

    private FacilioModule addRCAFaultRelModule() throws Exception {

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule("rcafaultrel",
                "Reading Rule RCA Fault Relationship",
                "ReadingRule_RCA_Fault_Rel",
                FacilioModule.ModuleType.BASE_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField parentIdField = FieldFactory.getDefaultField("faultId", "Fault Id", "FAULT_ID", FieldType.LOOKUP, true);
        parentIdField.setLookupModule(bean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM));
        fields.add(parentIdField);

        LookupField rcaFaultField = FieldFactory.getDefaultField("rcaFaultId", "RCA Fault Id", "RCA_FAULT_ID", FieldType.LOOKUP);
        rcaFaultField.setLookupModule(bean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM));
        fields.add(rcaFaultField);

        module.setFields(fields);


        return module;
    }
}
