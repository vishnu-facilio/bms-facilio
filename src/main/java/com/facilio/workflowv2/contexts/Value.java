package com.facilio.workflowv2.contexts;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;

import java.util.List;
import java.util.Map;

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

    public String asString() {
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

        if(value == o) {
            return true;
        }
        Value that = (Value)o;
        if(value == null && that != null && that.value == null) {
        	return true;
        }
        if(value == null || o == null || o.getClass() != value.getClass()) {
            return false;
        }

        return this.value.equals(that.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}