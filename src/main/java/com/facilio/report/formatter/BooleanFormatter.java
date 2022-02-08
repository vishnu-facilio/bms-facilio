package com.facilio.report.formatter;

import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

public class BooleanFormatter extends Formatter {

    private String trueValue;
    private String falseValue;

    public BooleanFormatter(FacilioField field) {
        super(field);
    }

    public String getTrueValue() {
        return trueValue;
    }

    public BooleanFormatter trueValue(String trueValue) {
        this.trueValue = trueValue;
        return this;
    }

    public String getFalseValue() {
        return falseValue;
    }

    public BooleanFormatter falseValue(String falseValue) {
        this.falseValue = falseValue;
        return this;
    }

    @Override
    public Object format(Object value) {
        if (value == null) {
            return null;
        }
        Boolean result = (Boolean) value;
        BooleanField booleanField = (BooleanField) getField();

        if (result == true) {
            return !isNullOrEmpty(getTrueValue()) ? getTrueValue() : !isNullOrEmpty(booleanField.getTrueVal()) ? booleanField.getTrueVal() : "true";
        }
        else {
            return !isNullOrEmpty(getFalseValue()) ? getFalseValue() : !isNullOrEmpty(booleanField.getFalseVal()) ? booleanField.getFalseVal() : "false";
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject formatJSON = new JSONObject();
        formatJSON.put("trueValue", getTrueValue());
        formatJSON.put("falseValue", getFalseValue());
        return formatJSON;
    }

    @Override
    public void deserialize(JSONObject formatJSON) {
        this.trueValue = (String) formatJSON.getOrDefault("trueValue", this.trueValue);
        this.falseValue = (String) formatJSON.getOrDefault("falseValue", this.falseValue);
    }
}