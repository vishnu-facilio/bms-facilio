package com.facilio.modules.fields;

import lombok.Getter;

import java.util.Objects;

public class FieldOption <Value> {
    private Value value;
    private String label, secondaryLabel,fourthLabel, subModule;
    @Getter
    private String color,accent,severityLevel,subModuleName;

    public FieldOption(Value value, Object label) {
        this(value, label, null);
    }

    public FieldOption(Value value, Object label, String subModule) {
        this (value, label, null, subModule);
    }

    public FieldOption(Value value, Object label, Object secondaryLabel, String subModule) {
        this.value = value;
        this.label = label == null ? null : Objects.toString(label);
        this.secondaryLabel = secondaryLabel == null ? null : Objects.toString(secondaryLabel);
        this.subModule = subModule;
    }
    public FieldOption(Value value, Object label, Object secondaryLabel,Object fourthLabel ,String subModule,String color,String accent,String severityLevel,String moduleName){
        this.value = value;
        this.label = label == null ? null : Objects.toString(label);
        this.secondaryLabel = secondaryLabel == null ? null : Objects.toString(secondaryLabel);
        this.fourthLabel = fourthLabel == null ? null : Objects.toString(fourthLabel);
        this.subModule = subModule;
        this.color = color;
        this.severityLevel = severityLevel;
        this.accent = accent;
        this.subModuleName=moduleName;
    }

    public String getLabel() {
        return label;
    }
    public String getSecondaryLabel() {
        return secondaryLabel;
    }
    public String getFourthLabel(){ return fourthLabel;}
    public Value getValue() {
        return value;
    }
    public String getSubModule() {
        return subModule;
    }
}
