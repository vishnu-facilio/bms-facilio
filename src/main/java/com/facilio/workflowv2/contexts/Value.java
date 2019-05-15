package com.facilio.workflowv2.contexts;

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
    
    public List<?> asList() {
    	 return (List<?>)value;
    }
    
    public Map<?,?> asMap() {
   	 return (Map<?,?>)value;
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

    public boolean isDouble() {
        return value instanceof Double;
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

        if(value == null || o == null || o.getClass() != value.getClass()) {
            return false;
        }

        Value that = (Value)o;

        return this.value.equals(that.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}