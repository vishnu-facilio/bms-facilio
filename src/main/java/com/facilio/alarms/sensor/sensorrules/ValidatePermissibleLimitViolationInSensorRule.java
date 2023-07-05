package com.facilio.alarms.sensor.sensorrules;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidatePermissibleLimitViolationInSensorRule implements SensorRuleTypeValidationInterface {
    private static final Logger LOGGER = Logger.getLogger(ValidatePermissibleLimitViolationInSensorRule.class.getName());

    @Override
    public List<String> getSensorRuleProps() {
        List<String> validatorProps = new ArrayList<>();
        validatorProps.add("lowerLimit");
        validatorProps.add("upperLimit");
        validatorProps.add("subject");
        validatorProps.add("severity");
        return validatorProps;
    }

    @Override
    public JSONObject getDefaultSeverityAndSubject() {
        JSONObject defaultProps = new JSONObject();
        defaultProps.put("subject", "Out of range");
        defaultProps.put("comment", "Current Reading doesn't lie between the limits of the reading field.");
        defaultProps.put("severity", FacilioConstants.Alarm.WARNING_SEVERITY);
        return defaultProps;
    }

    @Override
    public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) {

        ReadingContext reading = (ReadingContext) record;
        FacilioField readingField = sensorRule.getSensorField();

        if (readingField instanceof NumberField && reading != null && reading.getParentId() != -1) {
            NumberField numberField = (NumberField) readingField;
            Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
            if (currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField)) {
                return false;
            }

            return evaluateRule(fieldConfig, (Double)currentReadingValue);

        }

        return false;
    }

    @Override
    public SensorRuleType getSensorRuleTypeFromValidator() {
        return SensorRuleType.PERMISSIBLE_LIMIT_VIOLATION;
    }

    @Override
    public boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap, JSONObject fieldConfig) {
        if (currentValue != null) {
            Object value = FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, currentValue);
            return evaluateRule(fieldConfig, (Double) value);
        }
        return false;
    }

    @Override
    public Object calculateTimeInterval(Map<String, Object> ruleProp) {
        return null;
    }

    public boolean evaluateRule(JSONObject fieldConfig, double currentReadingValue) {
        Object lowerLim = fieldConfig.get("lowerLimit");
        Object upperLim = fieldConfig.get("upperLimit");

        if (lowerLim == null || upperLim == null) {
            LOGGER.error("Upper limit and lower limit cannot be empty. Upperlimit : " + upperLim + ", Lowerlimit : " + lowerLim);
            return false;
        }

        Double lowerLimit = Double.valueOf(String.valueOf(lowerLim));
        Double upperLimit = Double.valueOf(String.valueOf(upperLim));

		return !(currentReadingValue > lowerLimit) || !(currentReadingValue < upperLimit);
	}
}
