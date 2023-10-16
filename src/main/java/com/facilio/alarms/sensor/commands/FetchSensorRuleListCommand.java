package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorAlarmDetailsContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.NewSensorRuleUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchSensorRuleListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long readingFieldId = (long) context.get(FacilioConstants.ContextNames.READING_FIELD_ID);
        long categoryId = (long) context.get(FacilioConstants.ContextNames.CATEGORY_ID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField readingFieldObj = modBean.getField(readingFieldId);

        SensorRuleContext sensorRule = new SensorRuleContext();
        List<SensorRulePropContext> sensorRuleTypes = new ArrayList<>();

        if (readingFieldId > 0 && categoryId > 0 && readingFieldObj instanceof NumberField) {

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRuleFields());

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getSensorRuleFields())
                    .table(ModuleFactory.getSensorRuleModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("sensorFieldId"), String.valueOf(readingFieldId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("categoryId"), String.valueOf(categoryId), NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();

            if (CollectionUtils.isNotEmpty(props)) {
                sensorRule = FieldUtil.getAsBeanFromMap(props.get(0), SensorRuleContext.class);
                sensorRuleTypes = NewSensorRuleUtil.getSensorRuleValidatorPropsByParentRuleId(sensorRule.getId(), Boolean.FALSE);
            }

            // String[] specialFields = new String[]{"totalEnergyConsumption", "phaseEnergyR", "phaseEnergyY", "phaseEnergyB"};
            //  TODO:special fields need to be migrated from decimal to counter

            sensorRulesBasedOnField(readingFieldObj, sensorRuleTypes);
        }

        setSensorRuleDetails(sensorRule, sensorRuleTypes);
        context.put(FacilioConstants.ContextNames.SENSOR_RULE_MODULE, sensorRule);
        context.put(FacilioConstants.ContextNames.ID, sensorRule.getId());

        return false;
    }

    /***
     *
     * @param readingFieldObj
     * @param sensorRuleTypes
     * @implNote filter sensorrule types based on fields which where not configured by user
     */
    private static void sensorRulesBasedOnField(FacilioField readingFieldObj, List<SensorRulePropContext> sensorRuleTypes) {
        for (SensorRuleType type : SensorRuleType.values()) {
            if (type == SensorRuleType.NEGATIVE_VALUE) {
                //negative value rule is intentionally removed
                continue;
            }

            NewSensorRuleUtil.addSensorRuleType(sensorRuleTypes, type, readingFieldObj, true);

        }
    }

    private void setSensorRuleDetails(SensorRuleContext sensorRule, List<SensorRulePropContext> sensorRuleTypes) throws Exception {
        if (sensorRule.getId() > 0) {
            SensorAlarmDetailsContext sensorDetails = NewSensorRuleUtil.getSensorAlarmDetails(sensorRule.getId());
            sensorRule.setSensorAlarmDetails(sensorDetails);
        }
        sensorRule.setSensorRuleTypes(sensorRuleTypes);
    }

}

