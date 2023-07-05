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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateMeanVariationInSensorRule implements SensorRuleTypeValidationInterface {

    @Override
    public List<String> getSensorRuleProps() {
        List<String> validatorProps = new ArrayList<String>();
        validatorProps.add("averageBoundPercentage");
        validatorProps.add("timeInterval");
        validatorProps.add("subject");
        validatorProps.add("severity");
        return validatorProps;
    }

    @Override
    public JSONObject getDefaultSeverityAndSubject() {
        JSONObject defaultProps = new JSONObject();
        defaultProps.put("subject", "Abnormal energy reading ");
        defaultProps.put("comment", "Counter Field readings seems to high delta difference.");
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
            if (currentDeltaValue == null) {
                return false;
            }

            Long noOfHoursToBeFetched = (Long) calculateTimeInterval(fieldConfig);

            Integer averageBoundPercentage = Integer.valueOf(String.valueOf(fieldConfig.get("averageBoundPercentage")));
            if (averageBoundPercentage == null) {
                averageBoundPercentage = 100;
            }
            List<Double> readings = SensorRuleUtil.getLiveOrHistoryReadingsToBeEvaluated((NumberField) deltaField, reading.getParentId(), reading.getTtime(), noOfHoursToBeFetched.intValue(), isHistorical, historicalReadings, completeHistoricalReadingsMap, getSensorRuleTypeFromValidator());
            if (readings != null && !readings.isEmpty()) {

                return evaluateMeanVariation(currentDeltaValue, averageBoundPercentage, readings);
            }
        }

        return false;
    }

    private boolean evaluateMeanVariation(Double currentDeltaValue, Integer averageBoundPercentage, List<Double> readings) {
        Double averageValue = getAverage(readings);
        if (averageValue != null && averageValue > 0) {
            Double averageLowerLimit = averageValue - (averageValue * averageBoundPercentage / 100);
            Double averageHigherLimit = averageValue + (averageValue * averageBoundPercentage / 100);

			return currentDeltaValue < averageLowerLimit && currentDeltaValue > averageHigherLimit;
        }
        return true;
    }


    private Double getAverage(List<Double> readings) {

        if (readings != null && !readings.isEmpty()) {
            double sum = 0, avg = 0, n = 0;
            for (Double reading : readings) {
                sum += reading;
                n++;
            }
            if (n > 2) {
                avg = sum / n;
                return avg;
            }
        }
        return null;
    }

    @Override
    public SensorRuleType getSensorRuleTypeFromValidator() {
        return SensorRuleType.MEAN_VARIATION;
    }

    @Override
    public boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap, JSONObject fieldConfig) {
        if (MapUtils.isNotEmpty(readingsMap)) {
            Integer averageBoundPercentage = Integer.valueOf(String.valueOf(fieldConfig.get("averageBoundPercentage")));
            List<Double> readings = readingsMap.values().stream().collect(Collectors.toList());

            return evaluateMeanVariation((Double) currentValue, averageBoundPercentage, readings);
        }
        return false;
    }

    @Override
    public Object calculateTimeInterval(Map<String, Object> ruleProp) {
        Long noOfDaysToBeFetched = Long.valueOf(String.valueOf(ruleProp.get("timeInterval")));
        if (noOfDaysToBeFetched == null) {
            noOfDaysToBeFetched = 7L;
        }
        Long noOfHoursToBeFetched = noOfDaysToBeFetched * 24L;
        return noOfHoursToBeFetched;
    }
}
