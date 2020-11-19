package com.facilio.modules.fields;

import java.util.Objects;

public class FieldOption {
    String label, value, subModule;

    public FieldOption(long value, Object label) {
        this(String.valueOf(value), label);
    }

    public FieldOption(String value, Object label) {
        this(value, label, null);
    }

    public FieldOption(long value, Object label, String subModule) {
        this (String.valueOf(value), label, subModule);
    }

    public FieldOption(String value, Object label, String subModule) {
        this.value = value;
        this.label = label == null ? null : Objects.toString(label);
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
