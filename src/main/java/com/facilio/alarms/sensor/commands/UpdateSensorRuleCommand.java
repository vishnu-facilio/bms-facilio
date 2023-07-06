package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorAlarmDetailsContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.NewSensorRuleUtil;
import com.facilio.beans.NamespaceBean;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.ns.context.NSType;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateSensorRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SensorRuleContext sensorRule = (SensorRuleContext) context.get(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        List<SensorRulePropContext> sensorRuleTypes = (List<SensorRulePropContext>) context.get(FacilioConstants.ContextNames.SENSOR_RULE_TYPES);
        updateSensorAlarmDetails(sensorRule);
        NamespaceBean nsBean = Constants.getNsBean();
        nsBean.updateNsStatus(sensorRule.getId(),sensorRule.getStatus(),Collections.singletonList(NSType.SENSOR_RULE));

        filterRuleTypesAndUpdate(sensorRuleTypes);
        filterRuleTypesAndAdd(sensorRuleTypes, sensorRule);

        return false;
    }

    private void filterRuleTypesAndUpdate(List<SensorRulePropContext> sensorRuleTypes) throws Exception {
        List<SensorRulePropContext> existingRuleType = sensorRuleTypes.stream().filter(ruleType -> (ruleType.getId() != null && ruleType.getId() > 0)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(existingRuleType)) {
            NewSensorRuleUtil.updateSensorRuleType(existingRuleType);
        }

    }

    private void filterRuleTypesAndAdd(List<SensorRulePropContext> sensorRuleTypes, SensorRuleContext sensorRule) throws Exception {
        List<SensorRulePropContext> newRuleType = sensorRuleTypes.stream().filter(ruleType -> ruleType.getId() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newRuleType)) {
            NewSensorRuleUtil.setParentIdForRuleTypes(sensorRuleTypes, sensorRule);
            NewSensorRuleUtil.addSensorRuleProp(newRuleType);
        }
    }

    private void updateSensorAlarmDetails(SensorRuleContext sensorRule) throws Exception {
        SensorAlarmDetailsContext sensorAlarmDetails = sensorRule.getSensorAlarmDetails();

        AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(sensorAlarmDetails.getSeverity());
        Objects.requireNonNull(alarmSeverity, "Invalid severity value found!");
        sensorAlarmDetails.setSeverityId(alarmSeverity.getId());

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getSensorAlarmDetailFields())
                .table(ModuleFactory.getSensorAlarmDetailsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SENSOR_RULE_ID","sensorRuleId",String.valueOf(sensorRule.getId()),NumberOperators.EQUALS));
        updateRecordBuilder.update(FieldUtil.getAsProperties(sensorAlarmDetails));
    }
}
