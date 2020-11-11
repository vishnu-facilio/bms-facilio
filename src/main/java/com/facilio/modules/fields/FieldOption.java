package com.facilio.modules.fields;

public class FieldOption {
    String label, value, subModule;

    public FieldOption(String value, String label) {
        this(value, label, null);
    }

    public FieldOption(String value, String label, String subModule) {
        this.value = value;
        this.label = label;
        this.subModule = subModule;
    }

    public String getLabel() {
        return label;
    }
    public String getValue() {
        return value;
    }
    public String getSubModule() {
        return subModule;
    }
}
