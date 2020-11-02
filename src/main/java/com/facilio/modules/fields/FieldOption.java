package com.facilio.modules.fields;

public class FieldOption {
    String label, value;

    public FieldOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    public String getValue() {
        return value;
    }
}
