package com.facilio.readingrule.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingrule.context.*;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class NewReadingRuleAPI {

    public static final String READING_RULE_FIELD_TABLE_NAME = "Rule_Readings";

    public static void addReadingRule(NewReadingRuleContext ruleCtx) throws Exception {
        ruleCtx.setStatus(true);
        updateModuleAndFields(ruleCtx);
        Map<String, Object> fieldMap = FieldUtil.getAsProperties(ruleCtx);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getNewReadingRuleModule().getTableName())  //Table: New_Reading_Rule
                .fields(FieldFactory.getNewReadingRuleFields());
        long id = insertBuilder.insert(fieldMap);
        ruleCtx.setId(id);
        addAlarmDetails(ruleCtx);
        addRuleBuilder(ruleCtx);

    }

    private static void updateModuleAndFields(NewReadingRuleContext ruleCtx) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule ruleModule = modBean.getModule(ruleCtx.getModuleId());
        ruleCtx.setModule(ruleModule);
        List<FacilioField> field = modBean.getAllFields(ruleModule.getName());
        List<FacilioField> customFields = field.stream().filter(f -> f.getDisplayName().equals(ruleCtx.getName())).collect(Collectors.toList());
        if (!customFields.isEmpty()) {
            ruleCtx.setReadingField(customFields.get(0));
            ruleCtx.setFieldId(customFields.get(0).getFieldId());
        }
    }

    private static void addAlarmDetails(NewReadingRuleContext ruleCtx) throws Exception {
        RuleAlarmDetails alarmDetails = ruleCtx.getAlarmDetails();
        alarmDetails.setRuleId(ruleCtx.getId());
        AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(alarmDetails.getSeverity());
        Objects.requireNonNull(alarmSeverity, "Invalid severity value found!");
        alarmDetails.setSeverityId(alarmSeverity.getId());

        Map<String, Object> props = FieldUtil.getAsProperties(alarmDetails);

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getRuleAlarmDetailsModule().getTableName())
                .fields(FieldFactory.getRuleAlarmDetailsFields());
        long id = builder.insert(props);
        alarmDetails.setId(id);
    }

    private static void addRuleBuilder(NewReadingRuleContext ruleCtx) throws Exception {
        RuleBuilderConfiguration alarmConditionConfig = ruleCtx.getCondition();
        alarmConditionConfig.setRuleId(ruleCtx.getId());

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getRuleBuilderConfigModule().getTableName())
                .fields(FieldFactory.getRuleBuilderConfigFields());
        long id = insertBuilder.insert(FieldUtil.getAsProperties(alarmConditionConfig));
        ruleCtx.getCondition().setId(id);
    }

    public static List<NewReadingRuleContext> getRules() throws Exception {
        return getRules(new Condition[]{});
    }

    public static List<NewReadingRuleContext> getRules(@NonNull Condition[] conditions) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
        select.select(FieldFactory.getNewReadingRuleFields())
                .table(ModuleFactory.getNewReadingRuleModule().getTableName());
        if (conditions.length > 0) {
            Arrays.stream(conditions).forEach(c -> select.andCondition(c));
        }
        List<Map<String, Object>> maps = select.get();
        if (maps == null) {
            return null;
        }
        return getReadingRulesFromMap(maps);
    }

    private static List<NewReadingRuleContext> getReadingRulesFromMap(@NonNull List<Map<String, Object>> rulesMap) throws Exception {
        List<NewReadingRuleContext> rules = new ArrayList<>();
        for (Map<String, Object> ruleProps : rulesMap) {
            NewReadingRuleContext readingRule = FieldUtil.getAsBeanFromMap(ruleProps, NewReadingRuleContext.class);
            updateModuleAndFields(readingRule);
            fetchAndUpdateAlarmDetails(readingRule);
            updateRuleBuilderConfig(readingRule);
            rules.add(readingRule);
        }
        return rules;
    }

    private static void fetchAndUpdateAlarmDetails(NewReadingRuleContext readingRule) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(FieldFactory.getRuleAlarmDetailsFields())
                .table(ModuleFactory.getRuleAlarmDetailsModule().getTableName()) //Table:New_Reading_Rule_AlarmDetails
                .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(readingRule.getId()), NumberOperators.EQUALS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if (props != null) {
            RuleAlarmDetails ruleBuilderConfig = FieldUtil.getAsBeanFromMap(props, RuleAlarmDetails.class);
            readingRule.setAlarmDetails(ruleBuilderConfig);
        }
    }

    private static void updateRuleBuilderConfig(@NonNull NewReadingRuleContext readingRule) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(FieldFactory.getRuleBuilderConfigFields()) //Table:Rule_Builder_Config
                .table(ModuleFactory.getRuleBuilderConfigModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(readingRule.getId()), NumberOperators.EQUALS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if (props != null) {
            RuleBuilderConfiguration ruleBuilderConfig = FieldUtil.getAsBeanFromMap(props, RuleBuilderConfiguration.class);
            fetchAndUpdateNameSpace(ruleBuilderConfig);
            readingRule.setCondition(ruleBuilderConfig);
        }

    }

    private static void fetchAndUpdateNameSpace(RuleBuilderConfiguration ruleBuilderConfig) throws Exception {
        GenericSelectRecordBuilder selBuilder = new GenericSelectRecordBuilder();
        selBuilder.select(NamespaceModuleAndFieldFactory.getNamespaceFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", ruleBuilderConfig.getId().toString(), NumberOperators.EQUALS));
        Map<String, Object> props = selBuilder.fetchFirst();
        NameSpaceContext ruleGrpContext = FieldUtil.getAsBeanFromMap(props, NameSpaceContext.class);
        List<NameSpaceField> ruleGroupFields = getNameSpaceFields(ruleGrpContext);
        ruleGrpContext.setFields(ruleGroupFields);
        ruleBuilderConfig.setNs(ruleGrpContext);
    }

    private static List<NameSpaceField> getNameSpaceFields(NameSpaceContext nsCtx) throws Exception {
        List<NameSpaceField> fields = new ArrayList<>();
        GenericSelectRecordBuilder selBuilder = new GenericSelectRecordBuilder();
        selBuilder.select(NamespaceModuleAndFieldFactory.getNamespaceFieldFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("NAMESPACE_ID", "nsId", nsCtx.getId().toString(), NumberOperators.EQUALS));
        List<Map<String, Object>> fieldsProps = selBuilder.get();

        for (Map<String, Object> fieldProps : fieldsProps) {
            NameSpaceField ruleGrpField = FieldUtil.getAsBeanFromMap(fieldProps, NameSpaceField.class);
            ruleGrpField.setNamespace(nsCtx);
            ruleGrpField.setFieldKey(null);
            fields.add(ruleGrpField);
        }
        return fields;
    }

    public static NewReadingRuleContext getRuleForBuilderId(Long builderId) throws Exception {
        List<NewReadingRuleContext> rulesForBuilderId = getRulesByBuilderId(builderId);
        return CollectionUtils.isNotEmpty(rulesForBuilderId) ? rulesForBuilderId.get(0) : null;
    }

    public static List<NewReadingRuleContext> getRulesByBuilderId(Long builderId) throws Exception {
        FacilioModule builderConfigModule = ModuleFactory.getRuleBuilderConfigModule();
        FacilioModule ruleModule = ModuleFactory.getNewReadingRuleModule();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getNewReadingRuleFields())
                .table(ruleModule.getTableName())
                .innerJoin(ModuleFactory.getRuleBuilderConfigModule().getTableName())
                .on(builderConfigModule.getTableName() + ".RULE_ID=" + ruleModule.getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition(builderConfigModule.getTableName() + ".ID", "builderId", builderId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STATUS", "status", "true", BooleanOperators.IS));

        List<Map<String, Object>> maps = select.get();
        return (maps != null) ? getReadingRulesFromMap(maps) : null;
    }

    public static NewReadingRuleContext getRule(Long ruleId) throws Exception {
        List<NewReadingRuleContext> rules = getRules(new Condition[]{CriteriaAPI.getCondition("STATUS", "status", "true", BooleanOperators.IS),
                CriteriaAPI.getIdCondition(ruleId, ModuleFactory.getNewReadingRuleModule())});
        return CollectionUtils.isNotEmpty(rules) ? rules.get(0) : null;
    }
}
