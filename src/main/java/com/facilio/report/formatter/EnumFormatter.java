package com.facilio.report.formatter;

import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class EnumFormatter extends Formatter {

    private JSONObject enumValues = new JSONObject();

    public EnumFormatter(FacilioField field) {
        super(field);
        this.loadEnumValues(null);
    }

    public JSONObject getEnumValues() {
        return enumValues;
    }

    public Object getEnumValue(Integer enumNumber) {
        return enumValues.get(enumNumber);
    }

    public EnumFormatter enumValues(JSONObject enumValues) {
        this.enumValues = enumValues;
        return this;
    }

    public EnumFormatter enumValue(Integer enumNumber, String enumValue) {
        this.enumValues.put(enumNumber, enumValue);
        return this;
    }

    private void loadEnumValues(HashMap<String,String> existEnumValues) {
        EnumField enumField = (EnumField) getField();
        Map<Integer, Object> enumMap = enumField.getEnumMap();

        for (Map.Entry<Integer, Object> entry : enumMap.entrySet()) {
            if (existEnumValues != null && existEnumValues.containsKey(entry.getKey().toString())) {
                this.enumValues.put(entry.getKey(), existEnumValues.get(entry.getKey().toString()));
            }
            else {
                this.enumValues.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Object format(Object value) {
        if (value == null) {
            return null;
        }
        Integer result = Integer.parseInt(value.toString());
        return getEnumValue(result);
    }

    @Override
    public JSONObject serialize() {
        JSONObject format = new JSONObject();
        format.put("enumValues", getEnumValues());
        return format;
    }

    @Override
    public void deserialize(JSONObject format) {
        HashMap<String, String> enumValuesMap = (HashMap<String, String>) format.get("enumValues");
        this.loadEnumValues(enumValuesMap);
    }
}