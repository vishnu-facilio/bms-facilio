package com.facilio.v3.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.List;
import java.util.Map;

public class V3Context extends ModuleBaseWithCustomFields {

    private Map<String, List<SubFormContext>> relations;

    public Map<String, List<SubFormContext>> getRelations() {
        return relations;
    }

    public void setLineItems(Map<String, List<SubFormContext>> relations) {
        this.relations = relations;
    }
}
