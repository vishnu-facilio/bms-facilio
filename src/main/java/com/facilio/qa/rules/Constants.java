package com.facilio.qa.rules;

import com.facilio.fw.BeanFactory;
import com.facilio.modules.BaseFieldFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.rules.bean.QAndARuleBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constants {
    public static QAndARuleBean getRuleBean() throws IllegalAccessException, InstantiationException {
        return (QAndARuleBean) BeanFactory.lookup("QAndARuleBean");
    }

    public static class Command {
        public static final String QUESTION_TYPE_VS_OPERATORS = "questionTypeVsOperators";
        public static final String RULE_TYPE = "qandaRuleType";
        public static final String RULES = "qandaRules";
        public static final String RULES_TO_BE_ADDED = "qandaRulesToBeAdded";
        public static final String RULES_TO_BE_UPDATED = "qandaRulesToBeUpdated";
        public static final String CONDITIONS_TO_BE_DELETED = "qandaRuleConditionsToBeDeleted";
        public static final String ACTIONS_TO_BE_ADDED = "actionsToBeAdded";
        public static final String BASESCHEDULES = "baseSchedules";
    }

    public static class ModuleFactory {
        public static final FacilioModule qandaRuleModule() {
            return new FacilioModule("qandaRule", "Q And A Rules", "Q_And_A_Rules");
        }
        public static final FacilioModule ruleConditionModule() {
            return new FacilioModule("qandaScoringRuleCondition", "Q And A Rule Conditions", "Q_And_A_Rule_Conditions");
        }

        public static final FacilioModule scoringRuleModule() {
            return new FacilioModule("qandaScoringRule", "Q And A Scoring Rules", "Q_And_A_Scoring_Rules", null, qandaRuleModule());
        }
        public static final FacilioModule scoringRuleConditionModule() {
            return new FacilioModule("qandaScoringRuleCondition", "Q And A Scoring Rule Conditions", "Q_And_A_Scoring_Rule_Conditions", null, ruleConditionModule());
        }
        public static final FacilioModule evalRuleActionRelModule() {
            return new FacilioModule("evalRuleActionRel", "Eval Rule Action Rel", "Eval_Rule_Action_Rel");
        }
    }

    public static class FieldFactory extends BaseFieldFactory {

        private static void addSysFields (FacilioModule module, List<FacilioField> fields) {
            fields.add(getField("sysCreatedTime", "SYS_CREATED_TIME", module, FieldType.DATE_TIME));
            fields.add(getField("sysCreatedBy", "SYS_CREATED_BY", module, FieldType.NUMBER));
            fields.add(getField("sysModifiedTime", "SYS_MODIFIED_TIME", module, FieldType.DATE_TIME));
            fields.add(getField("sysModifiedBy", "SYS_MODIFIED_BY", module, FieldType.NUMBER));
        }

        public static final List<FacilioField> qandaRuleFields() {
            List<FacilioField> fields = new ArrayList<>();
            FacilioModule module = ModuleFactory.qandaRuleModule();

            fields.add(getIdField(module));
            fields.add(getField("templateId", "TEMPLATE_ID", module, FieldType.NUMBER));
            fields.add(getField("questionId", "QUESTION_ID", module, FieldType.NUMBER));
            fields.add(getField("type", "RULE_TYPE", module, FieldType.STRING));
            addSysFields(module, fields);

            return Collections.unmodifiableList(fields);
        }

        public static final List<FacilioField> evalRuleActionRelFields() {
            List<FacilioField> fields = new ArrayList<>();
            FacilioModule module = ModuleFactory.evalRuleActionRelModule();

            fields.add(getIdField(module));
            fields.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));
            fields.add(getField("conditionId","Q_AND_A_RULE_CONDITION_ID",module,FieldType.NUMBER));
            fields.add(getField("actionId", "ACTION_ID", module, FieldType.NUMBER));
            return fields;
        }

        public static final List<FacilioField> ruleConditionFields() {
            List<FacilioField> fields = new ArrayList<>();
            FacilioModule module = ModuleFactory.ruleConditionModule();

            fields.add(getIdField(module));
            fields.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));
            fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.NUMBER));
            fields.add(getField("sequence", "SEQUENCE_VAL", module, FieldType.NUMBER));
            fields.add(getField("operator", "OPERATOR_ID", module, FieldType.NUMBER));
            fields.add(getField("value", "VAL", module, FieldType.STRING));
			fields.add(getField("rowId","ROW_ID",module,FieldType.NUMBER));
			fields.add(getField("columnId","COLUMN_ID",module,FieldType.NUMBER));
            addSysFields(module, fields);

            return Collections.unmodifiableList(fields);
        }

        public static final List<FacilioField> scoringRuleFields() {
            List<FacilioField> fields = new ArrayList<>();
            FacilioModule module = ModuleFactory.scoringRuleModule();

            fields.add(getIdField(module));
            fields.add(getField("fullScore", "FULL_SCORE", module, FieldType.DECIMAL));
            fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.NUMBER));
            fields.addAll(qandaRuleFields());

            return Collections.unmodifiableList(fields);
        }

        public static final List<FacilioField> scoringRuleConditionFields() {
            List<FacilioField> fields = new ArrayList<>();
            FacilioModule module = ModuleFactory.scoringRuleConditionModule();

            fields.add(getIdField(module));
            fields.add(getField("score", "SCORE", module, FieldType.DECIMAL));
            fields.addAll(ruleConditionFields());

            return Collections.unmodifiableList(fields);
        }

    }
}
