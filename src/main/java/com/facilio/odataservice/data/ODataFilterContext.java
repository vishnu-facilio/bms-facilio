package com.facilio.odataservice.data;

import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.util.ODATAUtil;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ODataFilterContext {
    FacilioField facilioField;
    String valueList;
    Operator operator;
    String field;
    String moduleName;
    String operatorName;


    public FacilioField getfacilioField() throws Exception{
        String fieldName = ODATAUtil.getDisplayNameVsFieldNameMap(moduleName).get(ODATAUtil.addNameSpaces(field));
        facilioField = Constants.getModBean().getField(fieldName,moduleName);
        return facilioField;
    }

    public Operator getOperator() {
        switch (operatorName){
            case "le":
                return NumberOperators.LESS_THAN_EQUAL;
            case "lt":
                return NumberOperators.LESS_THAN;
            case "ge":
                return NumberOperators.GREATER_THAN_EQUAL;
            case "gt":
                return NumberOperators.GREATER_THAN;
            case "eq":
                return NumberOperators.EQUALS;
            case "ne":
                return NumberOperators.NOT_EQUALS;
        }
        return NumberOperators.EQUALS;
    }

    public String getValueList() throws Exception {
        if(getfacilioField().getDataTypeEnum() == FieldType.DATE_TIME || getfacilioField().getDataTypeEnum() ==FieldType.DATE){
            return String.valueOf(DateTimeUtil.getTime(valueList,"yyyy-MM-dd'T'HH:mm:ss'Z'"));
        }
        return valueList;
    }
}
