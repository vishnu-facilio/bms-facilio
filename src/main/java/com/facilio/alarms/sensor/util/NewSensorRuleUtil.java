package com.facilio.alarms.sensor.util;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
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
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
}
