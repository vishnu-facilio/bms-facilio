package com.facilio.alarms.sensor.util;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorAlarmDetailsContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewSensorRuleUtil {
    public static List<SensorRuleContext> fetchSensorRulesByModule(String moduleName, boolean isFetchSubProps, boolean isHistorical) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingModule = modBean.getModule(moduleName);
        FacilioModule categoryModule = modBean.getParentModule(readingModule.getModuleId());

        if (categoryModule != null) {
            List<SensorRuleContext> sensorRules = getSensorRuleByModuleId(categoryModule, isFetchSubProps, isHistorical);
            return sensorRules;
        }
        return null;
    }

    public static List<SensorRuleContext> getSensorRuleByModuleId(FacilioModule module, boolean isFetchSubProps, boolean isHistorical) throws Exception {

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRuleFields());
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSensorRuleFields())
                .table(ModuleFactory.getSensorRuleModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), module.getExtendedModuleIds(), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        return SensorRuleUtil.getSensorRuleFromProps(props, isFetchSubProps, isHistorical);
    }

    public static List<SensorRulePropContext> getSensorRuleValidatorPropsByParentRuleId(Long sensorRuleId, boolean includeStatus) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSensorRulePropsFields())
                .table(ModuleFactory.getSensorRulePropsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_SENSOR_RULE_ID", "parentSensorRuleId", String.valueOf(sensorRuleId), NumberOperators.EQUALS));
        if (includeStatus) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(Boolean.TRUE), BooleanOperators.IS));
        }
        List<Map<String, Object>> props = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            List<SensorRulePropContext> sensorProp = FieldUtil.getAsBeanListFromMapList(props, SensorRulePropContext.class);
            return sensorProp;
        }
        return null;
    }

    public static void checkDefaultSeverityProps(JSONObject defaultSeverityProps, JSONObject ruleProps) {
        if (ruleProps != null) {
            if (ruleProps.get("severity") != null) {
                defaultSeverityProps.put("severity", ruleProps.get("severity"));
            }
            if (ruleProps.get("subject") != null) {
                defaultSeverityProps.put("subject", ruleProps.get("subject"));
            }
        }
    }

    public static List<SensorRulePropContext> getSensorRuleTypesByParentId(SensorRuleContext sensorRule) throws Exception {

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRulePropsFields());

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSensorRulePropsFields())
                .table(ModuleFactory.getSensorRulePropsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentSensorRuleId"), Collections.singletonList(sensorRule.getId()), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            List<SensorRulePropContext> sensorProp = FieldUtil.getAsBeanListFromMapList(props, SensorRulePropContext.class);
            return sensorProp;
        }
        return null;
    }

    public static SensorRuleContext getSensorRuleById(Long ruleId) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSensorRuleFields())
                .table(ModuleFactory.getSensorRuleModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(ruleId, ModuleFactory.getSensorRuleModule()));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            SensorRuleContext sensorRule = FieldUtil.getAsBeanFromMap(props.get(0), SensorRuleContext.class);
            List<SensorRulePropContext> sensorRuleType = getSensorRuleValidatorPropsByParentRuleId(sensorRule.getId(), true);
            sensorRule.setSensorRuleTypes(sensorRuleType);
            return sensorRule;
        }
        return null;
    }

    public static void updateSensorRuleType(List<SensorRulePropContext> sensorRuleTypes) throws Exception {
        for (SensorRulePropContext sensorRuleType : sensorRuleTypes) {
            GenericUpdateRecordBuilder propsUpdateBuilder = new GenericUpdateRecordBuilder()
                    .fields(FieldFactory.getSensorRulePropsFields())
                    .table(ModuleFactory.getSensorRulePropsModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(sensorRuleType.getId(), ModuleFactory.getSensorRulePropsModule()));
            propsUpdateBuilder.update(FieldUtil.getAsProperties(sensorRuleType));
        }
    }

    public static void addSensorRuleProp(List<SensorRulePropContext> sensorRuleProp) throws Exception {
        for (SensorRulePropContext sensorProp : sensorRuleProp) {
            Map<String, Object> prop = FieldUtil.getAsProperties(sensorProp);
            GenericInsertRecordBuilder propsInsertBuilder = new GenericInsertRecordBuilder()
                    .fields(FieldFactory.getSensorRulePropsFields())
                    .table(ModuleFactory.getSensorRulePropsModule().getTableName());
            propsInsertBuilder.addRecord(prop);
            propsInsertBuilder.save();
        }
    }

    public static List<SensorRuleContext> getSensorRules(List<Long> sensorRuleIds) throws Exception {
        String moduleName = FacilioConstants.ContextNames.SENSOR_RULE_MODULE;
        FacilioContext summary = V3Util.getSummary(moduleName, sensorRuleIds);
        List<SensorRuleContext> sensorRules = Constants.getRecordListFromContext(summary, moduleName);
        return sensorRules;
    }

    public static void setParentIdForRuleTypes(List<SensorRulePropContext> sensorRuleTypes, SensorRuleContext sensorRuleContext) {
        for (SensorRulePropContext sensorRuleProps : sensorRuleTypes) {
            sensorRuleProps.setParentSensorRuleId(sensorRuleContext.getId());
        }
    }

    public static SensorAlarmDetailsContext getSensorAlarmDetails(Long sensorRuleId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSensorAlarmDetailFields())
                .table(ModuleFactory.getSensorAlarmDetailsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SENSOR_RULE_ID", "sensorRuleId", String.valueOf(sensorRuleId), NumberOperators.EQUALS));
        List<Map<String, Object>> alarmDetailProps = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(alarmDetailProps)) {
            SensorAlarmDetailsContext sensorAlarmDetailsContext = FieldUtil.getAsBeanFromMap(alarmDetailProps.get(0), SensorAlarmDetailsContext.class);
            sensorAlarmDetailsContext.setSeverity(AlarmAPI.getAlarmSeverity(sensorAlarmDetailsContext.getSeverityId()).getSeverity());
            return sensorAlarmDetailsContext;
        }
        return null;
    }

    public static void updateSensorRuleStatus(Long categoryId, boolean status) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule sensorModule = modBean.getModule(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        SensorRuleContext sensorRuleContext = new SensorRuleContext();
        sensorRuleContext.setStatus(status);
        GenericUpdateRecordBuilder updateSensorStatus = new GenericUpdateRecordBuilder()
                .table(sensorModule.getTableName())
                .fields(FieldFactory.getSensorRuleFields())
                .andCondition(CriteriaAPI.getCondition("ASSET_CATEGORY_ID", "assetCategory", String.valueOf(categoryId), NumberOperators.EQUALS));

        updateSensorStatus.update(FieldUtil.getAsProperties(sensorRuleContext));
    }

    public static void addSensorRuleType(List<SensorRulePropContext> sensorRuleTypes, SensorRuleType type, FacilioField readingFieldObj, boolean filterFields) {

        JSONObject rulePropInfo = new JSONObject();
        for (String prop : type.getSensorRuleValidationType().getSensorRuleProps()) {
            if (!prop.equals("subject") && !prop.equals("severity")) {
                rulePropInfo.put(prop, null);
            }
        }

        SensorRulePropContext newContext = new SensorRulePropContext();
        newContext.setSensorRuleType(type);
        newContext.setSubject(type.getValueString());
        newContext.setRuleValidatorProps(rulePropInfo);

        if (filterFields) {
            List<SensorRulePropContext> filteredRuleTypes = sensorRuleTypes.stream()
                    .filter(i -> i.getSensorRuleTypeEnum() == type)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(filteredRuleTypes)) {
                NumberField numberField = (NumberField) readingFieldObj;
                if (!numberField.isCounterField() && readingFieldObj instanceof NumberField && !type.isCounterFieldType()) {
                    sensorRuleTypes.add(newContext);
                } else if ((numberField.isCounterField() && type.isCounterFieldType())) {
                    sensorRuleTypes.add(newContext);
                }
            }
        }
        else{
            sensorRuleTypes.add(newContext);
        }
    }

    public static void setCategory(SensorRuleContext rule) throws Exception {
        ResourceType type = rule.getResourceTypeEnum();
        V3Context category = CommonConnectedUtil.getCategory(type, rule.getCategoryId());
        if(category != null) {
            rule.setCategory(new ResourceCategory<>(type, category));
        }
    }
}
