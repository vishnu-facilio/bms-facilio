package com.facilio.alarms.sensor.sensorrules;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateSameValueWithZeroDeltaInSensorRule implements SensorRuleTypeValidationInterface {

    @Override
    public List<String> getSensorRuleProps() {
        List<String> validatorProps = new ArrayList<>();
        validatorProps.add("timeInterval");
        validatorProps.add("subject");
        validatorProps.add("severity");
        return validatorProps;
    }

    @Override
    public JSONObject getDefaultSeverityAndSubject() {
        JSONObject defaultProps = new JSONObject();
        defaultProps.put("subject", "No change in value");
        defaultProps.put("comment", "Counter Field readings seems to have equal readings.");
        defaultProps.put("severity", FacilioConstants.Alarm.WARNING_SEVERITY);
        return defaultProps;
    }

    @Override
    public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception {

        ReadingContext reading = (ReadingContext) record;
        FacilioField readingField = sensorRule.getSensorField();

        if (readingField instanceof NumberField && reading != null && reading.getParentId() != -1) {
            NumberField numberField = (NumberField) readingField;
            Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
            if (currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField) || !SensorRuleUtil.isCounterField(numberField)) {
                return false;
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField deltaField = modBean.getField(numberField.getName() + "Delta", numberField.getModule().getName());
            if (deltaField == null) {
                return false;
            }
            Double currentDeltaValue = (Double) reading.getReading(deltaField.getName());
            if (currentDeltaValue == null || !currentDeltaValue.equals(0.0)) {
                return false;
            }

            Long noOfHoursToBeFetched = (Long) calculateTimeInterval(fieldConfig);

            List<Double> readings = SensorRuleUtil.getLiveOrHistoryReadingsToBeEvaluated((NumberField) deltaField, reading.getParentId(), reading.getTtime(), noOfHoursToBeFetched.intValue(), isHistorical, historicalReadings, completeHistoricalReadingsMap, getSensorRuleTypeFromValidator());
            if (readings != null && !readings.isEmpty()) {
                LinkedHashSet<Double> readingSet = new LinkedHashSet<Double>();
                readingSet.addAll(readings);
                if (readingSet != null && readingSet.size() == 1) {
                    for (Double readingSetValue : readingSet) {
                        if (readingSetValue != null && readingSetValue.equals(currentDeltaValue)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public SensorRuleType getSensorRuleTypeFromValidator() {
        return SensorRuleType.SAME_VALUE_WITH_ZERO_DELTA;
    }

    @Override
    public boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap, JSONObject fieldConfig) {
        if (!MapUtils.isEmpty(readingsMap) && readingsMap.size() > 1) {
            Set<Object> readingValue = readingsMap.values().stream().collect(Collectors.toSet());
            return readingValue.size() == 1;
        }
        return false;
    }

    @Override
    public Object calculateTimeInterval(Map<String, Object> ruleProp) {
        Long noOfHoursToBeFetched = Long.valueOf(String.valueOf(ruleProp.get("timeInterval")));
        if (noOfHoursToBeFetched == null) {
            noOfHoursToBeFetched = 6l;
        }
        return noOfHoursToBeFetched;
    }
}
