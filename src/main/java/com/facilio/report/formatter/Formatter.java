package com.facilio.report.formatter;

import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

public abstract class Formatter {

    private FacilioField field;

    protected Formatter(FacilioField field) {
        this.field = field;
    }

    public FacilioField getField() {
        return field;
    }

    protected boolean isNullOrEmpty(Object... values) {
        if (values != null) {
            for (int i=0; i < values.length; i++) {
                if (values[i] == null || values[i].toString().isEmpty()) {
                    return true;
                }
                else {
                    try {
                        if (Double.parseDouble(values[i].toString()) < 0) {
                            return true;
                        }
                    } catch (Exception e) {}
                }
            }
            return false;
        }
        return true;
    }

    protected Object getOrDefault(Object value, Object defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public abstract Object format(Object value);

    public abstract JSONObject serialize();

    public abstract void deserialize(JSONObject formatJSON);


    public static Formatter getInstance(@NotNull FacilioField field) {
        switch (field.getDataTypeEnum()) {
            case NUMBER:
                return new NumberFormatter(field);
            case DECIMAL:
                return new DecimalFormatter(field);
            case BOOLEAN:
                return new BooleanFormatter(field);
            case ENUM:
            case SYSTEM_ENUM:
                return new EnumFormatter(field);
            case MULTI_ENUM:
                return new MultiEnumFormatter(field);
            case DATE:
            case DATE_TIME:
                return new DateFormatter(field);
            case LOOKUP:
                return new LookupFormatter(field);
            default:
                return null;
        }
    }


}
