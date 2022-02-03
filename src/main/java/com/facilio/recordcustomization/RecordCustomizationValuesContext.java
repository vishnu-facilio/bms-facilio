package com.facilio.recordcustomization;

import com.facilio.db.criteria.manager.NamedCriteria;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RecordCustomizationValuesContext{
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long parentId;
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
    public long getParentId() {
        return parentId;
    }

    private long namedCriteriaId;
    public void setNamedCriteriaId(long namedCriteriaId) {
        this.namedCriteriaId = namedCriteriaId;
    }
    public long getNamedCriteriaId() {
        return namedCriteriaId;
    }

    private NamedCriteria namedCriteria;
    public NamedCriteria getNamedCriteria() {
        return namedCriteria;
    }
    public void setNamedCriteria(NamedCriteria namedCriteria) {
        this.namedCriteria = namedCriteria;
    }

    private String fieldValue;
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
    public String getFieldValue() {
        return fieldValue;
    }

    private JSONObject customization;
    public String getCustomization() {
        return (customization != null) ? customization.toJSONString() : null;
    }
    public void setCustomization(JSONObject customization) {
        this.customization = customization;
    }
    public void setCustomization(String data) throws ParseException{
        if (StringUtils.isNotEmpty(data)) {
            this.customization = (JSONObject) new JSONParser().parse(data);
        }
    }

}
