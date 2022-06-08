package com.facilio.report.formatter;

import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.Map;

public class LookupFormatter extends Formatter {
    protected LookupFormatter(FacilioField field) {
        super(field);
    }


    private Map<String,Object> lookupMap;
    private String alias;

    public Map<String, Object> getLookupMap() {
        return lookupMap;
    }

    public void setLookupMap(Map<String, Object> lookupMap) {
        this.lookupMap = lookupMap;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public Object format(Object value) {
        if(value != null && lookupMap != null && lookupMap.containsKey(value)){
            return lookupMap.get(value);
        }
        return value;
    }

    @Override
    public JSONObject serialize() {
        return new JSONObject();
    }

    @Override
    public void deserialize(JSONObject formatJSON) {

    }
}
