package com.facilio.modules.fields;

import java.util.Objects;

public class FieldOption <Value> {
    private Value value;
    private String label, subModule;

    public FieldOption(Value value, Object label) {
        this(value, label, null);
    }

    public FieldOption(Value value, Object label, String subModule) {
        this.value = value;
        this.label = label == null ? null : Objects.toString(label);
        this.subModule = subModule;
    }

    public String getLabel() {
        return label;
    }
    public Value getValue() {
        return value;
    }
    public String getSubModule() {
        return subModule;
    }
}
