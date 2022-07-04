package com.facilio.report.context;

public class PivotValueColumnContext {

    private String valueType;

    private String fieldDisplayName;

    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    private PivotDataColumnContext moduleMeasure;

    public PivotDataColumnContext getModuleMeasure() {
        return moduleMeasure;
    }

    public void setModuleMeasure(PivotDataColumnContext moduleMeasure) {
        this.moduleMeasure = moduleMeasure;
    }

    public PivotFormulaColumnContext getCustomMeasure() {
        return customMeasure;
    }

    public void setCustomMeasure(PivotFormulaColumnContext customMeasure) {
        this.customMeasure = customMeasure;
    }

    private PivotFormulaColumnContext customMeasure;


    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getFieldDisplayName() {
        return fieldDisplayName;
    }

    public void setFieldDisplayName(String fieldDisplayName) {
        this.fieldDisplayName = fieldDisplayName;
    }
}
