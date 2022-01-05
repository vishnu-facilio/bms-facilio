package com.facilio.report.context;

import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PivotFormulaColumnContext extends PivotColumnContext {
    private String scriptExpression;
    private String dataTypeEnum;
    private String expression;

    public String getScriptExpression() {
        return scriptExpression;
    }

    public void setScriptExpression(String scriptExpression) {
        this.scriptExpression = scriptExpression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDataTypeEnum() {
        return dataTypeEnum;
    }

    public void setDataTypeEnum(String dataTypeEnum) {
        this.dataTypeEnum = dataTypeEnum;
    }

    @JsonIgnore
    public FacilioField toFacilioField() {
        FacilioField facilioField;
        if (getDataTypeEnum() != null && getDataTypeEnum().equals("DECIMAL")) {
            facilioField = new NumberField();
            facilioField.setDataType(FieldType.DECIMAL);
        } else if (getDataTypeEnum() != null && getDataTypeEnum().equals("NUMBER")) {
            facilioField = new NumberField();
            facilioField.setDataType(FieldType.NUMBER);
        } else if (getDataTypeEnum() != null && getDataTypeEnum().equals("DATE_TIME")) {
            facilioField = new DateField();
            facilioField.setDataType(FieldType.DATE_TIME);
        } else if (getDataTypeEnum() != null && getDataTypeEnum().equals("BOOLEAN")) {
            facilioField = new BooleanField();
            facilioField.setDataType(FieldType.BOOLEAN);
        } else {
            facilioField = new StringField();
            facilioField.setDataType(FieldType.STRING);
        }
        return facilioField;
    }

}
