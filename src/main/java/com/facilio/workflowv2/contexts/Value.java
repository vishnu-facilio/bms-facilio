package com.facilio.workflowv2.contexts;

import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class Value {

    public static Value VOID = new Value(new Object());

    final Object value;

    public Value(Object value) {
        this.value = value;
    }
    
    public Object asObject() {
    	return value;
    }
    
    public FacilioModule asModule() {
    	return (FacilioModule) value;
    }
    
    public FacilioField asField() {
    	return (FacilioField) value;
    }
    
    public WorkflowReadingContext asWorkflowReadingContext() {
    	return (WorkflowReadingContext) value;
    }
    
    public DateRange asDateRange() {
    	return (DateRange) value;
    }
    
    public Criteria asCriteria() {
    	return (Criteria) value;
    }
    
    public DBParamContext asDbParams() {
    	return (DBParamContext) value;
    }
    
    public List<Object> asList() {
   	 	return (List<Object>)value;
    }
    
    public Map<Object,Object> asMap() {
      	 return (Map<Object,Object>)value;
    }

    public Boolean asBoolean() {
        return (Boolean)value;
    }

    public Double asDouble() {
        return Double.parseDouble(value.toString());
    }
    
    public Integer asInt() {
    	if(value instanceof Integer) {
    		return (Integer)value;
    	}
        return ((Double)value).intValue();
    }
    
    public Long asLong() {
    	if(value instanceof Long) {
    		return (Long)value;
    	}
        return ((Double)value).longValue();
    }

    public String asString() {
    	if(value instanceof DateRange) {
    		return value.toString();
    	}
        return String.valueOf(value);
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    @Override
    public int hashCode() {

        if(value == null) {
            return 0;
        }

        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object o) {

    	Value that = (Value)o;
    	
        if(this.value == null && that.value == null) {
        	return true;
        }
        if((this.value == null && that.value != null) || (this.value != null && that.value == null)) {
            return false;
        }
        return this.value.equals(that.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}