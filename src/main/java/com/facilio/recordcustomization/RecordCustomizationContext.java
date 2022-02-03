package com.facilio.recordcustomization;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordCustomizationContext {
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String uniqueId;
    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    private int customizationType;
    public void setCustomizationType(int customizationType) {
        this.customizationType = customizationType;
    }
    public int getCustomizationType() {
        return customizationType;
    }

    private long customizationFieldId;
    public void setCustomizationFieldId(long customizationFieldId) {
        this.customizationFieldId = customizationFieldId;
    }
    public long getCustomizationFieldId() {
        return customizationFieldId;
    }

    private List<RecordCustomizationValuesContext> values;
    public List<RecordCustomizationValuesContext> getValues() {
        return values;
    }
    public void setValues(List<RecordCustomizationValuesContext> values) {
        this.values = values;
    }

    private String defaultCustomization;
    public String getDefaultCustomization() {
        return defaultCustomization;
    }
    public void setDefaultCustomization(String defaultCustomization) {
        this.defaultCustomization = defaultCustomization;
    }


    public static enum CustomizationType {
        FIELD(1),
        NAMED_CRITERIA(2);

        private int intVal;

        private CustomizationType(int val) {
            this.intVal = val;
        }
        public static CustomizationType getCustomizationType(int val){
            return TYPE_MAP.get(val);
        }
        public int getIntVal() {
            return intVal;
        }

        private static final Map<Integer, CustomizationType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, CustomizationType> initTypeMap() {
            Map<Integer, CustomizationType> typeMap = new HashMap<>();
            for(CustomizationType type : values()) {
                typeMap.put(type.getIntVal(), type);
            }
            return typeMap;
        }
    }

}
